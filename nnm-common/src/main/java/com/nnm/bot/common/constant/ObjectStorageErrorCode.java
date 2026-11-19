package com.nnm.bot.common.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 对象存储操作失败，存储端细节仅在异常原因里保留。 */
@Getter
@RequiredArgsConstructor
public enum ObjectStorageErrorCode implements ErrorCode {
    OPERATION_FAILED(500960, "对象存储操作失败", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
