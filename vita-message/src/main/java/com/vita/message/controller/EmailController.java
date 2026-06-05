package com.vita.message.controller;

import com.vita.common.domain.Result;
import com.vita.common.domain.mail.CommonMailSendDto;
import com.vita.message.facade.EmailFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邮件相关接口
 *
 * @author Edward
 * @since 2024-02-28
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailFacade emailFacade;

    /**
     * 邮件发送
     */
    @PostMapping(value = "/send")
    public Result<Void> sendEmail(@Validated @RequestBody CommonMailSendDto commonMailSendDto) {
        emailFacade.send(commonMailSendDto);
        return Result.success();
    }

}
