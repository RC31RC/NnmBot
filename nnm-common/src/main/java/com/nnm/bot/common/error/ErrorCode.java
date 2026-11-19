package com.nnm.bot.common.error;

import org.springframework.http.HttpStatus;

/** 对外稳定的错误编号、默认文案及 HTTP 状态契约。 */
public interface ErrorCode {

    /** 返回跨接口识别错误的稳定编号。 */
    int getCode();

    /** 返回不包含临时诊断细节的默认文案。 */
    String getMessage();

    /** 返回 HTTP 边界应使用的响应状态。 */
    HttpStatus getStatus();
}
