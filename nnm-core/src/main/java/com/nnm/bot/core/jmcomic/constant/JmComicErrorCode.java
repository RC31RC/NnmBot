package com.nnm.bot.core.jmcomic.constant;

import com.nnm.bot.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum JmComicErrorCode implements ErrorCode {

    DISABLED(409, "JM 下载功能未启用", HttpStatus.CONFLICT),
    DOWNLOAD_METHODS_DISABLED(409, "JM 整本下载和分页下载均已关闭", HttpStatus.CONFLICT),
    BUSY(409, "已有 JM 下载任务正在执行，请稍后再试", HttpStatus.CONFLICT),
    INVALID_ALBUM_ID(400, "JM 漫画 ID 格式不正确", HttpStatus.BAD_REQUEST),
    ALBUM_TOO_LARGE(413, "JM 漫画页数超过系统限制", HttpStatus.CONTENT_TOO_LARGE),
    DOWNLOAD_TOO_LARGE(413, "JM 漫画下载内容超过系统限制", HttpStatus.CONTENT_TOO_LARGE),
    INVALID_IMAGE(422, "JM 漫画包含无法处理的图片", HttpStatus.UNPROCESSABLE_ENTITY),
    DOWNLOAD_INCOMPLETE(502, "JM 漫画图片下载不完整", HttpStatus.BAD_GATEWAY),
    PDF_TOO_LARGE(413, "JM 漫画 PDF 超过系统限制", HttpStatus.CONTENT_TOO_LARGE),
    DOWNLOAD_FAILED(502, "JM 漫画下载失败", HttpStatus.BAD_GATEWAY),
    PDF_CREATE_FAILED(500, "JM 漫画 PDF 生成失败", HttpStatus.INTERNAL_SERVER_ERROR),
    TEMPORARY_FILE_FAILED(500, "JM 临时文件处理失败", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;

    JmComicErrorCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }
}
