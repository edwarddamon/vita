package com.vita.common.domain.mail;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

/**
 * 公共邮件发送实体
 *
 * @author Edward
 * @date 2024-02-26 13:57
 */
@Data
@Accessors(chain = true)
public class CommonMailSendDto {

    /**
     * 邮件接收方
     */
    @NotBlank(message = "邮件接收方不能为空")
    @Length(max = 64, message = "邮件接收方长度不能超过64")
    private String receiver;

    /**
     * 邮件主题
     */
    @NotBlank(message = "邮件主题不能为空")
    @Length(max = 255, message = "邮件主题长度不能超过255")
    private String subject;

    /**
     * 邮件内容（包含 html 格式）
     */
    @NotBlank(message = "邮件内容不能为空")
    private String content;

}
