package com.nnm.bot.warframe.runtime.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 世界状态命令缓存生成失败，对外不泄露动态数据内容。 */
@Getter
@RequiredArgsConstructor
public enum WarframeWorldStateErrorCode implements ErrorCode {
    DESCENDIA_WEEK_CHANGED(409941, "沉沦之地已换周，请刷新后重新确认", HttpStatus.CONFLICT),
    DESCENDIA_REWARD_INVALID(400944, "所选楼层或奖励候选不可确认", HttpStatus.BAD_REQUEST),
    VENDOR_ROUND_CHANGED(409942, "玄骸商人已换轮，请刷新后重新填写", HttpStatus.CONFLICT),
    VENDOR_OBSERVATION_INVALID(400945, "玄骸武器、元素或加成数值无效", HttpStatus.BAD_REQUEST),
    IMAGE_CACHE_SERIALIZATION_FAILED(50092, "世界状态图片缓存数据处理失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR),
    POLLING_CONFIGURATION_INVALID(50093, "世界状态轮询间隔必须为正数", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
