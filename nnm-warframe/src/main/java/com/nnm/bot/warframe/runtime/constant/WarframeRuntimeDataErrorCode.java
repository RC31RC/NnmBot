package com.nnm.bot.warframe.runtime.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 独立动态排期管理查询的稳定输入错误码。 */
@Getter
@RequiredArgsConstructor
public enum WarframeRuntimeDataErrorCode implements ErrorCode {
    QUERY_INVALID(400215, "动态数据查询页码或大小不合法", HttpStatus.BAD_REQUEST),
    TIME_RANGE_INVALID(400216, "动态数据结束时间必须晚于开始时间", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
