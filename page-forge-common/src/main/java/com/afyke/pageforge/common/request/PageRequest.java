package com.afyke.pageforge.common.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 分页请求。
 */
@Data
public class PageRequest {

    /** 当前页码。 */
    @Min(value = 1, message = "页码不能小于 1")
    private Long pageNum = 1L;

    /** 每页数据条数。 */
    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 200, message = "每页条数不能大于 200")
    private Long pageSize = 10L;
}
