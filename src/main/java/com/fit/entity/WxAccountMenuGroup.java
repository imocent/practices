package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class WxAccountMenuGroup extends BaseEntity<WxAccountMenuGroup> {
    /**
     * 菜单组名称
     */
    private String name;

    /**
     * 是否启用：1-是，0-否
     */
    private Boolean enable;

    /**
     * 创建时间
     */
    private Date createTime;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", enable=").append(enable);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}