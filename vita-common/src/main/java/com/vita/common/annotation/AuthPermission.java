package com.vita.common.annotation;

import com.vita.common.domain.common.enums.RolePermissionCode;

import java.lang.annotation.*;

/**
 * 接口权限注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface AuthPermission {

    /**
     * 角色 {@link RolePermissionCode}
     */
    RolePermissionCode[] role() default {};
}
