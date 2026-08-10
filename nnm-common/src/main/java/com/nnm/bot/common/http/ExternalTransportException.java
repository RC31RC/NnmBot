package com.nnm.bot.common.http;

import com.nnm.bot.common.exception.SystemException;

public class ExternalTransportException extends SystemException {

    public ExternalTransportException(String method, String url, Throwable cause) {
        super("External HTTP transport failed: " + method + " " + url, cause);
    }
}
