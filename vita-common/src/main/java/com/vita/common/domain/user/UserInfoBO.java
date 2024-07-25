package com.vita.common.domain.user;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

/**
 * @author Edward
 * @date 2023-06-17 16:29
 */
@Data
@Accessors(chain = true)
public class UserInfoBO {

    /**
     * 用户 Id
     */
    private Long userId;

    /**
     * 头像地址
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
     * 角色列表
     */
    private List<RoleBO> roles;
}
