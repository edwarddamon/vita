package com.vita.message.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.vita.common.domain.mail.CommonMailSendDto;
import com.vita.message.entity.EmailRecord;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Edward
 * @since 2024-02-28
 */
public interface EmailRecordService extends IService<EmailRecord> {

    /**
     * 邮件记录落库
     *
     * @param commonMailSendDto 邮件信息
     */
    void saveEmailRecord(CommonMailSendDto commonMailSendDto);
}
