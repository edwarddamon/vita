package com.vita.common.domain.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Edward
 * @date 2023-06-17 16:17
 */
@Getter
@AllArgsConstructor
public enum DelFlagEnum {

    /**
     * 未删除 - 0
     */
    NOT_DELETED(0),

    /**
     * 已删除 - 1
     */
    DELETED(1);

    private final Integer flag;

    public static final String DEL_FLAG = "del_flag";

}
