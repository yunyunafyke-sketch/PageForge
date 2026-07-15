package com.afyke.pageforge.system.vo;

import lombok.Data;

/** 系统功能信息。 */
@Data
public class FunctionVO {

    /** 功能 ID。 */
    private Long id;

    /** 功能编码。 */
    private String functionCode;

    /** 功能名称。 */
    private String functionName;

    /** 功能说明。 */
    private String description;
}
