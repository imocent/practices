package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class WxMsgTemplateLog extends BaseEntity<WxMsgTemplateLog> {
    /**
     * appid
     */
    private String appid;

    /**
     * 用户openid
     */
    private String touser;

    /**
     * 模板ID
     */
    private String templateId;

    /**
     * 消息数据
     */
    private String data;

    /**
     * 消息链接
     */
    private String url;

    /**
     * 小程序信息
     */
    private String miniprogram;

    /**
     * 发送结果
     */
    private String sendResult;

    /**
     * 修改时间
     */
    private Date sendTime;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", appid=").append(appid);
        sb.append(", touser=").append(touser);
        sb.append(", templateId=").append(templateId);
        sb.append(", data=").append(data);
        sb.append(", url=").append(url);
        sb.append(", miniprogram=").append(miniprogram);
        sb.append(", sendResult=").append(sendResult);
        sb.append(", sendTime=").append(sendTime);
        sb.append("]");
        return sb.toString();
    }
}