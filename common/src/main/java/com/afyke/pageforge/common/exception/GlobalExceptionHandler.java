package com.afyke.pageforge.common.exception;

import com.afyke.pageforge.common.model.ResultModel;
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
        BindingResult bindingResult = exception instanceof MethodArgumentNotValidException validException
                ? validException.getBindingResult()
                : ((BindException) exception).getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String message = fieldError == null ? "请求参数错误" : fieldError.getDefaultMessage();
        return ResultModel.failure(400, "PARAMETER_ERROR", message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultModel<Void> handleMessageNotReadableException() {
        return ResultModel.failure(400, "PARAMETER_ERROR", "请求内容格式错误");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResultModel<Void> handleDuplicateKeyException() {
        return ResultModel.failure(409, "DUPLICATE_KEY", "数据已存在，请勿重复提交");
    }

    @ExceptionHandler(Exception.class)
    public ResultModel<Void> handleException(Exception exception) {
        return ResultModel.failure(500, "SYSTEM_ERROR", "系统异常");
    }
}
