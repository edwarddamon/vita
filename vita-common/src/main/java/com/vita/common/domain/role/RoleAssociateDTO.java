package com.vita.common.domain.role;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RoleAssociateDTO {

    /**
     * 用户id
     */
    @NotNull(message = "用户id不能为空")
    private Long userId;

    /**
     * 角色id
     */
    private List<Long> roleIds;
}

