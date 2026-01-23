package com.fit.config.shiro;

import com.fit.base.AjaxResult;
import com.fit.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * 支持动态 loginUrl 的自定义表单认证拦截器
 * 规则：
 * - 访问 /admin/** -> 跳到 /admin/login
 * - 其他未登录 -> 跳到 /login?redirect=原始URL
 * - AJAX请求 -> 返回JSON响应
 */
@Slf4j
public class DefaultAuthenticationFilter extends FormAuthenticationFilter {

    private static final String AJAX_HEADER = "X-Requested-With"; //AJAX请求头标识
    public static final String CAPTCHA_REQUIRED_KEY = "shiroCaptchaRequired"; //是否需要验证码
    public static final String CAPTCHA_ERROR_COUNT_KEY = "shiroCaptchaErrorCount"; //验证码错误次数
    public static final String DEFAULT_CAPTCHA_PARAM = "captcha";
    private static final String XML_HTTP_REQUEST = "XMLHttpRequest";

    /**
     * 根据请求动态决定 loginUrl（只支持 /admin/login 与 /login 两种）
     */
    private String resolveLoginUrl(HttpServletRequest request) {
        if (this.isAdminRequest(request)) {
            return WebUtil.ADMIN_LOGIN_URL; // e.g. "/admin/login"
        }
        return WebUtil.LOGIN_URL; // e.g. "/login"
    }

    @Override
    protected boolean isLoginRequest(ServletRequest req, ServletResponse resp) {
        super.setLoginUrl(resolveLoginUrl((HttpServletRequest) req));
        return super.isLoginRequest(req, resp);
    }

    private HttpSession getSession(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return (s != null) ? s : req.getSession(true);
    }

