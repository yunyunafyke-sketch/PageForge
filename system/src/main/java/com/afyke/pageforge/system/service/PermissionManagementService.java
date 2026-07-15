package com.afyke.pageforge.system.service;

import com.afyke.pageforge.common.exception.BusinessException;
import com.afyke.pageforge.common.model.PageResult;
import com.afyke.pageforge.system.entity.FunctionEntity;
import com.afyke.pageforge.system.entity.RoleEntity;
import com.afyke.pageforge.system.entity.RoleFunctionEntity;
import com.afyke.pageforge.system.entity.UserEntity;
import com.afyke.pageforge.system.entity.UserRoleEntity;
import com.afyke.pageforge.system.mapper.FunctionMapper;
import com.afyke.pageforge.system.mapper.RoleFunctionMapper;
import com.afyke.pageforge.system.mapper.RoleMapper;
import com.afyke.pageforge.system.mapper.UserMapper;
import com.afyke.pageforge.system.mapper.UserRoleMapper;
import com.afyke.pageforge.system.request.FunctionCreateRequest;
import com.afyke.pageforge.system.request.FunctionPageRequest;
import com.afyke.pageforge.system.request.FunctionUpdateRequest;
import com.afyke.pageforge.system.request.RoleAssignFunctionsRequest;
import com.afyke.pageforge.system.request.RoleCreateRequest;
import com.afyke.pageforge.system.request.RolePageRequest;
import com.afyke.pageforge.system.request.RoleUpdateRequest;
import com.afyke.pageforge.system.request.UserAssignRolesRequest;
import com.afyke.pageforge.system.request.UserPageRequest;
import com.afyke.pageforge.system.vo.FunctionVO;
import com.afyke.pageforge.system.vo.RoleVO;
import com.afyke.pageforge.system.vo.SystemUserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * 简化的权限管理服务。
 * 只维护“用户关联角色、角色关联功能”两层关系，不引入复杂的企业级权限模型。
 */
@Service
@RequiredArgsConstructor
public class PermissionManagementService {

    /** 用户 Mapper。 */
    private final UserMapper userMapper;

    /** 角色 Mapper。 */
    private final RoleMapper roleMapper;

    /** 功能 Mapper。 */
    private final FunctionMapper functionMapper;

    /** 用户角色关系 Mapper。 */
    private final UserRoleMapper userRoleMapper;

    /** 角色功能关系 Mapper。 */
    private final RoleFunctionMapper roleFunctionMapper;

    /** 分页查询用户及其当前角色，响应中不会返回密码。 */
    public PageResult<SystemUserVO> pageUsers(UserPageRequest request) {
        LambdaQueryWrapper<UserEntity> query = new LambdaQueryWrapper<>();
        query.and(StringUtils.hasText(request.getKeyword()), wrapper -> wrapper
                        .like(UserEntity::getUsername, request.getKeyword())
                        .or()
                        .like(UserEntity::getNickname, request.getKeyword()))
                .orderByDesc(UserEntity::getId);
        Page<UserEntity> page = userMapper.selectPage(
                Page.of(request.getPageNum(), request.getPageSize()), query);
        List<SystemUserVO> records = page.getRecords().stream()
                .map(this::toUserVO)
                .toList();
        return toPageResult(page, records);
    }

    /** 分页查询角色及角色已经关联的功能。 */
    public PageResult<RoleVO> pageRoles(RolePageRequest request) {
        LambdaQueryWrapper<RoleEntity> query = new LambdaQueryWrapper<>();
        query.and(StringUtils.hasText(request.getKeyword()), wrapper -> wrapper
                        .like(RoleEntity::getRoleCode, request.getKeyword())
                        .or()
                        .like(RoleEntity::getRoleName, request.getKeyword()))
                .orderByAsc(RoleEntity::getId);
        Page<RoleEntity> page = roleMapper.selectPage(
                Page.of(request.getPageNum(), request.getPageSize()), query);
        List<RoleVO> records = page.getRecords().stream()
                .map(this::toRoleVO)
                .toList();
        return toPageResult(page, records);
    }

