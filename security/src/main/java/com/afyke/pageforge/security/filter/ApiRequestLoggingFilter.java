package com.afyke.pageforge.security.filter;

import com.afyke.pageforge.common.util.WebRequestUtils;
import com.afyke.pageforge.security.model.LoginUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/** API 请求日志过滤器。 */
@Slf4j
public class ApiRequestLoggingFilter extends OncePerRequestFilter {

    /** 请求链路标识在请求头和响应头中的名称。 */
    static final String TRACE_ID_HEADER = "X-Request-Id";

    /** 请求链路标识在日志上下文中的字段名。 */
    private static final String TRACE_ID_MDC_KEY = "traceId";

    /**
     * 记录每个 API 请求的完成状态和耗时，并通过链路标识关联同一次请求内的业务日志。
     * 请求体和查询参数可能包含密码、Token 等敏感信息，因此不会写入日志。
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String traceId = resolveTraceId(request);
        MDC.put(TRACE_ID_MDC_KEY, traceId);
        response.setHeader(TRACE_ID_HEADER, traceId);

        Exception failure = null;
        try {
            filterChain.doFilter(request, response);
        } catch (IOException | ServletException | RuntimeException exception) {
            failure = exception;
            throw exception;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            RequestUser requestUser = resolveRequestUser();
            String method = WebRequestUtils.sanitizeLogValue(request.getMethod());
            String uri = WebRequestUtils.sanitizeLogValue(request.getRequestURI());
            String clientIp = WebRequestUtils.resolveClientIp(request);
            int status = response.getStatus();
            if (failure != null) {
                log.error("API 请求失败 method={} uri={} status={} durationMs={} userId={} username={} clientIp={} traceId={} reason={}",
                        method, uri, status, duration, requestUser.userId(), requestUser.username(), clientIp,
                        traceId, failure.getClass().getSimpleName());
            } else if (status >= 500) {
                log.error("API 请求失败 method={} uri={} status={} durationMs={} userId={} username={} clientIp={} traceId={}",
                        method, uri, status, duration, requestUser.userId(), requestUser.username(), clientIp, traceId);
            } else if (status >= 400) {
                log.warn("API 请求失败 method={} uri={} status={} durationMs={} userId={} username={} clientIp={} traceId={}",
                        method, uri, status, duration, requestUser.userId(), requestUser.username(), clientIp, traceId);
            } else {
                log.info("API 请求成功 method={} uri={} status={} durationMs={} userId={} username={} clientIp={} traceId={}",
                        method, uri, status, duration, requestUser.userId(), requestUser.username(), clientIp, traceId);
            }
            MDC.remove(TRACE_ID_MDC_KEY);
        }
    }

    /** 只记录业务 API，避免静态资源和接口文档产生大量无效日志。 */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    /** 优先复用网关传入的合法链路标识，否则生成新的 UUID。 */
    private String resolveTraceId(HttpServletRequest request) {
        String requestId = request.getHeader(TRACE_ID_HEADER);
        if (StringUtils.hasText(requestId) && requestId.matches("[A-Za-z0-9._-]{1,64}")) {
            return requestId;
        }
        return UUID.randomUUID().toString().replace("-", "");
    }

    /** 从 Spring Security 上下文提取当前请求用户，公开接口使用占位值。 */
    private RequestUser resolveRequestUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser loginUser) {
            return new RequestUser(
                    String.valueOf(loginUser.getUserId()),
                    WebRequestUtils.sanitizeLogValue(loginUser.getUsername()));
        }
        return new RequestUser("-", "-");
    }

    /** 请求日志中的用户信息。 */
    private record RequestUser(String userId, String username) {
    }
}
