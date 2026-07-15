package com.afyke.pageforge.common.oss.model;

import lombok.Builder;
import lombok.Data;

/** OSS 文件信息。 */
@Data
@Builder
public class OssFileVO {

    /** 原始文件名。 */
    private String originalName;

    /** OSS 文件标识。 */
    private String objectKey;

    /** 文件访问地址。 */
    private String url;

    /** 文件大小，单位为字节。 */
    private Long size;
}
