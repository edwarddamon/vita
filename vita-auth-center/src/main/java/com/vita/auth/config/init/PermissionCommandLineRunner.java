package com.vita.auth.config.init;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vita.auth.entity.InterfacePermission;
import com.vita.auth.service.InterfacePermissionService;
import com.vita.common.domain.common.enums.AuthEnum;
import com.vita.common.domain.common.redis.RedisKeyDictionary;
import com.vita.common.domain.user.InterfaceInfoBO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口权限初始化
 * <p/>
 * 每次认证中心微服务启动时都将无需鉴权的接口地址存入缓存中
 *
 * @author Edward
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionCommandLineRunner implements CommandLineRunner {

    private final StringRedisTemplate redisTemplate;

    private final InterfacePermissionService permissionService;

    @Override
    public void run(String... args) {
        List<InterfacePermission> permissionList = permissionService.list(new QueryWrapper<InterfacePermission>()
                .eq(InterfacePermission.NEED_AUTH, AuthEnum.NOT_NEED_AUTH.getAuthFlag()));
        if (CollectionUtils.isEmpty(permissionList)) {
            log.info("[初始化接口权限] 无不需要鉴权的接口权限");
            return;
        }

        final List<InterfaceInfoBO> infoBOList = permissionList.stream()
                .map(interfacePermission -> new InterfaceInfoBO()
                        .setName(interfacePermission.getName())
                        .setPath(interfacePermission.getPath()))
                .collect(Collectors.toList());
        redisTemplate.opsForValue().set(RedisKeyDictionary.NOT_NEED_AUTH_INTERFACE, new Gson().toJson(infoBOList));
    }
}
