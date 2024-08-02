package com.vita.auth.facade;

import com.vita.common.domain.PageResult;
import com.vita.common.domain.user.ListUserInfoDTO;
import com.vita.common.domain.user.ListUserInfoVO;
import com.vita.common.domain.user.LoginDTO;
import com.vita.common.domain.user.UserInfoVO;

import java.util.List;

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

    /**
     * 用户列表分页
     *
     * @param listUserInfoDTO 请求参数
     * @return 用户列表
     */
    PageResult<List<ListUserInfoVO>> listUser(ListUserInfoDTO listUserInfoDTO);
}
