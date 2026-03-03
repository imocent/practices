package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class SysOperationLog extends BaseEntity<SysOperationLog> {
    /**
     * 创建时间
     */
    private Date ctime;

    /**
     * 日志类型(字典)
     */
    private String logType;

    /**
     * 日志名称
     */
    private String logName;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 类名称
     */
    private String className;

    /**
     * 方法名称
     */
    private String method;

    /**
     * 是否成功(字典)
     */
    private String succeed;

    /**
     * 备注
     */
    private String message;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", ctime=").append(ctime);
        sb.append(", logType=").append(logType);
        sb.append(", logName=").append(logName);
        sb.append(", userId=").append(userId);
        sb.append(", className=").append(className);
        sb.append(", method=").append(method);
        sb.append(", succeed=").append(succeed);
        sb.append(", message=").append(message);
        sb.append("]");
        return sb.toString();
    }
}