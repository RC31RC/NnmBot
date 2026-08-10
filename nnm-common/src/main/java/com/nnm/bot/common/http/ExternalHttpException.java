package com.nnm.bot.common.http;

import com.nnm.bot.common.exception.SystemException;

public class ExternalHttpException extends SystemException {

    private final int statusCode;
    private final String responseBody;

    public ExternalHttpException(String method,
                                 String url,
                                 int statusCode,
                                 String responseBody,
                                 Throwable cause) {
        super("External HTTP request failed: " + method + " " + url
                + " returned " + statusCode, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public boolean isRetryable() {
        return statusCode == 429 || statusCode >= 500 && statusCode < 600;
    }
}
