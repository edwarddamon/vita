package com.vita.common.config;

import com.vita.common.interceptor.CommonFeignInterceptor;
import feign.Feign;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

/**
 * feign 配置
 *
 * @author Edward
 * @date 2024-02-28 17:25
 */
@ConditionalOnClass(value = {Feign.class})
@Import(value = CommonFeignInterceptor.class)
@EnableFeignClients(basePackages = "com.vita.common.client")
public class FeignAutoConfiguration {
}
