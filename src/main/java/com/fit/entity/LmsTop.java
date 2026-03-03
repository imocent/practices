package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class LmsTop extends BaseEntity<LmsTop> {
    /**
     * 创建时间
     */
    private Date ctime;

    /**
     * 创建人
     */
    private Long cuser;

    /**
     * 模型: 0-横幅,1-课程,
     */
    private Integer model;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 访问地址
     */
    private String url;

    /**
     * 图片ID
     */
    private Long imgId;

    /**
     * 类型: 1-首页推荐,2-横幅推荐
     */
    private Integer mold;

    /**
     * 访问量
     */
    private Integer visits;

    /**
     * 排序
     */
    private Integer sort;

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
        sb.append(", model=").append(model);
        sb.append(", title=").append(title);
        sb.append(", content=").append(content);
        sb.append(", url=").append(url);
        sb.append(", imgId=").append(imgId);
        sb.append(", mold=").append(mold);
        sb.append(", visits=").append(visits);
        sb.append(", sort=").append(sort);
        sb.append(", enabled=").append(enabled);
        sb.append(", etime=").append(etime);
        sb.append(", euser=").append(euser);
        sb.append("]");
        return sb.toString();
    }
}