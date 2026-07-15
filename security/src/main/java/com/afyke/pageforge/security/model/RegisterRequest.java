package com.afyke.pageforge.security.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 用户注册请求。 */
@Data
public class RegisterRequest {

    /** 登录用户名，只允许字母、数字和下划线。 */
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[A-Za-z0-9_]{4,32}$", message = "用户名须为 4 至 32 位字母、数字或下划线")
    private String username;

    /** 登录密码。 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 32, message = "密码长度须为 8 至 32 位")
    private String password;

    /** 用户昵称。 */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 32, message = "昵称不能超过 32 位")
    private String nickname;
}
