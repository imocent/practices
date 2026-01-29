package com.fit.web;

import com.fit.util.CaptchaUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class LoginController {

    @Value("${Fit.captcha-open}")
    private boolean captcha = false;

    @GetMapping({"/captcha", "/captcha.do", "/image.jsp", "/validate.jsp", "/validate"})
    public void getCaptcha(HttpServletResponse response, HttpSession session) throws IOException {
        CaptchaUtil.getCaptcha(response, session);
    }

    @GetMapping(value = {"/login", "/admin/login", "/admin/login.do"})
    public String login(HttpServletRequest request, Model model) {
        model.addAttribute("captchaOnOff", request.getRequestURI().startsWith("/admin") ? captcha : false);
        return "admin/login";
    }
}