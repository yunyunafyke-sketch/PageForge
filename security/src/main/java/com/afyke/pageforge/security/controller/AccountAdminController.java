package com.afyke.pageforge.security.controller;

import com.afyke.pageforge.common.model.ResultModel;
import com.afyke.pageforge.security.model.ResetPasswordRequest;
import com.afyke.pageforge.security.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 管理员账号维护接口。 */
@RestController
@RequestMapping("/api/admin/system/user")
@RequiredArgsConstructor
public class AccountAdminController {

    /** 用户账号服务。 */
    private final AccountService accountService;

    /** 将指定用户密码重置为默认密码 123456。 */
    @PostMapping("/reset-password")
    public ResultModel<Boolean> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        return ResultModel.success(accountService.resetPassword(request.getUserId()));
    }
}