    /**
     * 执行登录验证
     */
    @Override
    protected boolean executeLogin(ServletRequest servletRequest, ServletResponse servletResponse) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = getSession(request);
        String ip = WebUtil.getRemoteAddr(request);// 访问IP
        String loginName = this.getUsername(request);// 登录用户名
        String loginPwd = this.getPassword(request); // 登录用户密码
        boolean rememberMe = this.isRememberMe(request);// 记住我
        String captcha = WebUtils.getCleanParam(request, DEFAULT_CAPTCHA_PARAM);
        log.info("Access Username => {}, IP => {}", loginName, ip);
        // 错误次数
        Integer errorCount = (Integer) session.getAttribute(CAPTCHA_ERROR_COUNT_KEY);
        if (errorCount == null) errorCount = 0;
        // 提前保存 SavedRequest（因为我们可能会 logout() 清掉原 session）
        SavedRequest oldSavedRequest = WebUtils.getAndClearSavedRequest(request);
        // 构建 token（包含 RSA / 私钥逻辑）
        DefaultVerifyToken token = getToken(loginName, loginPwd, rememberMe, session);
        try {
            if (token == null) {
                throw new IllegalStateException("CreateToken method returned null");
            }
            // 验证码校验（仅管理员登录需要）
            if (isAdminRequest(request)) {
                token.setLoginType(LoginType.ADMIN.toString());
                Object code = session.getAttribute("code");
                if (code == null || captcha == null || !code.toString().equalsIgnoreCase(captcha)) {
                    session.setAttribute(CAPTCHA_ERROR_COUNT_KEY, errorCount + 1);
                    return onLoginFailure(token, new AuthenticationException("验证码错误"), request, servletResponse);
                } else {
                    session.removeAttribute(CAPTCHA_REQUIRED_KEY);
                    session.removeAttribute(CAPTCHA_ERROR_COUNT_KEY);
                }
            } else {
                token.setLoginType(LoginType.CUSTOM.toString());
            }
            Subject subject = getSubject(request, servletResponse);
            // 防止 session fixation：执行 logout 清理旧 session（我们已提前保存 oldSavedRequest）
            if (subject.getSession(false) != null) {
                subject.logout();
            }
            // 登录（login 会创建新的 session）
            subject.login(token);
            // 登录后把原来的 SavedRequest 写回新 session（如果有）
            if (oldSavedRequest != null) {
                try {
                    subject.getSession().setAttribute(WebUtils.SAVED_REQUEST_KEY, oldSavedRequest);
                } catch (Exception ex) {
                    log.warn("恢复 SavedRequest 失败: {}", ex.getMessage(), ex);
                }
            }
            subject.getSession().setAttribute("username", loginName);
            // 调用父类登录成功处理（会触发 issueSuccessRedirect）
            return super.onLoginSuccess(token, subject, request, servletResponse);
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage(), e);
            return onLoginFailure(token, new AuthenticationException("登录失败，请稍后重试"), servletRequest, servletResponse);
        }
    }

    /**
     * 修改 getToken：从 session 获取私钥（一次性），然后构建 DefaultVerifyToken
     */
    public static DefaultVerifyToken getToken(String username, String reqPwd, boolean rememberMe, HttpSession session) {
        DefaultVerifyToken token;
        try {
            RSAPrivateKey privateKey = (RSAPrivateKey) session.getAttribute("privateKey");
            if (privateKey == null) {
                log.error("RSA私钥不存在，请检查密钥生成逻辑");
                throw new AuthenticationException("系统错误，请刷新页面重试");
            }
            // 这里假设你在 DefaultVerifyToken 内部会处理解密，如果没有，需要自行解密
            token = new DefaultVerifyToken(username, reqPwd, rememberMe);
            // 一次性使用
            session.removeAttribute("privateKey");
        } catch (Exception e) {
            // 如果有问题也返回一个 token（按你的原逻辑）
            token = new DefaultVerifyToken(username, reqPwd, rememberMe);
        }
        return token;
    }

    /**
     * 登录失败回调：构建动态 loginUrl，并保留 redirect 参数（优先使用已传来的 redirect）
     * 支持AJAX请求返回JSON响应
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        log.debug("=== 登录失败 ===> {}", e.getMessage());
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession();
        session.setAttribute("errorMsg", getErrorMessage(e));
        // 如果是AJAX请求，返回JSON响应
        if (isAjaxRequest(req)) {
            handleAjaxResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "登录失败", getErrorMessage(e));
            return false;
        }

        try {
            String loginUrl = resolveLoginUrl(req);
            String redirectParam = req.getParameter("redirect");
            if (StringUtils.hasLength(redirectParam)) {
                loginUrl += "?redirect=" + java.net.URLEncoder.encode(redirectParam, "UTF-8");
            } else {
                SavedRequest savedRequest = WebUtils.getSavedRequest(request);
                if (savedRequest != null) {
                    String requestUrl = savedRequest.getRequestUrl();
                    if (StringUtils.hasLength(requestUrl) && !requestUrl.contains("/login")) {
                        loginUrl += "?redirect=" + java.net.URLEncoder.encode(requestUrl, "UTF-8");
                    }
                }
            }
            WebUtils.issueRedirect(req, response, loginUrl);
        } catch (Exception ex) {
            log.error("重定向失败: {}", ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
        return false;
    }

    /**
     * 登录成功后的重定向逻辑（redirect 参数优先 -> savedRequest -> successUrl）
     * 规则：如果原始请求是后台路径，统一跳转到后台首页 /admin/index
     * 支持AJAX请求返回JSON响应
     */
    @Override
    protected void issueSuccessRedirect(ServletRequest request, ServletResponse response) throws Exception {
        log.debug("=== 登录成功重定向 ===");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // 如果是AJAX请求，返回成功JSON
        if (isAjaxRequest(httpRequest)) {
            handleAjaxResponse(response, HttpServletResponse.SC_OK, "登录成功", null);
            return;
        }
        // 1. 优先使用 redirect 参数
        String redirectUrl = request.getParameter("redirect");
        if (StringUtils.hasLength(redirectUrl) && isValidRedirectUrl(redirectUrl)) {
            // 如果redirect参数是后台路径，统一跳转到后台首页
            if (isAdminPath(redirectUrl)) {
                redirectUrl = WebUtil.ADMIN_URL + WebUtil.ADMIN_MAIN_URL;
            }
            WebUtils.issueRedirect(request, response, redirectUrl, null, true);
            return;
        }
        // 2. 其次使用 SavedRequest（从 session 中拿并清除）
        SavedRequest savedRequest = WebUtils.getAndClearSavedRequest(request);
        if (savedRequest != null) {
            String savedUrl = savedRequest.getRequestUrl();
            if (isValidRedirectUrl(savedUrl)) {
                // 如果保存的请求是后台路径，统一跳转到后台首页
                if (isAdminPath(savedUrl)) {
                    savedUrl = WebUtil.ADMIN_URL + WebUtil.ADMIN_MAIN_URL;
                }
                WebUtils.issueRedirect(request, response, savedUrl, null, true);
                return;
            }
        }
        // 3. 根据当前请求判断是否是后台登录，设置默认成功页
        String successUrl = getSuccessUrl();
        if (isAdminPath(successUrl)) {
            successUrl = WebUtil.ADMIN_URL + WebUtil.ADMIN_MAIN_URL;
        }
        WebUtils.redirectToSavedRequest(request, response, successUrl);
    }

    /**
     * 判断URL是否是后台管理路径
     * 规则：以 /admin 开头，但不是登录页面的路径
     */
    private boolean isAdminPath(String url) {
        if (!StringUtils.hasLength(url)) {
            return false;
        }
        return url.startsWith("/admin") || url.startsWith("/admin/");
    }

    /**
     * 判断当前请求是否是后台请求
     */
    private boolean isAdminRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && (uri.startsWith("/admin") || uri.startsWith("/admin/"));
    }

    /**
     * 访问被拒绝时的处理（支持AJAX请求）
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();
        log.debug("=== 认证过滤器检查访问 ===> URI: {}", uri);
        // 如果是错误页面，直接允许访问
        if (uri.startsWith("/error") || "/403".equals(uri)) {
            log.debug("=== 错误页面，允许访问 ===");
            return true;
        }
        // 非AJAX请求，使用原有逻辑
        return super.onAccessDenied(request, response);
    }

    /**
     * 保存请求并跳转登录页（使用动态 loginUrl，并将原始请求作为 redirect 参数）
     * 支持AJAX请求返回JSON响应
     */
    @Override
    protected void saveRequestAndRedirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // 如果是AJAX请求，返回JSON响应
        if (isAjaxRequest(httpRequest)) {
            log.debug("=== AJAX请求重定向到登录，返回JSON响应 ===");
            handleAjaxResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "未登录或会话已过期", "请重新登录");
            return;
        }
        // 保存原始请求（Shiro 会在 session 中保存 SavedRequest）
        super.saveRequest(request);
        // 获取刚保存的请求
        SavedRequest savedRequest = WebUtils.getSavedRequest(request);
        // 动态 loginUrl
        String loginUrl = resolveLoginUrl(httpRequest);

        if (savedRequest != null) {
            String requestUrl = savedRequest.getRequestUrl();
            if (StringUtils.hasLength(requestUrl) && !requestUrl.contains("/login")) {
                try {
                    String encodedUrl = java.net.URLEncoder.encode(requestUrl, "UTF-8");
                    if (this.isAdminRequest(httpRequest)) {
                        loginUrl = WebUtil.ADMIN_LOGIN_URL + "?redirect=" + encodedUrl;
                    } else {
                        loginUrl = WebUtil.LOGIN_URL + "?redirect=" + encodedUrl;
                    }
                } catch (java.io.UnsupportedEncodingException e) {
                    log.warn("URL编码失败: {}", e.getMessage());
                }
            }
        }
        httpResponse.sendRedirect(loginUrl);
    }

    /**
     * 简单检查重定向地址的安全性（防止开放重定向）
     */
    private boolean isValidRedirectUrl(String url) {
        if (!StringUtils.hasLength(url)) return false;
        try {
            String lower = url.toLowerCase();
            if (lower.startsWith("http:") || lower.startsWith("https:")) return false;
            if (url.contains("/login")) return false;
            if (url.contains("..")) return false;
            return url.startsWith("/");
        } catch (Exception e) {
            log.warn("重定向URL验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 将 AuthenticationException 映射为友好消息
     */
    private String getErrorMessage(AuthenticationException e) {
        if (e instanceof LockedAccountException) return "账户已被锁定";
        if (e instanceof IncorrectCredentialsException) return "用户名或密码错误";
        if (e instanceof CredentialsException) return "登录失败，请检查用户名和密码";
        if (e instanceof DisabledAccountException) return "账户已被禁用";
        if (e instanceof ExcessiveAttemptsException) return "登录失败次数过多，请稍后再试";
        if (e instanceof UnknownAccountException) return "用户名不存在";
        return e.getMessage() != null ? e.getMessage() : "登录失败，请稍后重试";
    }

    /**
     * 判断是否为AJAX请求
     */
    private boolean isAjaxRequest(HttpServletRequest request) {
        return XML_HTTP_REQUEST.equals(request.getHeader(AJAX_HEADER)) || "application/json".equals(request.getContentType()) || "application/json".equals(request.getHeader("Accept")) || (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));
    }

    /**
     * 处理AJAX响应，返回JSON格式数据
     */
    private void handleAjaxResponse(ServletResponse response, int code, String msg, Object obj) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setStatus(code == 0 ? HttpServletResponse.SC_OK : code);
        httpResponse.setContentType("application/json;charset=UTF-8");
        httpResponse.setCharacterEncoding("UTF-8");

        try (PrintWriter out = httpResponse.getWriter()) {
            HashMap<String, Object> json = new HashMap<String, Object>();
            json.put("code", code);
            json.put("msg", msg);
            json.put("data", obj);
            if (code == HttpServletResponse.SC_UNAUTHORIZED) {
                json.put("redirect", true); // 告诉前端需要重定向到登录页
            }
            StringBuilder sb = new StringBuilder("{");
            for (String key : json.keySet()) {
                sb.append("\"").append(key).append("\":\"").append(json.get(key)).append("\",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("}");
            out.print(sb.toString());
            out.flush();
        } catch (IOException e) {
            log.error("写入AJAX响应失败: {}", e.getMessage(), e);
        }
    }
}