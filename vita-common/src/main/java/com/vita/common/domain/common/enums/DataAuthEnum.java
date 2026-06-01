package com.vita.common.domain.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Edward
 * @date 2023-06-17 16:17
 */
@Getter
@AllArgsConstructor
public enum DataAuthEnum {

    /**
     * 只有操作自己数据的权限
     */
    OWNER,

    /**
     * 可操作所有数据的权限
     */
    ALL
}
