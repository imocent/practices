package com.fit.service;

import com.fit.entity.ZTreeNode;
import com.fit.util.JdbcTemplateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @AUTO 获取ztree节点服务
 * @Author AIM
 * @DATE 2019/4/28
 */
@Service
public class ZtreeNodeService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public List<String> auths(String roleId) {
        StringBuffer sb = new StringBuffer("SELECT a.`RES_ID` FROM `sys_authorities_res` a ");
        sb.append(" left join `sys_role_auth` r on r.`AUTH_ID`=a.`AUTH_ID` where r.`ROLE_ID`=?");
        return jdbcTemplate.queryForList(sb.toString(), new Object[]{roleId}, String.class);
    }

    /**
     * 获取部门树节点集合
     */
    public List<ZTreeNode> deptZtree() {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT ID AS id, PID AS parentId, SIMPLE_NAME AS `title`,");
        sb.append("(CASE WHEN (PID = 0 OR PID IS NULL) THEN 'true' ELSE 'false' END ) AS OPEN ");
        sb.append(" FROM sys_dept");

        return JdbcTemplateUtil.queryForListBean(jdbcTemplate, sb.toString(), ZTreeNode.class);
    }

    public List<ZTreeNode> dictZtree() {
        String sb = "SELECT `ID` AS id,`PID` AS parentId,`NAME` as `title`, 'false' AS OPEN FROM `sys_dict`";
        return JdbcTemplateUtil.queryForListBean(jdbcTemplate, sb.toString(), ZTreeNode.class);
    }

    /**
     * 获取栏目树节点集合
     */
    public List<ZTreeNode> menuZtree() {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT sm.`ID` AS id, IFNULL(ms.`ID`,0) AS parentId, sm.`NAME` as `title`, sm.`ID` AS `CODE`, ");
        sb.append("(CASE WHEN (sm.`PID` = 0 OR sm.`PID` IS NULL) THEN 'true' ELSE 'false' END ) AS OPEN, ");
        sb.append("sm.`LEVELS`  FROM `sys_resources` sm LEFT JOIN `sys_resources` ms ON sm.`PID` = ms.`ID`");
        return JdbcTemplateUtil.queryForListBean(jdbcTemplate, sb.toString(), ZTreeNode.class);
    }

    /**
     * 获取角色树节点集合
     */
    public List<ZTreeNode> roleZtree() {
        String sb = "SELECT ID AS id, '0' AS parentId, `ROLE_NAME` AS `title`, 'true' AS OPEN FROM sys_role";
        return JdbcTemplateUtil.queryForListBean(jdbcTemplate, sb.toString(), ZTreeNode.class);
    }

    public List<ZTreeNode> subjectZtree() {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT t.`ID`,t.`PID` AS parentId, t.`NAME` AS `title`, t.`ID` AS `CODE`, t.`LEVELS`,");
        sb.append(" (CASE WHEN (t.`PID` = 0 OR t.`PID` IS NULL) THEN 'true' ELSE 'false' END ) AS OPEN");
        sb.append(" FROM `lms_exam_subject` t WHERE t.`ENABLED` = 1");
        return JdbcTemplateUtil.queryForListBean(jdbcTemplate, sb.toString(), ZTreeNode.class);
    }

    public List<ZTreeNode> roomsZtree() {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT t.`ID`,0 AS `PID`, t.`title`, t.`ID` AS `CODE`,'true' AS OPEN");
        sb.append(" FROM `lms_exam_room` t WHERE t.`ENABLED` = 2");
        return JdbcTemplateUtil.queryForListBean(jdbcTemplate, sb.toString(), ZTreeNode.class);
    }
}