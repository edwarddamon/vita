package com.vita.im.controller;

import com.vita.common.domain.Result;
import com.vita.common.domain.mail.CommonMailSendDto;
import com.vita.im.facade.EmailFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 邮件入口
 * </p>
 *
 * @author Edward
 * @since 2024-02-28
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
@Tag(name = "email", description = "邮件相关接口")
public class EmailController {

    private final EmailFacade emailFacade;

    @Operation(summary = "邮件发送")
    @PostMapping(value = "/send")
    public Result<Void> sendEmail(@Validated @RequestBody CommonMailSendDto commonMailSendDto) {
        emailFacade.send(commonMailSendDto);
        return Result.success();
    }

}
