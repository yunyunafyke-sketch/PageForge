package com.afyke.pageforge.common.exception;

import com.afyke.pageforge.common.model.ResultModel;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResultModel<Void> handleBusinessException(BusinessException exception) {
        return ResultModel.failure(
                exception.getStatus(),
                exception.getErrorCode(),
                exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResultModel<Void> handleValidationException(Exception exception) {
        String message = exception instanceof MethodArgumentNotValidException validException
                ? validException.getBindingResult().getFieldError().getDefaultMessage()
                : ((BindException) exception).getBindingResult().getFieldError().getDefaultMessage();
        return ResultModel.failure(400, "PARAMETER_ERROR", message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultModel<Void> handleMessageNotReadableException() {
        return ResultModel.failure(400, "PARAMETER_ERROR", "请求内容格式错误");
    }

    @ExceptionHandler(Exception.class)
    public ResultModel<Void> handleException(Exception exception) {
        return ResultModel.failure(500, "SYSTEM_ERROR", "系统异常");
    }
}
