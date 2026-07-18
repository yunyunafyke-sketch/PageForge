package com.afyke.pageforge.security.filter;

import com.afyke.pageforge.common.util.WebRequestUtils;
import com.afyke.pageforge.security.model.LoginUser;
import com.afyke.pageforge.security.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/** JWT 认证过滤器。 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** JWT Token 服务。 */
    private final JwtTokenService jwtTokenService;

    /**
     * 每个请求只执行一次的 JWT 认证流程。
     * 从请求头读取 Token，校验成功后把用户和角色放入 Spring Security 上下文。
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // 客户端请求头格式：Authorization: Bearer <token>。
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization)
                && authorization.startsWith("Bearer ")
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                LoginUser loginUser = jwtTokenService.parseToken(authorization.substring(7));
                // 把角色转换为 Spring Security 能识别的权限对象。
                List<SimpleGrantedAuthority> authorities = loginUser.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(loginUser, null, authorities);
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                // 设置认证信息后，后面的接口即可判断当前用户是否已登录及其角色。
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception exception) {
                // Token 无效或已过期时不建立登录状态，后续由 Security 返回 401。
                log.warn("JWT 认证失败 method={} uri={} clientIp={} reason={}",
                        request.getMethod(),
                        request.getRequestURI(),
                        WebRequestUtils.resolveClientIp(request),
                        exception.getClass().getSimpleName());
                SecurityContextHolder.clearContext();
            }
        }
        // 无论是否携带 Token，都必须继续执行后续过滤器。
        filterChain.doFilter(request, response);
    }
}
