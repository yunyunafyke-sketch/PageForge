package com.afyke.pageforge.system.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/** 角色功能分配参数。 */
@Data
public class RoleAssignFunctionsRequest {

    /** 角色 ID。 */
    @NotNull(message = "角色 ID 不能为空")
    private Long roleId;

    /** 角色需要拥有的全部功能 ID，传空列表表示清空功能。 */
    @NotNull(message = "功能列表不能为空")
    private List<@Valid @NotNull(message = "功能 ID 不能为空") Long> functionIds;
}
