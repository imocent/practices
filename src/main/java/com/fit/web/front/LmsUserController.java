package com.fit.web.front;

import com.fit.base.AjaxResult;
import com.fit.base.BaseController;
import com.fit.entity.LmsQuestionAnswerUser;
import com.fit.entity.SysUser;
import com.fit.service.LmsQuestionAnswerUserService;
import com.fit.service.SysUserService;
import com.fit.util.BeanUtil;
import com.fit.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @AUTO
 * @Author AIM
 * @DATE 2026/1/21
 */
@Slf4j
@Controller
@RequestMapping("/user")
public class LmsUserController extends BaseController {

    @Autowired
    private HashedCredentialsMatcher hashedCredentialsMatcher;
    @Autowired
    private SysUserService userService;
    @Autowired
    private LmsQuestionAnswerUserService answerUserService;

    @GetMapping("/login")
    public String userLogin(HttpServletRequest request, Model model) {
        return "front/login";
    }

    @GetMapping("/answers")
    public String answers(HttpServletRequest request, Model model) {
        Long userId = (Long) SecurityUtils.getSubject().getPrincipal();
        model.addAttribute("user", userService.get(userId));
        return "front/answers";
    }

    @PostMapping("/answers")
    @ResponseBody
    public AjaxResult answerList(HttpServletRequest request) {
        Long userId = (Long) SecurityUtils.getSubject().getPrincipal();
        if (userId == null) {
            return AjaxResult.error("请登录后刷新");
        } else {
            Map<String, Object> map = WebUtil.getRequestMap(request);
            map.put("cuser", userId);
            List<LmsQuestionAnswerUser> list = answerUserService.findList(map);
            int count = answerUserService.findCount(map);
            return AjaxResult.tables(count, list);
        }
    }

    @PostMapping("/answerSave")
    @ResponseBody
    public AjaxResult answerSave(HttpServletRequest request) {
        Long userId = (Long) SecurityUtils.getSubject().getPrincipal();
        if (userId == null) {
            return AjaxResult.error("请登录后提交");
        } else {
            Map<String, Object> map = WebUtil.getRequestMap(request);
            LmsQuestionAnswerUser bean = BeanUtil.map2Bean(LmsQuestionAnswerUser.class, map);
            bean.setCuser(userId);
            bean.setCtime(new Date());
            this.answerUserService.save(bean);
            return AjaxResult.success();
        }
    }

    @PostMapping("/answerDel")
    @ResponseBody
    public AjaxResult answerDel(Long id) {
        Long userId = (Long) SecurityUtils.getSubject().getPrincipal();
        if (userId == null) {
            return AjaxResult.error("请登录后提交");
        } else {
            int delete = this.answerUserService.delete(id);
            if (delete > 0) {
                return AjaxResult.success("删除成功");
            } else {
                return AjaxResult.error("删除失败");
            }
        }
    }

    @GetMapping("/account")
    public String account(HttpServletRequest request, Model model) {
        Long userId = (Long) SecurityUtils.getSubject().getPrincipal();
        if (userId != null) {
            SysUser user = userService.get(userId);
            Map<String, Object> map = BeanUtil.obj2Map(user);
            map.put("deptName", "");
            map.put("userImg", "");
            model.addAttribute("user", map);
        }
        return "front/account";
    }

    @GetMapping("/adjust")
    public String userPwd(HttpServletRequest request, Model model) {
        return "front/adjust";
    }

    @PostMapping("/account/save")
    @ResponseBody
    public Object accountSave(HttpServletRequest request) {
        Long userId = (Long) SecurityUtils.getSubject().getPrincipal();
        if (userId == null) {
            return AjaxResult.error("请登录后提交");
        } else {
            Map<String, Object> map = WebUtil.getRequestMap(request);
            SysUser user = this.userService.get(userId);
            if (map.containsKey("oldPassword") && map.containsKey("newPassword")) {
                Object oldPassword = map.get("oldPassword");
                Object newPassword = map.get("newPassword");
                int iterations = hashedCredentialsMatcher.getHashIterations();
                boolean hexEncoded = hashedCredentialsMatcher.isStoredCredentialsHexEncoded();
                String algorithmName = hashedCredentialsMatcher.getHashAlgorithmName();
                SimpleHash simpleHash = new SimpleHash(algorithmName, oldPassword, null, iterations);
                String oldMd5 = hexEncoded ? simpleHash.toHex() : simpleHash.toBase64();
                if (user.getPassword().equals(oldMd5)) {
                    simpleHash = new SimpleHash(algorithmName, newPassword, null, iterations);
                    user.setPassword(simpleHash.toHex());
                    this.userService.update(user);
                } else {
                    return AjaxResult.error("修改密码错误");
                }
            } else {
                SysUser sysUser = BeanUtil.map2Bean(SysUser.class, map);
                BeanUtil.copyProperties(sysUser, user);
                user.setEtime(new Date());
                user.setEuser(userId);
                this.userService.update(user);
            }
            return AjaxResult.success();
        }
    }
}