package com.fit.entity;

import com.fit.base.BaseEntity;
import java.util.Date;
import lombok.*;

@Data
public class WxMsgTemplate extends BaseEntity<WxMsgTemplate> {
    /**
     * appid
     */
    private String appid;

    /**
     * 模版名称
     */
    private String name;

    /**
     * 标题
     */
    private String title;

    /**
     * 模板内容
     */
    private String content;

    /**
     * 消息内容
     */
    private String data;

    /**
     * 链接
     */
    private String url;

    /**
     * 小程序信息
     */
    private String miniprogram;

    /**
     * 是否有效
     */
    private Byte enabled;

    /**
     * 修改时间
     */
    private Date utime;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", appid=").append(appid);
        sb.append(", name=").append(name);
        sb.append(", title=").append(title);
        sb.append(", content=").append(content);
        sb.append(", data=").append(data);
        sb.append(", url=").append(url);
        sb.append(", miniprogram=").append(miniprogram);
        sb.append(", enabled=").append(enabled);
        sb.append(", utime=").append(utime);
        sb.append("]");
        return sb.toString();
    }
}