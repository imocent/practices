package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class WxMsgTemplate extends BaseEntity<WxMsgTemplate> {
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 公众号
     */
    private String account;

    /**
     * 消息类型
     */
    private String msgType;

    /**
     * 关注者发送的消息
     */
    private String inputCode;

    /**
     * 规则, 目前是'相等'
     */
    private String rule;

    /**
     * 消息阅读数
     */
    private Integer readCount;

    /**
     * 消息点赞数
     */
    private Integer favourCount;

    /**
     * 禁用状态: 0-禁用,1-正常
     */
    private Boolean enabled;

    /**
     * 微信模板
     */
    private String wxTpl;

    /**
     * 模板ID
     */
    private String tplId;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", createTime=").append(createTime);
        sb.append(", account=").append(account);
        sb.append(", msgType=").append(msgType);
        sb.append(", inputCode=").append(inputCode);
        sb.append(", rule=").append(rule);
        sb.append(", readCount=").append(readCount);
        sb.append(", favourCount=").append(favourCount);
        sb.append(", enabled=").append(enabled);
        sb.append(", wxTpl=").append(wxTpl);
        sb.append(", tplId=").append(tplId);
        sb.append(", title=").append(title);
        sb.append(", content=").append(content);
        sb.append("]");
        return sb.toString();
    }
}