package com.afyke.pageforge.system.service;

import com.afyke.pageforge.common.exception.BusinessException;
import com.afyke.pageforge.system.entity.RoleEntity;
import com.afyke.pageforge.system.entity.UserEntity;
import com.afyke.pageforge.system.entity.UserRoleEntity;
import com.afyke.pageforge.system.mapper.FunctionMapper;
import com.afyke.pageforge.system.mapper.RoleFunctionMapper;
import com.afyke.pageforge.system.mapper.RoleMapper;
import com.afyke.pageforge.system.mapper.UserMapper;
import com.afyke.pageforge.system.mapper.UserRoleMapper;
import com.afyke.pageforge.system.request.UserAssignRolesRequest;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** 权限管理服务单元测试。 */
@ExtendWith(MockitoExtension.class)
class PermissionManagementServiceTest {

    /** 用户 Mapper。 */
    @Mock
    private UserMapper userMapper;

    /** 角色 Mapper。 */
    @Mock
    private RoleMapper roleMapper;

    /** 功能 Mapper。 */
    @Mock
    private FunctionMapper functionMapper;

    /** 用户角色关系 Mapper。 */
    @Mock
    private UserRoleMapper userRoleMapper;

    /** 角色功能关系 Mapper。 */
    @Mock
    private RoleFunctionMapper roleFunctionMapper;

    /** 被测试的权限管理服务。 */
    private PermissionManagementService permissionManagementService;

    @BeforeEach
    void setUp() {
        // 纯 Mockito 测试不会启动 Spring，需要手动初始化 Lambda 条件所依赖的实体字段缓存。
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(
                new MybatisConfiguration(), "permission-test");
        TableInfoHelper.initTableInfo(assistant, RoleEntity.class);
        TableInfoHelper.initTableInfo(assistant, UserRoleEntity.class);
        permissionManagementService = new PermissionManagementService(
                userMapper,
                roleMapper,
                functionMapper,
                userRoleMapper,
                roleFunctionMapper);
    }

    /** 重复角色 ID 应先去重，再恢复对应关系。 */
    @Test
    void shouldDeduplicateRolesWhenAssigning() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        when(userMapper.selectById(1L)).thenReturn(user);
        when(roleMapper.selectCount(any())).thenReturn(2L);

        UserAssignRolesRequest request = new UserAssignRolesRequest();
        request.setUserId(1L);
        request.setRoleIds(List.of(2L, 2L, 3L));

        assertTrue(permissionManagementService.assignUserRoles(request));
        verify(userRoleMapper).update(isNull(), any());
        verify(userRoleMapper).enableRelation(1L, 2L);
        verify(userRoleMapper).enableRelation(1L, 3L);
    }

    /** 请求中包含不存在的角色时，不允许修改用户现有角色。 */
    @Test
    void shouldRejectMissingRoleBeforeUpdatingRelations() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        when(userMapper.selectById(1L)).thenReturn(user);
        when(roleMapper.selectCount(any())).thenReturn(1L);

        UserAssignRolesRequest request = new UserAssignRolesRequest();
        request.setUserId(1L);
        request.setRoleIds(List.of(2L, 3L));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> permissionManagementService.assignUserRoles(request));
        assertEquals("ROLE_NOT_FOUND", exception.getErrorCode());
        verify(userRoleMapper, never()).update(isNull(), any());
    }
}
