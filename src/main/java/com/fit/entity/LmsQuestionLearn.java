package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class LmsQuestionLearn extends BaseEntity<LmsQuestionLearn> {
    /**
     * 主键
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
     * 标题
     */
    private String title;

    /**
     * 正文
     */
    private String content;

    /**
     * 备注
     */
    private String notes;

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
        sb.append(", uuid=").append(uuid);
        sb.append(", title=").append(title);
        sb.append(", content=").append(content);
        sb.append(", notes=").append(notes);
        sb.append(", enabled=").append(enabled);
        sb.append(", etime=").append(etime);
        sb.append(", euser=").append(euser);
        sb.append("]");
        return sb.toString();
    }
}