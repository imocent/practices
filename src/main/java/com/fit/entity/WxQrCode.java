package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class WxQrCode extends BaseEntity<WxQrCode> {
    /**
     * appid
     */
    private String appid;

    /**
     * 是否为临时二维码
     */
    private Boolean isTemp;

    /**
     * 场景值ID
     */
    private String sceneStr;

    /**
     * 二维码ticket
     */
    private String ticket;

    /**
     * 二维码图片解析后的地址
     */
    private String url;

    /**
     * 该二维码失效时间
     */
    private Date expireTime;

    /**
     * 该二维码创建时间
     */
    private Date createTime;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", appid=").append(appid);
        sb.append(", isTemp=").append(isTemp);
        sb.append(", sceneStr=").append(sceneStr);
        sb.append(", ticket=").append(ticket);
        sb.append(", url=").append(url);
        sb.append(", expireTime=").append(expireTime);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}