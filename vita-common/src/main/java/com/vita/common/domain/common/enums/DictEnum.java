package com.vita.common.domain.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Edward
 * @date 2024-02-29 17:23
 */
@Getter
@AllArgsConstructor
public enum DictEnum {

    APP_CODE("app_code", "微服务"),

    ;


    private final String key;

    private final String keyDesc;
}
