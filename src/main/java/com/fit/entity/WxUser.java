package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class WxUser extends BaseEntity<WxUser> {
    /**
     * 微信openid
     */
    private String openid;

    /**
     * appid
     */
    private String appid;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 性别(0-未知、1-男、2-女)
     */
    private Byte sex;

    /**
     * 城市
     */
    private String city;

    /**
     * 省份
     */
    private String province;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 订阅时间
     */
    private Date subscribeTime;

    /**
     * 是否关注: 0-未关注,1-已关注
     */
    private Byte subscribe;

    /**
     * unionid
     */
    private String unionid;

    /**
     * 备注
     */
    private String notes;

    /**
     * 标签ID列表
     */
    private String tagidList;

    /**
     * 关注场景
     */
    private String subscribeScene;

    /**
     * 扫码场景值
     */
    private String qrSceneStr;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", openid=").append(openid);
        sb.append(", appid=").append(appid);
        sb.append(", phone=").append(phone);
        sb.append(", nickname=").append(nickname);
        sb.append(", sex=").append(sex);
        sb.append(", city=").append(city);
        sb.append(", province=").append(province);
        sb.append(", avatarUrl=").append(avatarUrl);
        sb.append(", subscribeTime=").append(subscribeTime);
        sb.append(", subscribe=").append(subscribe);
        sb.append(", unionid=").append(unionid);
        sb.append(", notes=").append(notes);
        sb.append(", tagidList=").append(tagidList);
        sb.append(", subscribeScene=").append(subscribeScene);
        sb.append(", qrSceneStr=").append(qrSceneStr);
        sb.append("]");
        return sb.toString();
    }
}