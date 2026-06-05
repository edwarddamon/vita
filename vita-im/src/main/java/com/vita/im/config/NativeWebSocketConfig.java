package com.vita.im.config;

import com.google.gson.Gson;
import com.vita.common.client.AuthCenterClient;
import com.vita.common.domain.Result;
import com.vita.common.domain.user.UserInfoVO;
import com.vita.common.exception.ServiceException;
import com.vita.common.utils.AssertUtil;
import com.vita.im.domain.ImConstant;
import com.vita.im.ws.handler.WebSocketHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Objects;

/**
 * 原生 WebSocket 配置（支持文本+二进制消息）
 */
@Slf4j
@Configuration
@EnableWebSocket
public class NativeWebSocketConfig implements WebSocketConfigurer {

    @Resource
    private AuthCenterClient authCenterClient;

    private final WebSocketHandler webSocketHandler;

    public NativeWebSocketConfig(WebSocketHandler WebSocketHandler) {
        this.webSocketHandler = WebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws/{token}")
                .setAllowedOriginPatterns("*") // 生产环境替换为具体域名
                .addInterceptors(handshakeInterceptor()); // 兼容低版本浏览器（可选）
    }

    /**
     * 握手拦截器：从路径变量获取 token，调用认证中心获取 userId，存入 Session
     */
    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new HttpSessionHandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(org.springframework.http.server.ServerHttpRequest request,
                                           org.springframework.http.server.ServerHttpResponse response,
                                           org.springframework.web.socket.WebSocketHandler wsHandler,
                                           java.util.Map<String, Object> attributes) throws Exception {

                String token = extractTokenFromPath(request);
                AssertUtil.notBlankStr(token, "请求路径中未查询到当前用户token信息");

                UserInfoVO userInfoVO = authenticateToken(token);
                attributes.put(ImConstant.USER_ID, userInfoVO.getUserId());
                attributes.put(ImConstant.CURRENT_USER, new Gson().toJson(userInfoVO));

                return super.beforeHandshake(request, response, wsHandler, attributes);
            }

            /**
             * 从请求路径中提取 token
             */
            private String extractTokenFromPath(org.springframework.http.server.ServerHttpRequest request) {
                String requestPath = request.getURI().getPath();
                // 路径格式: /ws/{token}
                return requestPath.substring(requestPath.lastIndexOf("/") + 1);
            }

            /**
             * 通过 token 调用认证中心获取 userId
             */
            private UserInfoVO authenticateToken(String token) {
                Result<UserInfoVO> userInfoVOResult = authCenterClient.describeUserInfo(token);
                if (!userInfoVOResult.isSuccess() || Objects.isNull(userInfoVOResult.getData())) {
                    log.error("[ws握手] 用户token: [{}] 获取用户信息失败，用户信息: {}", token, userInfoVOResult.getData());
                    throw new ServiceException("用户token: [" + token + "] 获取用户信息失败");
                }

                return userInfoVOResult.getData();
            }
        };
    }
}