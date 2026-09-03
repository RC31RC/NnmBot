package com.nnm.bot.common.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    PARAM_INVALID(400, "请求参数不合法", HttpStatus.BAD_REQUEST),
    AUTHENTICATION_REQUIRED(401, "未登录或登录已过期", HttpStatus.UNAUTHORIZED),
    PERMISSION_DENIED(403, "没有访问权限", HttpStatus.FORBIDDEN),
    DATA_NOT_FOUND(404, "数据不存在", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED(405, "请求方法不支持", HttpStatus.METHOD_NOT_ALLOWED),
    NOT_ACCEPTABLE(406, "请求响应格式不受支持", HttpStatus.NOT_ACCEPTABLE),
    DATA_CONFLICT(409, "数据冲突", HttpStatus.CONFLICT),
    FILE_TOO_LARGE(413, "文件大小超过限制", HttpStatus.CONTENT_TOO_LARGE),
    UNSUPPORTED_MEDIA_TYPE(415, "请求媒体类型不受支持", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    UNPROCESSABLE_ENTITY(422, "请求内容无法处理", HttpStatus.UNPROCESSABLE_ENTITY),
    TASK_LIMIT_REACHED(429, "管理端任务过多，请稍后重试", HttpStatus.TOO_MANY_REQUESTS),
    SYSTEM_ERROR(500, "系统异常，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
