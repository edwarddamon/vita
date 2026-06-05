package com.vita.im;

import lombok.extern.slf4j.Slf4j;
//import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * IM服务（即时通讯）
 *
 * @author edward
 */
@Slf4j
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.vita.im")
//@EnableFeignClients(basePackages = "com.vita.im.client")
//@MapperScan("com.vita.message.mapper")
public class IMApplication {
    public static void main(String[] args) {
        SpringApplication.run(IMApplication.class, args);
        log.info("============================================================================");
        log.info("~~ IM即时通讯服务 启动成功 ~~");
        log.info("============================================================================");
    }
}
