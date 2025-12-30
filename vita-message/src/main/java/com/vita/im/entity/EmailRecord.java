package com.vita.im.entity;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 邮件记录
 * </p>
 *
 * @author Edward
 * @since 2024-02-28
 */
@Data
@Accessors(chain = true)
@TableName("biz_email_record")
public class EmailRecord {

    @TableId("id")
    private Long id;

    /**
     * 邮件收件人
     */
    @TableField("receiver")
    private String receiver;

    /**
     * 邮件主题
     */
    @TableField("subject")
    private String subject;

    /**
     * 邮件内容
     */
    @TableField("content")
    private String content;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    public static EmailRecord init() {
        LocalDateTime now = LocalDateTime.now();
        return new EmailRecord()
                .setId(IdUtil.getSnowflakeNextId())
                .setCreateTime(now)
                .setUpdateTime(now);
    }
}
