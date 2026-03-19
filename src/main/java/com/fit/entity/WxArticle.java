package com.fit.entity;

import com.fit.base.BaseEntity;
import lombok.*;

@Data
public class WxArticle extends BaseEntity<WxArticle> {
    /**
     * 标题
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 内容
     */
    private String content;

    /**
     * 内容原始链接
     */
    private String contentSourceUrl;

    /**
     * 是否显示封面：1-是，0-否
     */
    private Integer showCoverPic;

    /**
     * 摘要
     */
    private String digest;

    private String url;

    /**
     * 媒体ID
     */
    private String mediaId;

    /**
     * 图文消息ID
     */
    private Integer newsId;

    private Integer newsIndex;

    /**
     * 图片地址
     */
    private String picUrl;

    private String thumbMediaId;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", title=").append(title);
        sb.append(", author=").append(author);
        sb.append(", content=").append(content);
        sb.append(", contentSourceUrl=").append(contentSourceUrl);
        sb.append(", showCoverPic=").append(showCoverPic);
        sb.append(", digest=").append(digest);
        sb.append(", url=").append(url);
        sb.append(", mediaId=").append(mediaId);
        sb.append(", newsId=").append(newsId);
        sb.append(", newsIndex=").append(newsIndex);
        sb.append(", picUrl=").append(picUrl);
        sb.append(", thumbMediaId=").append(thumbMediaId);
        sb.append("]");
        return sb.toString();
    }
}