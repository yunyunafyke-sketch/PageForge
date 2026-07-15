package com.afyke.pageforge.business.staff.request;

import com.afyke.pageforge.common.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 外包人员分页请求。 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OutsourcedStaffPageRequest extends PageRequest {

    /** 姓名关键字。 */
    private String name;

    /** 状态：1 启用，0 禁用。 */
    private Integer status;
}
