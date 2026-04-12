package com.fit.service;

import com.alibaba.fastjson.JSONObject;
import com.fit.entity.WxAccount;
import com.fit.enums.WechatAPI;
import com.fit.util.DateUtils;
import com.fit.util.WechatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 微信Token服务类
 * 合并了配置管理和token缓存功能
 * 支持多公众号，配置从数据库读取，yaml提供全局默认值
 */
@Slf4j
@Service
public class WxApiTokenService {

    @Autowired
    private WxAccountService wxAccountService;

    // ============= yaml全局默认配置（非static） =============
    @Value("${wechat.schedule.enabled:true}")
    private boolean scheduleEnabled = true;
    @Value("${wechat.schedule.token-check-interval:1}")
    private int tokenCheckInterval = 1;
    @Value("${wechat.schedule.config-check-interval:5}")
    private int configCheckInterval = 5;
    @Value("${wechat.schedule.core-pool-size:2}")
    private int corePoolSize = 2;
    // 定时任务
    @Value("${wechat.global.default-refresh-interval:5}")
    private int defaultRefreshInterval = 5;
    @Value("${wechat.global.default-advance-refresh:300}")
    private int defaultAdvanceRefresh = 300;
    @Value("${wechat.global.default-expires-in:7200}")
    private int defaultExpiresIn = 7200;

    // ============= 缓存管理 =============
    // 缓存token和对应的过期时间
    private final Map<String, String> tokenCache = new ConcurrentHashMap<>();
    private final Map<String, Long> tokenExpireTimeCache = new ConcurrentHashMap<>();
    private final Map<String, WxAccount> accountCache = new ConcurrentHashMap<>();
    private final Map<String, Object> datacubeArticle = new ConcurrentHashMap<>();
    private final Map<String, Object> datacubeUser = new ConcurrentHashMap<>();
    private final Map<String, Object> industries = new ConcurrentHashMap<>();
    // ============= 当前公众号配置（ThreadLocal） =============
    private final AtomicReference<WxAccount> currentWxAccount = new AtomicReference<>();
    private ScheduledExecutorService scheduler;

    @PostConstruct
    public void init() {
        log.info("初始化WechatTokenService，配置信息：");
        log.info("  - token检查间隔: {}分钟", tokenCheckInterval);
        log.info("  - 配置检查间隔: {}分钟", configCheckInterval);
        log.info("  - 全局默认提前刷新时间: {}秒", defaultAdvanceRefresh);
        log.info("  - 全局默认token有效期: {}秒", defaultExpiresIn);
        log.info("  - 全局默认刷新间隔: {}分钟", defaultRefreshInterval);
        if (scheduleEnabled) {
            this.scheduler = Executors.newScheduledThreadPool(corePoolSize);
            reloadAccounts();
            if (autoSelectDefaultAccount()) {
                startTokenCheckTask();
                startConfigRefreshTask();
                getAccessDatacubeArticle();
                getAccessDatacubeUser();
                getIndustry();
            }
        }
        log.info("chat-token - 初始化完成，定时任务启用状态: {}", scheduleEnabled);
    }

    /**
     * 自动选择默认公众号
     * 优先选择 shift=1 的公众号，如果没有则选择第一个
     */
    private boolean autoSelectDefaultAccount() {
        if (accountCache.isEmpty()) {
            log.warn("没有可用的公众号配置，无法自动选中默认公众号");
            return false;
        }
        // 查找 shift=1 的公众号
        WxAccount defaultAccount = null;
        for (WxAccount account : accountCache.values()) {
            if (account.getShift() != null && account.getShift()) {
                defaultAccount = account;
                break;
            }
        }
        // 如果没有找到 shift=1 的，则选择第一个
        if (defaultAccount == null) {
            String firstAccount = accountCache.keySet().iterator().next();
            defaultAccount = accountCache.get(firstAccount);
            log.info("未找到标记为默认的公众号，将选择第一个公众号作为默认");
        }

        // 切换到选中的公众号
        if (defaultAccount != null) {
            switchTo(defaultAccount.getAccount());
            log.info("自动选中默认公众号: {} - {}, shift={}", defaultAccount.getAccount(), defaultAccount.getName(), defaultAccount.getShift());
        }
        return true;
    }

