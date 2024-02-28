package com.vita.message.client;

import com.vita.common.domain.Result;
import com.vita.common.domain.common.AppNameConstant;
import com.vita.common.domain.mail.CommonMailSendDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 外部邮件交互 openFeign
 *
 * @author Edward
 * @date 2024-02-28 17:04
 */
@FeignClient(name = AppNameConstant.VITA_API)
public interface MailFeignClient {

    /**
     * 邮件发送
     *
     * @param commonMailSendDto 邮件参数
     * @return 发送结果
     */
    @PostMapping(value = "/mail/send")
    Result<Void> send(@Validated @RequestBody CommonMailSendDto commonMailSendDto);
}
