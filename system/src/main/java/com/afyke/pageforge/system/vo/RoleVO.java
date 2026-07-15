package com.afyke.pageforge.system.vo;

import lombok.Data;

import java.util.List;

/** 系统角色信息。 */
@Data
public class RoleVO {

    /** 角色 ID。 */
    private Long id;

    /** 角色编码。 */
    private String roleCode;

    /** 角色名称。 */
    private String roleName;

    /** 角色说明。 */
    private String description;

    /** 角色当前拥有的功能 ID 列表。 */
    private List<Long> functionIds;
}
