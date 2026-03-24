package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class WxAccount extends BaseEntity<WxAccount> {
    /**
     * 公众号原始ID
     */
    private String account;

    /**
     * 验证时用的url
     */
    private String url;

    /**
     * 公众号名称
     */
    private String name;

    private String appid;

    private String appsecret;

    private String token;

    /**
     * 自动回复消息条数;默认是5条
     */
    private Integer msgCount;

    /**
     * 刷新检查间隔(分)
     */
    private Integer refreshInterval;

    /**
     * 提前刷新时间(秒)
     */
    private Integer advanceRefresh;

    /**
     * token有效期(秒)
     */
    private Integer expiresIn;

    /**
     * 创建时间
     */
    private Date tokenTime;

    /**
     * 备注
     */
    private String notes;

    /**
     * 是否使用: 0-否,1-是
     */
    private Boolean shift;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", account=").append(account);
        sb.append(", url=").append(url);
        sb.append(", name=").append(name);
        sb.append(", appid=").append(appid);
        sb.append(", appsecret=").append(appsecret);
        sb.append(", token=").append(token);
        sb.append(", msgCount=").append(msgCount);
        sb.append(", refreshInterval=").append(refreshInterval);
        sb.append(", advanceRefresh=").append(advanceRefresh);
        sb.append(", expiresIn=").append(expiresIn);
        sb.append(", tokenTime=").append(tokenTime);
        sb.append(", notes=").append(notes);
        sb.append(", shift=").append(shift);
        sb.append("]");
        return sb.toString();
    }
}