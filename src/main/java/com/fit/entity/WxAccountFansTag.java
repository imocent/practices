package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class WxAccountFansTag extends BaseEntity<WxAccountFansTag> {
    /**
     * 标签名称
     */
    private String name;

    /**
     * 该标签的粉丝数量
     */
    private Integer count;

    /**
     * 创建日期
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
        sb.append(", count=").append(count);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}