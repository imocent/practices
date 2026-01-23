package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class LmsQuestionWrong extends BaseEntity<LmsQuestionWrong> {
    /**
     * 记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 答题室ID
     */
    private Long roomId;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 错误次数
     */
    private Integer wrongCount;

    /**
     * 最后错误时间
     */
    private Date lastWrongTime;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", roomId=").append(roomId);
        sb.append(", questionId=").append(questionId);
        sb.append(", wrongCount=").append(wrongCount);
        sb.append(", lastWrongTime=").append(lastWrongTime);
        sb.append("]");
        return sb.toString();
    }
}