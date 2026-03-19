package com.fit.service;

import com.alibaba.fastjson.JSONObject;
import com.fit.util.WechatUtil;
import org.springframework.stereotype.Service;

/**
 * @AUTO
 * @Author AIM
 * @DATE 2026/3/19
 */
@Service
public class WxApiDraftService {

    public static final String DRAFT_SWITCH = "%s/cgi-bin/draft/switch?access_token=%s&checkonly=1";
    public static final String DRAFT_ADD = "%s/cgi-bin/draft/add?access_token=%s";
    public static final String DRAFT_DELETE = "%s/cgi-bin/draft/delete?access_token=%s";
    public static final String DRAFT_GET = "%s/cgi-bin/draft/get?access_token=%s";
    public static final String DRAFT_UPDATE = "%s/cgi-bin/draft/update?access_token=%s";

    public Object getSwitchDraft(String token) {
        return WechatUtil.apiPostCall(String.format(DRAFT_SWITCH, WechatUtil.API_HOST_URL, token));
    }

    public Object getAddDraft(String token, JSONObject param) {
        return WechatUtil.apiCall(String.format(DRAFT_ADD, WechatUtil.API_HOST_URL, token), "POST", param);
    }

    public Object getDeleteDraft(String token, String media_id) {
        JSONObject param = new JSONObject();
        param.put("media_id", media_id);
        return WechatUtil.apiCall(String.format(DRAFT_DELETE, WechatUtil.API_HOST_URL, token), "POST", param);
    }

    public Object getGetDraft(String token, String media_id) {
        JSONObject param = new JSONObject();
        param.put("media_id", media_id);
        return WechatUtil.apiCall(String.format(DRAFT_GET, WechatUtil.API_HOST_URL, token), "POST", param);
    }

    public Object getUpdateDraft(String token, JSONObject param, String media_id, String index) {
        param.put("media_id", media_id);
        param.put("index", index);
        return WechatUtil.apiCall(String.format(DRAFT_UPDATE, WechatUtil.API_HOST_URL, token), "POST", param);
    }
}
