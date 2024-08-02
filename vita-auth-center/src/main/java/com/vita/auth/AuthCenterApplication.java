package com.vita.auth;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 认证服务
 *
 * @author edward
 */
@Slf4j
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.vita.auth")
@MapperScan("com.vita.auth.mapper")
public class AuthCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthCenterApplication.class, args);
        log.info("============================================================================");
        log.info("~~ 认证中心服务 启动成功 ~~");
        log.info("============================================================================");
    }
}
