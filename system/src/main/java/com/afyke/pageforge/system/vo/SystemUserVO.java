package com.afyke.pageforge.system.vo;

import lombok.Data;

import java.util.List;

/** 系统用户权限信息。 */
@Data
public class SystemUserVO {

    /** 用户 ID。 */
    private Long id;

    /** 登录用户名。 */
    private String username;

    /** 用户昵称。 */
    private String nickname;

    /** 账号状态：1 启用，0 禁用。 */
    private Integer status;

    /** 用户当前拥有的角色 ID 列表。 */
    private List<Long> roleIds;
}
