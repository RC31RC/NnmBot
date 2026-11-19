package com.nnm.bot.warframe.persistent.curated.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 自维护订阅通知的持久记忆故障，不把损坏状态误报为用户参数。 */
@Getter
@RequiredArgsConstructor
public enum WarframeWatchErrorCode implements ErrorCode {
    MATCH_MEMORY_CAPACITY(50090, "订阅事件记忆超过容量限制", HttpStatus.INTERNAL_SERVER_ERROR),
    MATCH_MEMORY_INVALID(50091, "订阅事件记忆无法读取，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR),
    GROUP_NOT_FOUND(404911, "群组不存在", HttpStatus.NOT_FOUND),
    MEMBER_NOT_FOUND(404912, "成员不存在或不属于当前群组", HttpStatus.NOT_FOUND),
    SUBSCRIPTION_NOT_FOUND(404913, "订阅不存在或不属于当前成员", HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
