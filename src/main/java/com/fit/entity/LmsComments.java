package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class LmsComments extends BaseEntity<LmsComments> {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date ctime;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 内容
     */
    private String content;

    /**
     * 留言方式: web-网页, apk-安卓, ios-苹果
     */
    private String mode;

    /**
     * 点赞计数
     */
    private Integer likeCount;

    /**
     * 投票计数
     */
    private Integer voteCount;

    /**
     * 审核状态: 0-禁用,1-显示
     */
    private Boolean enabled;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", ctime=").append(ctime);
        sb.append(", userId=").append(userId);
        sb.append(", username=").append(username);
        sb.append(", content=").append(content);
        sb.append(", mode=").append(mode);
        sb.append(", likeCount=").append(likeCount);
        sb.append(", voteCount=").append(voteCount);
        sb.append(", enabled=").append(enabled);
        sb.append("]");
        return sb.toString();
    }
}