package com.vita.common.controller;

import cn.hutool.core.util.StrUtil;
import com.vita.common.domain.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查
 *
 * @since 2023/06/01 17:43
 */
@Slf4j
@RestController
@RequestMapping(value = "/health")
public class HealthController {

    @Value(value = "${spring.application.name}")
    private String serviceName;

    private static final String HEALTHY_MESSAGE = " is healthy";

    @GetMapping(value = "/check")
    public Result<String> healthCheck() {
        log.info("健康检查 >>>>>> {}{}", serviceName, HEALTHY_MESSAGE);
        return Result.success(StrUtil.builder(serviceName, HEALTHY_MESSAGE).toString());
    }

}