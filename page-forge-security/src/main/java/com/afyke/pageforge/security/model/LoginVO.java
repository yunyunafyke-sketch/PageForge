package com.afyke.pageforge.security.model;

import lombok.Data;

import java.util.List;

/** 登录结果。 */
@Data
public class LoginVO {

    /** 登录凭证。 */
    private String token;

    /** 用户 ID。 */
    private Long userId;

    /** 用户名。 */
    private String username;

    /** 用户拥有的角色列表。 */
    private List<String> roles;

    /** 用户拥有的功能列表。 */
    private List<String> functions;
}
