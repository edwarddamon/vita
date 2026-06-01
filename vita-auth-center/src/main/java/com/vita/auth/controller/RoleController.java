package com.vita.auth.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vita.auth.converter.RoleConverter;
import com.vita.auth.entity.Role;
import com.vita.auth.service.RoleService;
import com.vita.common.annotation.AuthPermission;
import com.vita.common.domain.PageResult;
import com.vita.common.domain.Result;
import com.vita.common.domain.common.enums.RolePermissionCode;
import com.vita.common.domain.role.*;
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
     * 角色列表分页
     */
    @PostMapping(value = "/page")
    @AuthPermission(role = RolePermissionCode.ROOT_ROLE)
    public PageResult<List<RoleListVO>> roleList(@RequestBody ListRoleDTO listRoleDTO) {
        IPage<Role> rolePage = roleService.listRole(new Page<>(listRoleDTO.getPageNum(), listRoleDTO.getPageSize()), listRoleDTO);
        return PageResult.success(
                rolePage.getCurrent(),
                rolePage.getSize(),
                rolePage.getTotal(),
                rolePage.getRecords()
                        .stream()
                        .map(RoleConverter.INSTANCE::convertDoToVo)
                        .collect(Collectors.toList()));
    }

    /**
     * 新增角色
     */
    @PostMapping(value = "/create")
    @AuthPermission(role = RolePermissionCode.ROOT_ROLE)
    public Result<Void> createRole(@RequestBody @Validated CreateRoleDTO createRoleDTO) {
        roleService.createRole(createRoleDTO);
        return Result.success();
    }

    /**
     * 编辑角色
     */
    @PostMapping(value = "/update")
    @AuthPermission(role = RolePermissionCode.ROOT_ROLE)
    public Result<Void> updateRole(@RequestBody @Validated EditRoleDTO editRoleDTO) {
        roleService.updateRole(editRoleDTO);
        return Result.success();
    }

    /**
     * 删除角色
     */
    @PostMapping(value = "/delete")
    @AuthPermission(role = RolePermissionCode.ROOT_ROLE)
    public Result<Void> deleteRole(@RequestBody @Validated DeleteRoleDTO deleteRoleDTO) {
        roleService.deleteRole(deleteRoleDTO.getId());
        return Result.success();
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
