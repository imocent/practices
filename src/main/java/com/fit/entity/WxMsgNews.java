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
     * 关键字: 关注者发送的消息
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
     * 图文类型: 0-回复消息,1-单图文,2-多图片
     */
    private Integer mode;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 图文消息的摘要，仅单图文有，多图文为空
     */
    private String digest;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 外部链接
     */
    private String contentSourceUrl;

    /**
     * 封面图片id
     */
    private String thumbMediaId;

    /**
     * 是否显示封面: 0-不显示，1-显示
     */
    private Integer showCoverPic;

    /**
     * 是否打开评论: 0-不打开，1-打开
     */
    private Integer needOpenComment;

    /**
     * 是否粉丝才可评论: 0-所有人可评论，1-粉丝才可评论
     */
    private Integer onlyFansCanComment;

    /**
     * 图文消息原文链接
     */
    private String url;

    /**
     * 封面图片
     */
    private String thumbUrl;

    /**
     * 上传后返回的媒体素材id
     */
    private String mediaId;

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
        sb.append(", mode=").append(mode);
        sb.append(", title=").append(title);
        sb.append(", author=").append(author);
        sb.append(", digest=").append(digest);
        sb.append(", content=").append(content);
        sb.append(", contentSourceUrl=").append(contentSourceUrl);
        sb.append(", thumbMediaId=").append(thumbMediaId);
        sb.append(", showCoverPic=").append(showCoverPic);
        sb.append(", needOpenComment=").append(needOpenComment);
        sb.append(", onlyFansCanComment=").append(onlyFansCanComment);
        sb.append(", url=").append(url);
        sb.append(", thumbUrl=").append(thumbUrl);
        sb.append(", mediaId=").append(mediaId);
        sb.append(", newsIndex=").append(newsIndex);
        sb.append("]");
        return sb.toString();
    }
}