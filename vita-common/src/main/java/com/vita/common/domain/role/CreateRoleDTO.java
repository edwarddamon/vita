package com.vita.common.domain.role;

import com.vita.common.domain.common.enums.DataAuthEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRoleDTO {

    /**
     * 角色code
     */
    @NotBlank(message = "角色code不能为空")
    private String code;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String name;

    /**
     * 数据权限
     * <p/>
     * {@link DataAuthEnum}
     */
    @NotBlank(message = "角色数据权限不能为空")
    private String dataAuth;
}
