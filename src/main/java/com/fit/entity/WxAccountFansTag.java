package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class WxAccountFansTag extends BaseEntity<WxAccountFansTag> {
    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 公众号
     */
    private String account;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 该标签的粉丝数量
     */
    private Integer count;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", createTime=").append(createTime);
        sb.append(", account=").append(account);
        sb.append(", name=").append(name);
        sb.append(", count=").append(count);
        sb.append("]");
        return sb.toString();
    }
}