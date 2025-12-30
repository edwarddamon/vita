package com.vita.im.facade;

import com.vita.common.domain.mail.CommonMailSendDto;

/**
 * 邮件抽象层
 *
 * @author Edward
 * @date 2024-02-28 16:58
 */
public interface EmailFacade {
    /**
     * 邮件发送
     *
     * @param commonMailSendDto 邮件参数
     */
    void send(CommonMailSendDto commonMailSendDto);
}
