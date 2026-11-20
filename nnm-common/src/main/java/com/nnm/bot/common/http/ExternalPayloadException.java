package com.nnm.bot.common.http;

import com.nnm.bot.common.error.SystemException;
import com.nnm.bot.common.constant.ExternalHttpErrorCode;

/** 外部 HTTP 响应无法按预期协议解析时抛出。 */
public class ExternalPayloadException extends SystemException {

    /**
     * 保留来源定位信息及解析原因供内部诊断。
     *
     * @param method HTTP 方法
     * @param url 请求地址
     * @param detail 受控的解析阶段标识
     * @param cause 解析异常
     */
    public ExternalPayloadException(String method, String url, String detail, Throwable cause) {
        super(ExternalHttpErrorCode.PAYLOAD_INVALID,
            "External HTTP payload " + detail + " failed: " + method + " " + url, cause);
    }
}
