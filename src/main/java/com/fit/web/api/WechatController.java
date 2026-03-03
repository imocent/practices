package com.fit.web.api;

import com.alibaba.fastjson.JSONObject;
import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.WxUser;
import com.fit.service.WxApiService;
import com.fit.service.WxApiTokenService;
import com.fit.service.WxUserService;
import com.fit.util.SHA1Util;
import com.fit.util.WebUtil;
import com.fit.util.WechatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/wechat")
public class WechatController extends BaseController {

    @Autowired
    private WxApiService apiService;
    @Autowired
    private WxApiTokenService tokenService;
    @Autowired
    private WxUserService wxUserService;

    @GetMapping
    @ResponseBody
    public String checkWebChat(HttpServletRequest request) {
        Map<String, Object> param = WebUtil.getRequestMap(request);
        String signature = param.get("signature").toString();
        String echostr = param.get("echostr").toString();
        param.remove("signature");
        param.remove("echostr");
        param.put("token", tokenService.getCurrentToken());
        //在生产环境中使用加密的方式是最好的。
        if (WechatUtil.checkSignature(signature, param.values().toArray(new String[0]))) {
            return echostr; //测试通过
        }
        return "error"; //测试失败
    }

    /**
     * 使用微信授权code换取openid
     */
    @PostMapping("/codeToOpenid")
    @CrossOrigin
    public AjaxResult codeToOpenid(HttpServletRequest request, HttpServletResponse response, @CookieValue String appid) {
        try {
            this.tokenService.switchTo(appid);
            String code = request.getParameter("code");
            JSONObject token = this.apiService.getAccessToken(code);
            String openid = token.getString("openid");
            this.apiService.setOpenInfoCookie(response, openid);
            return AjaxResult.ok().put(openid);
        } catch (Exception e) {
            log.error("code换取openid失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 使用微信授权code换取用户信息(需scope为 snsapi_userinfo)
     */
    @PostMapping("/codeToUserInfo")
    @CrossOrigin
    public AjaxResult codeToUserInfo(HttpServletRequest request, HttpServletResponse response, @CookieValue String appid) {
        try {
            this.tokenService.switchTo(appid);
            String code = request.getParameter("code");
            String token = this.tokenService.getAccessToken(code);
            Map<String, Object> userInfo = apiService.getOAuthUserInfo(token, "zh_CN");
            String openid = userInfo.get("openid").toString();
            this.apiService.setOpenInfoCookie(response, openid);
            return AjaxResult.ok().put(token);
        } catch (Exception e) {
            log.error("code换取用户信息失败", e);
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 获取粉丝信息
     *
     * @param appid
     * @param openid
     * @return
     */
    @GetMapping("/getUserInfo")
    public AjaxResult getUserInfo(@CookieValue String appid, @CookieValue String openid) {
        this.tokenService.switchTo(appid);
        WxUser wxUser = wxUserService.getByObjId(openid);
        return AjaxResult.ok().put(wxUser);
    }

    /**
     * 获取粉丝信息
     *
     * @param appid
     * @param openid
     * @return
     */
    @GetMapping("/isValidateAccessToken")
    public AjaxResult isValidateAccessToken(@CookieValue String appid, @CookieValue String openid) {
        this.tokenService.switchTo(appid);
        WxUser wxUser = wxUserService.getByObjId(openid);
        return AjaxResult.ok().put(wxUser);
    }

    /**
     * 获取微信分享的签名配置
     * 允许跨域（只有微信公众号添加了js安全域名的网站才能加载微信分享，故这里不对域名进行校验）
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/getShareSignature")
    public AjaxResult getShareSignature(HttpServletRequest request, HttpServletResponse response, @CookieValue String appid) {
        this.tokenService.switchTo(appid);
        // 1.拼接url（当前网页的URL，不包含#及其后面部分）
        String wxShareUrl = request.getHeader("wx-client-href");
        if (!StringUtils.hasText(wxShareUrl)) {
            return AjaxResult.error("header中缺少'wx-client-href'参数，微信分享加载失败");
        }
        wxShareUrl = wxShareUrl.split("#")[0];
        Map<String, String> wxMap = new TreeMap<>();
        String wxNonCeStr = UUID.randomUUID().toString();
        String wxTimestamp = (System.currentTimeMillis() / 1000) + "";
        wxMap.put("noncestr", wxNonCeStr);
        wxMap.put("timestamp", wxTimestamp);
        wxMap.put("jsapi_ticket", apiService.getJsapiTicket());
        wxMap.put("url", wxShareUrl);
        // 加密获取signature
        StringBuilder wxBaseString = new StringBuilder();
        wxMap.forEach((key, value) -> wxBaseString.append(key).append("=").append(value).append("&"));
        String wxSignString = wxBaseString.substring(0, wxBaseString.length() - 1);
        // signature
        String wxSignature = SHA1Util.sha1(wxSignString);
        Map<String, String> resMap = new TreeMap<>();
        resMap.put("appId", appid);
        resMap.put("wxTimestamp", wxTimestamp);
        resMap.put("wxNoncestr", wxNonCeStr);
        resMap.put("wxSignature", wxSignature);
        return AjaxResult.ok().put(resMap);
    }
}