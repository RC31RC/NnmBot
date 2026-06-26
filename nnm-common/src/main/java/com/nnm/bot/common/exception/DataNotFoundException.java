package com.nnm.bot.common.exception;

public class DataNotFoundException extends BusinessException {

    public DataNotFoundException(String message) {
        super(404, message);
    }
}
