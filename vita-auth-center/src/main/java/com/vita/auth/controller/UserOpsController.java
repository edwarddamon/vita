package com.vita.auth.controller;

import com.vita.auth.facade.UserFacade;
import com.vita.common.annotation.AuthPermission;
import com.vita.common.domain.PageResult;
import com.vita.common.domain.Result;
import com.vita.common.domain.common.enums.RolePermissionCode;
import com.vita.common.domain.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户运维接口（需 ROOT 权限）
 *
 * @author Edward
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/ops")
public class UserOpsController {

    private final UserFacade userFacade;

    /**
     * 用户列表分页
     */
    @PostMapping(value = "/list")
    @AuthPermission(role = RolePermissionCode.ROOT_ROLE)
    public PageResult<List<ListUserInfoVO>> listUser(@RequestBody ListUserInfoDTO listUserInfoDTO) {
        return userFacade.listUser(listUserInfoDTO);
    }

    /**
     * 新增用户
     */
    @PostMapping(value = "/create")
    @AuthPermission(role = RolePermissionCode.ROOT_ROLE)
    public Result<String> createUser(@RequestBody @Validated CreateUserDTO createUserDTO) {
        return Result.success(userFacade.createUser(createUserDTO));
    }

    /**
     * 编辑用户
     */
    @PostMapping(value = "/update")
    @AuthPermission(role = RolePermissionCode.ROOT_ROLE)
    public Result<Void> updateUser(@RequestBody @Validated EditUserDTO editUserDTO) {
        userFacade.updateUser(editUserDTO);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @PostMapping(value = "/delete")
    @AuthPermission(role = RolePermissionCode.ROOT_ROLE)
    public Result<Void> deleteUser(@RequestBody @Validated DeleteUserDTO deleteUserDTO) {
        userFacade.deleteUser(deleteUserDTO.getId());
        return Result.success();
    }

    /**
     * 用户详情（查库）
     */
    @GetMapping(value = "/detail/{id}")
    @AuthPermission(role = RolePermissionCode.ROOT_ROLE)
    public Result<UserDetailVO> getUserDetail(@PathVariable Long id) {
        return Result.success(userFacade.getUserDetail(id));
    }
}
