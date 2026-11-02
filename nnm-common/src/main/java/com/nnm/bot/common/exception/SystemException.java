package com.nnm.bot.common.exception;

import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/** 系统故障保留内部诊断 message，对外只使用稳定 description，不泄漏协议或文件细节。 */
@Getter
public class SystemException extends RuntimeException {

    /** 模块定义的稳定系统错误码。 */
    private final int code;
    /** 可对外展示的安全提示，与内部诊断 message 区分。 */
    private final String description;
    /** HTTP 边界使用的状态。 */
    private final HttpStatus status;

    public SystemException(String message) {
        super(message);
        this.code = CommonErrorCode.SYSTEM_ERROR.getCode();
        this.description = CommonErrorCode.SYSTEM_ERROR.getMessage();
        this.status = CommonErrorCode.SYSTEM_ERROR.getStatus();
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
        this.code = CommonErrorCode.SYSTEM_ERROR.getCode();
        this.description = CommonErrorCode.SYSTEM_ERROR.getMessage();
        this.status = CommonErrorCode.SYSTEM_ERROR.getStatus();
    }

    /** 按错误码创建系统故障，内部说明与对外说明分开。 */
    public SystemException(ErrorCode errorCode, String message) {
        this(errorCode, message, null);
    }

    public SystemException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
        this.description = errorCode.getMessage();
        this.status = errorCode.getStatus();
    }

    public SystemException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode.getCode();
        this.description = errorCode.getMessage();
        this.status = errorCode.getStatus();
    }
}
