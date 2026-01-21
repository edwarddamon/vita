package com.vita.common.domain.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录信息
 *
 * @author Edward
 * @date 2024-03-11 19:16
 */
@Data
public class LoginDTO {

    @NotBlank(message = "邮箱不能为空")
    private String email;

    @NotBlank(message = "密码不能为空")
    private String password;

}
