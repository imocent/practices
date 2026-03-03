package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class WxMsg extends BaseEntity<WxMsg> {
    /**
     * 修改时间
     */
    private Date ctime;

    /**
     * 公众ID
     */
    private String appid;

    /**
     * 微信用户ID
     */
    private String openid;

    /**
     * 消息方向
     */
    private Byte inOut;

    /**
     * 消息类型
     */
    private String msgType;

    /**
     * 消息详情
     */
    private String detail;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", ctime=").append(ctime);
        sb.append(", appid=").append(appid);
        sb.append(", openid=").append(openid);
        sb.append(", inOut=").append(inOut);
        sb.append(", msgType=").append(msgType);
        sb.append(", detail=").append(detail);
        sb.append("]");
        return sb.toString();
    }
}