package com.vita.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关服务
 *
 * @author edward
 */
@Slf4j
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.vita.gateway")
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        log.info("============================================================================");
        log.info("~~ 网关服务 启动成功 ~~");
        log.info("============================================================================");
    }
}
