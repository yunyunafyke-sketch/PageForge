package com.afyke.pageforge.system.service;

import com.afyke.pageforge.common.exception.BusinessException;
import com.afyke.pageforge.system.entity.UserEntity;
import com.afyke.pageforge.system.mapper.SystemAuthMapper;
import com.afyke.pageforge.system.model.SystemUserAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/** 登录认证数据服务。 */
@Service
@RequiredArgsConstructor
public class SystemAuthService {

    /** 登录认证查询 Mapper。 */
    private final SystemAuthMapper systemAuthMapper;

    public SystemUserAuth loadByUsername(String username) {
        UserEntity user = systemAuthMapper.findUserByUsername(username);
        if (user == null || !Integer.valueOf(1).equals(user.getStatus())) {
            throw BusinessException.unauthorized("用户名或密码错误");
        }

        SystemUserAuth auth = new SystemUserAuth();
        auth.setUserId(user.getId());
        auth.setUsername(user.getUsername());
        auth.setPassword(user.getPassword());
        auth.setRoles(systemAuthMapper.findRoleCodes(user.getId()));
        auth.setFunctions(systemAuthMapper.findFunctionCodes(user.getId()));
        return auth;
    }
}
