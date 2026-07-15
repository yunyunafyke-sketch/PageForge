package com.afyke.pageforge.common.exception;

import lombok.Getter;

/**
 * 业务异常。
 */
@Getter
public class BusinessException extends RuntimeException {

    /** HTTP 状态码。 */
    private final Integer status;

    /** 业务错误码。 */
    private final String errorCode;

    public BusinessException(Integer status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static BusinessException badRequest(String errorCode, String message) {
        return new BusinessException(400, errorCode, message);
    }

    public static BusinessException unauthorized(String message) {
        return new BusinessException(401, "UNAUTHORIZED", message);
    }
}
