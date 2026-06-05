package com.vita.api.controller;

import com.vita.api.service.MailService;
import com.vita.common.domain.Result;
import com.vita.common.domain.mail.CommonMailSendDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邮箱入口
 *
 * @author Edward
 * @date 2024-02-26 13:39
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/mail")
public class MailController {

    private final MailService mailService;

    /**
     * 外部邮件发送
     *
     * @param commonMailSendDto 邮件发送参数
     * @return 响应信息
     */
    @PostMapping(value = "/send")
    public Result<Void> send(@Validated @RequestBody CommonMailSendDto commonMailSendDto) {
        mailService.send(commonMailSendDto);
        return Result.success();
    }
}
