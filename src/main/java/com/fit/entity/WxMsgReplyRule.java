package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class WxMsgReplyRule extends BaseEntity<WxMsgReplyRule> {
    /**
     * appid
     */
    private String appid;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 匹配的关键词、事件等
     */
    private String matchValue;

    /**
     * 是否精确匹配
     */
    private Boolean exactMatch;

    /**
     * 回复消息类型
     */
    private String replyType;

    /**
     * 回复消息内容
     */
    private String replyContent;

    /**
     * 规则是否有效
     */
    private Boolean enabled;

    /**
     * 备注说明
     */
    private String notes;

    /**
     * 生效起始时间
     */
    private Date effectTimeStart;

    /**
     * 生效结束时间
     */
    private Date effectTimeEnd;

    /**
     * 规则优先级
     */
    private Integer priority;

    /**
     * 修改时间
     */
    private Date utime;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", appid=").append(appid);
        sb.append(", ruleName=").append(ruleName);
        sb.append(", matchValue=").append(matchValue);
        sb.append(", exactMatch=").append(exactMatch);
        sb.append(", replyType=").append(replyType);
        sb.append(", replyContent=").append(replyContent);
        sb.append(", enabled=").append(enabled);
        sb.append(", notes=").append(notes);
        sb.append(", effectTimeStart=").append(effectTimeStart);
        sb.append(", effectTimeEnd=").append(effectTimeEnd);
        sb.append(", priority=").append(priority);
        sb.append(", utime=").append(utime);
        sb.append("]");
        return sb.toString();
    }
}