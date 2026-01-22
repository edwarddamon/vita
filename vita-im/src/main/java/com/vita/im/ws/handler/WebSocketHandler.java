package com.vita.im.ws.handler;

import cn.hutool.core.convert.Convert;
import com.google.gson.Gson;
import com.vita.common.domain.im.MessageBO;
import com.vita.common.domain.im.enums.MsgStatusEnum;
import com.vita.common.domain.user.UserInfoVO;
import com.vita.im.domain.ImConstant;
import com.vita.im.ws.manager.WsSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * 增强版处理器：支持文本+二进制消息（继承 BinaryWebSocketHandler）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends BinaryWebSocketHandler {

    private final WsSessionManager sessionManager;

    /**
     * 连接建立：绑定 userId 和 Session
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = Convert.toStr(session.getAttributes().get(ImConstant.USER_ID));
        UserInfoVO currentUser = new Gson().fromJson(Convert.toStr(session.getAttributes().get(ImConstant.CURRENT_USER)), UserInfoVO.class);
        if (Objects.isNull(userId)) {
            session.close();
            return;
        }

        sessionManager.addSession(userId, session);
        log.info("用户id: [{}], 用户名: [{}] 已连接，当前在线人数：{}", userId, currentUser.getUsername(), sessionManager.getSessionCount());
    }

    /**
     * 处理文本消息（核心：文本/文件元信息）
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {
        MessageBO messageBO = new Gson().fromJson(textMessage.getPayload(), MessageBO.class);

        // 消息确认（通知消息发送者）
        sendMessageToUser(messageBO.getSenderId(), MessageBO.generateAckMsg(messageBO.getId(), MsgStatusEnum.SUCCESS));

        // 广播消息（除了消息发送者）
        broadcastMessageExceptSender(messageBO.getSenderId(), messageBO);


        // 1. 文本消息：直接转发
//        if (MsgTypeEnum.TEXT.name().equalsIgnoreCase(messageBO.getType())) {
//            // 默认群发
//            sendMessageToUser(toUserId, message);
//            // 群聊逻辑：if ("group".equals(toUserId)) broadcastMessage(message);
//        }

//        // 2. 文件元信息：初始化分片计数
//        else if ("file_meta".equals(message.getType())) {
//            ImMessage.FileMeta fileMeta = message.getFileMeta();
//            sessionManager.initChunkCounter(fileMeta.getFileId(), fileMeta.getTotalChunks());
//            // 回复前端：元信息接收成功，可开始传分片
//            ImMessage resp = new ImMessage();
//            resp.setType("text");
//            resp.setFromUserId("system");
//            resp.setToUserId(fromUserId);
//            resp.setContent("文件元信息接收成功，开始上传分片");
//            sendMessageToUser(fromUserId, resp);
//        }
    }

    /**
     * 处理二进制消息（核心：音视频分片）
     */
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage binaryMessage) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        if (userId == null) return;

        // 1. 解析二进制消息中的分片元信息（前端需先传 fileId + chunkIndex，再传二进制数据）
        // 注：二进制消息无法直接带元信息，前端需先通过文本消息发送 file_meta，再按顺序发分片
        // 此处简化：通过 Session 属性获取当前上传的 fileId（前端可通过文本消息指定）
