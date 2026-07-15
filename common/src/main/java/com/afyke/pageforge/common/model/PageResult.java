package com.afyke.pageforge.common.model;

import lombok.Data;

import java.util.List;

/**
 * 统一分页结果。
 *
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> {

    /** 数据总数。 */
    private Long total;

    /** 当前页数据列表。 */
    private List<T> records;

    /** 当前页码。 */
    private Long pageNum;

    /** 每页数据条数。 */
    private Long pageSize;
}
