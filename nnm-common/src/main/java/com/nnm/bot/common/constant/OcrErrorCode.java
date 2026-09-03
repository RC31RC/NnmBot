package com.nnm.bot.common.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OcrErrorCode implements ErrorCode {

    DISABLED(409, "OCR 功能未启用", HttpStatus.CONFLICT),
    IMAGE_REQUIRED(400, "OCR 图片不能为空", HttpStatus.BAD_REQUEST),
    IMAGE_TOO_LARGE(413, "OCR 图片超过系统限制", HttpStatus.CONTENT_TOO_LARGE),
    INVALID_IMAGE(422, "OCR 图片格式或尺寸无效", HttpStatus.UNPROCESSABLE_ENTITY),
    RECOGNITION_FAILED(500, "OCR 识别失败", HttpStatus.INTERNAL_SERVER_ERROR),
    TEMPORARY_FILE_FAILED(500, "OCR 临时文件处理失败", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
