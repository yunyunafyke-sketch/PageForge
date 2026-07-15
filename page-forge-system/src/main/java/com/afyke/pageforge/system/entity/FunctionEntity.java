package com.afyke.pageforge.system.entity;

import com.afyke.pageforge.common.model.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 系统功能实体。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_function")
public class FunctionEntity extends BaseEntity {

    /** 功能 ID。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 功能编码。 */
    private String functionCode;

    /** 功能名称。 */
    private String functionName;

    /** 功能说明。 */
    private String description;
}
