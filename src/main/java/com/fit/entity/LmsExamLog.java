package com.fit.entity;

import com.fit.base.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;
import lombok.*;

@Data
public class LmsExamLog extends BaseEntity<LmsExamLog> {
    /**
     * 记录ID
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
     * 答题室ID
     */
    private Long examRoomId;

    /**
     * 得分
     */
    private BigDecimal score;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 总题数
     */
    private Integer totalQuestions;

    /**
     * 正确题数
     */
    private Integer correctQuestions;

    /**
     * 错误题数
     */
    private Integer wrongQuestions;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", ctime=").append(ctime);
        sb.append(", userId=").append(userId);
        sb.append(", examRoomId=").append(examRoomId);
        sb.append(", score=").append(score);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", totalQuestions=").append(totalQuestions);
        sb.append(", correctQuestions=").append(correctQuestions);
        sb.append(", wrongQuestions=").append(wrongQuestions);
        sb.append("]");
        return sb.toString();
    }
}