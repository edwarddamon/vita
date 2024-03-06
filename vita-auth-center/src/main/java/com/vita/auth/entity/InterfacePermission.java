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
 * 接口权限表
 * </p>
 *
 * @author Edward
 * @since 2024-03-06
 */
@Getter
@Setter
@TableName("sys_interface_permission")
public class InterfacePermission {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 接口路径
     */
    @TableField("path")
    private String path;

    /**
     * 接口名称
     */
    @TableField("name")
    private String name;

    /**
     * 微服务code
     */
    @TableField("app_code")
    private String appCode;

    /**
     * 是否需要鉴权：0 - 不需要；1 - 需要
     */
    @TableField("need_auth")
    private Integer needAuth;

    /**
     * 逻辑删除：0 - 未删除；1 - 已删除
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

    public static final String NEED_AUTH = "need_auth";
}
