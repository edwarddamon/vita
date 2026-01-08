package com.vita.common.domain.im;

import lombok.Data;
import lombok.experimental.Accessors;

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
     */
    private String type;

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
}
