package com.nnm.bot.common.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** OCR 输入校验、资源上限及识别失败的稳定错误码。 */
@Getter
@RequiredArgsConstructor
public enum OcrErrorCode implements ErrorCode {

    DISABLED(409, "OCR 功能未启用", HttpStatus.CONFLICT), IMAGE_REQUIRED(400, "OCR 图片不能为空", HttpStatus.BAD_REQUEST),
    IMAGE_BYTES_TOO_LARGE(413101, "OCR 图片大小为 %d 字节，超过限制 %d 字节", HttpStatus.CONTENT_TOO_LARGE),
    IMAGE_PIXELS_TOO_LARGE(413102, "OCR 图片像素数为 %d，超过限制 %d", HttpStatus.CONTENT_TOO_LARGE),
    INVALID_IMAGE(422, "OCR 图片格式或尺寸无效", HttpStatus.UNPROCESSABLE_ENTITY),
    RECOGNITION_FAILED(500, "OCR 识别失败", HttpStatus.INTERNAL_SERVER_ERROR),
    TEMPORARY_FILE_FAILED(500, "OCR 临时文件处理失败", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;

    /** 仅向用户展示已验证的数字，不拼接外部异常或未验证的文本。 */
    public String formatLimit(long actual, long allowed) {
        return message.formatted(actual, allowed);
    }
}
