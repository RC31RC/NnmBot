package com.nnm.bot.core.channel.notification.constant;

import cn.hutool.core.text.CharSequenceUtil;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 同一业务域的稳定枚举；持久化值与 API 枚举字符串保持不变。 */
public final class NotificationConstants {
    private NotificationConstants() {}

    /** 通知图片支持的文件格式及对应媒体类型。 */
    @Getter
    @RequiredArgsConstructor
    public enum ImageFormat {
        PNG("png", "image/png"), JPEG("jpeg", "image/jpeg"), JPG("jpg", "image/jpeg"), GIF("gif", "image/gif");

        private final String code;
        private final String contentType;

        public static ImageFormat fromCode(String code) {
            for (ImageFormat value : values()) {
                if (CharSequenceUtil.equalsIgnoreCase(value.code, code)) {
                    return value;
                }
            }
            return null;
        }
    }

    /** 通知图片与草稿资源的可预期输入错误。 */
    @Getter
    @RequiredArgsConstructor
    public enum ResourceErrorCode implements ErrorCode {
        IMAGE_SIZE_INVALID(400721, "图片大小超过限制", HttpStatus.BAD_REQUEST),
        IMAGE_UNREADABLE(400722, "仅支持可识别的 PNG、JPEG 或 GIF 图片", HttpStatus.BAD_REQUEST),
        IMAGE_FORMAT_UNSUPPORTED(400723, "不支持的图片格式", HttpStatus.BAD_REQUEST),
        IMAGE_PIXELS_INVALID(400724, "图片像素超过限制", HttpStatus.BAD_REQUEST),
        IMAGE_RESOURCE_UNAVAILABLE(400725, "图片资源不存在、已过期或无权访问", HttpStatus.BAD_REQUEST),
        RESOURCE_RETENTION_INVALID(400726, "任务资源保留时间超出限制", HttpStatus.BAD_REQUEST),
        TASK_RESOURCE_UNAVAILABLE(400727, "任务资源不存在或已过期", HttpStatus.BAD_REQUEST);

        private final int code;
        private final String message;
        private final HttpStatus status;
    }

    public enum BlockType {
        TEXT, MARKDOWN, IMAGE
    }

    public enum ActionType {
        COMMAND, LINK
    }

    public enum GroupResultStatus {
        IN_PROGRESS, CANCELLED, SUCCEEDED, PARTIAL, FAILED, UNKNOWN
    }

    /** 通知请求的操作互斥用途，键名与既有消费者保持一致。 */
    public enum LockPurpose {
        REQUEST("request");

        private final String key;

        LockPurpose(String key) {
            this.key = key;
        }

        /** 返回请求互斥的稳定操作名。 */
        public String key() {
            return key;
        }
    }
}
