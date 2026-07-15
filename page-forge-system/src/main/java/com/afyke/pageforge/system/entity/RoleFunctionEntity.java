package com.afyke.pageforge.system.entity;

import com.afyke.pageforge.common.model.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 角色功能关系实体。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_function")
public class RoleFunctionEntity extends BaseEntity {

    /** 关系 ID。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色 ID。 */
    private Long roleId;

    /** 功能 ID。 */
    private Long functionId;
}
