package com.vita.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author Edward
 * @since 2024-03-06
 */
@Getter
@Setter
@TableName("inf_user")
public class User {

    @TableId("id")
    private Long id;

    /**
     * 头像路径
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码（MD5）
     */
    @TableField("password")
    private String password;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 逻辑删除: 0 - 未删除；1 - 已删除
     */
    @TableField("del_flag")
    private Integer delFlag;

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

    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
}