    /** 分页查询所有可分配的功能。 */
    public PageResult<FunctionVO> pageFunctions(FunctionPageRequest request) {
        LambdaQueryWrapper<FunctionEntity> query = new LambdaQueryWrapper<>();
        query.and(StringUtils.hasText(request.getKeyword()), wrapper -> wrapper
                        .like(FunctionEntity::getFunctionCode, request.getKeyword())
                        .or()
                        .like(FunctionEntity::getFunctionName, request.getKeyword()))
                .orderByAsc(FunctionEntity::getId);
        Page<FunctionEntity> page = functionMapper.selectPage(
                Page.of(request.getPageNum(), request.getPageSize()), query);
        List<FunctionVO> records = page.getRecords().stream()
                .map(this::toFunctionVO)
                .toList();
        return toPageResult(page, records);
    }

    /** 新增角色，角色编码在有效数据中不能重复。 */
    @Transactional(rollbackFor = Exception.class)
    public Long createRole(RoleCreateRequest request) {
        ensureRoleCodeAvailable(request.getRoleCode(), null);
        RoleEntity entity = new RoleEntity();
        entity.setRoleCode(request.getRoleCode());
        entity.setRoleName(request.getRoleName());
        entity.setDescription(request.getDescription());
        roleMapper.insert(entity);
        return entity.getId();
    }

