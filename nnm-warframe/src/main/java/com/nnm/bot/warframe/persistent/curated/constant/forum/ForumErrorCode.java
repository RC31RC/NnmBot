package com.nnm.bot.warframe.persistent.curated.constant.forum;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 论坛业务对外使用的稳定错误码合同。 */
@Getter
@RequiredArgsConstructor
public enum ForumErrorCode implements ErrorCode {

    INVALID_FILE(400, "论坛附件格式不受支持", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(413, "论坛附件大小超过限制", HttpStatus.CONTENT_TOO_LARGE),
    FILE_STORAGE_FAILED(500, "论坛附件存储失败", HttpStatus.INTERNAL_SERVER_ERROR),
    ARTICLE_OPERATION_DENIED(
            403,
            "权限不足，不能维护同级或更高级用户创建的文章",
            HttpStatus.FORBIDDEN);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
