package com.afyke.pageforge.business.staff.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** 修改外包人员请求。 */
@Data
public class OutsourcedStaffUpdateRequest {

    /** 外包人员 ID。 */
    @NotNull(message = "ID 不能为空")
    private Long id;

    /** 姓名。 */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /** 账号 ID。 */
    @NotBlank(message = "账号 ID 不能为空")
    private String accountId;

    /** 手机号。 */
    private String phone;

    /** 状态：1 启用，0 禁用。 */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
