package com.vita.auth.test;

import cn.hutool.crypto.SecureUtil;
import com.vita.common.utils.PasswordUtil;

/**
 * @author Edward
 * @date 2024-03-12 16:07
 */
public class SecurityTest {
    public static void main(String[] args) {
        // 密码加密
        System.out.println(PasswordUtil.encodePassword(SecureUtil.md5("123456")));
    }
}
