package com.nnm.bot.warframe.runtime.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 世界状态命令缓存生成失败，对外不泄露动态数据内容。 */
@Getter
@RequiredArgsConstructor
public enum WarframeWorldStateErrorCode implements ErrorCode {
    IMAGE_CACHE_SERIALIZATION_FAILED(50050, "世界状态图片缓存数据处理失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
