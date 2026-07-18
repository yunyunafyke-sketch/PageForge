package com.afyke.pageforge.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

/** Web 请求日志辅助工具。 */
public final class WebRequestUtils {

    /** 日志字段允许保留的最大长度。 */
    private static final int MAX_LOG_VALUE_LENGTH = 128;

    private WebRequestUtils() {
    }

    /**
     * 获取客户端 IP，优先读取反向代理传递的请求头。
     * X-Forwarded-For 可能包含多级代理地址，只取最前面的原始客户端地址。
     */
    public static String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwardedFor)) {
            return sanitizeLogValue(forwardedFor.split(",", 2)[0].trim());
        }
        String realIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(realIp)) {
            return sanitizeLogValue(realIp.trim());
        }
        return sanitizeLogValue(request.getRemoteAddr());
    }

    /** 清理换行符并限制长度，避免外部输入污染日志格式。 */
    public static String sanitizeLogValue(String value) {
        if (!StringUtils.hasText(value)) {
            return "-";
        }
        String sanitized = value.replace('\r', '_').replace('\n', '_');
        return sanitized.length() <= MAX_LOG_VALUE_LENGTH
                ? sanitized
                : sanitized.substring(0, MAX_LOG_VALUE_LENGTH);
    }
}
