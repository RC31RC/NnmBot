package com.nnm.bot.common.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 外部 HTTP 客户端失败；URL、响应体和异常原因只作内部诊断。 */
@Getter
@RequiredArgsConstructor
public enum ExternalHttpErrorCode implements ErrorCode {
    REQUEST_FAILED(502960, "外部 HTTP 请求失败", HttpStatus.BAD_GATEWAY),
    PAYLOAD_INVALID(502961, "外部 HTTP 响应内容无效", HttpStatus.BAD_GATEWAY),
    RESPONSE_TOO_LARGE(502962, "外部 HTTP 响应超过允许大小", HttpStatus.BAD_GATEWAY),
    TIMEOUT(504960, "外部 HTTP 请求超时", HttpStatus.GATEWAY_TIMEOUT),
    TRANSPORT_FAILED(502963, "外部 HTTP 网络传输失败", HttpStatus.BAD_GATEWAY);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
