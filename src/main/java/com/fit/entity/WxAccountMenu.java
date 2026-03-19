package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class WxAccountMenu extends BaseEntity<WxAccountMenu> {
    /**
     * 消息类型： click - 事件消息；view - 链接消息
     */
    private String mtype;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 关键词
     */
    private String inputCode;

    /**
     * 跳转URL
     */
    private String url;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 上级ID
     */
    private Long parentId;

    /**
     * 消息类型
     */
    private String msgType;

    /**
     * 消息ID
     */
    private String msgId;

    /**
     * 菜单组ID
     */
    private Long gid;

    /**
     * 所属账号ID
     */
    private String account;

    /**
     * 创建时间
     */
    private Date createTime;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", mtype=").append(mtype);
        sb.append(", eventType=").append(eventType);
        sb.append(", name=").append(name);
        sb.append(", inputCode=").append(inputCode);
        sb.append(", url=").append(url);
        sb.append(", sort=").append(sort);
        sb.append(", parentId=").append(parentId);
        sb.append(", msgType=").append(msgType);
        sb.append(", msgId=").append(msgId);
        sb.append(", gid=").append(gid);
        sb.append(", account=").append(account);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}