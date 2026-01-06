package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class SysResources extends BaseEntity<SysResources> {
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

    /**
     * 父ID
     */
    private Long pid;

    /**
     * 名称
     */
    private String name;

    /**
     * 图标
     */
    private String icon;

    /**
     * 类型,1:url 2:method
     */
    private String type;

    /**
     * 链接
     */
    private String url;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 描述
     */
    private String notes;

    /**
     * 层级
     */
    private Integer levels;

    /**
     * 是否是菜单
     */
    private String ismenu;

    /**
     * 是否被禁用: 0-禁用,1-正常
     */
    private Boolean enabled;

    /**
     * 是否是超级权限: 0-非,1-是
     */
    private Boolean isys;

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
        sb.append(", pid=").append(pid);
        sb.append(", name=").append(name);
        sb.append(", icon=").append(icon);
        sb.append(", type=").append(type);
        sb.append(", url=").append(url);
        sb.append(", sort=").append(sort);
        sb.append(", notes=").append(notes);
        sb.append(", levels=").append(levels);
        sb.append(", ismenu=").append(ismenu);
        sb.append(", enabled=").append(enabled);
        sb.append(", isys=").append(isys);
        sb.append(", etime=").append(etime);
        sb.append(", euser=").append(euser);
        sb.append("]");
        return sb.toString();
    }
}