package com.nnm.bot.common.exception;

import com.nnm.bot.common.error.CommonErrorCode;
import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;

@Getter
public class SystemException extends RuntimeException {

    private final int code;

    public SystemException(String message) {
        super(message);
        this.code = CommonErrorCode.SYSTEM_ERROR.getCode();
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
        this.code = CommonErrorCode.SYSTEM_ERROR.getCode();
    }

    public SystemException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
    }
}
