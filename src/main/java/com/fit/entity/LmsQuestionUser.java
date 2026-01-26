package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class LmsQuestionUser extends BaseEntity<LmsQuestionUser> {
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
     * 答题室ID
     */
    private Long examRoomId;

    /**
     * 答题室名
     */
    private String examRoomName;

    /**
     * 学科名
     */
    private String subjectName;

    /**
     * 题总分数
     */
    private String totalScore;

    /**
     * 答题分数
     */
    private String answerScore;

    /**
     * 答题用时
     */
    private String duration;

    /**
     * 是否启用: 0-临时,1-答完
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
        sb.append(", subjectName=").append(subjectName);
        sb.append(", totalScore=").append(totalScore);
        sb.append(", answerScore=").append(answerScore);
        sb.append(", duration=").append(duration);
        sb.append(", enabled=").append(enabled);
        sb.append(", etime=").append(etime);
        sb.append(", euser=").append(euser);
        sb.append("]");
        return sb.toString();
    }
}