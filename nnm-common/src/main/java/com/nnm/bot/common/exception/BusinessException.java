package com.nnm.bot.common.exception;

import cn.hutool.core.util.ObjectUtil;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final int code;
    private final HttpStatus status;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.status = resolveStatus(code);
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.status = resolveStatus(code);
    }

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

    private static HttpStatus resolveStatus(int code) {
        HttpStatus status = HttpStatus.resolve(code);
        return ObjectUtil.isNull(status) ? HttpStatus.INTERNAL_SERVER_ERROR : status;
    }
}
