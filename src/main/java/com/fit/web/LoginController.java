package com.fit.web;

import com.fit.util.CaptchaUtil;
import com.fit.util.WebUtil;
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

@Controller
public class LoginController {

    @Value("${Fit.captcha-open}")
    private boolean captcha = false;

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
        boolean isAdmin = request.getRequestURI().startsWith("/admin");
        if (SecurityUtils.getSubject().isAuthenticated()) {
            if (isAdmin) {
                return "redirect:/admin/index";
            } else {
                return "redirect:/index";
            }
        } else {
            model.addAttribute("captchaOnOff", isAdmin ? captcha : false);
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
}