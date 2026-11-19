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
    ARTICLE_OPERATION_DENIED(403, "权限不足，不能维护同级或更高级用户创建的文章", HttpStatus.FORBIDDEN),
    ARTICLE_NOT_FOUND(404901, "论坛文章不存在", HttpStatus.NOT_FOUND),
    ARTICLE_ID_REQUIRED(400901, "修改或删除文章必须提供有效 id", HttpStatus.BAD_REQUEST),
    ARTICLE_SLUG_REQUIRED(400902, "文章标识不能为空", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_ALLOWED(400903, "分类必须从已有选项中选择", HttpStatus.BAD_REQUEST),
    TAG_NOT_ALLOWED(400904, "标签必须从已有选项中选择", HttpStatus.BAD_REQUEST),
    ARTICLE_SLUG_CONFLICT(409901, "论坛文章 slug 已存在", HttpStatus.CONFLICT),
    ARTICLE_DATA_CONFLICT(409902, "论坛文章数据冲突", HttpStatus.CONFLICT),
    STORAGE_ID_MISMATCH(500951, "论坛附件存储状态异常", HttpStatus.INTERNAL_SERVER_ERROR),
    ATTACHMENT_NOT_FOUND(404902, "论坛附件不存在", HttpStatus.NOT_FOUND),
    TEMP_ATTACHMENT_NOT_FOUND(404903, "论坛临时附件不存在", HttpStatus.NOT_FOUND),
    TEMP_URL_INVALID(400905, "论坛临时附件地址不合法", HttpStatus.BAD_REQUEST),
    UPLOAD_REQUIRED(400906, "请选择需要上传的论坛附件", HttpStatus.BAD_REQUEST),
    EXTENSION_UNSUPPORTED(400907, "不支持上传该类型的论坛附件", HttpStatus.BAD_REQUEST),
    MAX_FILE_BYTES_INVALID(500952, "论坛附件大小限制必须大于 0", HttpStatus.INTERNAL_SERVER_ERROR),
    PUBLIC_PATH_REQUIRED(500953, "论坛附件公开路径不能为空", HttpStatus.INTERNAL_SERVER_ERROR),
    TEMP_PUBLIC_PATH_REQUIRED(500954, "论坛临时附件公开路径不能为空", HttpStatus.INTERNAL_SERVER_ERROR),
    TEMP_RETENTION_INVALID(500955, "论坛临时附件保留时间必须大于 0", HttpStatus.INTERNAL_SERVER_ERROR),
    EXTENSIONS_REQUIRED(500956, "论坛附件扩展名白名单不能为空", HttpStatus.INTERNAL_SERVER_ERROR),
    YEAR_INVALID(400908, "论坛附件年份不合法", HttpStatus.BAD_REQUEST),
    MONTH_INVALID(400909, "论坛附件月份不合法", HttpStatus.BAD_REQUEST),
    FILE_NAME_INVALID(400910, "论坛附件文件名不合法", HttpStatus.BAD_REQUEST),
    FILE_NAME_REQUIRED(400911, "论坛附件文件名不能为空", HttpStatus.BAD_REQUEST),
    FILE_NAME_TOO_LONG(400912, "论坛附件文件名过长", HttpStatus.BAD_REQUEST),
    FILE_EXTENSION_REQUIRED(400913, "论坛附件必须包含扩展名", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
