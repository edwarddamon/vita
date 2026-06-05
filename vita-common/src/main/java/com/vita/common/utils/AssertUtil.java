package com.vita.common.utils;

import com.vita.common.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 断言工具类
 *
 * @author Edward
 * @date 2023-06-17 21:37
 */
public class AssertUtil {

    /**
     * 断言为真
     *
     * @param expression 断言
     * @param message    错误信息
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new ServiceException(message);
        }
    }

    /**
     * 断言字符串不为空
     */
    public static void notEmptyStr(String str, String message) {
        if (StringUtils.isEmpty(str)) {
            throw new ServiceException(message);
        }
    }

    /**
     * 断言字符串不为空白字符串
     */
    public static void notBlankStr(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new ServiceException(message);
        }
    }

    /**
     * 断言对象不为空
     */
    public static void notNullObject(Object object, String message) {
        if (Objects.isNull(object)) {
            throw new ServiceException(message);
        }
    }
}
