package com.afyke.pageforge.security.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 当前用户修改密码请求。 */
@Data
public class ChangePasswordRequest {

    /** 当前登录密码。 */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /** 准备设置的新密码。 */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, max = 32, message = "新密码长度须为 8 至 32 位")
    private String newPassword;
}
