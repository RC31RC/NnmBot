package com.nnm.bot.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/** 可向调用方说明的业务条件失败；错误码与 HTTP 状态均由 ErrorCode 明确提供。 */
@Getter
public class BusinessException extends RuntimeException {

    /** 模块定义的稳定业务错误码。 */
    private final int code;
    /** HTTP 边界使用的响应状态，命令边界不依赖 HTTP。 */
    private final HttpStatus status;

    /** 使用错误码中的稳定默认文案构造业务失败。
     *
     * @param errorCode 业务错误码
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
    }

    /** 保留内部原因，不把异常详情当作客户端文案。
     *
     * @param errorCode 业务错误码
     * @param cause 内部诊断原因
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
    }

    /** 使用经业务边界确认的文案构造异常。
     *
     * @param errorCode 业务错误码
     * @param message 对调用方展示的文案
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
    }

    /** 使用经业务边界确认的文案并保留内部原因。
     *
     * @param errorCode 业务错误码
     * @param message 对调用方展示的文案
     * @param cause 内部诊断原因
     */
    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
    }

}
