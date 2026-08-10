package com.nnm.bot.common.http;

import com.nnm.bot.common.exception.SystemException;

public class ExternalTimeoutException extends SystemException {

    public ExternalTimeoutException(String method, String url, Throwable cause) {
        super("External HTTP request timed out: " + method + " " + url, cause);
    }
}
