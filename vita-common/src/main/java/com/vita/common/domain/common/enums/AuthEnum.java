package com.vita.common.domain.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Edward
 * @date 2023-06-17 16:17
 */
@Getter
@AllArgsConstructor
public enum AuthEnum {

    /**
     * 不需要鉴权 - 0
     */
    NOT_NEED_AUTH(0),

    /**
     * 需要鉴权 - 1
     */
    NEED_AUTH(1);

    private final Integer authFlag;

}
