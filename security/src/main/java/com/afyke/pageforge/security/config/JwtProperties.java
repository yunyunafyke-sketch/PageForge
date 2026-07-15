package com.afyke.pageforge.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** JWT 配置。 */
@Data
@ConfigurationProperties(prefix = "page-forge.jwt")
public class JwtProperties {

    /** JWT 签名密钥，长度至少 32 字节。 */
    private String secret;

    /** Token 有效期，单位为秒。 */
    private Long expirationSeconds = 7200L;
}
