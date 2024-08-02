package com.vita.common.interceptor;

import com.vita.common.annotation.AuthPermission;
import com.vita.common.domain.common.enums.RolePermissionCode;
import com.vita.common.domain.user.RoleBO;
import com.vita.common.utils.TokenUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 接口权限 拦截器
 */
public class AuthPermissionHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        // 获取方法上的 接口权限 注解
        AuthPermission authPermission = handlerMethod.getMethod().getAnnotation(AuthPermission.class);
        if (Objects.isNull(authPermission)) {
            // 获取类上的 接口权限 注解
            authPermission = handlerMethod.getMethod().getDeclaringClass().getAnnotation(AuthPermission.class);
        }

        // 没有注解 或 注解角色为空 -> 无需鉴权
        if (Objects.isNull(authPermission) || CollectionUtils.isEmpty(Arrays.asList(authPermission.role()))) {
            return true;
        }

        // 当前用户角色包含接口需要的角色 -> 通过
        List<RoleBO> userRoles = TokenUtil.getCurrentUser().getRoles();
        RolePermissionCode[] needRoles = authPermission.role();
        if (!CollectionUtils.isEmpty(userRoles)) {
            // userRoles 包含 needRoles 中任何一个即视为通过
            Set<String> needRoleSet = Arrays.stream(needRoles).map(RolePermissionCode::name).collect(Collectors.toSet());
            for (RoleBO userRole : userRoles) {
                if (needRoleSet.contains(userRole.getCode())) {
                    return true;
                }
            }
        }

        return false;
    }
}
