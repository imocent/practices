package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class WxMsgNews extends BaseEntity<WxMsgNews> {
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 公众号
     */
    private String account;

    /**
     * 消息类型
     */
    private String msgType;

    /**
     * 关注者发送的消息
     */
    private String inputCode;

    /**
     * 规则, 目前是'相等'
     */
    private String rule;

    /**
     * 消息阅读数
     */
    private Integer readCount;

    /**
     * 消息点赞数
     */
    private Integer favourCount;

    /**
     * 禁用状态: 0-禁用,1-正常
     */
    private Boolean enabled;

    /**
     * 图文类型: 单图文,多图片
     */
    private String multType;

    /**
     * 标题
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 简介
     */
    private String brief;

    /**
     * 描述
     */
    private String description;

    /**
     * 封面图片
     */
    private String picPath;

    /**
     * 是否显示图片
     */
    private Integer showPic;

    /**
     * 图文消息原文链接
     */
    private String url;

    /**
     * 外部链接
     */
    private String fromUrl;

    /**
     * 上传后返回的媒体素材id
     */
    private String mediaId;

    /**
     * 封面图片id
     */
    private String thumbMediaId;

    /**
     * 多图文中的第几条
     */
    private Integer newsIndex;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", createTime=").append(createTime);
        sb.append(", account=").append(account);
        sb.append(", msgType=").append(msgType);
        sb.append(", inputCode=").append(inputCode);
        sb.append(", rule=").append(rule);
        sb.append(", readCount=").append(readCount);
        sb.append(", favourCount=").append(favourCount);
        sb.append(", enabled=").append(enabled);
        sb.append(", multType=").append(multType);
        sb.append(", title=").append(title);
        sb.append(", author=").append(author);
        sb.append(", brief=").append(brief);
        sb.append(", description=").append(description);
        sb.append(", picPath=").append(picPath);
        sb.append(", showPic=").append(showPic);
        sb.append(", url=").append(url);
        sb.append(", fromUrl=").append(fromUrl);
        sb.append(", mediaId=").append(mediaId);
        sb.append(", thumbMediaId=").append(thumbMediaId);
        sb.append(", newsIndex=").append(newsIndex);
        sb.append("]");
        return sb.toString();
    }
}