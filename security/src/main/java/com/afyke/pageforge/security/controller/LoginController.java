package com.afyke.pageforge.security.controller;

import com.afyke.pageforge.common.model.ResultModel;
import com.afyke.pageforge.security.model.LoginRequest;
import com.afyke.pageforge.security.model.LoginVO;
import com.afyke.pageforge.security.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 登录接口。 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    /** 登录服务。 */
    private final LoginService loginService;

    @PostMapping("/login")
    public ResultModel<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return ResultModel.success(loginService.login(request));
    }
}
