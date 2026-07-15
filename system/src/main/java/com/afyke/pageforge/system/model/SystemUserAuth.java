package com.afyke.pageforge.system.model;

import lombok.Data;

import java.util.List;

/** 登录用户认证信息。 */
@Data
public class SystemUserAuth {

    /** 用户 ID。 */
    private Long userId;

    /** 登录用户名。 */
    private String username;

    /** BCrypt 加密密码。 */
    private String password;

    /** 用户拥有的角色编码列表。 */
    private List<String> roles;

    /** 用户拥有的功能编码列表。 */
    private List<String> functions;
}
