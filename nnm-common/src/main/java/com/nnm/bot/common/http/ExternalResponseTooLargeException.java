package com.nnm.bot.common.http;

import com.nnm.bot.common.exception.SystemException;

/** 外部服务响应超过配置上限时抛出，避免异常响应耗尽应用内存或磁盘。 */
public class ExternalResponseTooLargeException extends SystemException {

    private final String method;
    private final String url;
    private final long maxBytes;

    public ExternalResponseTooLargeException(
            String method,
            String url,
            long maxBytes,
            Throwable cause) {
        super("External HTTP response is larger than " + maxBytes
                + " bytes: " + method + " " + url, cause);
        this.method = method;
        this.url = url;
        this.maxBytes = maxBytes;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public long getMaxBytes() {
        return maxBytes;
    }
}
