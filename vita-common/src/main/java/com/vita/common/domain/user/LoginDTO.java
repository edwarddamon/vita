package com.vita.common.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Edward
 * @date 2024-03-11 19:16
 */
@Data
@Schema(description = "登录信息")
public class LoginDTO {

    @NotBlank(message = "邮箱不能为空")
    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

}
