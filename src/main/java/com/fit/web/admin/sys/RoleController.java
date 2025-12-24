package com.fit.web.admin.sys;

import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.SysRole;
import com.fit.entity.SysRoleAuth;
import com.fit.entity.ZTreeNode;
import com.fit.service.SysRoleAuthService;
import com.fit.service.SysRoleService;
import com.fit.service.ZtreeNodeService;
import com.fit.util.BeanUtil;
import com.fit.util.OftenUtil;
import com.fit.util.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author AIM
 * @Des 角色管理
 * @DATE 2020/8/10
 */
@Controller
@RequestMapping("/admin/role")
public class RoleController extends BaseController {

    private static String PREFIX = "/admin/sys/role/";

    @Autowired
    private SysRoleService roleService;
    @Autowired
    private SysRoleAuthService roleAuthService;
    @Autowired
    private ZtreeNodeService ztreeNodeService;

    @GetMapping("/list")
    public String adminUser(Model model) {
        model.addAttribute("title", "角色列表");
        return PREFIX + "list";
    }

    /**
     * 用户列表
     */
    @PostMapping("/list")
    @ResponseBody
    public void adminUser(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        List<SysRole> userList = roleService.findList(map);
        int count = roleService.findCount(map);
        writeJson(response, AjaxResult.tables(count, userList));
    }

    /**
     * 添加更新页面
     */
    @RequestMapping("edit")
    public String edit(Long id, Model model) {
        if (OftenUtil.isNotEmpty(id)) {
            SysRole role = roleService.get(id);
            model.addAttribute("role", role);
        }
        return PREFIX + "edit";
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @ResponseBody
    public Object save(SysRole roles) {
        SysRole sysRole = this.roleService.get(roles.getId());
        if (null == sysRole) {
            roles.setCtime(new Date());
            this.roleService.save(roles);
        } else {
            BeanUtil.copyProperties(roles, sysRole);
            sysRole.setEtime(new Date());
            this.roleService.update(sysRole);
        }
        return AjaxResult.success();
    }

    /**
     * 删除
     *
     * @param ids 删除ID集合
     */
    @PostMapping("/del")
    @ResponseBody
    public Object del(@RequestParam("ids") List<Long> ids) {
        if (OftenUtil.isNotEmpty(ids)) {
            ids.remove("1");
            if (ids.isEmpty()) {
                return AjaxResult.error("含有不能删角色");
            } else {
                this.roleService.batchDelete(ids);
                return AjaxResult.success();
            }
        } else {
            return AjaxResult.error("参数异常");
        }
    }

    /**
     * 获取部门的tree列表
     */
    @RequestMapping(value = "/tree")
    @ResponseBody
    public Object tree() {

        List<ZTreeNode> tree = this.ztreeNodeService.roleZtree();
        tree.add(ZTreeNode.createParent());
        return AjaxResult.success(tree);
    }

    /**
     * 设置角色页面
     */
    @RequestMapping("/setAssign")
    public String setRoleView(String id, Model model) {
        if (OftenUtil.isNotEmpty(id)) {
            List<String> auths = this.ztreeNodeService.auths(id);
            model.addAttribute("menuIds", String.join(",", auths));
        }
        model.addAttribute("roleId", id);
        return PREFIX + "permiss.html";
    }

    /**
     * 保存
     */
    @PostMapping("/saveAssign")
    @ResponseBody
    public Object saveAssign(final String roleId, String menus) {
        try {
            this.roleAuthService.selectBySQL("DELETE FROM `sys_role_auth` WHERE ROLE_ID =" + roleId);
            final List<String> list = Arrays.asList(menus.split(","));
            for (String s : list) {
                SysRoleAuth auth = new SysRoleAuth();
                auth.setRoleId(Long.valueOf(roleId));
                auth.setAuthId(Long.valueOf(s));
                this.roleAuthService.save(auth);
            }
            return AjaxResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("保存权限失败");
        }
    }
}