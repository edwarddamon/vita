package com.vita.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 * 字典表
 * </p>
 *
 * @author Edward
 * @since 2024-03-06
 */
@Getter
@Setter
@TableName("sys_dict")
public class Dict {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 字典key
     */
    @TableField("key")
    private String key;

    /**
     * 字典key描述
     */
    @TableField("key_label")
    private String keyLabel;

    /**
     * 字典value
     */
    @TableField("value")
    private String value;

    /**
     * 字典value描述
     */
    @TableField("value_label")
    private String valueLabel;

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
}
