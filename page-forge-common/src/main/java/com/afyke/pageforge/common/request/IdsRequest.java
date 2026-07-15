package com.afyke.pageforge.common.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量主键请求。
 */
@Data
public class IdsRequest {

    /** 主键 ID 列表。 */
    @NotEmpty(message = "ID 列表不能为空")
    private List<Long> ids;
}
