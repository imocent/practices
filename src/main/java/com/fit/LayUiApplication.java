package com.fit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication
public class LayUiApplication {

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext run = SpringApplication.run(LayUiApplication.class, args);
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = System.getenv("POST");
        ConfigurableEnvironment environment = run.getEnvironment();
        if (StringUtils.isEmpty(port)) {
            port = environment.getProperty("server.port");
        }
        String path = environment.getProperty("server.servlet.context-path", "").replace("/", "");
        log.info("---------------------------------------------------------");
        log.info("Access URLs:");
        log.info("\tLocal: \t\thttp://localhost:{}/{}", port, path);
        log.info("\tExternal:\t{}://{}:{}/{}", "http", ip, port, path);
        log.info("---------------------- admin-web ------------------------");
    }
}