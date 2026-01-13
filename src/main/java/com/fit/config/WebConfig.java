package com.fit.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @AUTO web 配置类
 * @Author AIM
 * @DATE 2019/4/24
 */
@EnableAsync
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:/uploads}")
    private String uploadDir;

    @Value("${file.static-access-path:/uploads/**}")
    private String staticAccessPath;

    /**
     * 访问根路径默认跳转 index.html页面 （简化部署方案： 可以把前端打包直接放到项目的 webapp，上面的配置）
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
    }

    /**
     * 添加静态资源
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/favicon.ico");
        String uploadPath = String.format("%s/%s", System.getProperty("user.dir"), uploadDir);
        String currentDir = System.getProperty("user.dir");
        if (uploadDir.startsWith("./") || uploadDir.startsWith(".\\")) {
            uploadPath = String.format("file:%s/%s/", currentDir, uploadDir.substring(2));
        } else if (uploadDir.startsWith("/") || uploadDir.matches("^[A-Za-z]:.*")) {
            // 绝对路径（Linux 或 Windows）
            uploadPath = String.format("file:%s/", uploadDir);
        } else {// 默认相对路径
            uploadPath = String.format("file:%s/%s/", currentDir, uploadDir);
        }
        registry.addResourceHandler(staticAccessPath).addResourceLocations(uploadPath);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang"); // 请求参数中的 lang 用于切换语言
        registry.addInterceptor(interceptor);
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) {
                req.setAttribute("ctx", req.getContextPath());
                if (req.getSession().getAttribute("user") != null) {
                    req.setAttribute("user", req.getSession().getAttribute("user"));
                }
                return true;
            }
        }).order(1).addPathPatterns("/**"); // 设置拦截的路径
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        /* 是否通过请求Url的扩展名来决定media type */
        configurer.favorPathExtension(true).favorParameter(false)
                /* 不检查Accept请求头 */.ignoreAcceptHeader(false).parameterName("mediaType")
                /* 设置默认的media */.defaultContentType(MediaType.TEXT_HTML)
                /* 请求以.html*/.mediaType("html", MediaType.TEXT_HTML)
                /* 请求以.json*/.mediaType("json", MediaType.APPLICATION_JSON);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //定义一个convert转换消息的对象
        FastJsonConfig config = new FastJsonConfig();
        config.setDateFormat("yyyy-MM-dd HH:mm:ss");
        config.setSerializerFeatures(
                // 保留map空的字段
                SerializerFeature.WriteMapNullValue,
                // 将String类型的null转成""
                SerializerFeature.WriteNullStringAsEmpty,
                // 将Number类型的null转成0
                SerializerFeature.WriteNullNumberAsZero,
                // 将List类型的null转成[]
                SerializerFeature.WriteNullListAsEmpty,
                // 将Boolean类型的null转成false
                SerializerFeature.WriteNullBooleanAsFalse,
                // 避免循环引用
                SerializerFeature.DisableCircularReferenceDetect);
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        //在convert中添加配置信息
        fastConverter.setFastJsonConfig(config);
        //设置支持的媒体类型
        fastConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8));
        //设置默认字符集
        fastConverter.setDefaultCharset(StandardCharsets.UTF_8);
        //将convert添加到converters
        converters.add(0, fastConverter);
        //解决返回字符串带双引号问题
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.TEXT_HTML));
        stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
        converters.add(stringHttpMessageConverter);
    }
}