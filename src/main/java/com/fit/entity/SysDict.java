package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class SysDict extends BaseEntity<SysDict> {
    /**
     * 创建时间
     */
    private Date ctime;

    /**
     * 创建人
     */
    private Long cuser;

    /**
     * 父级字典id
     */
    private Long pid;

    /**
     * 字典名称
     */
    private String name;

    /**
     * 字典的编码
     */
    private String code;

    /**
     * 是否系统标识
     */
    private String sign;

    /**
     * 描述
     */
    private String notes;

    /**
     * 排序
     */
    private Integer sort;

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
        sb.append(", code=").append(code);
        sb.append(", sign=").append(sign);
        sb.append(", notes=").append(notes);
        sb.append(", sort=").append(sort);
        sb.append(", etime=").append(etime);
        sb.append(", euser=").append(euser);
        sb.append("]");
        return sb.toString();
    }
}