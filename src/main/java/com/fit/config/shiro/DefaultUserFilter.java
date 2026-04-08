package com.fit.config.shiro;

import com.alibaba.fastjson.JSONObject;
import com.fit.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author AIM
 * @Des 自定义用户拦截器
 * @DATE 2018/3/6
 */
@Slf4j
public class DefaultUserFilter extends UserFilter {

    @Override
    protected void redirectToLogin(ServletRequest req, ServletResponse resp) throws IOException {
        log.debug("=== 访问DefaultUserFilter =====>redirectToLogin");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        response.setCharacterEncoding("UTF-8");
        String loginUrl = getLoginUrl();
        String uri = request.getRequestURI();
        if (uri.startsWith("/admin") || uri.startsWith("/admin/")) {
            loginUrl = WebUtil.ADMIN_LOGIN_URL;
        }
        if (WebUtil.isAjax(request)) {
            log.debug("=== AJAX请求重定向到登录，返回JSON响应 ===");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try (PrintWriter out = response.getWriter()) {
                JSONObject json = new JSONObject();
                json.put("code", HttpServletResponse.SC_UNAUTHORIZED);
                json.put("msg", "未登录或会话已过期");
                json.put("data", "请重新登录");
                json.put("redirect", true); // 告诉前端需要重定向到登录页
                json.put("url", loginUrl);
                out.print(json.toJSONString());
                out.flush();
            } catch (IOException e) {
                log.error("写入AJAX响应失败: {}", e.getMessage(), e);
            }
            return;
        }
        log.info("访问用户拦截器重定向到登录接口：{}", "跳转到=>" + loginUrl);
        WebUtils.issueRedirect(request, response, loginUrl);
    }
}