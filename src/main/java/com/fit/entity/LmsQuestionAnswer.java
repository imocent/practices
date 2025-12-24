package com.fit.entity;

import com.fit.base.BaseEntity;
import lombok.*;

@Data
public class LmsQuestionAnswer extends BaseEntity<LmsQuestionAnswer> {
    /**
     * 答案ID
     */
    private Long id;

    /**
     * 题目ID
     */
    private Long questionId;

    /**
     * 答案内容
     */
    private String content;

    /**
     * 答案校验: 0-无,1-正确,2-错误
     */
    private Integer verify;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", questionId=").append(questionId);
        sb.append(", content=").append(content);
        sb.append(", verify=").append(verify);
        sb.append("]");
        return sb.toString();
    }
}