package com.vita.gateway.filter;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vita.common.domain.ResultCode;
import com.vita.common.domain.common.constant.HttpConstant;
import com.vita.common.domain.common.redis.RedisKeyDictionary;
import com.vita.common.domain.user.InterfaceInfoBO;
import com.vita.common.domain.user.UserInfoBO;
import com.vita.common.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Edward
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter, Ordered {

    public static final String AUTHORIZATION_PREFIX = "vita.";
    public static final String EMPTY = "";
    public static final String API_DOCS = "api-docs";
    private static final String HEATH_CHECK = "/health/check";

    private final StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        final ServerHttpRequest request = exchange.getRequest();
        final AntPathMatcher antPathMatcher = new AntPathMatcher();

        // 校验是否为无需鉴权的接口（swagger、健康检查、无需鉴权的接口）
        boolean unAuth = validateUnAuth(request, antPathMatcher);
        if (unAuth) {
            return chain.filter(exchange);
        }

        // 校验用户权限
        UserInfoBO userInfoBO = validateUserAUth(request, antPathMatcher);

        // 用户信息存请求头
        final ServerHttpRequest finalRequest = request.mutate()
                .header(HttpConstant.USER_INFO, new Gson().toJson(userInfoBO.setInterfaceInfoBos(null)))
                .build();
        return chain.filter(exchange.mutate().request(finalRequest).build());
    }

    private UserInfoBO validateUserAUth(ServerHttpRequest request, AntPathMatcher antPathMatcher) {
        // 校验用户是否登录
        final List<String> headerToken = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(headerToken)) {
            log.warn("[authFilter] 用户未登录");
            throw new ServiceException(ResultCode.UNAUTHORIZED);
        }

        // 校验用户是否登录
        String token = headerToken.get(0).replace(AUTHORIZATION_PREFIX, EMPTY);
        final String userInfoString = redisTemplate.opsForValue().get(RedisKeyDictionary.USER_TOKEN + token);
        if (!StringUtils.hasLength(userInfoString)) {
            log.warn("[authFilter] 未查询到该用户的登录信息或已过期, token: {}", token);
            throw new ServiceException(ResultCode.UNAUTHORIZED);
        }

        // 校验登录用户是否有接口权限
        UserInfoBO userInfoBO = new Gson().fromJson(userInfoString, UserInfoBO.class);
        final List<InterfaceInfoBO> authInterfaces = userInfoBO.getInterfaceInfoBos();
        if (CollectionUtils.isEmpty(authInterfaces)) {
            log.warn("[authFilter] 该用户接口权限为空, token: {}, 用户昵称: {}, 手机号：{}", token, userInfoBO.getUsername(), userInfoBO.getPhone());
            throw new ServiceException(ResultCode.NO_INTERFACE_PERMISSION);
        }

        // 校验是否有该接口的权限
        boolean authMatch = authInterfaces.stream()
                .anyMatch(item -> antPathMatcher.match(item.getPath(), request.getURI().getPath()));
        if (!authMatch) {
            log.error("[authFilter] 该用户无该接口权限, token: {}, 用户昵称: {}, 手机号：{}", token, userInfoBO.getUsername(), userInfoBO.getPhone());
            throw new ServiceException(ResultCode.NO_INTERFACE_PERMISSION);
        }

        return userInfoBO;
    }

    private boolean validateUnAuth(ServerHttpRequest request, AntPathMatcher antPathMatcher) {
        String path = request.getURI().getPath();

        // swagger && 健康检查
        if (path.contains(API_DOCS) || path.contains(HEATH_CHECK)) {
            return true;
        }

        // 无需鉴权的接口
        final String notNeedAuthInterfaces = redisTemplate.opsForValue().get(RedisKeyDictionary.NOT_NEED_AUTH_INTERFACE);
        if (StringUtils.hasLength(notNeedAuthInterfaces)) {
            List<InterfaceInfoBO> notNeedAuthInterfaceList = new Gson().fromJson(notNeedAuthInterfaces, new TypeToken<List<InterfaceInfoBO>>() {
            }.getType());
            return notNeedAuthInterfaceList.stream().anyMatch(item -> antPathMatcher.match(item.getPath(), path));
        }

        return false;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 10;
    }
}
