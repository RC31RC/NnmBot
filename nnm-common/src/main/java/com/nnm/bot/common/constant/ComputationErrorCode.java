package com.nnm.bot.common.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ComputationErrorCode implements ErrorCode {
    BUSY(50310, "计算资源繁忙，请稍后重试", HttpStatus.SERVICE_UNAVAILABLE),
    SHUTDOWN(50311, "计算服务正在停止", HttpStatus.SERVICE_UNAVAILABLE),
    QUEUE_EXPIRED(50410, "计算任务排队超时", HttpStatus.GATEWAY_TIMEOUT),
    WAIT_TIMEOUT(50411, "等待计算结果超时", HttpStatus.GATEWAY_TIMEOUT),
    INTERRUPTED(50312, "等待计算结果被中断", HttpStatus.SERVICE_UNAVAILABLE),
    INPUT_TOO_LARGE(41310, "计算输入超过限制", HttpStatus.CONTENT_TOO_LARGE),
    RESULT_TOO_LARGE(41311, "计算结果超过限制", HttpStatus.CONTENT_TOO_LARGE);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
