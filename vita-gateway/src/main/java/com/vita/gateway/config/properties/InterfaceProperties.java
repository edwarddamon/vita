package com.vita.gateway.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "interface")
public class InterfaceProperties {

    /**
     * 白名单接口
     */
    private List<String> whiteList;

    /**
     * url token 转为 header 接口
     */
    private List<String> urlTokenToHeaderList;
}
