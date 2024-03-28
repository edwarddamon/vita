package com.vita.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vita.auth.entity.User;
import com.vita.auth.mapper.UserMapper;
import com.vita.auth.service.UserService;
import com.vita.common.domain.user.InterfaceInfoBO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
