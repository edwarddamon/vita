package com.vita.common.utils;

import com.google.gson.Gson;
import com.vita.common.domain.ResultCode;
import com.vita.common.domain.common.constant.HttpConstant;
import com.vita.common.domain.user.UserInfoBO;
import com.vita.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * @author Edward
 * @date 2023-06-17 21:37
 */
@Slf4j
public class TokenUtil {

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    public static UserInfoBO getCurrentUser() {
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(requestAttributes)) {
            log.warn("[tokenUtil] 获取 requestAttributes 为空!");
            throw new ServiceException(ResultCode.USER_INFO_ERROR);
        }
        final String userInfoString = requestAttributes.getRequest().getHeader(HttpConstant.USER_INFO);
        if (!StringUtils.hasLength(userInfoString)) {
            log.warn("[tokenUtil] 获取用户为空!");
            throw new ServiceException(ResultCode.USER_INFO_ERROR);
        }
        return new Gson().fromJson(userInfoString, UserInfoBO.class);
    }

    /**
     * 获取用户信息（返回字符串）
     *
     * @return 用户信息
     */
    public static String getCurrentUserAsString() {
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(requestAttributes)) {
            log.warn("[tokenUtil] 获取 requestAttributes 为空!");
            return null;
        }
        return requestAttributes.getRequest().getHeader(HttpConstant.USER_INFO);
    }

}
