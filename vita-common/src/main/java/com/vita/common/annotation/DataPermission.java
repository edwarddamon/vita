package com.vita.common.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DataPermission {
}
