package com.vita.common.domain.im;

import com.vita.common.domain.im.enums.MsgStatusEnum;
import com.vita.common.domain.im.enums.MsgTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 消息BO
 *
 * @author Edward
 */
@Data
@Accessors(chain = true)
public class MessageBO {

    /**
     * 消息Id
     */
    private String id;

    /**
     * 发送方Id
     */
    private String senderId;

    /**
     * 发送方名称
     */
    private String senderName;

    /**
     * 接收方Id
     */
    private String receiverId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型
     * <p/>
     * {@link MsgTypeEnum}
     */
    private String type;

    /**
     * 消息状态
     * <p/>
     * {@link MsgStatusEnum}
     */
    private String status;

    /**
     * 消息时间
     */
    private String time;

    /**
     * 交换消息方向
     */
    public MessageBO switchSide() {
        return new MessageBO()
                .setId(this.getId())
                .setSenderId(this.getReceiverId())
                .setReceiverId(this.getSenderId())
                .setContent(this.getContent())
                .setType(this.getType())
                .setTime(this.getTime());
    }

    /**
     * ACK消息构建
     */
    public static MessageBO generateAckMsg(String messageId, MsgStatusEnum status) {
        return new MessageBO()
                .setId(messageId)
                .setType(MsgTypeEnum.ACK.name())
                .setStatus(status.name());
    }

    /**
     * 更新时间
     */
    public MessageBO updateTime() {
        // 转为 yyyy-MM-dd HH:mm:ss
        return this.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
