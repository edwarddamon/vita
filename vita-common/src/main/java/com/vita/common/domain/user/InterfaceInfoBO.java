package com.vita.common.domain.user;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Edward
 */
@Data
@Accessors(chain = true)
public class InterfaceInfoBO {

    /**
     * 接口名称
     */
    private String name;

    /**
     * 接口路径
     */
    private String path;

}
