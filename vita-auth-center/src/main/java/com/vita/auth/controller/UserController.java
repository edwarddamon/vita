package com.vita.auth.controller;

import com.vita.auth.facade.UserFacade;
import com.vita.common.domain.Result;
import com.vita.common.domain.user.LoginDTO;
import com.vita.common.domain.user.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
@Tag(name = "user", description = "用户相关")
public class UserController {

    private final UserFacade userFacade;

    @Operation(summary = "邮箱验证码发送")
    @PostMapping(value = "/mail/verification")
    public Result<Void> sendVerificationCode() {
        return null;
    }

    @Operation(summary = "注册用户")
    @PostMapping(value = "/register")
    public Result<Void> register() {
        return null;
    }

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

}
