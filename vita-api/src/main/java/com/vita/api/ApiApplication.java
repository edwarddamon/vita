package com.vita.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 外部接口服务
 *
 * @author edward
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.vita.api")
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
