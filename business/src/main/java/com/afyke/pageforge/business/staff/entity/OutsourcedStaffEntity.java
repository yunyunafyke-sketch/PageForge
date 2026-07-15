package com.afyke.pageforge.business.staff.entity;

import com.afyke.pageforge.common.model.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 外包人员实体。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_outsourced_staff")
public class OutsourcedStaffEntity extends BaseEntity {

    /** 外包人员 ID。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 姓名。 */
    private String name;

    /** 账号 ID。 */
    private String accountId;

    /** 手机号。 */
    private String phone;

    /** 状态：1 启用，0 禁用。 */
    private Integer status;
}
