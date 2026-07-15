package com.afyke.pageforge.security.service;

import com.afyke.pageforge.common.exception.BusinessException;
import com.afyke.pageforge.security.model.ChangePasswordRequest;
import com.afyke.pageforge.security.model.LoginUser;
import com.afyke.pageforge.security.model.RegisterRequest;
import com.afyke.pageforge.system.entity.RoleEntity;
import com.afyke.pageforge.system.entity.UserEntity;
import com.afyke.pageforge.system.mapper.RoleMapper;
import com.afyke.pageforge.system.mapper.UserMapper;
import com.afyke.pageforge.system.mapper.UserRoleMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 用户账号服务。 */
@Service
@RequiredArgsConstructor
public class AccountService {

    /** 普通用户角色编码。 */
    private static final String USER_ROLE_CODE = "USER";

    /** 管理员重置密码时使用的默认明文密码。 */
    private static final String DEFAULT_PASSWORD = "123456";

    /** 用户 Mapper。 */
    private final UserMapper userMapper;

    /** 角色 Mapper。 */
    private final RoleMapper roleMapper;

    /** 用户角色关系 Mapper。 */
    private final UserRoleMapper userRoleMapper;

    /** BCrypt 密码编码器。 */
    private final PasswordEncoder passwordEncoder;

    /**
     * 注册普通用户并自动关联 USER 角色。
     * 数据库只保存 BCrypt 密文，不保存请求中的明文密码。
     */
    @Transactional(rollbackFor = Exception.class)
    public Long register(RegisterRequest request) {
        if (userMapper.countByUsername(request.getUsername()) > 0) {
            throw BusinessException.badRequest("USERNAME_EXISTS", "用户名已存在");
        }

        RoleEntity userRole = roleMapper.selectOne(new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getRoleCode, USER_ROLE_CODE)
                .last("LIMIT 1"));
        if (userRole == null) {
            throw BusinessException.badRequest("USER_ROLE_NOT_FOUND", "普通用户角色尚未初始化");
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setStatus(1);
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException exception) {
            // 并发注册相同用户名时，由数据库唯一索引做最终保护。
            throw BusinessException.badRequest("USERNAME_EXISTS", "用户名已存在");
        }
        userRoleMapper.enableRelation(user.getId(), userRole.getId());
        return user.getId();
    }

    /** 校验旧密码后修改当前登录用户的密码。 */
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(LoginUser loginUser, ChangePasswordRequest request) {
        UserEntity user = requireUser(loginUser.getUserId());
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw BusinessException.badRequest("OLD_PASSWORD_ERROR", "旧密码错误");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw BusinessException.badRequest("PASSWORD_NOT_CHANGED", "新密码不能与旧密码相同");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        return userMapper.updateById(user) > 0;
    }

    /** 管理员将指定用户的密码重置为默认密码 123456。 */
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long userId) {
        UserEntity user = requireUser(userId);
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        return userMapper.updateById(user) > 0;
    }

    /** 查询有效用户，不存在时返回明确的业务错误。 */
    private UserEntity requireUser(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw BusinessException.badRequest("USER_NOT_FOUND", "用户不存在");
        }
        return user;
    }
}
