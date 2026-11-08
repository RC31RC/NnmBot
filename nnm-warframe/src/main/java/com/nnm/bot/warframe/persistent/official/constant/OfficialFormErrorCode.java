package com.nnm.bot.warframe.persistent.official.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 官方资料表单的字段释义合同故障；原始字段路径仅保留在内部诊断。 */
@Getter
@RequiredArgsConstructor
public enum OfficialFormErrorCode implements ErrorCode {
    LABELS_UNAVAILABLE(50040, "官方资料字段释义不可用，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
