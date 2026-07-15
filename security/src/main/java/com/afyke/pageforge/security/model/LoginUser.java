package com.afyke.pageforge.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/** 当前登录用户。 */
@Data
@AllArgsConstructor
public class LoginUser {

    /** 用户 ID。 */
    private Long userId;

    /** 用户名。 */
    private String username;

    /** 用户拥有的角色列表。 */
    private List<String> roles;
}
