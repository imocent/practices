package com.fit.web.admin.wx;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fit.base.AjaxResult;
import com.fit.entity.WxAccount;
import com.fit.entity.WxAccountFans;
import com.fit.service.WxAccountFansService;
import com.fit.service.WxApiTokenService;
import com.fit.util.BeanUtil;
import com.fit.util.OftenUtil;
import com.fit.util.WebUtil;
import com.fit.util.WechatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @AUTO 控制器
 * @Author AIM
 * @DATE 2026/2/26
 */
@Slf4j
@Controller
@RequestMapping("/admin/wx/fans")
public class WechatAccountFansController {

    private static String PREFIX = "/admin/wx/accountFans/";

    @Autowired
    private WxApiTokenService tokenService;
    @Autowired
    private WxAccountFansService service;

    /**
     * 列表页面
     */
    @GetMapping("/list")
    public String index() {
        return PREFIX + "list";
    }

    /**
     * 查询列表
     */
    @PostMapping("/list")
    @ResponseBody
    public AjaxResult list(HttpServletRequest request) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        map.put("account", tokenService.getCurrentAccount());
        List<WxAccountFans> list = service.findList(map);
        int count = service.findCount(map);
        return AjaxResult.tables(count, list);
    }

    /**
     * 添加编辑页面
     */
    @GetMapping("/edit")
    public String editView(String id, Model model) {
        if (OftenUtil.isNotEmpty(id)) {
            WxAccountFans bean = service.getByObjId(id);
            model.addAttribute("bean", bean);
        }
        return PREFIX + "edit";
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ResponseBody
    public Object save(WxAccountFans bean) {
        WxAccountFans entity = this.service.get(bean.getId());
        if (null == entity) {
            this.service.save(bean);
        } else {
            BeanUtil.copyProperties(bean, entity);
            this.service.update(entity);
        }
        return AjaxResult.success();
    }

    /**
     * 删除
     *
     * @param ids 删除ID集合
     */
    @PostMapping("/del")
    @ResponseBody
    public Object del(@RequestParam("ids") List<Long> ids) {
        if (OftenUtil.isNotEmpty(ids)) {
            this.service.batchDelete(ids);
            return AjaxResult.success();
        } else {
            return AjaxResult.error("参数异常");
        }
    }

    /**
     * 同步公众号粉丝列表
     */
    @PostMapping("/syncAccountFansList")
    @ResponseBody
    public Object syncAccountFansList() {
        WxAccount wxAccount = tokenService.getCurrentWxAccount();
        String accessToken = WechatUtil.getAccessToken(wxAccount.getAppid(), wxAccount.getAppsecret());
        JSONObject fansJson = WechatUtil.getFansList(accessToken);
        if (fansJson.containsKey("data")) {
            List<WxAccountFans> fansList = new ArrayList<WxAccountFans>();
            if (fansJson.getJSONObject("data").containsKey("openid")) {
                JSONArray openidArr = fansJson.getJSONObject("data").getJSONArray("openid");
                for (Object openId : openidArr) {
                    JSONObject fansInfo = WechatUtil.getFansInfo(openId.toString(), accessToken);
                    WxAccountFans fans = fansInfo.toJavaObject(WxAccountFans.class);
                    if (fans.getHeadImgUrl().isEmpty()) {
                        fans.setHeadImgUrl("/images/avatar.png");
                    }
                    // 设置公众号
                    fans.setAccount(wxAccount.getAccount());
                    fansList.add(fans);
                }
                // 批处理
                service.batchAdd(fansList);
            }
            return AjaxResult.success();
        } else {
            log.error("粉丝列表错误信息: {}", fansJson);
        }
        return AjaxResult.error("同步失败");
    }

    /**
     * 根据用户的ID更新用户信息
     *
     * @param openId
     * @return
     */
    @RequestMapping(value = "/syncAccountFans")
    @ResponseBody
    public Object syncAccountFans(String openId) {
        WxAccount wxAccount = tokenService.getCurrentWxAccount();
        if (wxAccount != null) {
            String accessToken = WechatUtil.getAccessToken(wxAccount.getAppid(), wxAccount.getAppsecret());
            JSONObject fansInfo = WechatUtil.getFansInfo(openId.toString(), accessToken);
            WxAccountFans fans = fansInfo.toJavaObject(WxAccountFans.class);
            if (fans != null) {
                return AjaxResult.success(fans);
            } else {
                log.error("粉丝错误信息: {}", fansInfo);
            }
        }
        return AjaxResult.error("同步失败");
    }

    /**
     * 列表页面
     */
    @GetMapping("/mass")
    public String mass() {
        return PREFIX + "mass";
    }

    @RequestMapping(value = "/accountFans")
    @ResponseBody
    public AjaxResult accountFans(HttpServletRequest request) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        map.put("account", tokenService.getCurrentAccount());
        List<WxAccountFans> list = service.findList(map);
        int count = service.findCount(map);
        return AjaxResult.tables(count, list);
    }
}