package com.afyke.pageforge.common.exception;

import com.afyke.pageforge.common.model.ResultModel;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器。
 */
@RestControllerAdvice
@Slf4j
// Springdoc 2.3 不兼容 Spring 6.2 的 ControllerAdviceBean API，因此不扫描全局异常处理器。
@Hidden
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResultModel<Void> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request,
            HttpServletResponse response) {
        response.setStatus(exception.getStatus());
        log.warn("业务请求失败 method={} uri={} status={} errorCode={} message={}",
                request.getMethod(), request.getRequestURI(), exception.getStatus(),
                exception.getErrorCode(), exception.getMessage());
        return ResultModel.failure(
                exception.getStatus(),
                exception.getErrorCode(),
                exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResultModel<Void> handleValidationException(
            Exception exception,
            HttpServletRequest request,
            HttpServletResponse response) {
        BindingResult bindingResult = exception instanceof MethodArgumentNotValidException validException
                ? validException.getBindingResult()
                : ((BindException) exception).getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String message = fieldError == null ? "请求参数错误" : fieldError.getDefaultMessage();
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        log.warn("请求参数校验失败 method={} uri={} message={}",
                request.getMethod(), request.getRequestURI(), message);
        return ResultModel.failure(400, "PARAMETER_ERROR", message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultModel<Void> handleMessageNotReadableException(
            HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        log.warn("请求内容格式错误 method={} uri={}", request.getMethod(), request.getRequestURI());
        return ResultModel.failure(400, "PARAMETER_ERROR", "请求内容格式错误");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResultModel<Void> handleDuplicateKeyException(
            HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_CONFLICT);
        log.warn("数据重复提交 method={} uri={}", request.getMethod(), request.getRequestURI());
        return ResultModel.failure(409, "DUPLICATE_KEY", "数据已存在，请勿重复提交");
    }

    @ExceptionHandler(Exception.class)
    public ResultModel<Void> handleException(
            Exception exception,
            HttpServletRequest request,
            HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        // 未知异常必须记录完整堆栈，响应中仍使用统一提示，避免向前端泄露内部实现。
        log.error("系统发生未处理异常 method={} uri={}",
                request.getMethod(), request.getRequestURI(), exception);
        return ResultModel.failure(500, "SYSTEM_ERROR", "系统异常");
    }
}
