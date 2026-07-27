package com.nnm.bot.common.exception;

import com.nnm.bot.common.error.CommonErrorCode;

public class DataNotFoundException extends BusinessException {

    public DataNotFoundException(String message) {
        super(CommonErrorCode.DATA_NOT_FOUND, message);
    }
}
