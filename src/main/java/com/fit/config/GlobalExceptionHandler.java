package com.fit.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.session.InvalidSessionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

/**
 * @AUTO
 * @Author AIM
 * @DATE 2026/3/23
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    // 处理无权限异常：不要让用户退出，只是提示无权访问
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleUnauthorized(UnauthorizedException e) {
        // 记录日志，返回无权限提示页面或JSON
        log.error(e.getMessage());
        return "error/403";
    }

    // 处理会话失效异常：引导用户重新登录
    @ExceptionHandler(InvalidSessionException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleInvalidSession(InvalidSessionException e) {
        // 清理本地Session，引导至登录页
        log.error(e.getMessage());
        return "redirect:/login?timeout=true";
    }

    // 兜底：处理所有其他异常，保证不暴露系统错误给前端，同时不让Subject失效
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> handleGlobalException(HttpServletRequest request, Exception e) {
        // 记录详细日志
        log.error("系统异常: ", e);
        // 返回通用错误码，前端根据code提示
        return Collections.singletonMap("code", 500);
    }
}
