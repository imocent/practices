package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class WxAssetMedia extends BaseEntity<WxAssetMedia> {
    /**
     * 微信返回的media_id
     */
    private String mediaId;

    /**
     * 媒体类型: image, video, voice, thumb
     */
    private String mediaType;

    /**
     * 微信服务器返回的URL
     */
    private String wxUrl;

    /**
     * 图片/文件原名称 (对应原 realname)
     */
    private String realname;

    /**
     * 文件存储名称
     */
    private String filename;

    /**
     * 文件大小byte
     */
    private Long size;

    /**
     * 文件后缀/类型
     */
    private String suffix;

    /**
     * 本地存储路径 
     */
    private String url;

    /**
     * 素材标题
     */
    private String title;

    /**
     * 简介说明
     */
    private String description;

    /**
     * 分类/标签
     */
    private String category;

    /**
     * 状态: 0-未引用, 1-已引用
     */
    private Boolean flag;

    /**
     * 微信账号/公众号标识
     */
    private String account;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", mediaId=").append(mediaId);
        sb.append(", mediaType=").append(mediaType);
        sb.append(", wxUrl=").append(wxUrl);
        sb.append(", realname=").append(realname);
        sb.append(", filename=").append(filename);
        sb.append(", size=").append(size);
        sb.append(", suffix=").append(suffix);
        sb.append(", url=").append(url);
        sb.append(", title=").append(title);
        sb.append(", description=").append(description);
        sb.append(", category=").append(category);
        sb.append(", flag=").append(flag);
        sb.append(", account=").append(account);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}