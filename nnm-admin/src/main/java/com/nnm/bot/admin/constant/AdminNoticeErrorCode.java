package com.nnm.bot.admin.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 管理端通知资源和预约任务相关的稳定错误码。 */
@Getter
@RequiredArgsConstructor
public enum AdminNoticeErrorCode implements ErrorCode {
    INVALID_EXPIRY(400301, "通知过期时间须在将来且不得超过最长有效期", HttpStatus.BAD_REQUEST),
    HISTORY_PAGE_INVALID(400302, "通知历史页码或条数不合法", HttpStatus.BAD_REQUEST),
    CONTENT_REQUIRED(400303, "通知标题与正文不能为空", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
