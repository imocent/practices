package com.fit.service;

import com.alibaba.fastjson.JSONObject;
import com.fit.entity.WxAccount;
import com.fit.util.CookieUtil;
import com.fit.util.MD5Util;
import com.fit.util.WechatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@Service
public class WxApiService {

    public final int maxAge = 365 * 24 * 60 * 60;
    private final String API_DEFAULT_HOST_URL = "https://api.weixin.qq.com";
    private final String OAUTH2_ACCESS_TOKEN_URL = "%s/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    private final String OAUTH2_AUTHORIZE_URL = "%s/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s&connect_redirect=1#wechat_redirect";
    private final String OAUTH2_USERINFO_URL = "%s/sns/userinfo?access_token=%s&openid=%s&lang=%s";
    private final String OAUTH2_VALIDATE_TOKEN_URL = "%s/sns/auth?access_token=%s&openid=%s";

    @Autowired
    private WxApiTokenService tokenService;

    /**
     * 获取access token
     *
     * @param code 授权码
     * @return JSONObject
     */
    public JSONObject getAccessToken(String code) {
        WxAccount wxAccount = tokenService.getCurrentWxAccount();
        String uri = String.format(OAUTH2_ACCESS_TOKEN_URL, API_DEFAULT_HOST_URL, wxAccount.getAppid(), wxAccount.getAppsecret(), code);
        log.info("获取access token, appid: {}", wxAccount.getAppid());
        return WechatUtil.apiCall(uri, "GET", null);
    }

    /**
     * 获取OAuth用户信息
     *
     * @param access_token 用户TOKEN
     * @param lang         语言
     * @return 用户信息
     */
    public JSONObject getOAuthUserInfo(String access_token, String lang) {
        if (lang == null) {
            lang = "zh_CN";
        }
        String uri = String.format(OAUTH2_USERINFO_URL, API_DEFAULT_HOST_URL, access_token, tokenService.getCurrentAppid(), lang);
        return WechatUtil.apiCall(uri);
    }

    /**
     * 获取jsapi ticket
     *
     * @return ticket
     */
    public String getJsapiTicket() {
        WxAccount wxAccount = tokenService.getCurrentWxAccount();
        // 实现获取jsapi ticket的逻辑
        // 可以使用wxAccount中的配置
        return null;
    }

    /**
     * 设置用户cookie
     *
     * @param response HttpServletResponse
     * @param openid   用户openid
     */
    public void setOpenInfoCookie(HttpServletResponse response, String openid) {
        CookieUtil.setCookie(response, "openid", openid, maxAge);
        String openidToken = MD5Util.getMd5AndSalt(openid);
        CookieUtil.setCookie(response, "openidToken", openidToken, maxAge);
    }

    /**
     * 生成OAuth2授权URL
     *
     * @param redirectUri 回调地址
     * @param scope       授权作用域
     * @param state       状态参数
     * @return 授权URL
     */
    public String getOAuth2AuthorizeUrl(String redirectUri, String scope, String state) {
        return String.format(OAUTH2_AUTHORIZE_URL, tokenService.getCurrentAppid(), redirectUri, scope, state);
    }

    /**
     * 验证access token是否有效
     *
     * @param accessToken access token
     * @param openid      用户openid
     * @return 验证结果
     */
    public JSONObject validateAccessToken(String accessToken, String openid) {
        String uri = String.format(OAUTH2_VALIDATE_TOKEN_URL, API_DEFAULT_HOST_URL, accessToken, openid);
        return WechatUtil.apiCall(uri, "GET", null);
    }
}