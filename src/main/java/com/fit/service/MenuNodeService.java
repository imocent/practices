package com.fit.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fit.entity.MenuNode;
import com.fit.util.JdbcTemplateUtil;
import com.fit.util.OftenUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.*;

/**
 * @AUTO 菜单节点服务
 * @Author AIM
 * @DATE 2019/4/25
 */
@Service
public class MenuNodeService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取用户权限集合
     */
    public List<Long> getUserRoles(String userName) {
        if (OftenUtil.isEmpty(userName)) {
            return new ArrayList<Long>();
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT r.`role_id` FROM `sys_user_role` r ");
            sb.append(" LEFT JOIN `sys_user` u ON r.`user_id`=u.`id`");
            sb.append(" WHERE u.`username`=?");
            return JdbcTemplateUtil.queryForList(jdbcTemplate, sb.toString(), Long.class, userName);
        }
    }

    /**
     * 获取栏目列表
     */
    public List<MenuNode> getUserMenuNodes(List<Long> roleIds, HttpServletRequest request) {
        if (roleIds == null || roleIds.size() == 0) {
            return new ArrayList<MenuNode>();
        } else {
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT r.`id`,r.`pid`,r.`icon`,r.`name`,r.`url`,r.`type`,r.`sort`,");
            sb.append(" r.`levels`,r.`ismenu` FROM `sys_resources` r  ");
            sb.append(" INNER JOIN `sys_authorities_res` ar ON r.`id` = ar.`res_id` ");
            sb.append(" INNER JOIN `sys_role_auth` ra ON ar.`auth_id` = ra.`auth_id` ");
            sb.append(" WHERE ra.`role_id` IN ( ");
            for (int i = 0; i < roleIds.size(); i++) {
                sb.append("?,");
            }
            if (sb.toString().endsWith(",")) {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append(" ) AND r.ismenu = 'Y' AND r.`enabled`=1 AND r.`pid` != 0 ORDER BY r.`levels`,r.`sort` ASC");
            List<MenuNode> menus = JdbcTemplateUtil.queryForListBean(jdbcTemplate, sb.toString(), roleIds, MenuNode.class);
            // 给所有的菜单url加上ctxPath
            for (MenuNode menuItem : menus) {
                if (!(menuItem.getUrl().equals("#"))) {
                    menuItem.setUrl(request.getContextPath() + menuItem.getUrl());
                }
            }

            return MenuNode.buildTitle(menus);
        }
    }

    /**
     * 根据配置文件设置过滤接口文档信息
     *
     * @param nodes    查询到的数据
     * @param menuName 指定的栏目名称
     */
    public List<MenuNode> filterMenuByName(List<MenuNode> nodes, String menuName) {
        List<MenuNode> menuNodesCopy = new ArrayList<>(nodes.size());
        for (MenuNode menuNode : nodes) {
            if (!menuName.equals(menuNode.getName())) {
                menuNodesCopy.add(menuNode);
            }
            List<MenuNode> childrenList = menuNode.getChildren();
            if (childrenList != null && childrenList.size() > 0) {
                menuNode.setChildren(filterMenuByName(childrenList, menuName));
            }
        }
        return menuNodesCopy;
    }

    /**
     * 栏目列表
     *
     * @param condition 查询条件
     * @param level     菜单层级
     */
    public List<Map<String, Object>> getMenuTreeList(String condition, String level) {
        List<String> params = new LinkedList<String>();
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT ID AS id, CODE AS code, PCODE AS pcode, NAME AS name, ICON AS icon,");
        sb.append("URL AS url, SORT AS sort, LEVELS AS levels, MENU_FLAG AS ismenu, DESCRIPTION AS description,");
        sb.append("STATUS AS status, NEW_PAGE_FLAG AS newPageFlag, OPEN_FLAG AS openFlag, CTIME AS createTime, ");
        sb.append("ETIME AS updateTime, CUSER AS createUser, EUSER AS updateUser ");
        sb.append(" FROM sys_resources WHERE ENABLED = 1 ");
        if (OftenUtil.isNotEmpty(condition)) {
            sb.append(" AND (NAME LIKE CONCAT('%',?,'%') OR CODE LIKE CONCAT('%',?,'%')) ");
            params.add(condition);
            params.add(condition);
        }
        if (OftenUtil.isNotEmpty(level)) {
            sb.append("AND LEVELS = ? ");
            params.add(level);
        }

        List<Map<String, Object>> list = JdbcTemplateUtil.queryForListMap(jdbcTemplate, sb.toString(), params);

        return this.buildTreeList(list, null, "code", "pcode", "children");
    }

    /**
     * @param list  数据集合
     * @param pCode 父级节点值
     * @param nKey  主关键字名
     * @param pKey  父级关键字名
     * @param iKey  子节点关键字名
     * @return
     */
    private List<Map<String, Object>> buildTreeList(List<Map<String, Object>> list, String pCode, String nKey, String pKey, String iKey) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (!list.isEmpty()) {
            for (Map<String, Object> node : list) {
                String pVal = node.get(pKey) != null ? node.get(pKey).toString().trim() : null;
                // 判断是否为当前层级的节点
                boolean isCurrentLevel = false;
                if (pCode == null) {
                    isCurrentLevel = pVal == null;
                } else {
                    isCurrentLevel = pCode.equals(pVal);
                }

                if (isCurrentLevel) {
                    Map<String, Object> treeNode = new HashMap<>(node);
                    String code = node.get(nKey).toString().trim();
                    // 递归获取子节点
                    List<Map<String, Object>> children = buildTreeList(list, code, nKey, pKey, iKey);
                    if (!children.isEmpty()) {
                        treeNode.put(iKey, children);
                    }
                    result.add(treeNode);
                }
            }
        }
        return result;
    }

    public List<Map<String, Object>> getScreen(String pid) {
        String sql = "SELECT `id`,`pid`,`name` FROM `lms_exam_subject` WHERE `ENABLED`=1 and `pid`=?";
        return JdbcTemplateUtil.queryForListMap(jdbcTemplate, sql, pid);
    }

    public List<Map<String, Object>> getSubjectsDropdown() {
        String sql = "SELECT `id`,`pid`,`name` as `title` FROM `lms_exam_subject` WHERE `ENABLED`=1";
        List<Map<String, Object>> maps = JdbcTemplateUtil.queryForListMap(jdbcTemplate, sql);
        return buildTreeList(maps, "0", "ID", "PID", "child");
    }
}