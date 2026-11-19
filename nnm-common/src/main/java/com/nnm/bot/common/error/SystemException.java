package com.nnm.bot.common.error;

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

    /** 使用错误码的默认安全文案创建系统故障。 */
    public SystemException(ErrorCode errorCode) {
        this(errorCode, (Throwable)null);
    }

    /** 按错误码创建系统故障，内部说明与对外说明分开。 */
    public SystemException(ErrorCode errorCode, String message) {
        this(errorCode, message, null);
    }

    /** 保留内部原因，对外文案仍取自错误码。
     *
     * @param errorCode 稳定系统错误码
     * @param cause 内部诊断原因
     */
    public SystemException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
        this.description = errorCode.getMessage();
        this.status = errorCode.getStatus();
    }

    /** 分离内部诊断文案与对外稳定描述。
     *
     * @param errorCode 稳定系统错误码
     * @param message 内部日志诊断文案
     * @param cause 内部诊断原因
     */
    public SystemException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode.getCode();
        this.description = errorCode.getMessage();
        this.status = errorCode.getStatus();
    }
}
