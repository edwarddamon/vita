package com.vita.auth.facade;

import com.vita.common.domain.user.LoginDTO;

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
}
