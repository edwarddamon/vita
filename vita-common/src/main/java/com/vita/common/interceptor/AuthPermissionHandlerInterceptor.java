package com.vita.common.interceptor;

import com.google.gson.Gson;
import com.vita.common.annotation.AuthPermission;
import com.vita.common.domain.Result;
import com.vita.common.domain.ResultCode;
import com.vita.common.domain.common.enums.RolePermissionCode;
import com.vita.common.domain.user.RoleBO;
import com.vita.common.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 接口权限 拦截器
 */
@Slf4j
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

        // 未通过 - 提示信息返回前端
        returnJson(response, userRoles, needRoles);
        return false;
    }

    private void returnJson(HttpServletResponse response, List<RoleBO> userRoles, RolePermissionCode[] needRoles) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            log.warn("[接口权限校验拦截] 当前用户权限不足，当前用户角色信息：{}，所需角色信息：{}", new Gson().toJson(userRoles), new Gson().toJson(needRoles));
            writer.print(new Gson().toJson(Result.fail(ResultCode.NO_INTERFACE_PERMISSION.getCode(), "权限不足")));
        } catch (Exception e) {
            log.error("[接口权限校验拦截] 拦截器返回信息异常，原因：{}", e.getMessage(), e);
        }
    }
}
