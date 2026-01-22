package com.vita.common.client;

import com.vita.common.domain.Result;
import com.vita.common.domain.common.constant.AppCodeConstant;
import com.vita.common.domain.user.UserInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 认证中心客户端
 */
@FeignClient(name = AppCodeConstant.VITA_AUTH_CENTER)
public interface AuthCenterClient {


    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping(value = "/user/info")
    Result<UserInfoVO> describeUserInfo();

    /**
     * 获取用户信息
     *
     * @param token 登录token
     * @return 用户信息
     */
    @GetMapping(value = "/user/info/{token}")
    Result<UserInfoVO> describeUserInfo(@PathVariable(required = false) String token);
}
