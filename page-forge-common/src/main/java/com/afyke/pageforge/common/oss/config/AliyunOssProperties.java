package com.afyke.pageforge.common.oss.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/** 阿里云 OSS 配置。 */
@Data
@Validated
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOssProperties {

    /** OSS 服务地址。 */
    @NotBlank
    private String endpoint;

    /** 访问密钥 ID。 */
    @NotBlank
    private String accessKeyId;

    /** 访问密钥 Secret。 */
    @NotBlank
    private String accessKeySecret;

    /** 存储空间名称。 */
    @NotBlank
    private String bucket;

    /** 文件公开访问域名。 */
    @NotBlank
    private String publicDomain;
}
