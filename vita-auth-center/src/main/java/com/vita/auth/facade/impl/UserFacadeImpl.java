package com.vita.auth.facade.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.vita.auth.converter.RoleConverter;
import com.vita.auth.converter.UserConverter;
import com.vita.auth.entity.Role;
import com.vita.auth.entity.User;
import com.vita.auth.facade.UserFacade;
import com.vita.auth.service.RoleService;
import com.vita.auth.service.UserService;
import com.vita.common.domain.PageResult;
import com.vita.common.domain.ResultCode;
import com.vita.common.domain.common.constant.CommonConstant;
import com.vita.common.domain.common.dict.RedisKeyDict;
import com.vita.common.domain.common.enums.DelFlagEnum;
import com.vita.common.domain.user.*;
import com.vita.common.exception.ServiceException;
import com.vita.common.utils.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户相关逻辑处理
 *
 * @author Edward
 * @date 2024-03-07 10:13
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final StringRedisTemplate redisTemplate;

    private final UserService userService;
    private final RoleService roleService;

    @Override
    public String login(LoginDTO loginDTO) {
        // 校验邮箱密码
        String realPassword = SecureUtil.md5(CommonConstant.AUTHORIZATION_PREFIX + loginDTO.getPassword());
        User user = userService.getOne(new QueryWrapper<User>()
                .eq(User.EMAIL, loginDTO.getEmail())
                .eq(User.PASSWORD, realPassword));
        validateUserInfo(user);

        // 查询用户关联角色信息
        List<Role> roles = roleService.describeUserRoles(user.getId());

        // 生成token && 存入redis
        return describeAndGenerateToken(user, roles);
    }

    @Override
    public UserInfoVO describeUserInfo() {
        UserInfoBO currentUser = TokenUtil.getCurrentUser();

        return new UserInfoVO()
                .setAvatar(currentUser.getAvatar())
                .setUsername(currentUser.getUsername())
                .setPhone(currentUser.getPhone())
                .setEmail(currentUser.getEmail());
    }

    @Override
    public void logout() {
        UserInfoBO currentUser = TokenUtil.getCurrentUser();
        // 获取当前用户登录的所有登录token
        Set<String> tokenList = redisTemplate.opsForSet().members(RedisKeyDict.USER_ONLINE + currentUser.getUserId());
        if (CollectionUtils.isEmpty(tokenList)) {
            return;
        }
        // 清理所有token
        tokenList.forEach(token -> redisTemplate.delete(RedisKeyDict.USER_TOKEN + token));
        // 删除用户在线信息
        redisTemplate.delete(RedisKeyDict.USER_ONLINE + currentUser.getUserId());
    }

    @Override
    public PageResult<List<ListUserInfoVO>> listUser(ListUserInfoDTO listUserInfoDTO) {
        IPage<User> userPage = userService.listUserInfo(new Page<>(listUserInfoDTO.getPageNum(), listUserInfoDTO.getPageSize()), listUserInfoDTO);

        return PageResult.success(
                userPage.getCurrent(),
                userPage.getSize(),
                userPage.getTotal(),
                userPage.getRecords()
                        .stream()
                        .map(UserConverter.INSTANCE::convertDoToVo)
                        .collect(Collectors.toList()));
    }

    private String describeAndGenerateToken(User user, List<Role> roles) {
        // 转为序列化对象
        UserInfoBO userInfoBO = new UserInfoBO()
                .setUserId(user.getId())
                .setUsername(user.getUsername())
                .setAvatar(user.getAvatar())
                .setPhone(user.getPhone())
                .setEmail(user.getEmail())
                .setRoles(roles.stream().map(RoleConverter.INSTANCE::convertDoToBo).collect(Collectors.toList()));

        // 生成 token
        String vitaToken = CommonConstant.AUTHORIZATION_PREFIX + UUID.randomUUID().toString(true);
        try {
            // 将用户信息和接口权限存入redis
            redisTemplate.opsForValue().set(
                    RedisKeyDict.USER_TOKEN + vitaToken,
                    new Gson().toJson(userInfoBO),
                    CommonConstant.AUTHORIZATION_TOKEN_EXPIRE_DAY,
                    TimeUnit.DAYS);
            // 记录用户登录的 token
            redisTemplate.opsForSet().add(RedisKeyDict.USER_ONLINE + userInfoBO.getUserId(), vitaToken);
        } catch (Exception e) {
            log.error("[登录] token 处理异常, 用户手机号：{}, 邮箱：{}, 原因: {}", user.getPhone(), user.getEmail(), e.getMessage(), e);
            throw new ServiceException(ResultCode.FAIL);
        }
        return vitaToken;
    }

    private void validateUserInfo(User user) {
        if (Objects.isNull(user)) {
            throw new ServiceException("邮箱或密码错误！");
        }

        if (DelFlagEnum.DELETED.getFlag().equals(user.getDelFlag())) {
            log.warn("[登录] 登陆失败，该账号已逻辑删除，用户信息：{}", new Gson().toJson(user));
            throw new ServiceException("邮箱或密码错误！");
        }
    }
}
