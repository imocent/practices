package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class LmsExamRoom extends BaseEntity<LmsExamRoom> {
    /**
     * 答题室ID
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

    private String uuid;

    /**
     * 所属父科ID
     */
    private String subjectPid;

    /**
     * 所属学科ID
     */
    private String subjectId;

    /**
     * 所属学科名
     */
    private String subjectName;

    /**
     * 考场图标ID
     */
    private Long imgId;

    /**
     * 答题室名称
     */
    private String title;

    /**
     * 答题室说明
     */
    private String content;

    /**
     * 难度等级
     */
    private String level;

    /**
     * 答题室描述
     */
    private String notes;

    /**
     * 答题室状态: 0-停用,1-新建,2-发布,3-结束,4-归档
     */
    private Integer enabled;

    /**
     * 是否公开 0-公开 1-私有
     */
    private Boolean share;

    /**
     * 答卷模式: 0-标准答题模式,1-随机抽取模式,2-习题练习模式,3-只读学习模式
     */
    private Integer examMode;

    /**
     * 时间类型: 0-永久,1-限时
     */
    private Boolean timeMode;

    /**
     * 答题时长(分钟)
     */
    private Integer timeLen;

    /**
     * 开始时间
     */
    private Date timeOn;

    /**
     * 结束时间
     */
    private Date timeOff;

    /**
     * 答题人员模式: 0-任何人员,1-指定人员
     */
    private Boolean examineeMode;

    /**
     * 答题次数
     */
    private Integer examineeCount;

    /**
     * 答卷展示题目排序: 0-固定,1-随机
     */
    private Boolean subjectSortMode;

    /**
     * 答卷展示选项排序: 0-固定,1-随机
     */
    private Boolean subjectOptSortMode;

    /**
     * 判卷类型: 0-用户交卷后,1-全场收卷后
     */
    private Boolean adjudgeMode;

    /**
     * 阅卷类型: 0-自动&人工,1-人工
     */
    private Boolean markMode;

    /**
     * 成绩类型: 0-得分&答卷,1-得分,2-答卷
     */
    private Integer markShowMode;

    /**
     * 成绩时间类型: 0-不发布,1-全场阅卷后,2-答卷阅卷后
     */
    private Integer markTimeMode;

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
        sb.append(", uuid=").append(uuid);
        sb.append(", subjectPid=").append(subjectPid);
        sb.append(", subjectId=").append(subjectId);
        sb.append(", subjectName=").append(subjectName);
        sb.append(", imgId=").append(imgId);
        sb.append(", title=").append(title);
        sb.append(", content=").append(content);
        sb.append(", level=").append(level);
        sb.append(", notes=").append(notes);
        sb.append(", enabled=").append(enabled);
        sb.append(", share=").append(share);
        sb.append(", examMode=").append(examMode);
        sb.append(", timeMode=").append(timeMode);
        sb.append(", timeLen=").append(timeLen);
        sb.append(", timeOn=").append(timeOn);
        sb.append(", timeOff=").append(timeOff);
        sb.append(", examineeMode=").append(examineeMode);
        sb.append(", examineeCount=").append(examineeCount);
        sb.append(", subjectSortMode=").append(subjectSortMode);
        sb.append(", subjectOptSortMode=").append(subjectOptSortMode);
        sb.append(", adjudgeMode=").append(adjudgeMode);
        sb.append(", markMode=").append(markMode);
        sb.append(", markShowMode=").append(markShowMode);
        sb.append(", markTimeMode=").append(markTimeMode);
        sb.append(", etime=").append(etime);
        sb.append(", euser=").append(euser);
        sb.append("]");
        return sb.toString();
    }
}