package com.nnm.bot.common.http;

import com.nnm.bot.common.error.SystemException;
import com.nnm.bot.common.constant.ExternalHttpErrorCode;

/** 外部 HTTP 请求超过配置的时间预算时抛出。 */
public class ExternalTimeoutException extends SystemException {

    /**
     * 保留请求来源和底层超时原因。
     *
     * @param method HTTP 方法
     * @param url 请求地址
     * @param cause 底层超时原因
     */
    public ExternalTimeoutException(String method, String url, Throwable cause) {
        super(ExternalHttpErrorCode.TIMEOUT, "External HTTP request timed out: " + method + " " + url, cause);
    }
}
