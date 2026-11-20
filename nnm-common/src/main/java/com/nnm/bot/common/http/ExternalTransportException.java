package com.nnm.bot.common.http;

import com.nnm.bot.common.error.SystemException;
import com.nnm.bot.common.constant.ExternalHttpErrorCode;

/** 外部 HTTP 请求未能建立或完成网络传输时抛出。 */
public class ExternalTransportException extends SystemException {

    /**
     * 保留请求来源和网络传输原因。
     *
     * @param method HTTP 方法
     * @param url 请求地址
     * @param cause 底层传输原因
     */
    public ExternalTransportException(String method, String url, Throwable cause) {
        super(ExternalHttpErrorCode.TRANSPORT_FAILED, "External HTTP transport failed: " + method + " " + url, cause);
    }
}
