package com.afyke.pageforge.security.service;

import com.afyke.pageforge.common.exception.BusinessException;
import com.afyke.pageforge.security.model.LoginRequest;
import com.afyke.pageforge.security.model.LoginVO;
import com.afyke.pageforge.system.model.SystemUserAuth;
import com.afyke.pageforge.system.service.SystemAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/** 登录服务日志单元测试。 */
@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class LoginServiceTest {

    /** 系统认证数据服务。 */
    @Mock
    private SystemAuthService systemAuthService;

    /** 密码编码器。 */
    @Mock
    private PasswordEncoder passwordEncoder;

    /** JWT Token 服务。 */
    @Mock
    private JwtTokenService jwtTokenService;

    /** 被测试的登录服务。 */
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        loginService = new LoginService(systemAuthService, passwordEncoder, jwtTokenService);
    }

    /** 登录成功应记录用户身份，但不能记录密码和 Token。 */
    @Test
    void shouldLogSuccessfulLoginWithoutCredentials(CapturedOutput output) {
        LoginRequest request = createRequest();
        SystemUserAuth auth = createAuth();
        when(systemAuthService.loadByUsername("test_user")).thenReturn(auth);
        when(passwordEncoder.matches("secret-password", "bcrypt-password")).thenReturn(true);
        when(jwtTokenService.createToken(10L, "test_user", List.of("USER")))
                .thenReturn("secret-token");

        LoginVO result = loginService.login(request);

        assertEquals(10L, result.getUserId());
        assertTrue(output.getOut().contains("用户登录成功"));
        assertTrue(output.getOut().contains("username=test_user"));
        assertFalse(output.getOut().contains("secret-password"));
        assertFalse(output.getOut().contains("secret-token"));
    }

    /** 密码错误应记录登录失败，但不能记录明文密码。 */
    @Test
    void shouldLogFailedLoginWithoutPassword(CapturedOutput output) {
        LoginRequest request = createRequest();
        SystemUserAuth auth = createAuth();
        when(systemAuthService.loadByUsername("test_user")).thenReturn(auth);
        when(passwordEncoder.matches("secret-password", "bcrypt-password")).thenReturn(false);

        assertThrows(BusinessException.class, () -> loginService.login(request));

        assertTrue(output.getOut().contains("用户登录失败"));
        assertTrue(output.getOut().contains("username=test_user"));
        assertFalse(output.getOut().contains("secret-password"));
    }

    /** 创建登录请求。 */
    private LoginRequest createRequest() {
        LoginRequest request = new LoginRequest();
        request.setUsername("test_user");
        request.setPassword("secret-password");
        return request;
    }

    /** 创建认证数据。 */
    private SystemUserAuth createAuth() {
        SystemUserAuth auth = new SystemUserAuth();
        auth.setUserId(10L);
        auth.setUsername("test_user");
        auth.setPassword("bcrypt-password");
        auth.setRoles(List.of("USER"));
        auth.setFunctions(List.of("profile:view"));
        return auth;
    }
}