    @PreDestroy
    public void destroy() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
        currentWxAccount.getAndSet(null);
        log.info("chat-token - 已销毁");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("应用启动完成，开始预加载所有公众号token");
        preloadAllTokens();
    }

    /**
     * 预加载所有token
     */
    private void preloadAllTokens() {
        int successCount = 0;
        for (String account : accountCache.keySet()) {
            try {
                getAccessToken(account);
                successCount++;
                log.debug("预加载token成功 for appid: {}", account);
            } catch (Exception e) {
                log.error("预加载token失败 for appid: {}", account, e);
            }
        }
        log.info("预加载token完成，成功：{}，总数：{}", successCount, accountCache.size());
    }

    /**
     * 重新加载所有启用的公众号配置
     */
    public synchronized void reloadAccounts() {
        try {
            Map<String, WxAccount> newAccountCache = new HashMap<>();
            List<WxAccount> accounts = this.wxAccountService.findList();
            for (WxAccount bean : accounts) {
                newAccountCache.put(bean.getAccount(), bean);
            }

            for (String key : accountCache.keySet()) {
                if (!newAccountCache.containsKey(key)) {
                    tokenCache.remove(key);
                    tokenExpireTimeCache.remove(key);
                    log.info("公众号 {} 已被禁用或删除，已清除其token缓存", key);
                }
            }
            accountCache.clear();
            accountCache.putAll(newAccountCache);

            // 重新加载后，检查当前选中的公众号是否还存在
            WxAccount current = currentWxAccount.get();
            if (current != null) {
                WxAccount updatedAccount = accountCache.get(current.getAccount());
                if (updatedAccount == null) {
                    // 当前选中的公众号已被删除，需要重新选择
                    log.warn("当前选中的公众号 {} 已被删除，将重新选择默认公众号", current.getAccount());
                    autoSelectDefaultAccount();
                } else {
                    // 更新当前选中的公众号配置
                    currentWxAccount.set(updatedAccount);
                    log.debug("当前选中的公众号配置已更新: {}", updatedAccount.getAccount());
                }
            }
        } catch (Exception e) {
            log.error("加载公众号配置失败", e);
        } finally {
            log.info("重新加载公众号配置完成，当前启用数量：{}", accountCache.size());
        }
    }

    /**
     * 启动token检查任务
     */
    private void startTokenCheckTask() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                int refreshCount = 0;
                log.debug("开始检查access_token是否需要刷新");
                for (String key : tokenCache.keySet()) {
                    if (isTokenExpired(key)) {
                        log.info("检测到access_token即将过期，主动刷新 for appid: {}", key);
                        refreshTokenSync(key);
                        refreshCount++;
                    }

                    if (!tokenCache.containsKey(key)) {
                        log.info("检测到新公众号，预加载token for appid: {}", key);
                        refreshTokenSync(key);
                        refreshCount++;
                    }
                }

                if (refreshCount > 0) {
                    log.info("定时刷新任务完成，共刷新{}个token", refreshCount);
                }
            }
        }, 1, tokenCheckInterval, TimeUnit.MINUTES);
    }

    /**
     * 启动配置刷新任务
     */
    private void startConfigRefreshTask() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                log.debug("开始刷新公众号配置");
                try {
                    reloadAccounts();
                } catch (Exception e) {
                    log.error("刷新公众号配置失败", e);
                }
            }
        }, configCheckInterval, configCheckInterval, TimeUnit.MINUTES);
    }

    /**
     * 获取access_token
     */
    public String getAccessToken(String account) {
        // 1. 参数校验
        if (account == null || account.trim().isEmpty()) {
            log.error("appid不能为空");
            return null;
        }
        // 2. 先从缓存获取
        String token = tokenCache.get(account);
        if (token != null && !isTokenExpired(account)) {
            return token;
        }
        // 3. 加锁刷新
        synchronized (this) {
            return refreshTokenSync(account);
        }
    }

    public WxAccount getAccess(String account) {
        return accountCache.get(account);
    }

    /**
     * 判断token是否过期
     */
    private boolean isTokenExpired(String appid) {
        Long expireTime = tokenExpireTimeCache.get(appid);
        if (expireTime == null) {
            return true;
        }
        return System.currentTimeMillis() >= expireTime;
    }

    /**
     * 获取token的过期时间
     */
    private long getTokenExpireTime(WxAccount account) {
        int advanceRefresh = account.getAdvanceRefresh() != null ? account.getAdvanceRefresh() : defaultAdvanceRefresh;
        int expiresIn = account.getExpiresIn() != null ? account.getExpiresIn() : defaultExpiresIn;
        return System.currentTimeMillis() + (expiresIn - advanceRefresh) * 1000L;
    }

    /**
     * 同步刷新指定appid的token
     */
    private String refreshTokenSync(String account) {
        try {
            WxAccount wxAccount = accountCache.get(account);
            if (wxAccount == null) {
                wxAccount = this.wxAccountService.getByObjId(account);
                if (wxAccount == null) {
                    log.error("未找到account: {} 的公众号配置或该公众号已被禁用", account);
                    return null;
                }
                accountCache.put(account, wxAccount);
            }
            String newToken = WechatUtil.getAccessToken(wxAccount.getAppid(), wxAccount.getAppsecret());
            if (newToken != null) {
                tokenCache.put(account, newToken);
                tokenExpireTimeCache.put(account, getTokenExpireTime(wxAccount));
                try {
                    wxAccount.setTokenTime(new Date());
                    wxAccountService.update(wxAccount);
                } catch (Exception e) {
                    log.warn("更新token获取时间失败", e);
                }
                log.info("成功获取/刷新access_token for account: {}, 公众号: {}", account, wxAccount.getName());
                return newToken;
            } else {
                log.error("获取access_token失败 for account: {}", account);
                String oldToken = tokenCache.get(account);
                if (oldToken != null) {
                    log.warn("使用缓存的旧token for account: {}", account);
                    return oldToken;
                }
            }
        } catch (Exception e) {
            log.error("刷新access_token异常 for account: {}", account, e);
            String oldToken = tokenCache.get(account);
            if (oldToken != null) {
                return oldToken;
            }
        }
        return null;
    }

    /**
     * 获取token剩余有效时间（秒）
     */
    public long getRemainingTime(String account) {
        Long expireTime = tokenExpireTimeCache.get(account);
        if (expireTime == null) {
            return -1;
        }
        return Math.max(0, (expireTime - System.currentTimeMillis()) / 1000);
    }

    /**
     * 获取token过期时间字符串
     */
    public String getExpireTimeStr(String account) {
        Long expireTime = tokenExpireTimeCache.get(account);
        if (expireTime == null) {
            return "未获取";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(expireTime));
    }

    /**
     * 批量获取多个公众号的token
     */
    public Map<String, String> getAccessTokens(List<String> accounts) {
        Map<String, String> result = new HashMap<>();
        for (String appid : accounts) {
            result.put(appid, getAccessToken(appid));
        }
        return result;
    }

    /**
     * 获取所有启用的公众号token
     */
    public Map<String, String> getAllEnabledTokens() {
        Map<String, String> result = new HashMap<>();
        for (String appid : accountCache.keySet()) {
            result.put(appid, getAccessToken(appid));
        }
        return result;
    }

    /**
     * 清除指定account的token缓存
     */
    public String clearToken(String account) {
        log.info("手动刷新access_token for account: {}", account);
        tokenCache.remove(account);
        tokenExpireTimeCache.remove(account);
        return refreshTokenSync(account);
    }

    /**
     * 清除所有token缓存
     */
    public void clearAllTokens() {
        tokenCache.clear();
        tokenExpireTimeCache.clear();
        log.info("已清除所有access_token缓存");
    }

    /**
     * 检查token是否有效
     */
    public boolean isTokenValid(String appid) {
        String token = tokenCache.get(appid);
        return token != null && !isTokenExpired(appid);
    }

    /**
     * 获取缓存统计信息
     */
    public Map<String, Object> getStats() {
        long totalRemainingTime = 0;
        for (String appid : tokenCache.keySet()) {
            totalRemainingTime += getRemainingTime(appid);
        }

        long averageRemainingTime = tokenCache.isEmpty() ? 0 : totalRemainingTime / tokenCache.size();

        Map<String, Object> stats = new HashMap<>();
        stats.put("cachedCount", tokenCache.size());
        stats.put("configuredCount", accountCache.size());
        stats.put("tokenCheckInterval", tokenCheckInterval);
        stats.put("configCheckInterval", configCheckInterval);
        stats.put("scheduleEnabled", scheduleEnabled);
        stats.put("totalRemainingTime", totalRemainingTime);
        stats.put("averageRemainingTime", averageRemainingTime);
        stats.put("cachedAppids", new ArrayList<>(tokenCache.keySet()));
        stats.put("configuredAppids", new ArrayList<>(accountCache.keySet()));
        stats.put("defaultAdvanceRefresh", defaultAdvanceRefresh);
        stats.put("defaultExpiresIn", defaultExpiresIn);
        stats.put("defaultRefreshInterval", defaultRefreshInterval);
        // 添加当前选中的公众号信息
        WxAccount currentAccount = getCurrentWxAccount();
        stats.put("currentAppid", currentAccount != null ? currentAccount.getAppid() : null);
        stats.put("currentAccountName", currentAccount != null ? currentAccount.getName() : null);
        stats.put("currentShift", currentAccount != null ? currentAccount.getShift() : null);

        return stats;
    }

    // ============= 当前公众号配置管理方法 =============

    /**
     * 切换当前使用的公众号配置
     * 会将选中的公众号的 shift 设为 1，其他公众号的 shift 设为 0
     *
     * @param account 公众号ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void switchTo(String account) {
        if (account == null || account.trim().isEmpty()) {
            log.error("account不能为空");
            return;
        }

        // 先从缓存获取
        WxAccount config = accountCache.get(account);
        if (config == null) { // 如果缓存不存在，从数据库查询
            config = wxAccountService.getByObjId(account);
            if (config != null) { // 存入缓存
                accountCache.put(account, config);
            }
        }

        if (config == null) {
            log.error("未找到公众号配置: {}", account);
            return;
        }
        // 更新数据库中的 shift 标志
        try {
            // 1. 先将所有公众号的 shift 设为 0
            wxAccountService.updateBySQL("update `wx_account` set `shift`=0 ", null);
            // 2. 将选中的公众号的 shift 设为 1
            config.setShift(true);
            wxAccountService.update(config);
            // 3. 更新缓存中的 shift 状态
            for (WxAccount acc : accountCache.values()) {
                acc.setShift(false);
            }
            config.setShift(true);
            accountCache.put(account, config);
            // 4. 更新当前选中的公众号
            currentWxAccount.set(config);
            log.info("切换公众号成功: {} - {}, shift已更新为1", config.getAccount(), config.getName());
        } catch (Exception e) {
            log.error("切换公众号时更新shift标志失败", e);
            throw new RuntimeException("切换公众号失败", e);
        }
    }

    /**
     * 获取当前选中的公众号配置
     *
     * @return 当前选中的公众号配置，如果没有选中则返回null
     */
    public WxAccount getCurrentWxAccount() {
        return currentWxAccount.get();
    }

    /**
     * 获取当前选中的公众号的token
     */
    public String getCurrentToken() {
        WxAccount account = currentWxAccount.get();
        if (account == null) {
            log.error("当前未选中任何公众号");
            return null;
        }
        return getAccessToken(account.getAccount());
    }

    /**
     * 获取当前选中的公众号appid
     *
     * @return 当前选中的公众号appid，如果没有选中则返回null
     */
    public String getCurrentAppid() {
        WxAccount current = currentWxAccount.get();
        return current != null ? current.getAppid() : null;
    }

    /**
     * 获取当前选中的公众号account
     *
     * @return 当前选中的公众号account，如果没有选中则返回null
     */
    public String getCurrentAccount() {
        WxAccount current = currentWxAccount.get();
        try {
            return current.getAccount();
        } catch (Exception e) {
            reloadCurrentAccount();
            current = currentWxAccount.get();
            return current != null ? current.getAccount() : null;
        }
    }

    public Map<String, Object> getAccessDatacubeUser() {
        if (datacubeUser.isEmpty()) {
            JSONObject param = new JSONObject();
            param.put("begin_date", DateUtils.getLastWeek());
            param.put("end_date", DateUtils.nowDateStr());
            JSONObject call = WechatUtil.apiPostCall(WechatAPI.DATACUBE_GET_USER_CUMULATE.format(getCurrentToken()), param);
            if (call.containsKey("list")) {
                param.put("cumulate", call.get("list"));
            }
            JSONObject res = WechatUtil.apiPostCall(WechatAPI.DATACUBE_GET_USER_SUMMARY.format(getCurrentToken()), param);
            if (res.containsKey("list")) {
                param.put("summary", res.get("list"));
            }
            datacubeUser.clear();
            datacubeUser.putAll(param);
        }

        return datacubeUser;
    }

    public Map<String, Object> getAccessDatacubeArticle() {
        if (datacubeArticle.isEmpty()) {
            JSONObject param = new JSONObject();
            param.put("begin_date", DateUtils.getLastWeek());
            param.put("end_date", DateUtils.nowDateStr());
            JSONObject call = WechatUtil.apiPostCall(WechatAPI.DATACUBE_GET_ARTICLE_READ.format(getCurrentToken()), param);
            if (call.containsKey("list")) {
                param.put("cumulate", call.get("list"));
            }
            JSONObject res = WechatUtil.apiPostCall(WechatAPI.DATACUBE_GET_ARTICLE_SHARE.format(getCurrentToken()), param);
            if (res.containsKey("list")) {
                param.put("summary", res.get("list"));
            }
            datacubeArticle.clear();
            datacubeArticle.putAll(param);
        }

        return datacubeArticle;
    }

    public Map<String, Object> getIndustry() {
        if (industries.isEmpty()) {
            JSONObject call = WechatUtil.apiGetCall(WechatAPI.TEMPLATE_GET_INDUSTRY.format(getCurrentToken()));
            if (!WechatUtil.isWxError(call)) {
                industries.clear();
                industries.putAll(call);
            }
        }
        return industries;
    }

    /**
     * 重新加载当前选中的公众号配置（从数据库刷新）
     */
    public void reloadCurrentAccount() {
        WxAccount current = getCurrentWxAccount();
        if (current == null) {
            log.warn("当前未选中任何公众号，无需重新加载");
            return;
        }
        String account = current.getAccount();
        WxAccount freshConfig = wxAccountService.getByObjId(current.getAccount());
        if (freshConfig != null) {
            // 更新缓存
            accountCache.put(account, freshConfig);
            // 更新ThreadLocal
            currentWxAccount.set(freshConfig);
            log.info("重新加载当前公众号配置成功: {}", account);
        } else {
            currentWxAccount.getAndSet(null);
            log.error("重新加载当前公众号配置失败，公众号可能已被删除: {}", account);
        }
    }
}