package com.afyke.pageforge.system.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 系统角色新增参数。 */
@Data
public class RoleCreateRequest {

    /** 角色编码，例如 MANAGER。 */
    @NotBlank(message = "角色编码不能为空")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]{1,31}$", message = "角色编码格式错误")
    private String roleCode;

    /** 角色名称。 */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 64, message = "角色名称不能超过 64 个字符")
    private String roleName;

    /** 角色说明。 */
    @Size(max = 255, message = "角色说明不能超过 255 个字符")
    private String description;
}
