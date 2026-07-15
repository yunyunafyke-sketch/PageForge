package com.afyke.pageforge.security.controller;

import com.afyke.pageforge.common.model.ResultModel;
import com.afyke.pageforge.security.model.ChangePasswordRequest;
import com.afyke.pageforge.security.model.LoginUser;
import com.afyke.pageforge.security.model.RegisterRequest;
import com.afyke.pageforge.security.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 注册及当前用户账号接口。 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AccountController {

    /** 用户账号服务。 */
    private final AccountService accountService;

    /** 注册普通用户，成功后返回新用户 ID。 */
    @PostMapping("/register")
    public ResultModel<Long> register(@Valid @RequestBody RegisterRequest request) {
        return ResultModel.success(accountService.register(request));
    }

    /** 当前登录用户修改自己的密码。 */
    @PostMapping("/change-password")
    public ResultModel<Boolean> changePassword(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody ChangePasswordRequest request) {
        return ResultModel.success(accountService.changePassword(loginUser, request));
    }
}
