package com.afyke.pageforge.common.oss.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** OSS 文件删除参数。 */
@Data
public class OssDeleteRequest {

    /** OSS 文件标识。 */
    @NotBlank(message = "文件标识不能为空")
    private String objectKey;
}
