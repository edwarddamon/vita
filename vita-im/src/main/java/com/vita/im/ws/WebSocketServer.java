package com.vita.im.ws;

import com.google.gson.Gson;
import com.vita.common.domain.im.MessageBO;
import com.vita.common.domain.im.enums.MsgStatusEnum;
import com.vita.common.domain.im.enums.MsgTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
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
        log.info("用户[{}]已连接，当前在线人数：{}", userId, wsSessions.size());
    }

    /**
     * 接收客户端消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException, InterruptedException {
        MessageBO messageBO = new Gson().fromJson(message, MessageBO.class);

        // 消息发送结果通知 - 成功
        sendMessage(
                session,
                new MessageBO()
                        .setId(messageBO
                        .getId())
                        .setType(MsgTypeEnum.ACK.name())
                        .setStatus(MsgStatusEnum.SUCCESS.name()));

        // 私聊
//        Session toSession = wsSessions.get(messageBO.getReceiverId());
//        if (Objects.isNull(toSession)) {
//            messageBO.setContent("用户" + messageBO.getReceiverId() + "已下线！");
//            messageBO.setSenderId("system");
//            sendMessage(session, messageBO);
//            return;
//        }
//
//        sendMessage(toSession, messageBO);
        broadcastMessageExceptSender(session, messageBO);
    }

    /**
     * 连接关闭调用
     */
    @OnClose
    public void onClose(@PathParam("userId") String userId, Session session) {
        wsSessions.remove(userId);
        log.info("用户[{}]已断开连接，当前在线人数：{}", userId, wsSessions.size());
    }

    /**
     * 发生错误调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        wsSessions.remove(session.getId());
        log.info("用户[{}] 发生错误，已断开连接，当前在线人数：{}", session.getId(), wsSessions.size());
    }


    /**
     * 给指定用户发送消息
     */
    private void sendMessage(Session session, MessageBO message) {
        try {
            message.updateTime();
            session.getBasicRemote().sendText(new Gson().toJson(message));
        } catch (IOException e) {
            log.error("[WebSocket-1v1] 广播消息失败，原因：{}", e.getMessage(), e);
        }
    }

    /**
     * 广播消息给所有在线用户
     */
    private void broadcastMessage(MessageBO message) {
        String messageStr = new Gson().toJson(message);
        wsSessions.values().forEach(session -> {
            try {
                session.getBasicRemote().sendText(messageStr);
            } catch (IOException e) {
                log.error("[WebSocket-broadcast] 广播消息失败，原因：{}", e.getMessage(), e);
            }
        });
    }

    /**
     * 广播消息给所有在线用户（除了消息发送方）
     */
    private void broadcastMessageExceptSender(Session session, MessageBO message) {
        message.updateTime();
        String messageStr = new Gson().toJson(message);
        wsSessions.values().forEach(s -> {
            if (!s.getId().equals(session.getId())) {
                try {
                    s.getBasicRemote().sendText(messageStr);
                } catch (IOException e) {
                    log.error("[WebSocket-broadcast] 广播消息失败，原因：{}", e.getMessage(), e);
                }
            }
        });
    }
}