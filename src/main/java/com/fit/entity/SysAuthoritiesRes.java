package com.fit.entity;

import com.fit.base.BaseEntity;
import lombok.*;

@Data
public class SysAuthoritiesRes extends BaseEntity<SysAuthoritiesRes> {
    /**
     * 权限id
     */
    private Long authId;

    /**
     * 资源id
     */
    private Long resId;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", authId=").append(authId);
        sb.append(", resId=").append(resId);
        sb.append("]");
        return sb.toString();
    }
}