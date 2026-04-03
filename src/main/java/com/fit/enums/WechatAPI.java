package com.fit.enums;

/**
 * @AUTO 控制器
 * @Author AIM
 * @DATE 2026/3/24
 */
public enum WechatAPI {

    ADD_NEWS_MATERIAL(1, "%s/cgi-bin/material/add_news?access_token=%s", "新增永久图文素材"),
    BATCH_GET(1, "%s/cgi-bin/user/info/batchget?access_token=%s", ""),
    CALL_BACK_IP(1, "%s/cgi-bin/getcallbackip?access_token=%s", "微信服务器ip"),
    CHANGE_OPENID(1, "%s/cgi-bin/changeopenid?access_token=%s", ""),
    DRAFT_ADD(1, "%s/cgi-bin/draft/add?access_token=%s", ""),
    DRAFT_DELETE(1, "%s/cgi-bin/draft/delete?access_token=%s", ""),
    DRAFT_GET(1, "%s/cgi-bin/draft/get?access_token=%s", ""),
    DRAFT_UPDATE(1, "%s/cgi-bin/draft/update?access_token=%s", ""),
    DRAFT_SWITCH(1, "%s/cgi-bin/draft/switch?access_token=%s&checkonly=1", ""),
    FANS_INFO(2, "%s/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN", "获取账号粉丝信息"),
    FANS_LIST(1, "%s/cgi-bin/user/get?access_token=%s", "获取账号粉丝列表"),
    GET_ARTICLE_READ(1, "%s/datacube/getuserread?access_token=%s", "获取图文统计数据-3d"),
    GET_ARTICLE_READ_HOUR(1, "%s/datacube/getuserreadhour?access_token=%s", "获取图文统计分时数据-1d"),
    GET_ARTICLE_SHARE(1, "%s/datacube/getusershare?access_token=%s", "获取图文分享转发数据-7d"),
    GET_ARTICLE_SHARE_HOUR(1, "%s/datacube/getusersharehour?access_token=%s", "获取图文分享转发分时数据-1d"),
    GET_ARTICLE_SUMMARY(1, "%s/datacube/getarticlesummary?access_token=%s", "获取图文群发每日数据-1d"),
    GET_ARTICLE_TOTAL(1, "%s/datacube/getarticletotal?access_token=%s", "获取图文群发总数据-1d"),
    GET_INTERFACE_SUMMARY(1, "%s/datacube/getinterfacesummary?access_token=%s", "获取接口分析数据-30d"),
    GET_INTERFACE_SUMMARY_HOUR(1, "%s/datacube/getinterfacesummaryhour?access_token=%s", "获取接口分析分时数据-1d"),
    GET_MATERIAL(1, "%s/cgi-bin/material/get_material?access_token=%s", "根据media_id获取永久素材"),
    GET_MEDIA(2, "%s/cgi-bin/media/get?access_token=%s&media_id=%s", "获取临时素材"),
    GET_OAUTH_CODE(5, "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=%s&scope=%s&state=%s#wechat_redirect", "网页授权OAuth2.0获取code"),
    GET_OAUTH_TOKEN(3, "%s/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code", "网页授权OAuth2.0获取token"),
    GET_OAUTH_USERINFO(2, "%s/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN", "网页授权OAuth2.0获取用户信息"),
    GET_DOMAIN_IP(1, "%s/cgi-bin/get_api_domain_ip?access_token=%s", "获取微信API服务器IP"),
    GET_TICKET(2, "%s/cgi-bin/ticket/getticket?access_token=%s&type=%s", ""),
    GET_UPSTREAM_MSG(1, "%s/datacube/getupstreammsg?access_token=%s", "获取消息发送概况数据-7d"),
    GET_UPSTREAM_MSG_DIST(1, "%s/datacube/getupstreammsgdist?access_token=%s", "获取消息发送分布数据-15d"),
    GET_UPSTREAM_MSG_DIST_MONTH(1, "%s/datacube/getupstreammsgdistmonth?access_token=%s", "获取消息发送分布月数据-30d"),
    GET_UPSTREAM_MSG_DIST_WEEK(1, "%s/datacube/getupstreammsgdistweek?access_token=%s", "获取消息发送分布周数据-30d"),
    GET_UPSTREAM_MSG_HOUR(1, "%s/datacube/getupstreammsghour?access_token=%s", "获取消息分送分时数据-1d"),
    GET_UPSTREAM_MSG_MONTH(1, "%s/datacube/getupstreammsgmonth?access_token=%s", "获取消息发送月数据-30d"),
    GET_UPSTREAM_MSG_WEEK(1, "%s/datacube/getupstreammsgweek?access_token=%s", "获取消息发送周数据-30d"),
    GET_USER_CUMULATE(1, "%s/datacube/getusercumulate?access_token=%s", "获取累计用户数据-7d"),
    GET_USER_LIST_BY_TAG(1, "%s/cgi-bin/user/tag/get?access_token=%s", "获取标签下粉丝列表"),
    GET_USER_SUMMARY(1, "%s/datacube/getusersummary?access_token=%s", "获取用户增减数据-7d"),
    MASS_DELETE(1, "%s/cgi-bin/message/mass/delete?access_token=%s", "删除群发"),
    MASS_PREVIEW(1, "%s/cgi-bin/message/mass/preview?access_token=%s", "群发预览"),
    MASS_SEND(1, "%s/cgi-bin/message/mass/send?access_token=%s", "群发接口"),
    MASS_SPEED_GET(1, "%s/cgi-bin/message/mass/speed/get?access_token=%s", "获取群发速度"),
    MASS_SPEED_SET(1, "%s/cgi-bin/message/mass/speed/set?access_token=%s", "设置群发速度"),
    MASS_STATUS(1, "%s/cgi-bin/message/mass/get?access_token=%s", "查询群发消息发送状态"),
    MASS_TAG(1, "%s/cgi-bin/message/mass/sendall?access_token=%s", "根据标签进行群发"),
    MATERIAL_ADD(2, "%s/cgi-bin/material/add_material?access_token=%s&type=%s", "新增其他类型永久素材"),
    MATERIAL_DEL(1, "%s/cgi-bin/material/del_material?access_token=%s", "删除永久素材"),
    MATERIAL_GET(1, "%s/cgi-bin/material/get_material?access_token=%s", "获取永久素材"),
    MATERIAL_LIST(1, "%s/cgi-bin/material/batchget_material?access_token=%s", "获取批量素材"),
    MATERIAL_UPLOAD_IMG(1, "%s/cgi-bin/media/uploadimg?access_token=%s", "上传永久图片素材"),
    MEDIA_UPLOAD(2, "%s/cgi-bin/media/upload?access_token=%s&type=%s", ""),
    MENU_ADD_CONDITIONAL(1, "%s/cgi-bin/menu/addconditional?access_token=%s", "创建个性化菜单"),
    MENU_CREATE(1, "%s/cgi-bin/menu/create?access_token=%s", ""),
    MENU_DELETE(1, "%s/cgi-bin/menu/delete?access_token=%s", ""),
    MSG_TEMPLATE_SEND(1, "%s/cgi-bin/message/template/send?access_token=%s", ""),
    PAY_UNIFIED_ORDER(0, "https://api.mch.weixin.qq.com/pay/unifiedorder", "统一下单订购接口"),
    QRCODE_CREATE(1, "%s/cgi-bin/qrcode/create?access_token=%s", ""),
    QRCODE_SHOW(1, "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=%s", ""),
    SEND_CUSTOM_MESSAGE(1, "%s/cgi-bin/message/custom/send?access_token=%s", "发送客服消息"),
    TAGS_CREATE(1, "%s/cgi-bin/tags/create?access_token=%s", "创建用户标签"),
    TAGS_DELETE(1, "%s/cgi-bin/tags/delete?access_token=%s", "删除用户标签"),
    TAGS_LIST(1, "%s/cgi-bin/tags/get?access_token=%s", "获取标签列表"),
    TAGS_MEMBERS_TAGGING(1, "%s/cgi-bin/tags/members/batchtagging?access_token=%s", ""),
    TAGS_MEMBERS_UNTAGGING(1, "%s/cgi-bin/tags/members/batchuntagging?access_token=%s", ""),
    TEMPLATE_API_ADD(1, "%s/cgi-bin/template/api_add_template?access_token=%s", "选用模板"),
    TEMPLATE_GET_INDUSTRY(1, "%s/cgi-bin/template/get_industry?access_token=%s", "获取行业信息"),
    TEMPLATE_LIST(1, "%s/cgi-bin/template/get_all_private_template?access_token=%s", "获取已选用模板列表"),
    TEMPLATE_SEND(1, "%s/cgi-bin/message/template/send?access_token=%s", "发送模板消息"),
    TOKEN(2, "%s/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s", ""),
    USER_INFO(1, "%s/cgi-bin/user/info?access_token=%s&openid=%s&lang=%s", "用户信息");

    private final Integer fmt;
    private final String url;
    private final String notes;

    private WechatAPI(Integer fmt, String url, String notes) {
        this.fmt = fmt;
        this.url = url;
        this.notes = notes;
    }

    public String format(Object... params) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        if (params.length == fmt) {
            Object[] allParams = new Object[fmt + 1];
            allParams[0] = "https://api.weixin.qq.com";
            System.arraycopy(params, 0, allParams, 1, params.length);
            return String.format(url, allParams);
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("WechatAPI{fmt='%s', url='%s', notes='%s'}", fmt, url, notes);
    }
}