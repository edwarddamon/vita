package com.vita.auth.facade;

import com.vita.common.domain.user.LoginDTO;
import com.vita.common.domain.user.UserInfoVO;

/**
 * @author Edward
 * @date 2024-03-07 10:13
 */
public interface UserFacade {

    /**
     * 登录
     *
     * @return token
     */
    String login(LoginDTO loginDTO);

    /**
     * 获取当前登录用户信息
     *
     * @return 当前登录用户信息
     */
    UserInfoVO describeUserInfo();

    /**
     * 退出登录
     */
    void logout();
}
