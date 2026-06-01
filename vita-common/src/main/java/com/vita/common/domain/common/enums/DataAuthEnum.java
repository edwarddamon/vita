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
     * 仅自己
     */ 
    OWNER,

    /**
     * 所有人
     */
    ALL
}
