package com.afyke.pageforge.system.controller;

import com.afyke.pageforge.common.model.PageResult;
import com.afyke.pageforge.common.model.ResultModel;
import com.afyke.pageforge.system.request.FunctionCreateRequest;
import com.afyke.pageforge.system.request.FunctionPageRequest;
import com.afyke.pageforge.system.request.FunctionUpdateRequest;
import com.afyke.pageforge.system.request.RoleAssignFunctionsRequest;
import com.afyke.pageforge.system.request.RoleCreateRequest;
import com.afyke.pageforge.system.request.RolePageRequest;
import com.afyke.pageforge.system.request.RoleUpdateRequest;
import com.afyke.pageforge.system.request.UserAssignRolesRequest;
import com.afyke.pageforge.system.request.UserPageRequest;
import com.afyke.pageforge.system.service.PermissionManagementService;
import com.afyke.pageforge.system.vo.FunctionVO;
import com.afyke.pageforge.system.vo.RoleVO;
import com.afyke.pageforge.system.vo.SystemUserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 管理员权限管理接口。 */
@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
public class PermissionManagementController {

    /** 权限管理服务。 */
    private final PermissionManagementService permissionManagementService;

    /** 查询用户及其已经分配的角色。 */
    @PostMapping("/user/page")
    public ResultModel<PageResult<SystemUserVO>> pageUsers(
            @Valid @RequestBody UserPageRequest request) {
        return ResultModel.success(permissionManagementService.pageUsers(request));
    }

    /** 使用完整角色 ID 列表覆盖指定用户的角色。 */
    @PostMapping("/user/assign-roles")
    public ResultModel<Boolean> assignUserRoles(
            @Valid @RequestBody UserAssignRolesRequest request) {
        permissionManagementService.assignUserRoles(request);
        return ResultModel.success();
    }

    /** 查询角色及其已经分配的功能。 */
    @PostMapping("/role/page")
    public ResultModel<PageResult<RoleVO>> pageRoles(
            @Valid @RequestBody RolePageRequest request) {
        return ResultModel.success(permissionManagementService.pageRoles(request));
    }

    /** 新增角色。 */
    @PostMapping("/role/create")
    public ResultModel<Long> createRole(
            @Valid @RequestBody RoleCreateRequest request) {
        return ResultModel.success(permissionManagementService.createRole(request));
    }

    /** 修改角色基础信息。 */
    @PostMapping("/role/update")
    public ResultModel<Boolean> updateRole(
            @Valid @RequestBody RoleUpdateRequest request) {
        permissionManagementService.updateRole(request);
        return ResultModel.success();
    }

    /** 使用完整功能 ID 列表覆盖指定角色的功能。 */
    @PostMapping("/role/assign-functions")
    public ResultModel<Boolean> assignRoleFunctions(
            @Valid @RequestBody RoleAssignFunctionsRequest request) {
        permissionManagementService.assignRoleFunctions(request);
        return ResultModel.success();
    }

    /** 查询可以分配给角色的功能。 */
    @PostMapping("/function/page")
    public ResultModel<PageResult<FunctionVO>> pageFunctions(
            @Valid @RequestBody FunctionPageRequest request) {
        return ResultModel.success(permissionManagementService.pageFunctions(request));
    }

    /** 新增菜单或按钮功能。 */
    @PostMapping("/function/create")
    public ResultModel<Long> createFunction(
            @Valid @RequestBody FunctionCreateRequest request) {
        return ResultModel.success(permissionManagementService.createFunction(request));
    }

    /** 修改功能基础信息。 */
    @PostMapping("/function/update")
    public ResultModel<Boolean> updateFunction(
            @Valid @RequestBody FunctionUpdateRequest request) {
        permissionManagementService.updateFunction(request);
        return ResultModel.success();
    }
}
