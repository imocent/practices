package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class WxAccount extends BaseEntity<WxAccount> {
    /**
     * 公众ID
     */
    private String appid;

    /**
     * 公众密钥
     */
    private String appsecret;

    /**
     * aesKey
     */
    private String aesKey;

    /**
     * token
     */
    private String token;

    /**
     * 公众号名称
     */
    private String name;

    /**
     * 认证状态
     */
    private Byte verified;

    /**
     * 公众号类型: 1-订阅号,2-服务号
     */
    private Integer mold;

    /**
     * 刷新检查间隔（分钟）
     */
    private Integer refreshInterval;

    /**
     * 提前刷新时间（秒）
     */
    private Integer advanceRefresh;

    /**
     * token有效期（秒）
     */
    private Integer expiresIn;

    /**
     * 最后获取token时间
     */
    private Date lastTokenTime;

    /**
     * 描述
     */
    private String notes;

    /**
     * 禁用状态: 0-禁用,1-正常
     */
    private Boolean enabled;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", appid=").append(appid);
        sb.append(", appsecret=").append(appsecret);
        sb.append(", aesKey=").append(aesKey);
        sb.append(", token=").append(token);
        sb.append(", name=").append(name);
        sb.append(", verified=").append(verified);
        sb.append(", mold=").append(mold);
        sb.append(", refreshInterval=").append(refreshInterval);
        sb.append(", advanceRefresh=").append(advanceRefresh);
        sb.append(", expiresIn=").append(expiresIn);
        sb.append(", lastTokenTime=").append(lastTokenTime);
        sb.append(", notes=").append(notes);
        sb.append(", enabled=").append(enabled);
        sb.append("]");
        return sb.toString();
    }
}