package com.vita.im.ws.manager;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocketSession 管理类
 */
@Component
public class WsSessionManager {
    // userId -> WebSocketSession
    private final Map<String, WebSocketSession> SESSION_MAP = new ConcurrentHashMap<>();

    // 连接管理方法（复用之前的）
    public void addSession(String userId, WebSocketSession session) {
        SESSION_MAP.put(userId, session);
    }

    public void removeSession(String userId) {
        SESSION_MAP.remove(userId);
    }

    public WebSocketSession getSession(String userId) {
        return SESSION_MAP.get(userId);
    }

    public Map<String, WebSocketSession> getAllSessions() {
        return new ConcurrentHashMap<>(SESSION_MAP);
    }

    public Integer getSessionCount() {
        return SESSION_MAP.size();
    }
}