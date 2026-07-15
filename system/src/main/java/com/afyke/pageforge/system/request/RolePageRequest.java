package com.afyke.pageforge.system.request;

import com.afyke.pageforge.common.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 系统角色分页查询参数。 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RolePageRequest extends PageRequest {

    /** 角色编码或名称关键字。 */
    private String keyword;
}
