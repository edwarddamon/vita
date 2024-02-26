package com.vita.api.service.impl;

import com.google.gson.Gson;
import com.vita.api.service.MailService;
import com.vita.common.domain.mail.CommonMailSendDto;
import com.vita.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author Edward
 * @date 2024-02-26 13:50
 */
@Slf4j
@Service
public class MailServiceImpl implements MailService {

    /**
     * 邮件发送人
     */
    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private JavaMailSender mailSender;

    @Override
    public void send(CommonMailSendDto commonMailSendDto) {

        String commonMailSendDtoString = new Gson().toJson(commonMailSendDto);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            generateMailSendDto(commonMailSendDto, message);

            log.info("[公共邮件发送] 开始发送 >>> 请求参数：{}", commonMailSendDtoString);
            mailSender.send(message);
            log.info("[公共邮件发送] 发送成功 <<< 请求参数：{}", commonMailSendDtoString);
        } catch (Exception e) {
            log.error("[公共邮件发送] 发送失败，请求参数：{}，原因：{}", commonMailSendDtoString, e.getMessage(), e);
            throw new ServiceException("发送邮件失败，原因：" + e.getMessage());
        }
    }

    private void generateMailSendDto(CommonMailSendDto commonMailSendDto,
                                     MimeMessage message) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(commonMailSendDto.getTo());
        helper.setSubject(commonMailSendDto.getSubject());
        helper.setText(commonMailSendDto.getContent(), true);
        // helper.addAttachment(); 添加邮件附件
    }
}
