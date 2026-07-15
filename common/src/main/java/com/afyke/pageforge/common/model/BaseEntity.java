package com.afyke.pageforge.common.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体。
 */
@Data
public class BaseEntity implements Serializable {

    /** 创建时间。 */
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    /** 修改时间。 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

    /** 创建者 ID。 */
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    /** 修改者 ID。 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String modifier;

    /** 有效标识：1 有效，0 无效。 */
    @TableField(fill = FieldFill.INSERT)
    private Integer isValid;
}
