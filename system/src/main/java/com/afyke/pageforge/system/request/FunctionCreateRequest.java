package com.afyke.pageforge.system.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 系统功能新增参数。 */
@Data
public class FunctionCreateRequest {

    /** 功能编码，格式为模块:资源:操作。 */
    @NotBlank(message = "功能编码不能为空")
    @Pattern(
            regexp = "^[a-z][a-z0-9-]*:[a-z][a-z0-9-]*:[a-z][a-z0-9-]*$",
            message = "功能编码格式必须为模块:资源:操作")
    private String functionCode;

    /** 功能名称。 */
    @NotBlank(message = "功能名称不能为空")
    @Size(max = 64, message = "功能名称不能超过 64 个字符")
    private String functionName;

    /** 功能说明。 */
    @Size(max = 255, message = "功能说明不能超过 255 个字符")
    private String description;
}
