package com.fit.web.api;

import com.alibaba.fastjson.JSONObject;
import com.fit.base.AjaxResult;
import com.fit.entity.WxUser;
import com.fit.service.WxApiService;
import com.fit.service.WxApiTokenService;
import com.fit.service.WxUserService;
import com.fit.util.WechatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/wechat/user/tags")
public class WechatUserTagsController {

    @Autowired
    private WxApiTokenService tokenService;
    @Autowired
    private WxUserService wxUserService;

    @GetMapping("/userTags")
    public AjaxResult userTags(@CookieValue String appid, @CookieValue String openid) {
        if (openid == null) {
            return AjaxResult.error("none_openid");
        }
        this.tokenService.switchTo(appid);
        WxUser wxUser = wxUserService.getByObjId(openid);
        if (wxUser == null) {
            JSONObject userInfo = WechatUtil.getUserInfo(openid, null);
            wxUser = userInfo.toJavaObject(WxUser.class);
            if (wxUser == null) {
                return AjaxResult.error("not_subscribed");
            } else {
                wxUser.setAppid(appid);
                wxUserService.save(wxUser);
            }
        }
        return AjaxResult.ok().put(wxUser.getTagidList());
    }

    @PostMapping("/tagging")
    public AjaxResult tagging(@CookieValue String appid, @CookieValue String openid, Long tagid) {
        this.tokenService.switchTo(appid);
        try {
//            wxUserTagsService.tagging(tagid, openid);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
        return AjaxResult.ok();
    }

    @PostMapping("/untagging")
    public AjaxResult untagging(@CookieValue String appid, @CookieValue String openid, Long tagid) {
        this.tokenService.switchTo(appid);
//        wxUserTagsService.untagging(tagid, openid);
        return AjaxResult.ok();
    }
}
