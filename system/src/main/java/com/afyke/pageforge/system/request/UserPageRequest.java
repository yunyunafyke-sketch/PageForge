package com.afyke.pageforge.system.request;

import com.afyke.pageforge.common.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 系统用户分页查询参数。 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageRequest extends PageRequest {

    /** 用户名或昵称关键字。 */
    private String keyword;
}
