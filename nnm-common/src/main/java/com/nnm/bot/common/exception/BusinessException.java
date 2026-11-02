package com.nnm.bot.common.exception;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/** 可向调用方说明的业务条件失败；错误码与 HTTP 状态均由 ErrorCode 明确提供。 */
@Getter
public class BusinessException extends RuntimeException {

    /** 模块定义的稳定业务错误码。 */
    private final int code;
    /** HTTP 边界使用的响应状态，命令边界不依赖 HTTP。 */
    private final HttpStatus status;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
    }

    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode.getCode();
        this.status = errorCode.getStatus();
    }

}
