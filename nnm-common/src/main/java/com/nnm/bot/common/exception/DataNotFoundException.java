package com.nnm.bot.common.exception;

import com.nnm.bot.common.constant.CommonErrorCode;

public class DataNotFoundException extends BusinessException {

    public DataNotFoundException(String message) {
        super(CommonErrorCode.DATA_NOT_FOUND, message);
    }

    public DataNotFoundException(String message, Throwable cause) {
        super(CommonErrorCode.DATA_NOT_FOUND, message, cause);
    }
}
