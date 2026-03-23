package com.fit.config.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.config.Ini;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author AIM
 * @Des
 * @DATE 2018/1/31
 */
@Slf4j
@Configuration
public class ShiroConfig {

    /**
     * @Des 加载Shiro Ini配置
     */
    @Bean
    public Ini shiroIni() {
        Ini ini = new Ini();
        ini.loadFromPath("classpath:shiro.ini");
        return ini;
    }

    /**
     * @Des 获取主配置部分
     */
    @Bean
    public Ini.Section shiroMainSection() {
        return shiroIni().getSection("main");
    }

    /**
     * @Des EhCache管理器
     */
    @Bean("shiroEhcacheManager")
    public EhCacheManager ehCacheManager() throws IOException {
        log.debug("注入Shiro的Web过滤器-->ehCacheManager");
        EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile(shiroMainSection().get("shiroCacheManagerConfigFile"));
        return cacheManager;
    }

    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("MD5");
        if (shiroMainSection().containsKey("shiroHashCount")) {
            matcher.setHashIterations(Integer.parseInt(shiroMainSection().get("shiroHashCount")));
        } else {
            matcher.setHashIterations(2);
        }
        matcher.setStoredCredentialsHexEncoded(true);
        return matcher;
    }

    /**
     * @Des 创建认证凭证
     */
    @Bean("shiroRealm")
    public UserRealm userRealm() {
        log.debug("注入Shiro的Web过滤器-->userRealm");
        UserRealm userRealm = new UserRealm();
        userRealm.setCredentialsMatcher(hashedCredentialsMatcher());
//        // 启用缓存
//        userRealm.setCachingEnabled(true);
//        // 启用身份验证缓存
//        userRealm.setAuthenticationCachingEnabled(true);
//        userRealm.setAuthenticationCacheName("authenticationCache");
//        // 启用授权缓存
//        userRealm.setAuthorizationCachingEnabled(true);
//        userRealm.setAuthorizationCacheName("authorizationCache");
        return userRealm;
    }

    /**
     * cookie对象;
     * rememberMeCookie()方法是设置Cookie的生成模版，比如cookie的name，cookie的有效时间等等。
     */
    @Bean
    public SimpleCookie rememberMeCookie() throws IOException {
        //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        //<!-- 记住我cookie生效时间30天 ,单位秒;-->
        if (shiroMainSection().containsKey("rememberMeCookie")) {
            simpleCookie.setMaxAge(Integer.parseInt(shiroMainSection().get("rememberMeCookie")));
        } else {
            simpleCookie.setMaxAge(259200);
        }
        return simpleCookie;
    }

    /**
     * cookie管理对象;
     * rememberMeManager()方法是生成rememberMe管理器，而且要将这个rememberMe管理器设置到securityManager中
     */
    @Bean
    public CookieRememberMeManager rememberMeManager() throws IOException {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
        String base64Encoded = shiroMainSection().get("base64Encoded");
        cookieRememberMeManager.setCipherKey(Base64.decode(base64Encoded));
        return cookieRememberMeManager;
    }


    /**
     * @Des 配置会话管理器 - 关键修复
     */
    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // 使用jsessionid存储会话ID
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        // 使用Cookie存储会话ID
        sessionManager.setSessionIdCookieEnabled(true);
        // 设置会话超时时间（可选）
        sessionManager.setGlobalSessionTimeout(1800000); // 30分钟
        return sessionManager;
    }

    /**
     * @Des 不指定名字的话，自动创建一个方法名第一个字母小写的bean
     */
    @Bean("securityManager")
    public SecurityManager securityManager() throws IOException {
        log.debug("注入Shiro的Web过滤器-->securityManager");
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //设置Realm，用于获取认证凭证
        securityManager.setRealm(userRealm());
        // 注入会话管理器
        securityManager.setSessionManager(sessionManager());
        //注入EhCacheManager缓存管理器
        securityManager.setCacheManager(ehCacheManager());//这个如果执行多次，也是同样的一个对象;
        //注入记住我管理器
        securityManager.setRememberMeManager(rememberMeManager());
        return securityManager;
    }

    /**
     * @Des Shiro的Web过滤器Factory 命名:shiroFilter<br />
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(BeanFactory beanFactory) throws IOException {
        log.debug("注入Shiro的Web过滤器-->shiroFilter", ShiroFilterFactoryBean.class);
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //shiro的核心安全接口,这个属性是必须的
        shiroFilterFactoryBean.setSecurityManager(securityManager());
        //登录成功后要跳转的连接,逻辑也可以自定义，例如返回上次请求的页面
        //shiroFilterFactoryBean.setSuccessUrl("/index");
        //用户访问未对其授权的资源时,所显示的连接
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        //定义shiro过滤器
        Map<String, Filter> filters = new LinkedHashMap<String, Filter>();
        filters.put("authc", new DefaultAuthenticationFilter());
        filters.put("user", new DefaultUserFilter());
        shiroFilterFactoryBean.setFilters(filters);
        /* 定义shiro过滤链 Map结构
         * Map中key(xml中是指value值)的第一个'/'代表的路径是相对于HttpServletRequest.getContextPath()的值来的
         * anon：它对应的过滤器里面是空的,什么都没做,这里.do和.jsp后面的*表示参数,比方说login.jsp?main这种
         * authc：该过滤器下的页面必须验证后才能访问,它是Shiro内置的一个拦截器org.apache.shiro.web.filter.authc.FormAuthenticationFilter
         */
        shiroFilterFactoryBean.setFilterChainDefinitionMap(shiroIni().getSection("urls"));

        return shiroFilterFactoryBean;
    }


    /**
     * Shiro生命周期处理器 * @return
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证 * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能 * @return
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}