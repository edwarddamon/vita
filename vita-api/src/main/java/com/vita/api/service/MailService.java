package com.vita.api.service;

import com.vita.common.domain.mail.CommonMailSendDto;

/**
 * 邮件相关实现
 *
 * @author Edward
 * @date 2024-02-26 13:48
 */
public interface MailService {
    /**
     * 邮件发送（区分渠道，目前接入公共邮箱配置）
     *
     * @param commonMailSendDto 发送内容
     */
    void send(CommonMailSendDto commonMailSendDto);
}
