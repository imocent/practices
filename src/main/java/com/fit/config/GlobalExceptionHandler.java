package com.fit.config;

import com.fit.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.session.InvalidSessionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    /**
     * 处理未认证异常
     */
    @ExceptionHandler(UnauthenticatedException.class)
    public String handleUnauthenticatedException(UnauthenticatedException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("未认证异常：{}", e.getMessage());
        // 判断是否为Ajax请求
        if (WebUtil.isAjax(request)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(Collections.singletonMap("请先登录", 401).toString());
            return null;
        } else {
            return "redirect:/login";
        }
    }

    /**
     * 处理未授权异常
     */
    @ExceptionHandler(AuthorizationException.class)
    @ResponseBody
    public Map<String, Object> handleAuthorizationException(AuthorizationException e) {
        log.error("未授权异常：{}", e.getMessage());
        return Collections.singletonMap("没有权限访问", 403);
    }

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