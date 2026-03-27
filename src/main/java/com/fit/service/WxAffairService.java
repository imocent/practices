package com.fit.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fit.entity.WxAssetMedia;
import com.fit.enums.WechatAPI;
import com.fit.util.WechatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @AUTO 微信业务服务
 * @Author AIM
 * @DATE 2026/3/19
 */
@Slf4j
@Service
public class WxAffairService {

    @Autowired
    private WxApiTokenService tokenService;
    @Autowired
    private WxAssetMediaService mediaService;

    /**
     * 异步同步所有素材
     */
    @Async("syncExecutor")
    public CompletableFuture<String> syncDelMaterials(List<Long> ids) {
        log.info("开始删除微信素材");
        long startTime = System.currentTimeMillis();
        try {
            String token = tokenService.getCurrentToken();
            for (Long id : ids) {
                WxAssetMedia media = this.mediaService.get(id);
                JSONObject param = new JSONObject();
                param.put("media_id", media.getMediaId());
                JSONObject call = WechatUtil.apiPostCall(WechatAPI.MATERIAL_DEL.format(token), param);
                if (WechatUtil.isWxError(call)) {
                    log.error("删除永久素材失败：{}", param.toJSONString());
                }
            }

            long cost = System.currentTimeMillis() - startTime;
            String result = String.format("同步完成，耗时: %d ms", cost);
            log.info(result);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("同步失败", e);
            return CompletableFuture.completedFuture("同步失败: " + e.getMessage());
        }
    }

    /**
     * 异步同步所有素材
     */
    @Async("syncExecutor")
    public CompletableFuture<String> syncAllMaterials() {
        log.info("开始同步微信素材");
        long startTime = System.currentTimeMillis();
        try {
            // 同步图文
            syncMaterialByType("news");
            // 同步图片
            syncMaterialByType("image");
            // 同步视频
            syncMaterialByType("video");
            // 同步语音
            syncMaterialByType("voice");

            long cost = System.currentTimeMillis() - startTime;
            String result = String.format("同步完成，耗时: %d ms", cost);
            log.info(result);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("同步失败", e);
            return CompletableFuture.completedFuture("同步失败: " + e.getMessage());
        }
    }

    /**
     * 同步指定类型的素材
     */
    private void syncMaterialByType(String type) {
        log.info("开始同步{}素材", type);
        int offset = 0;
        int count = 20;
        String account = tokenService.getCurrentAccount();
        String token = tokenService.getCurrentToken();
        while (true) {
            // 调用微信接口
            JSONObject param = new JSONObject();
            param.put("type", type);
            param.put("offset", offset);
            param.put("count", count);
            JSONObject call = WechatUtil.apiPostCall(WechatAPI.MATERIAL_LIST.format(token), param);
            if (call == null || !call.containsKey("item")) {
                break;
            }
            int totalCount = call.getIntValue("total_count");
            JSONArray items = call.getJSONArray("item");
            for (int i = 0; i < items.size() - 1; i++) {
                JSONObject item = items.getJSONObject(i);
                WxAssetMedia bean = mediaService.queryByKey("wx_asset_media", "media_id", item.getString("media_id"));
                if (bean == null) {
                    WxAssetMedia media = item.toJavaObject(WxAssetMedia.class);
                    media.setAccount(account);
                    media.setMediaType(type);
                    media.setTitle(item.getString("name"));
                    media.setUpdateTime(item.getDate("update_time"));
                    mediaService.save(media);
                } else {
                    bean.setUpdateTime(item.getDate("update_time"));
                    bean.setTitle(item.getString("name"));
                    mediaService.update(bean);
                }
            }
            offset += items.size();
            log.info("同步{}素材进度: {}/{}", type, offset, totalCount);
            if (offset >= totalCount) {
                break;
            }
        }
        log.info("完成同步{}素材", type);
    }
}