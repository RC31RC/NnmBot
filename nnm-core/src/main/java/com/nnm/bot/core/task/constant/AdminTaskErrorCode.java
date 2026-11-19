package com.nnm.bot.core.task.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 分布式管理任务的可预期查询失败。 */
@Getter
@RequiredArgsConstructor
public enum AdminTaskErrorCode implements ErrorCode {
    NOT_FOUND(404501, "管理端任务不存在", HttpStatus.NOT_FOUND), EXPIRED(404502, "任务不存在或已过期", HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
