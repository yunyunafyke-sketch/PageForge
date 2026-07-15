package com.afyke.pageforge.system.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/** 用户角色分配参数。 */
@Data
public class UserAssignRolesRequest {

    /** 用户 ID。 */
    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    /** 用户需要拥有的全部角色 ID，传空列表表示清空角色。 */
    @NotNull(message = "角色列表不能为空")
    private List<@Valid @NotNull(message = "角色 ID 不能为空") Long> roleIds;
}
