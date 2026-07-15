package com.afyke.pageforge.business.staff.vo;

import lombok.Data;

import java.util.Date;

/** 外包人员返回对象。 */
@Data
public class OutsourcedStaffVO {

    /** 外包人员 ID。 */
    private Long id;

    /** 姓名。 */
    private String name;

    /** 账号 ID。 */
    private String accountId;

    /** 手机号。 */
    private String phone;

    /** 状态：1 启用，0 禁用。 */
    private Integer status;

    /** 创建时间。 */
    private Date gmtCreate;

    /** 修改时间。 */
    private Date gmtModified;
}
