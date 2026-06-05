package com.vita.auth.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author Edward
 * @since 2024-07-19
 */
@Getter
@Setter
@TableName("sys_role")
public class Role {

    @TableId("id")
    private Long id;

    /**
     * 角色code
     */
    @TableField("code")
    private String code;

    /**
     * 角色名称
     */
    @TableField("name")
    private String name;

    /**
     * 角色数据权限（OWNER-仅自己；ALL-所有人）
     */
    @TableField("data_auth")
    private String dataAuth;

    /**
     * 是否删除（0-未删除；1-已删除）
     */
    @TableField("del_flag")
    private Integer delFlag;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
