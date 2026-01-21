package com.vita.auth.controller;

import com.vita.auth.converter.RoleConverter;
import com.vita.auth.entity.Role;
import com.vita.auth.service.RoleService;
import com.vita.common.annotation.AuthPermission;
import com.vita.common.domain.Result;
import com.vita.common.domain.common.enums.RolePermissionCode;
import com.vita.common.domain.role.RoleAssociateDTO;
import com.vita.common.domain.role.RoleListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
public class RoleController {

    private final RoleService roleService;

    /**
     * 角色列表
     */
    @GetMapping(value = "/list")
    @AuthPermission(role = RolePermissionCode.ROOT_ROLE)
    public Result<List<RoleListVO>> roleList() {
        List<Role> roleList = roleService.list();
        return Result.success(roleList.stream().map(RoleConverter.INSTANCE::convertDoToVo).collect(Collectors.toList()));
    }

    /**
     * 关联角色
     */
    @PostMapping(value = "/associate")
    @AuthPermission(role = RolePermissionCode.ROOT_ROLE)
    public Result<Void> associateRole(@RequestBody @Validated RoleAssociateDTO roleAssociateDTO) {
        roleService.associateRole(roleAssociateDTO);
        return Result.success();
    }
}
