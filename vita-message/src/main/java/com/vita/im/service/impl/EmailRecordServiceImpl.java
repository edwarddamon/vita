package com.vita.im.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.vita.common.domain.mail.CommonMailSendDto;
import com.vita.im.entity.EmailRecord;
import com.vita.im.mapper.EmailRecordMapper;
import com.vita.im.service.EmailRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Edward
 * @since 2024-02-28
 */
@Slf4j
@Service
public class EmailRecordServiceImpl extends ServiceImpl<EmailRecordMapper, EmailRecord> implements EmailRecordService {

    @Override
    public void saveEmailRecord(CommonMailSendDto commonMailSendDto) {
        EmailRecord emailRecord = EmailRecord.init()
                .setReceiver(commonMailSendDto.getReceiver())
                .setSubject(commonMailSendDto.getSubject())
                .setContent(commonMailSendDto.getContent());

        boolean save = this.save(emailRecord);
        if (!save) {
            log.error("[邮件记录] 保存邮件记录失败，邮件信息：{}", new Gson().toJson(emailRecord));
        }

    }
}
