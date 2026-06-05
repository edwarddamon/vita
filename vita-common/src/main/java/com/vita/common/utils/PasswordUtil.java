package com.vita.common.utils;

import cn.hutool.crypto.SecureUtil;
import com.vita.common.domain.common.constant.CommonConstant;

/**
 * 密码工具类
 *
 * @author Edward
 * @since 2024-03-06
 */
public class PasswordUtil {

    /**
     * 对明文密码进行编码
     *
     * @param plainPassword 明文密码
     * @return 编码后的密码
     */
    public static String encodePassword(String plainPassword) {
        return SecureUtil.md5(CommonConstant.AUTHORIZATION_PREFIX + plainPassword);
    }
}
