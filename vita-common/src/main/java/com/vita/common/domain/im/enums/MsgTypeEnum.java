package com.vita.common.domain.im.enums;

import lombok.Getter;

/**
 * 消息类型
 */
@Getter
public enum MsgTypeEnum {

    /**
     * 文本
     */
    TEXT,

    /**
     * 图片
     */
    IMAGE,

    /**
     * 文件
     */
    FILE,

    /**
     * 语音
     */
    VOICE,

    /**
     * 视频
     */
    VIDEO,

    /**
     * 确认消息
     */
    ACK,
    ;
}
