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
    private Integer subscribeStatus;

    /**
     * 订阅时间
     */
    private String subscribeTime;

    /**
     * 性别 0-女；1-男；2-未知
     */
    private Boolean gender;

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
    private String notes;

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
        sb.append(", subscribeStatus=").append(subscribeStatus);
        sb.append(", subscribeTime=").append(subscribeTime);
        sb.append(", gender=").append(gender);
        sb.append(", language=").append(language);
        sb.append(", country=").append(country);
        sb.append(", province=").append(province);
        sb.append(", city=").append(city);
        sb.append(", headImgUrl=").append(headImgUrl);
        sb.append(", status=").append(status);
        sb.append(", notes=").append(notes);
        sb.append(", wxId=").append(wxId);
        sb.append(", account=").append(account);
        sb.append(", createTime=").append(createTime);
        sb.append(", nickname=").append(nickname);
        sb.append("]");
        return sb.toString();
    }
}