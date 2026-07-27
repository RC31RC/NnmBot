package com.nnm.bot.common.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    int getCode();

    String getMessage();

    HttpStatus getStatus();
}
