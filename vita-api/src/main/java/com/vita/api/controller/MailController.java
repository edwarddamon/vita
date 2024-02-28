package com.vita.api.controller;

import com.vita.api.service.MailService;
import com.vita.common.domain.Result;
import com.vita.common.domain.mail.CommonMailSendDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "email", description = "邮件相关")
public class MailController {

    private final MailService mailService;

    @Operation(summary = "外部邮件发送")
    @PostMapping(value = "/send")
    public Result<Void> send(@Validated @RequestBody CommonMailSendDto commonMailSendDto) {
        mailService.send(commonMailSendDto);
        return Result.success();
    }
}
