package com.vita.message;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 消息服务（短信、邮件、消息通知等）
 *
 * @author edward
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.vita.message")
@MapperScan("com.vita.message.mapper")
public class MessageApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessageApplication.class, args);
    }
}
