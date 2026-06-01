package com.vita.common.domain.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteUserDTO {

    /**
     * 用户id
     */
    @NotNull(message = "用户id不能为空")
    private Long id;
}
