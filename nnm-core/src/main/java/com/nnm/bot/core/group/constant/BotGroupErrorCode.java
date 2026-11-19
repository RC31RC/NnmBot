package com.nnm.bot.core.group.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 逻辑群组及成员身份的持久一致性错误。 */
@Getter
@RequiredArgsConstructor
public enum BotGroupErrorCode implements ErrorCode {
    MEMBER_MERGE_INCONSISTENT(50020, "群组成员合并状态不一致，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR),
    MEMBER_MERGE_UNAVAILABLE(50021, "群组成员合并服务不可用，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR),
    MEMBER_MERGE_IDS_INVALID(400601, "成员合并必须提供两个不同的持久成员", HttpStatus.BAD_REQUEST),
    MEMBER_MERGE_GROUP_INVALID(400602, "只能合并同一逻辑群组的成员", HttpStatus.BAD_REQUEST),
    MEMBER_NOT_FOUND(404601, "群组成员不存在", HttpStatus.NOT_FOUND),
    MEMBER_MERGE_IDENTITY_CONFLICT(409601, "成员存在冲突的平台身份，不能自动合并", HttpStatus.CONFLICT),
    QQ_ID_ALREADY_BOUND(409602, "QQ号已经绑定其他 QQ 官方成员身份", HttpStatus.CONFLICT),
    PLATFORM_ID_ALREADY_BOUND(409603, "当前平台身份已经绑定其他 QQ 号", HttpStatus.CONFLICT),
    MEMBER_IDENTITY_CONFLICT(409604, "成员已有冲突的其他平台身份，不能自动合并", HttpStatus.CONFLICT);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
