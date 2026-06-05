package com.vita.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vita.auth.entity.User;
import com.vita.auth.mapper.UserMapper;
import com.vita.auth.mapper.UserRoleRelMapper;
import com.vita.auth.service.UserService;
import com.vita.common.domain.common.enums.DelFlagEnum;
import com.vita.common.domain.user.CreateUserDTO;
import com.vita.common.domain.user.EditUserDTO;
import com.vita.common.domain.user.InterfaceInfoBO;
import com.vita.common.domain.user.ListUserInfoDTO;
import com.vita.common.utils.AssertUtil;
import com.vita.common.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
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
    private final UserRoleRelMapper userRoleRelMapper;

    @Override
    public Set<InterfaceInfoBO> describeInterfaces(Long userId) {
        return userMapper.describeUserInterfaces(userId);
    }

    @Override
    public IPage<User> listUserInfo(Page<User> page, ListUserInfoDTO listUserInfoDTO) {
        return userMapper.listUser(page, listUserInfoDTO);
    }

    @Override
    public Long createUser(CreateUserDTO createUserDTO) {
        AssertUtil.isTrue(!existsEmail(createUserDTO.getEmail(), null), "邮箱已存在");

        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.setUsername(createUserDTO.getUsername());
        user.setPassword(PasswordUtil.encodePassword(createUserDTO.getPassword()));
        user.setPhone(createUserDTO.getPhone());
        user.setEmail(createUserDTO.getEmail());
        user.setAvatar(createUserDTO.getAvatar());
        user.setDelFlag(DelFlagEnum.NOT_DELETED.getFlag());
        user.setCreateTime(now);
        user.setUpdateTime(now);
        AssertUtil.isTrue(save(user), "新增用户失败");
        return user.getId();
    }

    @Override
    public void updateUser(EditUserDTO editUserDTO) {
        User user = getValidUser(editUserDTO.getId());
        AssertUtil.isTrue(!existsEmail(editUserDTO.getEmail(), editUserDTO.getId()), "邮箱已存在");

        user.setUsername(editUserDTO.getUsername());
        user.setPhone(editUserDTO.getPhone());
        user.setEmail(editUserDTO.getEmail());
        user.setAvatar(editUserDTO.getAvatar());
        if (StrUtil.isNotBlank(editUserDTO.getAvatar())) {
            user.setAvatar(editUserDTO.getAvatar());
        }
        if (StrUtil.isNotBlank(editUserDTO.getPassword())) {
            user.setPassword(PasswordUtil.encodePassword(editUserDTO.getPassword()));
        }
        user.setUpdateTime(LocalDateTime.now());

        AssertUtil.isTrue(updateById(user), "编辑用户失败");
    }

    @Override
    public void deleteUser(Long id) {
        User user = getValidUser(id);
        userRoleRelMapper.deleteUserRoleRel(id);

        user.setDelFlag(DelFlagEnum.DELETED.getFlag());
        user.setUpdateTime(LocalDateTime.now());
        AssertUtil.isTrue(updateById(user), "删除用户失败");
    }

    @Override
    public User getValidUser(Long id) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getId, id)
                .eq(User::getDelFlag, DelFlagEnum.NOT_DELETED.getFlag()));
        AssertUtil.notNullObject(user, "用户不存在");
        return user;
    }

    private boolean existsEmail(String email, Long excludeId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email)
                .eq(User::getDelFlag, DelFlagEnum.NOT_DELETED.getFlag());
        if (Objects.nonNull(excludeId)) {
            wrapper.ne(User::getId, excludeId);
        }
        return count(wrapper) > 0;
    }


}
