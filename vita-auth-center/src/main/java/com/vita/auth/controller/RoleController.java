package com.vita.auth.controller;

import com.vita.auth.converter.RoleConverter;
import com.vita.auth.entity.Role;
import com.vita.auth.service.RoleService;
import com.vita.common.annotation.AuthPermission;
import com.vita.common.domain.Result;
import com.vita.common.domain.common.enums.RolePermissionCode;
import com.vita.common.domain.user.RoleListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author Edward
 * @since 2024-07-19
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/role")
@Tag(name = "角色", description = "角色相关")
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "角色列表")
    @GetMapping(value = "/list")
    @AuthPermission(role = RolePermissionCode.ROOT_ROLE)
    public Result<List<RoleListVO>> roleList() {
        List<Role> roleList = roleService.list();
        return Result.success(roleList.stream().map(RoleConverter.INSTANCE::convertDoToVo).collect(Collectors.toList()));
    }
}
