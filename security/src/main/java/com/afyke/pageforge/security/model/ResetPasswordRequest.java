package com.afyke.pageforge.security.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** 管理员重置用户密码请求。 */
@Data
public class ResetPasswordRequest {

    /** 需要重置密码的用户 ID。 */
    @NotNull(message = "用户 ID 不能为空")
    private Long userId;
}
