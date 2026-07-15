package com.afyke.pageforge.common.model;

import lombok.Data;

/**
 * 统一响应结果。
 *
 * @param <T> 数据类型
 */
@Data
public class ResultModel<T> {

    /** 状态码。 */
    private Integer status;

    /** 是否成功。 */
    private boolean success;

    /** 错误码。 */
    private String errorCode;

    /** 错误信息。 */
    private String errorMessage;

    /** 返回数据。 */
    private T data;

    public static <T> ResultModel<T> success(T data) {
        ResultModel<T> result = new ResultModel<>();
        result.setStatus(200);
        result.setSuccess(true);
        result.setData(data);
        return result;
    }

    public static ResultModel<Void> success() {
        return success(null);
    }

    public static <T> ResultModel<T> failure(
            Integer status, String errorCode, String errorMessage) {
        ResultModel<T> result = new ResultModel<>();
        result.setStatus(status);
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        return result;
    }
}
