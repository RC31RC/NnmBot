package com.nnm.bot.warframe.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 数据集缓存与版本初始化故障，不将基础设施缺失误报为用户输入错误。 */
@Getter
@RequiredArgsConstructor
public enum WarframeCacheErrorCode implements ErrorCode {
    DATASET_VERSION_MISSING(50053, "数据集版本未初始化，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR),
    REGION_UNREGISTERED(50054, "数据集缓存未注册，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR),
    CONFIGURATION_INVALID(50055, "数据集缓存配置不合法，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
