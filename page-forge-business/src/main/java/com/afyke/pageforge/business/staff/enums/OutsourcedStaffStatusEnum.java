package com.afyke.pageforge.business.staff.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 外包人员状态。 */
@Getter
@AllArgsConstructor
public enum OutsourcedStaffStatusEnum {

    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    /** 状态值。 */
    private final Integer value;

    /** 状态说明。 */
    private final String description;
}
