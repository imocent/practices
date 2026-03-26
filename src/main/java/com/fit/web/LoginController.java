package com.fit.web;

import com.alibaba.fastjson.JSONObject;
import com.fit.util.CaptchaUtil;
import com.fit.util.SecureUtils;
import com.fit.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;

@Slf4j
@Controller
public class LoginController {

    @Value("${Fit.captcha-open}")
    private boolean captcha = false;
    @Value("${Fit.cipher-open}")
    private boolean cipher = false;

    @RequestMapping(value = {"/captcha", "/captcha.do", "/validate"}, method = RequestMethod.GET)
    public void getCaptcha(HttpServletResponse response, HttpSession session) throws IOException {
        CaptchaUtil.getCaptcha(response, session);
    }

    @RequestMapping(value = {"/login", "/admin/login", "/admin/login.do"}, method = RequestMethod.GET)
    public String login(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        // 从 Session 中获取错误信息并传递给页面
        if (session != null) {
            String tips = (String) session.getAttribute("tips");
            if (tips != null) {
                model.addAttribute("tips", tips);
            }
        }
        model.addAttribute("cipher", cipher);
        boolean isAdmin = request.getRequestURI().startsWith("/admin");
        model.addAttribute("captchaOnOff", isAdmin ? captcha : false);
        if (SecurityUtils.getSubject().isAuthenticated()) {
            if (isAdmin) {
                return "redirect:/admin/index";
            } else {
                return "redirect:/index";
            }
        } else {
            return "admin/login";
        }
    }

    @ResponseBody
    @RequestMapping(value = "/clear-login-error", method = RequestMethod.POST)
    public void clearLoginError(HttpSession session) {
        session.removeAttribute("tips");
    }

    @RequestMapping(value = {"/logout", "/admin/logout"}, method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpSession session) {
        String redirectUrl = WebUtil.LOGIN_URL;
        if (request.getRequestURI().startsWith("/admin") || request.getRequestURI().startsWith("/admin/")) {
            redirectUrl = WebUtil.ADMIN_LOGIN_URL;
        }
        Subject subject = SecurityUtils.getSubject();
        if (subject.getSession(false) != null) {
            session.removeAttribute("captcha");
            session.removeAttribute("loginType");
            session.removeAttribute("tips");
            session.removeAttribute("user");
            session.removeAttribute("username");
            subject.logout();
        }
        return "redirect:" + redirectUrl;
    }

    /**
     * 获取加密公钥
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/cipherKey", method = RequestMethod.POST)
    public String getPublicKey() {
        HashMap<String, Object> map;
        JSONObject jsonObject = new JSONObject();
        try {
            map = SecureUtils.getKeys();
            //生成公钥和私钥
            RSAPublicKey publicKey = (RSAPublicKey) map.get("public");
            RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");
            Subject subject = SecurityUtils.getSubject();
            subject.getSession(true).setAttribute("privateKey", privateKey);
            //提取密钥参数
            String publicKeyExponent = publicKey.getPublicExponent().toString(16);
            String publicKeyModulus = publicKey.getModulus().toString(16);
            //设置参数到返回内容中
            jsonObject.put("publicKeyExponent", publicKeyExponent);
            jsonObject.put("publicKeyModulus", publicKeyModulus);
            jsonObject.put("result", 0);
            jsonObject.put("msg", "成功获得公钥");
            return jsonObject.toJSONString();
        } catch (Exception e) {
            jsonObject.put("result", 1);
            jsonObject.put("msg", "登录失败，请稍后再试。");
            return jsonObject.toJSONString();
        }
    }
}