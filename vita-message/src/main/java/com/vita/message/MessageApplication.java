package com.vita.message;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 消息服务（短信、邮件、消息通知等）
 *
 * @author edward
 */
@Slf4j
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.vita.message")
@EnableFeignClients(basePackages = "com.vita.message.client")
@MapperScan("com.vita.message.mapper")
public class MessageApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessageApplication.class, args);
        log.info("============================================================================");
        log.info("~~ 消息中心服务 启动成功 ~~");
        log.info("============================================================================");
    }
}
