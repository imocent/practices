package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class LmsQuestion extends BaseEntity<LmsQuestion> {
    /**
     * 题目ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date ctime;

    /**
     * 创建人
     */
    private Long cuser;

    /**
     * 所属答题室ID
     */
    private Long examRoomId;

    /**
     * 所属答题室名
     */
    private String examRoomName;

    /**
     * 题类型：0-单选,1-多选,2-判断
     */
    private Integer mold;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 难度等级 1-5
     */
    private Integer difficulty;

    /**
     * 题目是否公开: 0-私有,1-公开
     */
    private Boolean share;

    /**
     * 禁用状态: 0-禁用,1-正常
     */
    private Boolean enabled;

    /**
     * 修改时间
     */
    private Date etime;

    /**
     * 修改人
     */
    private Long euser;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", ctime=").append(ctime);
        sb.append(", cuser=").append(cuser);
        sb.append(", examRoomId=").append(examRoomId);
        sb.append(", examRoomName=").append(examRoomName);
        sb.append(", mold=").append(mold);
        sb.append(", content=").append(content);
        sb.append(", difficulty=").append(difficulty);
        sb.append(", share=").append(share);
        sb.append(", enabled=").append(enabled);
        sb.append(", etime=").append(etime);
        sb.append(", euser=").append(euser);
        sb.append("]");
        return sb.toString();
    }
}