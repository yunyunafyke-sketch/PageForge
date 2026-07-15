package com.afyke.pageforge.common.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 单个主键请求。
 */
@Data
public class IdRequest {

    /** 主键 ID。 */
    @NotNull(message = "ID 不能为空")
    private Long id;
}
