package com.vita.common.domain.role;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteRoleDTO {

    /**
     * 角色id
     */
    @NotNull(message = "角色id不能为空")
    private Long id;
}
