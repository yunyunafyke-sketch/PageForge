package com.afyke.pageforge.common.oss.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/** OSS 文件上传参数。 */
@Data
public class OssUploadRequest {

    /** 上传文件。 */
    @NotNull(message = "请选择需要上传的文件")
    private MultipartFile file;

    /** 存储目录，只允许字母、数字、斜杠、横线和下划线。 */
    @Pattern(regexp = "^[a-zA-Z0-9/_-]{0,64}$", message = "文件目录格式错误")
    private String directory;
}
