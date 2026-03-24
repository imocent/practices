package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class WxAccountFans extends BaseEntity<WxAccountFans> {
    /**
     * 每个用户都是唯一的
     */
    private String openId;

    /**
     * 订阅状态
     */
    private Integer subscribe;

    /**
     * 订阅时间
     */
    private String subscribeTime;

    /**
     * 返回用户关注的渠道来源，ADD_SCENE_SEARCH 公众号搜索，ADD_SCENE_ACCOUNT_MIGRATION 公众号迁移，ADD_SCENE_PROFILE_CARD 名片分享，ADD_SCENE_QR_CODE 扫描二维码，ADD_SCENE_PROFILE_LINK 图文页内名称点击，ADD_SCENE_PROFILE_ITEM 图文页右上角菜单，ADD_SCENE_PAID 支付后关注，ADD_SCENE_WECHAT_ADVERTISEMENT 微信广告，ADD_SCENE_REPRINT 他人转载，ADD_SCENE_LIVESTREAM 视频号直播，ADD_SCENE_CHANNELS 视频号，ADD_SCENE_WXA 小程序关注，ADD_SCENE_OTHERS 其他
     */
    private String subscribeScene;

    /**
     * 性别 0-未知；1-男；2-女
     */
    private Integer sex;

    /**
     * 语言
     */
    private String language;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 头像
     */
    private String headImgUrl;

    /**
     * 用户状态 1-可用；0-不可用
     */
    private Byte status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 微信号
     */
    private String wxId;

    /**
     * 公众号
     */
    private String account;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 二维码扫码场景
     */
    private Integer qrScene;

    /**
     * 二维码扫码场景描述
     */
    private String qrSceneStr;

    /**
     * 昵称,二进制保存emoji表情
     */
    private byte[] nickname;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", openId=").append(openId);
        sb.append(", subscribe=").append(subscribe);
        sb.append(", subscribeTime=").append(subscribeTime);
        sb.append(", subscribeScene=").append(subscribeScene);
        sb.append(", sex=").append(sex);
        sb.append(", language=").append(language);
        sb.append(", country=").append(country);
        sb.append(", province=").append(province);
        sb.append(", city=").append(city);
        sb.append(", headImgUrl=").append(headImgUrl);
        sb.append(", status=").append(status);
        sb.append(", remark=").append(remark);
        sb.append(", wxId=").append(wxId);
        sb.append(", account=").append(account);
        sb.append(", createTime=").append(createTime);
        sb.append(", qrScene=").append(qrScene);
        sb.append(", qrSceneStr=").append(qrSceneStr);
        sb.append(", nickname=").append(nickname);
        sb.append("]");
        return sb.toString();
    }
}