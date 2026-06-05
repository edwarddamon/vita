package com.vita.common.domain.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ListUserInfoVO {

    /**
     * 用户id
     */
    private String id;

    /**
     * 头像路径
     */
    private String avatar;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 逻辑删除: 0 - 未删除；1 - 已删除
     */
    private Integer delFlag;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
