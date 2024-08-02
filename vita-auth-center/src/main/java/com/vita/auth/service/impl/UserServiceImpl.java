package com.vita.auth.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vita.auth.entity.User;
import com.vita.auth.mapper.UserMapper;
import com.vita.auth.service.UserService;
import com.vita.common.domain.user.InterfaceInfoBO;
import com.vita.common.domain.user.ListUserInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Edward
 * @since 2024-03-06
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;

    @Override
    public Set<InterfaceInfoBO> describeInterfaces(Long userId) {
        return userMapper.describeUserInterfaces(userId);
    }

    @Override
    public IPage<User> listUserInfo(Page<User> page, ListUserInfoDTO listUserInfoDTO) {
        return userMapper.listUser(page, listUserInfoDTO);
    }
}
