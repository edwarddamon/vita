package com.vita;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Edward
 * @date 2022-11-27 20:34
 */
@EnableDiscoveryClient
@SpringBootApplication
public class CertificationAuthorityApplication {
    public static void main(String[] args) {
        SpringApplication.run(CertificationAuthorityApplication.class, args);
    }
}
