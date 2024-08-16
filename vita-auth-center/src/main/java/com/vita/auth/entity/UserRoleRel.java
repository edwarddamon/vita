package com.vita.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户-角色关联表
 * </p>
 *
 * @author Edward
 * @since 2024-07-19
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("inf_user_role_rel")
public class UserRoleRel {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户表Id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 角色表Id
     */
    @TableField("role_id")
    private Long roleId;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}
