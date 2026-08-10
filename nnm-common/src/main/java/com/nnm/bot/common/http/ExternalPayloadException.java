package com.nnm.bot.common.http;

import com.nnm.bot.common.exception.SystemException;

public class ExternalPayloadException extends SystemException {

    public ExternalPayloadException(String method,
                                    String url,
                                    String detail,
                                    Throwable cause) {
        super("External HTTP payload " + detail + " failed: " + method + " " + url, cause);
    }
}
