package com.nnm.bot.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;

/** 管理 API 的统一响应信封。 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final int code;
    private final String message;
    private final T data;

    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /** 构造带结果的成功响应。
     *
     * @param data 业务结果
     * @param <T> 结果类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    /** 构造无业务数据的成功响应。
     *
     * @param <T> 响应声明类型
     * @return data 为空的成功响应
     */
    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(200, "success", null);
    }

    /** 按指定错误编号和安全文案构造失败响应。
     *
     * @param code 稳定错误编号
     * @param message 对调用方展示的文案
     * @param <T> 响应声明类型
     * @return data 为空的失败响应
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    /** 使用错误码中的编号和默认文案构造失败响应。
     *
     * @param errorCode 稳定错误码
     * @param <T> 响应声明类型
     * @return data 为空的失败响应
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMessage());
    }

    /** 构造通用服务端失败响应。
     *
     * @param message 对调用方展示的文案
     * @param <T> 响应声明类型
     * @return data 为空的失败响应
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null);
    }
}
