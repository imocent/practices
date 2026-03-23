package com.fit.web;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * @AUTO
 * @Author AIM
 * @DATE 2025/11/20
 */
@Controller
public class ErrorsController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        int statusCode = 500;
        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }
        model.addAttribute("status", statusCode);
        model.addAttribute("error", HttpStatus.valueOf(statusCode).getReasonPhrase());
        model.addAttribute("message", message != null ? message : "系统发生错误");
        model.addAttribute("path", path != null ? path : "未知路径");
        model.addAttribute("timestamp", new java.util.Date());

        if (exception instanceof Exception) {
            model.addAttribute("exception", ((Exception) exception).getMessage());
        }
        return "error/error.html";
    }

    public String getErrorPath() {
        return "/error";
    }
}