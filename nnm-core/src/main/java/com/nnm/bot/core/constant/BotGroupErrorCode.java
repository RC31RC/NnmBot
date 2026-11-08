package com.nnm.bot.core.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 逻辑群组及成员身份的持久一致性错误。 */
@Getter
@RequiredArgsConstructor
public enum BotGroupErrorCode implements ErrorCode {
    MEMBER_MERGE_INCONSISTENT(50020, "群组成员合并状态不一致，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR),
    MEMBER_MERGE_UNAVAILABLE(50021, "群组成员合并服务不可用，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
