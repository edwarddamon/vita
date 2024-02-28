package com.vita.message.facade.impl;

import com.vita.common.domain.Result;
import com.vita.common.domain.mail.CommonMailSendDto;
import com.vita.common.exception.ServiceException;
import com.vita.message.client.MailFeignClient;
import com.vita.message.facade.EmailFacade;
import com.vita.message.service.EmailRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * @author Edward
 * @date 2024-02-28 16:58
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailFacadeImpl implements EmailFacade {

    @Resource(name = "commonTaskExecutor")
    private ThreadPoolTaskExecutor commonTaskExecutor;

    private final MailFeignClient mailFeignClient;

    private final EmailRecordService emailRecordService;

    @Override
    public void send(CommonMailSendDto commonMailSendDto) {

        // 邮件发送
        Result<Void> result = mailFeignClient.send(commonMailSendDto);
        if (!result.isSuccess()) {
            throw new ServiceException(result.getCode(), result.getMessage());
        }

        // 邮件发送记录落库
        CompletableFuture.runAsync(
                () -> emailRecordService.saveEmailRecord(commonMailSendDto),
                commonTaskExecutor);
    }
}
