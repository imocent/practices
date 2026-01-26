package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class LmsQuestionUserAnswer extends BaseEntity<LmsQuestionUserAnswer> {
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
     * 用户答题记录ID
     */
    private Long quid;

    /**
     * 题目ID
     */
    private Long qid;

    /**
     * 用户答题内容
     */
    private String qoValue;

    /**
     * 是否正确: 0-错误,1-正确
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
        sb.append(", quid=").append(quid);
        sb.append(", qid=").append(qid);
        sb.append(", qoValue=").append(qoValue);
        sb.append(", enabled=").append(enabled);
        sb.append(", etime=").append(etime);
        sb.append(", euser=").append(euser);
        sb.append("]");
        return sb.toString();
    }
}