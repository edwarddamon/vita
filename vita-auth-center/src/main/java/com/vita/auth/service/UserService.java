package com.vita.auth.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vita.auth.entity.User;
import com.vita.common.domain.user.InterfaceInfoBO;
import com.vita.common.domain.user.ListUserInfoDTO;

import java.util.Set;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author Edward
 * @since 2024-03-06
 */
public interface UserService extends IService<User> {

    Set<InterfaceInfoBO> describeInterfaces(Long userId);

    IPage<User> listUserInfo(Page<User> page, ListUserInfoDTO listUserInfoDTO);
}
