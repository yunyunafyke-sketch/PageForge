package com.afyke.pageforge.security.service;

import com.afyke.pageforge.common.exception.BusinessException;
import com.afyke.pageforge.common.util.WebRequestUtils;
import com.afyke.pageforge.security.model.LoginRequest;
import com.afyke.pageforge.security.model.LoginVO;
import com.afyke.pageforge.system.model.SystemUserAuth;
import com.afyke.pageforge.system.service.SystemAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** 登录服务。 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    /** 系统认证数据服务。 */
    private final SystemAuthService systemAuthService;

    /** 密码编码器。 */
    private final PasswordEncoder passwordEncoder;

    /** JWT Token 服务。 */
    private final JwtTokenService jwtTokenService;

    /**
     * 校验账号密码并返回登录结果。
     * 登录结果同时包含 Token、角色和功能列表，前端登录后即可完成菜单和按钮展示。
     */
    public LoginVO login(LoginRequest request) {
        String username = WebRequestUtils.sanitizeLogValue(request.getUsername());
        SystemUserAuth auth;
        try {
            auth = systemAuthService.loadByUsername(request.getUsername());
        } catch (BusinessException exception) {
            // 未注册、已禁用用户与密码错误统一记录为凭证校验失败，避免泄露账号状态。
            log.warn("用户登录失败 username={} reason=凭证校验失败", username);
            throw exception;
        }
        // 数据库存储 BCrypt 密文，不能直接比较明文密码。
        if (!passwordEncoder.matches(request.getPassword(), auth.getPassword())) {
            log.warn("用户登录失败 username={} reason=凭证校验失败", username);
            throw BusinessException.unauthorized("用户名或密码错误");
        }

        LoginVO result = new LoginVO();
        result.setUserId(auth.getUserId());
        result.setUsername(auth.getUsername());
        result.setRoles(auth.getRoles());
        result.setFunctions(auth.getFunctions());
        // Token 只保存身份和角色；功能列表直接返回前端，不塞入 Token，避免 Token 过大。
        result.setToken(jwtTokenService.createToken(
                auth.getUserId(), auth.getUsername(), auth.getRoles()));
        log.info("用户登录成功 userId={} username={} roles={}",
                auth.getUserId(), username, auth.getRoles());
        return result;
    }
}
