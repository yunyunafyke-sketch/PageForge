package com.afyke.pageforge.security.service;

import com.afyke.pageforge.security.config.JwtProperties;
import com.afyke.pageforge.security.model.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/** JWT Token 服务。 */
@Service
@RequiredArgsConstructor
public class JwtTokenService {

    /** JWT 配置。 */
    private final JwtProperties jwtProperties;

    /**
     * 登录成功后生成 Token。
     * 用户 ID 放在 subject 中，用户名和角色放在自定义载荷中。
     *
     * @param userId 用户 ID
     * @param username 用户名
     * @param roles 用户角色
     * @return 已签名的 JWT Token
     */
    public String createToken(Long userId, String username, List<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                // subject 用于唯一标识当前用户。
                .subject(String.valueOf(userId))
                .claim("username", username)
                // 角色写入 Token，请求接口时直接用于管理员或普通用户判断。
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(jwtProperties.getExpirationSeconds())))
                // 使用服务端密钥签名，防止客户端伪造或修改 Token。
                .signWith(signingKey())
                .compact();
    }

    /**
     * 校验并解析 Token。
     * 签名错误、格式错误或 Token 过期时，JWT 组件会直接抛出异常。
     *
     * @param token 客户端提交的 JWT Token
     * @return 当前登录用户
     */
    public LoginUser parseToken(String token) {
        Claims claims = Jwts.parser()
                // 必须使用生成 Token 时的同一密钥校验签名。
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        List<?> values = claims.get("roles", List.class);
        List<String> roles = values == null
                ? List.of()
                : values.stream().map(String::valueOf).toList();
        return new LoginUser(
                Long.valueOf(claims.getSubject()),
                claims.get("username", String.class),
                roles);
    }

    /** 根据配置的字符串密钥生成 HMAC-SHA 签名密钥。 */
    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}
