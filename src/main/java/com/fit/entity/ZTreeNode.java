package com.fit.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * ztree 插件的节点
 */
@Data
public class ZTreeNode {

    /**
     * 节点id
     */
    private Long id;

    /**
     * 父节点id
     */
    private Long parentId;

    /**
     * 节点名称
     */
    private String title;

    /**
     * 自定义码
     */
    private String code;

    /**
     * 自定义层级
     */
    private Integer levels = 0;

    /**
     * 是否打开节点
     */
    private Boolean open = false;

    /**
     * 是否被选中
     */
    private Boolean checked;

    /**
     * 节点图标  single or group
     */
    private String iconSkin;

    /**
     * dtree复选框集合
     */
    private List<CheckArr> checkArr = new ArrayList<>();

    public ZTreeNode() {
        this.checkArr.add(new CheckArr());
    }

    /**
     * 创建ztree的父级节点
     */
    public static ZTreeNode createParent() {
        return createParent("顶级", 0L);
    }

    public static ZTreeNode createParent(String zname, Long zid) {
        return createParent(zname, zid, -1L);
    }

    /**
     * 创建ztree的父级节点
     */
    public static ZTreeNode createParent(String title, Long zid, Long pid) {
        ZTreeNode zTreeNode = new ZTreeNode();
        zTreeNode.setChecked(false);
        zTreeNode.setId(zid);
        zTreeNode.setTitle(title);
        zTreeNode.setOpen(true);
        zTreeNode.setParentId(pid);
        zTreeNode.setLevels(0);
        return zTreeNode;
    }

    /**
     * 复选框状态类
     */
    @Data
    public static class CheckArr {
        private String type = "0";
        private String checked = "0";
    }
}