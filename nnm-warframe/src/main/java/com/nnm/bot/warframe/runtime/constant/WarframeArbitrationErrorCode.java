package com.nnm.bot.warframe.runtime.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 仲裁时间窗口查询的可预期输入错误。 */
@Getter
@RequiredArgsConstructor
public enum WarframeArbitrationErrorCode implements ErrorCode {
    QUERY_INVALID(400213, "仲裁查询页码或大小不合法", HttpStatus.BAD_REQUEST),
    TIME_RANGE_INVALID(400214, "结束时间必须晚于开始时间", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
