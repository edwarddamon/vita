package com.vita.auth.controller;

import com.vita.auth.facade.UserFacade;
import com.vita.common.annotation.AuthPermission;
import com.vita.common.domain.PageResult;
import com.vita.common.domain.Result;
import com.vita.common.domain.common.enums.RolePermissionCode;
import com.vita.common.domain.user.ListUserInfoDTO;
import com.vita.common.domain.user.ListUserInfoVO;
import com.vita.common.domain.user.LoginDTO;
import com.vita.common.domain.user.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author Edward
 * @since 2024-03-06
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "用户", description = "用户相关")
public class UserController {

    private final UserFacade userFacade;

    @Operation(summary = "登录")
    @PostMapping(value = "/login")
    public Result<String> login(@Validated @RequestBody LoginDTO loginDTO) {
        return Result.success(userFacade.login(loginDTO));
    }

    @Operation(summary = "用户信息查询")
    @GetMapping(value = "/info")
    public Result<UserInfoVO> info() {
        return Result.success(userFacade.describeUserInfo());
    }

    @Operation(summary = "登出")
    @GetMapping(value = "/logout")
    public Result<Void> logout() {
        userFacade.logout();
        return Result.success();
    }

    @Operation(summary = "用户列表分页")
    @PostMapping(value = "/list")
    @AuthPermission(role = RolePermissionCode.ROOT_ROLE)
    public PageResult<List<ListUserInfoVO>> listUser(@RequestBody ListUserInfoDTO listUserInfoDTO) {
        return userFacade.listUser(listUserInfoDTO);
    }

}
