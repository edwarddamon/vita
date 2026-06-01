package com.vita.common.domain.role;

import lombok.Data;

@Data
public class RoleListVO {

    /**
     * 角色id
     */
    private String id;

    /**
     * 角色code
     */
    private String code;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色数据权限（OWNER-仅自己；ALL-所有人）
     */
    private String dataAuth;

}