    /** 修改角色基础信息，不会改变角色已经拥有的功能。 */
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleUpdateRequest request) {
        RoleEntity entity = requireRole(request.getId());
        ensureRoleCodeAvailable(request.getRoleCode(), request.getId());
        entity.setRoleCode(request.getRoleCode());
        entity.setRoleName(request.getRoleName());
        entity.setDescription(request.getDescription());
        roleMapper.updateById(entity);
    }

    /** 新增前端菜单或按钮所使用的功能编码。 */
    @Transactional(rollbackFor = Exception.class)
    public Long createFunction(FunctionCreateRequest request) {
        ensureFunctionCodeAvailable(request.getFunctionCode(), null);
        FunctionEntity entity = new FunctionEntity();
        entity.setFunctionCode(request.getFunctionCode());
        entity.setFunctionName(request.getFunctionName());
        entity.setDescription(request.getDescription());
        functionMapper.insert(entity);
        return entity.getId();
    }

    /** 修改功能基础信息，不会自动改变角色关联关系。 */
    @Transactional(rollbackFor = Exception.class)
    public void updateFunction(FunctionUpdateRequest request) {
        FunctionEntity entity = requireFunction(request.getId());
        ensureFunctionCodeAvailable(request.getFunctionCode(), request.getId());
        entity.setFunctionCode(request.getFunctionCode());
        entity.setFunctionName(request.getFunctionName());
        entity.setDescription(request.getDescription());
        functionMapper.updateById(entity);
    }

    /**
     * 覆盖用户的全部角色。
     * 先停用旧关系，再启用本次提交的关系，因此前端每次提交的是完整角色 ID 列表。
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignUserRoles(UserAssignRolesRequest request) {
        requireUser(request.getUserId());
        List<Long> roleIds = distinctIds(request.getRoleIds());
        ensureRolesExist(roleIds);

        userRoleMapper.update(null, new LambdaUpdateWrapper<UserRoleEntity>()
                .eq(UserRoleEntity::getUserId, request.getUserId())
                .set(UserRoleEntity::getIsValid, 0)
                .set(UserRoleEntity::getGmtModified, new Date())
                .set(UserRoleEntity::getModifier, "system"));
        roleIds.forEach(roleId ->
                userRoleMapper.enableRelation(request.getUserId(), roleId));
    }

    /**
     * 覆盖角色的全部功能。
     * 传入空列表可以清空该角色的功能权限。
     */
    @Transactional(rollbackFor = Exception.class)
    public void assignRoleFunctions(RoleAssignFunctionsRequest request) {
        requireRole(request.getRoleId());
        List<Long> functionIds = distinctIds(request.getFunctionIds());
        ensureFunctionsExist(functionIds);

        roleFunctionMapper.update(null, new LambdaUpdateWrapper<RoleFunctionEntity>()
                .eq(RoleFunctionEntity::getRoleId, request.getRoleId())
                .set(RoleFunctionEntity::getIsValid, 0)
                .set(RoleFunctionEntity::getGmtModified, new Date())
                .set(RoleFunctionEntity::getModifier, "system"));
        functionIds.forEach(functionId ->
                roleFunctionMapper.enableRelation(request.getRoleId(), functionId));
    }

    private SystemUserVO toUserVO(UserEntity entity) {
        SystemUserVO result = new SystemUserVO();
        result.setId(entity.getId());
        result.setUsername(entity.getUsername());
        result.setNickname(entity.getNickname());
        result.setStatus(entity.getStatus());
        result.setRoleIds(userRoleMapper.selectList(
                        new LambdaQueryWrapper<UserRoleEntity>()
                                .eq(UserRoleEntity::getUserId, entity.getId())
                                .orderByAsc(UserRoleEntity::getRoleId))
                .stream()
                .map(UserRoleEntity::getRoleId)
                .toList());
        return result;
    }

    private RoleVO toRoleVO(RoleEntity entity) {
        RoleVO result = new RoleVO();
        result.setId(entity.getId());
        result.setRoleCode(entity.getRoleCode());
        result.setRoleName(entity.getRoleName());
        result.setDescription(entity.getDescription());
        result.setFunctionIds(roleFunctionMapper.selectList(
                        new LambdaQueryWrapper<RoleFunctionEntity>()
                                .eq(RoleFunctionEntity::getRoleId, entity.getId())
                                .orderByAsc(RoleFunctionEntity::getFunctionId))
                .stream()
                .map(RoleFunctionEntity::getFunctionId)
                .toList());
        return result;
    }

    private FunctionVO toFunctionVO(FunctionEntity entity) {
        FunctionVO result = new FunctionVO();
        result.setId(entity.getId());
        result.setFunctionCode(entity.getFunctionCode());
        result.setFunctionName(entity.getFunctionName());
        result.setDescription(entity.getDescription());
        return result;
    }

    private void ensureRoleCodeAvailable(String roleCode, Long excludedId) {
        Long count = roleMapper.selectCount(new LambdaQueryWrapper<RoleEntity>()
                .eq(RoleEntity::getRoleCode, roleCode)
                .ne(excludedId != null, RoleEntity::getId, excludedId));
        if (count > 0) {
            throw BusinessException.badRequest("ROLE_CODE_EXISTS", "角色编码已存在");
        }
    }

    private void ensureFunctionCodeAvailable(String functionCode, Long excludedId) {
        Long count = functionMapper.selectCount(new LambdaQueryWrapper<FunctionEntity>()
                .eq(FunctionEntity::getFunctionCode, functionCode)
                .ne(excludedId != null, FunctionEntity::getId, excludedId));
        if (count > 0) {
            throw BusinessException.badRequest("FUNCTION_CODE_EXISTS", "功能编码已存在");
        }
    }

    private UserEntity requireUser(Long userId) {
        UserEntity entity = userMapper.selectById(userId);
        if (entity == null) {
            throw BusinessException.badRequest("USER_NOT_FOUND", "用户不存在");
        }
        return entity;
    }

    private RoleEntity requireRole(Long roleId) {
        RoleEntity entity = roleMapper.selectById(roleId);
        if (entity == null) {
            throw BusinessException.badRequest("ROLE_NOT_FOUND", "角色不存在");
        }
        return entity;
    }

    private FunctionEntity requireFunction(Long functionId) {
        FunctionEntity entity = functionMapper.selectById(functionId);
        if (entity == null) {
            throw BusinessException.badRequest("FUNCTION_NOT_FOUND", "功能不存在");
        }
        return entity;
    }

    private void ensureRolesExist(List<Long> roleIds) {
        if (!roleIds.isEmpty() && roleMapper.selectCount(
                new LambdaQueryWrapper<RoleEntity>().in(RoleEntity::getId, roleIds))
                != roleIds.size()) {
            throw BusinessException.badRequest("ROLE_NOT_FOUND", "角色列表中包含不存在的角色");
        }
    }

    private void ensureFunctionsExist(List<Long> functionIds) {
        if (!functionIds.isEmpty() && functionMapper.selectCount(
                new LambdaQueryWrapper<FunctionEntity>().in(FunctionEntity::getId, functionIds))
                != functionIds.size()) {
            throw BusinessException.badRequest(
                    "FUNCTION_NOT_FOUND", "功能列表中包含不存在的功能");
        }
    }

    /** 去重并保留前端提交顺序，避免重复写入同一关联关系。 */
    private List<Long> distinctIds(List<Long> ids) {
        return List.copyOf(new LinkedHashSet<>(ids));
    }

    private <E, V> PageResult<V> toPageResult(Page<E> page, List<V> records) {
        PageResult<V> result = new PageResult<>();
        result.setTotal(page.getTotal());
        result.setPageNum(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setRecords(records);
        return result;
    }
}
