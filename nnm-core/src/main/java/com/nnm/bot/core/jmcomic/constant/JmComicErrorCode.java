package com.nnm.bot.core.jmcomic.constant;

import com.nnm.bot.common.error.ErrorCode;
import org.springframework.http.HttpStatus;

/** 漫画下载、文件暂存和 PDF 转换的稳定错误码。 */
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
    TEMPORARY_FILE_FAILED(500, "JM 临时文件处理失败", HttpStatus.INTERNAL_SERVER_ERROR),
    DOWNLOAD_QUEUE_INTERRUPTED(502501, "JM 下载任务在排队等待时被中断", HttpStatus.BAD_GATEWAY),
    WHOLE_DOWNLOAD_UNAVAILABLE(502502, "JM 整本下载不可用，分页下载已关闭", HttpStatus.BAD_GATEWAY),
    WHOLE_FILE_FALLBACK_REQUIRED(502510, "JM 整本下载不可用，可改用分页下载", HttpStatus.BAD_GATEWAY),
    TEMP_WORKSPACE_FAILED(500501, "JM 临时目录处理失败", HttpStatus.INTERNAL_SERVER_ERROR),
    ALBUM_DOWNLOAD_FAILED(502503, "JM 漫画下载失败", HttpStatus.BAD_GATEWAY),
    IMAGE_VALIDATION_FAILED(502504, "校验 JM 图片下载结果失败", HttpStatus.BAD_GATEWAY),
    ALBUM_RESPONSE_MISSING(502505, "JM 上游未返回漫画信息", HttpStatus.BAD_GATEWAY),
    ALBUM_PAGE_LIMIT(413501, "漫画共 %d 页，超过限制 %d 页", HttpStatus.CONTENT_TOO_LARGE),
    IMAGE_DOWNLOAD_EMPTY(502506, "JM 上游没有下载到图片", HttpStatus.BAD_GATEWAY),
    IMAGE_DOWNLOAD_FAILURES(502507, "JM 漫画有 %d 张图片下载失败", HttpStatus.BAD_GATEWAY),
    IMAGE_DOWNLOAD_UNREADABLE(502508, "下载结果包含不可读图片", HttpStatus.BAD_GATEWAY),
    IMAGE_TOTAL_TOO_LARGE(413502, "下载图片总大小超过限制 %d 字节", HttpStatus.CONTENT_TOO_LARGE),
    PDF_INPUT_EMPTY(502509, "没有可用于生成 PDF 的图片", HttpStatus.BAD_GATEWAY),
    PDF_OUTPUT_TOO_LARGE(413503, "生成的 PDF 大小为 %d 字节，超过限制 %d 字节", HttpStatus.CONTENT_TOO_LARGE),
    PDF_GENERATION_FAILED(500502, "生成 JM 漫画 PDF 失败", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_PIXEL_LIMIT(422501, "图片像素超过限制", HttpStatus.UNPROCESSABLE_ENTITY),
    IMAGE_FORMAT_INVALID(422502, "无法识别图片格式", HttpStatus.UNPROCESSABLE_ENTITY),
    IMAGE_FILE_UNREADABLE(422503, "图片文件不存在或不可读", HttpStatus.UNPROCESSABLE_ENTITY),
    IMAGE_READ_FAILED(422504, "无法读取图片", HttpStatus.UNPROCESSABLE_ENTITY),
    IMAGE_DIMENSIONS_INVALID(422505, "图片尺寸无效", HttpStatus.UNPROCESSABLE_ENTITY),
    WHOLE_FILE_SIZE_LIMIT(413504, "JM 整本文件大小为 %d 字节，超过限制 %d 字节", HttpStatus.CONTENT_TOO_LARGE),
    WHOLE_PDF_SIZE_LIMIT(413505, "JM 整本 PDF 大小为 %d 字节，超过限制 %d 字节", HttpStatus.CONTENT_TOO_LARGE),
    WHOLE_PDF_PAGE_LIMIT(413506, "JM 整本 PDF 共 %d 页，超过限制 %d 页", HttpStatus.CONTENT_TOO_LARGE),
    ARCHIVE_ENTRY_LIMIT(413507, "JM 整本压缩包文件项过多", HttpStatus.CONTENT_TOO_LARGE),
    WHOLE_STREAM_SIZE_LIMIT(413508, "JM 整本文件超过下载大小限制 %d 字节", HttpStatus.CONTENT_TOO_LARGE),
    ARCHIVE_UNPACK_SIZE_LIMIT(413509, "JM 整本压缩包解压后超过大小限制 %d 字节", HttpStatus.CONTENT_TOO_LARGE);

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
