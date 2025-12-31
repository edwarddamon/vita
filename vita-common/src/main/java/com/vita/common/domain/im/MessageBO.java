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
     * 消息来源用户Id
     */
    private String from;

    /**
     * 消息目标用户Id
     */
    private String to;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型
     */
    private String type;

    /**
     * 交换消息方向
     */
    public MessageBO switchSide() {
        return new MessageBO()
                .setFrom(this.getTo())
                .setTo(this.getFrom())
                .setContent(this.getContent())
                .setType(this.getType());
    }
}
