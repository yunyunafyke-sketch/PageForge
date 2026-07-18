package com.afyke.pageforge.security.filter;

import com.afyke.pageforge.security.model.LoginUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** API 请求日志过滤器单元测试。 */
@ExtendWith(OutputCaptureExtension.class)
class ApiRequestLoggingFilterTest {

    /** 被测试的 API 请求日志过滤器。 */
    private final ApiRequestLoggingFilter filter = new ApiRequestLoggingFilter();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    /** 成功请求应记录用户、来源 IP 和链路标识。 */
    @Test
    void shouldLogSuccessfulApiRequest(CapturedOutput output) throws Exception {
        MockHttpServletRequest request = createRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        LoginUser loginUser = new LoginUser(10L, "test_user", List.of("USER"));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, List.of()));

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                ((MockHttpServletResponse) servletResponse).setStatus(200));

        assertEquals("test-trace-id", response.getHeader(ApiRequestLoggingFilter.TRACE_ID_HEADER));
        assertTrue(output.getOut().contains("API 请求成功"));
        assertTrue(output.getOut().contains("userId=10"));
        assertTrue(output.getOut().contains("clientIp=203.0.113.10"));
        assertTrue(output.getOut().contains("traceId=test-trace-id"));
    }

    /** 失败请求应按失败状态记录，便于服务器筛选告警日志。 */
    @Test
    void shouldLogFailedApiRequest(CapturedOutput output) throws Exception {
        MockHttpServletRequest request = createRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                ((MockHttpServletResponse) servletResponse).setStatus(401));

        assertTrue(output.getOut().contains("API 请求失败"));
        assertTrue(output.getOut().contains("status=401"));
    }

    /** 创建带反向代理信息的模拟 API 请求。 */
    private MockHttpServletRequest createRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/user/profile");
        request.addHeader(ApiRequestLoggingFilter.TRACE_ID_HEADER, "test-trace-id");
        request.addHeader("X-Forwarded-For", "203.0.113.10, 10.0.0.1");
        return request;
    }
}
