package com.vita.common.domain.role;

import com.vita.common.domain.BasePageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListRoleDTO extends BasePageDTO {

    /**
     * 角色code
     */
    private String code;

    /**
     * 角色名称
     */
    private String name;
}
