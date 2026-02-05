package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class LmsCommentsLike extends BaseEntity<LmsCommentsLike> {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 编辑时间
     */
    private Date etime;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 留言ID
     */
    private Long commentId;

    /**
     * 点赞IP
     */
    private String ip;

    /**
     * 数字指纹
     */
    private String signed;

    /**
     * 状态: 0-删除,1-显示
     */
    private Boolean enabled;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", etime=").append(etime);
        sb.append(", userId=").append(userId);
        sb.append(", commentId=").append(commentId);
        sb.append(", ip=").append(ip);
        sb.append(", signed=").append(signed);
        sb.append(", enabled=").append(enabled);
        sb.append("]");
        return sb.toString();
    }
}