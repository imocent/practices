package com.fit.service;

import com.alibaba.fastjson.JSONObject;
import com.fit.enums.WechatAPI;
import com.fit.util.WechatUtil;
import org.springframework.stereotype.Service;

/**
 * @AUTO
 * @Author AIM
 * @DATE 2026/3/19
 */
@Service
public class WxApiDraftService {

    public Object getSwitchDraft(String token) {
        return WechatUtil.apiPostCall(WechatAPI.DRAFT_SWITCH.format(token));
    }

    public Object getAddDraft(String token, JSONObject param) {
        return WechatUtil.apiCall(WechatAPI.DRAFT_ADD.format(token), "POST", param);
    }

    public Object getDeleteDraft(String token, String media_id) {
        JSONObject param = new JSONObject();
        param.put("media_id", media_id);
        return WechatUtil.apiCall(WechatAPI.DRAFT_DELETE.format(token), "POST", param);
    }

    public Object getGetDraft(String token, String media_id) {
        JSONObject param = new JSONObject();
        param.put("media_id", media_id);
        return WechatUtil.apiCall(WechatAPI.DRAFT_GET.format(token), "POST", param);
    }

    public Object getUpdateDraft(String token, JSONObject param, String media_id, String index) {
        param.put("media_id", media_id);
        param.put("index", index);
        return WechatUtil.apiCall(WechatAPI.DRAFT_UPDATE.format(token), "POST", param);
    }
}