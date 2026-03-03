package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class SysFiles extends BaseEntity<SysFiles> {
    /**
     * 创建时间
     */
    private Date ctime;

    /**
     * 创建人
     */
    private Long cuser;

    /**
     * 文件类型: 0-图片,1-文件
     */
    private Integer fileMold;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * base64编码的文件
     */
    private String fileData;

    /**
     * 是否被禁用 0禁用1正常
     */
    private Boolean enabled;

    /**
     * 是否是超级权限 0非1是
     */
    private Integer isys;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", ctime=").append(ctime);
        sb.append(", cuser=").append(cuser);
        sb.append(", fileMold=").append(fileMold);
        sb.append(", fileName=").append(fileName);
        sb.append(", fileSuffix=").append(fileSuffix);
        sb.append(", fileData=").append(fileData);
        sb.append(", enabled=").append(enabled);
        sb.append(", isys=").append(isys);
        sb.append("]");
        return sb.toString();
    }
}