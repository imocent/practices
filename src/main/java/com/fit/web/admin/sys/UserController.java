package com.fit.web.admin.sys;

import com.fit.aop.BizLog;
import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.SysUser;
import com.fit.service.SysUserService;
import com.fit.util.BeanUtil;
import com.fit.util.DateUtils;
import com.fit.util.OftenUtil;
import com.fit.util.WebUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author AIM
 * @Des 用户管理
 * @DATE 2020/8/10
 */
@Controller
@RequestMapping("/admin/user")
public class UserController extends BaseController {

    private static String PREFIX = "/admin/sys/user/";

    @Autowired
    private SysUserService userService;
    @Autowired
    private HashedCredentialsMatcher hashedCredentialsMatcher;

    @GetMapping("/list")
    public String adminUser(Model model) {
        model.addAttribute("title", "用户列表");
        return PREFIX + "list";
    }

    /**
     * 用户列表
     */
    @PostMapping("/list")
    @ResponseBody
    public void adminUser(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        List<SysUser> userList = userService.findList(map);
        int count = userService.findCount(map);
        writeJson(response, AjaxResult.tables(count, userList));
    }

    /**
     * 添加更新页面
     */
    @RequestMapping("edit")
    public String edit(Long id, Model model) {
        if (OftenUtil.isNotEmpty(id)) {
            SysUser sysUser = userService.get(id);
            model.addAttribute("user", sysUser);
        }
        return PREFIX + "edit";
    }

    @PostMapping("/save")
    @ResponseBody
    public Object save(SysUser user) {
        SysUser sysUser = this.userService.get(user.getId());
        Long userId = (Long) SecurityUtils.getSubject().getPrincipal();
        if (null == sysUser) {
            user.setCtime(new Date());
            user.setCuser(userId);
            this.userService.save(user);
        } else {
            BeanUtil.copyProperties(user, sysUser);
            sysUser.setEtime(new Date());
            sysUser.setEuser(userId);
            this.userService.update(sysUser);
        }
        return AjaxResult.success();
    }

    @PostMapping("/del")
    @ResponseBody
    public void userDel(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = WebUtil.getRequestMap(request);
        SysUser sysUser = BeanUtil.map2Bean(SysUser.class, map);
        int delete = userService.delete(sysUser.getId());
        if (delete > 0) {
            writeJson(response, AjaxResult.success("删除成功"));
        } else {
            writeJson(response, AjaxResult.error("删除失败"));
        }
    }

    /**
     * 详情页面
     */
    @RequestMapping("/detail")
    public String detail(Model model) {
        Long userId = (Long) SecurityUtils.getSubject().getPrincipal();
        if (OftenUtil.isNotEmpty(userId)) {
            SysUser sysUser = userService.get(userId);
            Map<String, Object> map = BeanUtil.obj2Map(sysUser);
            map.put("deptName", "");
            model.addAttribute("user", map);
            return PREFIX + "info";
        }
        return PREFIX + "edit";
    }

    /**
     * 修改密码页面
     */
    @GetMapping("/cypher")
    @BizLog(logType = "USER_CYPHER", logName = "修改密码页面")
    public String cypher() {
        return PREFIX + "cypher";
    }

    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @RequestMapping("/changePwd")
    @ResponseBody
    @BizLog(logType = "USER_SET_PWD", logName = "用户修改密码")
    public Object changePwd(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        if (OftenUtil.isEmpty(oldPassword, newPassword)) {
            throw new RuntimeException();
        }
        Long userId = (Long) SecurityUtils.getSubject().getPrincipal();
        SysUser user = this.userService.get(userId);
        String algorithmName = hashedCredentialsMatcher.getHashAlgorithmName();
        int iterations = hashedCredentialsMatcher.getHashIterations();
        boolean hexEncoded = hashedCredentialsMatcher.isStoredCredentialsHexEncoded();
        SimpleHash simpleHash = new SimpleHash(algorithmName, oldPassword, null, iterations);
        String oldMd5 = hexEncoded ? simpleHash.toHex() : simpleHash.toBase64();
        if (user.getPassword().equals(oldMd5)) {
            simpleHash = new SimpleHash(algorithmName, newPassword, null, iterations);
            user.setPassword(simpleHash.toHex());
            this.userService.update(user);
        }
        return AjaxResult.success();
    }

    /**
     * 重置用户的密码
     */
    @RequestMapping("/reset")
    @ResponseBody
    @BizLog(logType = "USER_RESET_PWD", logName = "重置用户密码")
    public Object reset(@RequestParam Long id) {
        if (OftenUtil.isEmpty(id)) {
            throw new RuntimeException();
        }
        // 判断当前登录的用户是否有操作这个用户的权限
        SysUser user = this.userService.get(id);
        String algorithmName = hashedCredentialsMatcher.getHashAlgorithmName();
        int iterations = hashedCredentialsMatcher.getHashIterations();
        boolean hexEncoded = hashedCredentialsMatcher.isStoredCredentialsHexEncoded();
        SimpleHash simpleHash = new SimpleHash(algorithmName, "admin", null, iterations);
        String pwd = hexEncoded ? simpleHash.toHex() : simpleHash.toBase64();
        user.setPassword(pwd);
        user.setEtime(DateUtils.nowDate());
        this.userService.update(user);
        return AjaxResult.success();
    }

    /**
     * 修改状态
     */
    @RequestMapping("/setState")
    @ResponseBody
    @BizLog(logType = "USER_SET_STATE", logName = "用户修改状态", value = "更新用户ID：#{#id}")
    public Object changeState(Long id) {
        SysUser sysUser = this.userService.get(id);
        if (sysUser != null) {
            if (sysUser.getEnabled()) {
                sysUser.setEnabled(false);
            } else {
                sysUser.setEnabled(true);
            }
            sysUser.setEtime(DateUtils.nowDate());
            this.userService.update(sysUser);
            return AjaxResult.success("修改成功");
        }
        return AjaxResult.error("修改状态失败");
    }

    /**
     * 设置角色页面
     */
    @RequestMapping("/setRole")
    public String setRoleView(Long id, Model model) {
        if (OftenUtil.isNotEmpty(id)) {
            SysUser sysUser = userService.get(id);
            model.addAttribute("roleId", sysUser.getRid());
        }
        model.addAttribute("userId", id);
        return PREFIX + "setRole";
    }

    /**
     * 修改角色
     */
    @RequestMapping("/authRole")
    @ResponseBody
    public Object changeFreeze(Long userId, Long roleId) {
        SysUser sysUser = this.userService.get(userId);
        if (sysUser != null) {
            sysUser.setRid(roleId);
            sysUser.setEtime(DateUtils.nowDate());
            this.userService.update(sysUser);
            return AjaxResult.success("修改成功");
        }
        return AjaxResult.error("修改角色失败");
    }
}