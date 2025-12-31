package com.vita.im.ws;

import com.google.gson.Gson;
import com.vita.common.domain.im.MessageBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 服务器端点
 * 支持文本、语音、文件传输
 */
@Slf4j
@Component
@ServerEndpoint("/ws/{userId}")
public class WebSocketServer {

    /**
     * 在线用户会话列表
     */
    private static final Map<String, Session> wsSessions = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用
     */
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        wsSessions.put(userId, session);
        System.out.println("用户[" + userId + "]已连接，当前在线人数：" + wsSessions.size());

        MessageBO messageBO = new MessageBO().setContent("用户" + userId + "已上线！");
        broadcastMessage(new Gson().toJson(messageBO));
    }

    /**
     * 接收客户端消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        MessageBO messageBO = new Gson().fromJson(message, MessageBO.class);
        System.out.println("用户[" + messageBO.getFrom() + "]已发送消息：" + message);

        // 广播消息给所有在线用户
        broadcastMessage(message);
    }

    /**
     * 连接关闭调用
     */
    @OnClose
    public void onClose(@PathParam("userId") String userId, Session session) {
        wsSessions.remove(userId);
        System.out.println("用户[" + userId + "]已断开连接，当前在线人数：" + wsSessions.size());

        // 广播消息给所有在线用户
        MessageBO messageBO = new MessageBO().setContent("用户" + userId + "已下线！");
        broadcastMessage(new Gson().toJson(messageBO));
    }

    /**
     * 发生错误调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("[WebSocket] 发生错误：{}", error.getMessage(), error);
    }

    /**
     * 广播消息给所有在线用户
     */
    private void broadcastMessage(String message) {
        wsSessions.values().forEach(session -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("[WebSocket] 广播消息失败，原因：{}", e.getMessage(), e);
            }
        });
    }
}