package com.nnm.bot.common.http;

import com.nnm.bot.common.exception.SystemException;
import org.springframework.http.HttpHeaders;
import java.util.List;

public class ExternalHttpException extends SystemException {

    private final int statusCode;
    private final String responseBody;
    private final HttpHeaders responseHeaders;

    public ExternalHttpException(String method,
                                 String url,
                                 int statusCode,
                                 String responseBody,
                                 HttpHeaders responseHeaders,
                                 Throwable cause) {
        super("External HTTP request failed: " + method + " " + url
                + " returned " + statusCode, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        HttpHeaders copy = new HttpHeaders();
        if (responseHeaders != null) {
            responseHeaders.forEach((name, values) -> copy.put(name, List.copyOf(values)));
        }
        this.responseHeaders = HttpHeaders.readOnlyHttpHeaders(copy);
    }

    public ExternalHttpException(String method,
                                 String url,
                                 int statusCode,
                                 String responseBody,
                                 Throwable cause) {
        this(method, url, statusCode, responseBody, HttpHeaders.EMPTY, cause);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public HttpHeaders getResponseHeaders() {
        return responseHeaders;
    }

    public boolean isRetryable() {
        return statusCode == 429 || statusCode >= 500 && statusCode < 600;
    }
}
