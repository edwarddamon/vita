package com.vita.common.domain.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码（明文，入库时加密）
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    private String email;

    /**
     * 头像路径
     */
    private String avatar;
}
