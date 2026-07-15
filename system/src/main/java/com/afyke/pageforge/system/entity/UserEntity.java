package com.afyke.pageforge.system.entity;

import com.afyke.pageforge.common.model.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 系统用户实体。 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class UserEntity extends BaseEntity {

    /** 用户 ID。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录用户名。 */
    private String username;

    /** BCrypt 加密密码。 */
    private String password;

    /** 用户昵称。 */
    private String nickname;

    /** 账号状态：1 启用，0 禁用。 */
    private Integer status;
}
