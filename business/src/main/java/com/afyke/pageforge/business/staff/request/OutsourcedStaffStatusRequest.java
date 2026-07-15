package com.afyke.pageforge.business.staff.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** 修改外包人员状态请求。 */
@Data
public class OutsourcedStaffStatusRequest {

    /** 外包人员 ID。 */
    @NotNull(message = "ID 不能为空")
    private Long id;

    /** 状态：1 启用，0 禁用。 */
    @NotNull(message = "状态不能为空")
    private Integer status;
}
