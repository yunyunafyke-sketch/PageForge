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
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** 用户账号服务单元测试。 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    /** 用户 Mapper。 */
    @Mock
    private UserMapper userMapper;

    /** 角色 Mapper。 */
    @Mock
    private RoleMapper roleMapper;

    /** 用户角色关系 Mapper。 */
    @Mock
    private UserRoleMapper userRoleMapper;

    /** 密码编码器。 */
    @Mock
    private PasswordEncoder passwordEncoder;

    /** 被测试的账号服务。 */
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        // 纯 Mockito 测试不会启动 Spring，需要初始化 Lambda 条件使用的实体信息。
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(
                new MybatisConfiguration(), "account-test");
        TableInfoHelper.initTableInfo(assistant, RoleEntity.class);
        accountService = new AccountService(
                userMapper, roleMapper, userRoleMapper, passwordEncoder);
    }

    /** 注册成功后应加密密码并关联普通用户角色。 */
    @Test
    void shouldRegisterUserAndAssignUserRole() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test_user");
        request.setPassword("password123");
        request.setNickname("测试用户");
        RoleEntity role = new RoleEntity();
        role.setId(2L);

        when(userMapper.countByUsername("test_user")).thenReturn(0L);
        when(roleMapper.selectOne(any())).thenReturn(role);
        when(passwordEncoder.encode("password123")).thenReturn("bcrypt-password");
        when(userMapper.insert(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            user.setId(10L);
            return 1;
        });

        assertEquals(10L, accountService.register(request));
        verify(userRoleMapper).enableRelation(10L, 2L);
    }

    /** 用户名重复时不应写入用户或角色关系。 */
    @Test
    void shouldRejectDuplicateUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test_user");
        when(userMapper.countByUsername("test_user")).thenReturn(1L);

        BusinessException exception = assertThrows(
                BusinessException.class, () -> accountService.register(request));

        assertEquals("USERNAME_EXISTS", exception.getErrorCode());
        verify(userMapper, never()).insert(any(UserEntity.class));
        verify(userRoleMapper, never()).enableRelation(any(), any());
    }

    /** 当前用户提供正确旧密码后可以修改密码。 */
    @Test
    void shouldChangeCurrentUserPassword() {
        UserEntity user = new UserEntity();
        user.setId(10L);
        user.setPassword("old-bcrypt-password");
        when(userMapper.selectById(10L)).thenReturn(user);
        when(passwordEncoder.matches("oldPassword", "old-bcrypt-password")).thenReturn(true);
        when(passwordEncoder.matches("newPassword", "old-bcrypt-password")).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("new-bcrypt-password");
        when(userMapper.updateById(user)).thenReturn(1);

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPassword");
        request.setNewPassword("newPassword");
        LoginUser loginUser = new LoginUser(10L, "test_user", List.of("USER"));

        assertTrue(accountService.changePassword(loginUser, request));
        assertEquals("new-bcrypt-password", user.getPassword());
    }

    /** 管理员重置时应使用默认密码 123456 生成新密文。 */
    @Test
    void shouldResetPasswordToDefaultValue() {
        UserEntity user = new UserEntity();
        user.setId(10L);
        when(userMapper.selectById(10L)).thenReturn(user);
        when(passwordEncoder.encode("123456")).thenReturn("default-bcrypt-password");
        when(userMapper.updateById(user)).thenReturn(1);

        assertTrue(accountService.resetPassword(10L));
        assertEquals("default-bcrypt-password", user.getPassword());
    }
}
