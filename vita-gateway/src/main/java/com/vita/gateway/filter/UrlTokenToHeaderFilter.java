package com.vita.gateway.filter;

import com.vita.gateway.config.properties.InterfaceProperties;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Edward
 */
@Component
@RequiredArgsConstructor
public class UrlTokenToHeaderFilter implements GlobalFilter, Ordered {

    private final InterfaceProperties interfaceProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestPath = exchange.getRequest().getURI().getPath();

        final AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean needFlag = validateNeedUrlTokenToHeader(antPathMatcher, requestPath);
        if (!needFlag) {
            return chain.filter(exchange);
        }

        // 从 url 中获取 token -> 将token放入请求头中（取url上最后一个参数作为token）
        String token = requestPath.substring(requestPath.lastIndexOf("/") + 1);
        ServerHttpRequest modifiedReq = exchange.getRequest().mutate()
                .header(HttpHeaders.AUTHORIZATION, token)
                .build();
        return chain.filter(exchange.mutate().request(modifiedReq).build());
    }

    /**
     * 校验是否需要将url token 转为 header
     */
    private boolean validateNeedUrlTokenToHeader(AntPathMatcher antPathMatcher, String requestPath) {
        List<String> urlTokenToHeaderList = interfaceProperties.getUrlTokenToHeaderList();
        if (CollectionUtils.isEmpty(urlTokenToHeaderList)) {
            return false;
        }
        return urlTokenToHeaderList.stream()
                .anyMatch(item -> antPathMatcher.match(item, requestPath));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
