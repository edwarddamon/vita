package com.vita.auth.test;

import cn.hutool.crypto.SecureUtil;
import com.vita.common.domain.common.constant.CommonConstant;

/**
 * @author Edward
 * @date 2024-03-12 16:07
 */
public class SecurityTest {
    public static void main(String[] args) {
        System.out.println(SecureUtil.md5(CommonConstant.AUTHORIZATION_PREFIX + "123456"));
    }
}
