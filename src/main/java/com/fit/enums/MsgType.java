package com.fit.enums;

/**
 * 消息类型：所有微信涉及到的消息类型统一管理
 */
public enum MsgType {

    Event("event", "事件消息", ""), Image("image", "图片消息", "jpg|jpeg|png"),
    Location("location", "地理位置", ""), MASS("MASS", "群发图文消息", ""),
    News("news", "图文消息", ""), Text("text", "文本消息", ""),
    Voice("voice", "语音消息", "amr"), Video("video", "视频消息", "mp4|MP4"),
    SUBSCRIBE("subscribe", "订阅消息", ""), UNSUBSCRIBE("unsubscribe", "取消订阅", ""),
    FILE("file", "普通文件", "txt|pdf|doc|docx|xls|xlsx|ppt|pptx");

    public String name;
    public String desc;
    public String extensions;

    private MsgType(String name, String desc, String extensions) {
        this.name = name;
        this.desc = desc;
        this.extensions = extensions;
    }


    /**
     * 根据文件名判断媒体类型
     */
    public static String getTypeByName(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        for (MsgType type : MsgType.values()) {
            for (String supportExt : type.extensions.split("\\|")) {
                if (supportExt.toLowerCase().equals(ext)) {
                    return type.name;
                }
            }
        }
        return FILE.name; // 默认返回普通文件
    }

    /**
     * 根据MIME类型判断（辅助判断）
     */
    public static MsgType detectByContentType(String contentType) {
        if (contentType == null) return null;

        if (contentType.startsWith("image/")) return Image;
        if (contentType.startsWith("video/")) return Video;
        if (contentType.startsWith("audio/")) return Voice;

        return FILE;
    }

    @Override
    public String toString() {
        return String.format("MsgType{name='%s', desc='%s'}", name, desc);
    }
}