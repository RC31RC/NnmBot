package com.nnm.bot.common.exception;

import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class SystemException extends RuntimeException {

    private final int code;
    private final String description;
    private final HttpStatus status;

    public SystemException(String message) {
        super(message);
        this.code = CommonErrorCode.SYSTEM_ERROR.getCode();
        this.description = CommonErrorCode.SYSTEM_ERROR.getMessage();
        this.status = CommonErrorCode.SYSTEM_ERROR.getStatus();
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
        this.code = CommonErrorCode.SYSTEM_ERROR.getCode();
        this.description = CommonErrorCode.SYSTEM_ERROR.getMessage();
        this.status = CommonErrorCode.SYSTEM_ERROR.getStatus();
    }

    public SystemException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
        this.description = errorCode.getMessage();
        this.status = errorCode.getStatus();
    }

    public SystemException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode.getCode();
        this.description = errorCode.getMessage();
        this.status = errorCode.getStatus();
    }
}