//        String fileId = (String) session.getAttributes().get("currentFileId");
//        if (fileId == null) {
//            sendTextToUser(userId, "未指定文件ID，请先发送文件元信息");
//            return;
//        }
//
//        // 2. 保存当前分片
//        ByteBuffer byteBuffer = binaryMessage.getPayload();
//        ImMessage.FileMeta fileMeta = new ImMessage.FileMeta();
//        fileMeta.setFileId(fileId);
//        fileMeta.setChunkIndex(sessionManager.getChunkCount(fileId));
//        // 分片文件命名：fileId_chunkIndex.chunk
//        File chunkFile = new File(fileStorageUtil.initChunkFile(fileId), fileId + "_" + fileMeta.getChunkIndex() + ".chunk");
//        fileStorageUtil.saveChunk(chunkFile, byteBuffer.asInputStream(), byteBuffer.remaining());
//
//        // 3. 分片计数，判断是否上传完成
//        int currentChunkCount = sessionManager.incrementChunkCounter(fileId);
//        int totalChunks = (int) session.getAttributes().get("totalChunks");
//        if (currentChunkCount == totalChunks) {
//            // 4. 拼接分片，生成访问链接
//            fileMeta.setFileName((String) session.getAttributes().get("fileName"));
//            fileMeta.setFileType((String) session.getAttributes().get("fileType"));
//            fileMeta.setFileSize((long) session.getAttributes().get("fileSize"));
//            String fileUrl = fileStorageUtil.mergeChunks(fileMeta);
//
//            // 5. 推送文件链接给接收方
//            ImMessage fileMsg = new ImMessage();
//            fileMsg.setType(fileMeta.getFileType().startsWith("audio") ? "audio" : "video");
//            fileMsg.setFromUserId(userId);
//            fileMsg.setToUserId((String) session.getAttributes().get("toUserId"));
//            fileMsg.getFileMeta().setFileUrl(fileUrl);
//            fileMsg.getFileMeta().setFileName(fileMeta.getFileName());
//            sendMessageToUser(fileMsg.getToUserId(), fileMsg);
//
//            // 6. 清理临时数据
//            sessionManager.removeChunkCounter(fileId);
//            session.getAttributes().remove("currentFileId");
//            session.getAttributes().remove("totalChunks");
//            session.getAttributes().remove("fileName");
//            session.getAttributes().remove("fileType");
//            session.getAttributes().remove("fileSize");
//            session.getAttributes().remove("toUserId");
//
//            // 7. 通知上传方完成
//            sendTextToUser(userId, "文件上传完成，链接：" + fileUrl);
//        } else {
//            // 通知上传方当前进度
//            sendTextToUser(userId, "分片 " + currentChunkCount + "/" + totalChunks + " 上传成功");
//        }
    }

    /**
     * 连接关闭：清理资源
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        String userId = (String) session.getAttributes().get(ImConstant.USER_ID);
        if (userId != null) {
            sessionManager.removeSession(userId);
        }
    }

    /**
     * 异常处理
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
        if (session.isOpen()) {
            session.close();
        }
        String userId = (String) session.getAttributes().get(ImConstant.USER_ID);
        if (userId != null) {
            sessionManager.removeSession(userId);
        }
    }


    /**
     * 发送结构化消息给指定用户
     */
    public void sendMessageToUser(String userId, MessageBO message) {
        message.updateTime();
        WebSocketSession session = sessionManager.getSession(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(new Gson().toJson(message)));
            } catch (IOException e) {
                throw new RuntimeException("发送消息失败：" + userId, e);
            }
        }
    }

    /**
     * 广播消息（群聊）
     */
    public void broadcastMessage(MessageBO message) {
        message.updateTime();
        for (Map.Entry<String, WebSocketSession> entry : sessionManager.getAllSessions().entrySet()) {
            WebSocketSession session = entry.getValue();
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(new Gson().toJson(message)));
                } catch (IOException e) {
                    log.error("[WebSocket-broadcastMessage] 发送失败，消息接收方：[{}]，原因：{}",
                            session.getAttributes().get(ImConstant.USER_ID), e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 广播消息（群聊，排除发送方）
     */
    public void broadcastMessageExceptSender(String userId, MessageBO message) {
        message.updateTime();
        for (Map.Entry<String, WebSocketSession> entry : sessionManager.getAllSessions().entrySet()) {
            WebSocketSession s = entry.getValue();
            if (s.isOpen() && !Objects.equals(Convert.toStr(s.getAttributes().get(ImConstant.USER_ID)), userId)) {
                try {
                    s.sendMessage(new TextMessage(new Gson().toJson(message)));
                } catch (IOException e) {
                    log.error("[WebSocket-broadcastMessageExceptSender] 发送失败，消息接收方：[{}]，原因：{}",
                            s.getAttributes().get(ImConstant.USER_ID), e.getMessage(), e);
                }
            }
        }
    }
}