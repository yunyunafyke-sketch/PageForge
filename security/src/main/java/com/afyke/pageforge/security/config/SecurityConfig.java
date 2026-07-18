package com.afyke.pageforge.security.config;

import com.afyke.pageforge.common.model.ResultModel;
import com.afyke.pageforge.common.util.WebRequestUtils;
import com.afyke.pageforge.security.filter.ApiRequestLoggingFilter;
import com.afyke.pageforge.security.filter.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.nio.charset.StandardCharsets;

/** Spring Security 配置。 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
@Slf4j
public class SecurityConfig {

    /** JWT 认证过滤器。 */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /** JSON 序列化工具。 */
    private final ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // JWT 本身不依赖服务端 Session，因此使用无状态认证。
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // 登录、注册、公开查询和接口文档无需携带 Token。
                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/public/**").permitAll()
                        .requestMatchers("/doc.html", "/webjars/**", "/v3/api-docs/**").permitAll()
                        // 管理员接口只允许 ADMIN，普通用户接口允许 ADMIN 和 USER。
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/user/**").hasAnyAuthority("ADMIN", "USER")
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("请求未认证 method={} uri={} clientIp={} reason={}",
                                    request.getMethod(), request.getRequestURI(),
                                    WebRequestUtils.resolveClientIp(request),
                                    authException.getClass().getSimpleName());
                            writeError(response, 401, "UNAUTHORIZED", "请先登录");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.warn("请求无权限 method={} uri={} user={} clientIp={}",
                                    request.getMethod(), request.getRequestURI(),
                                    request.getUserPrincipal() == null
                                            ? "-" : WebRequestUtils.sanitizeLogValue(
                                                    request.getUserPrincipal().getName()),
                                    WebRequestUtils.resolveClientIp(request));
                            writeError(response, 403, "FORBIDDEN", "无权访问");
                        }))
                // 在 Spring 默认账号密码认证过滤器之前解析 JWT。
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 先登记 JWT 过滤器顺序，再把请求日志放到它之前，以便认证日志共享链路标识。
                .addFilterBefore(new ApiRequestLoggingFilter(), JwtAuthenticationFilter.class);
        return http.build();
    }

    private void writeError(
            HttpServletResponse response,
            int status,
            String errorCode,
            String message) throws java.io.IOException {
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(
                response.getWriter(),
                ResultModel.failure(status, errorCode, message));
    }
}
