package com.vita.common.domain.user;

import lombok.Data;

@Data
public class RoleListVO {

    /**
     * 角色id
     */
    private Integer id;

    /**
     * 角色code
     */
    private String code;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色数据权限（OWNER-只有操作自己数据的权限；ALL-可操作所有数据的权限）
     */
    private String dataAuth;

}
