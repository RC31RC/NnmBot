package com.nnm.bot.warframe.persistent.official.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 官方资料展示、维护约束及持久化一致性的稳定错误码。 */
@Getter
@RequiredArgsConstructor
public enum OfficialDataErrorCode implements ErrorCode {
    GROUP_INVALID(40085, "业务分组不属于当前官方 JSON", HttpStatus.BAD_REQUEST),
    METADATA_READ_ONLY(40084, "文件级信息仅可通过官方数据采纳更新", HttpStatus.BAD_REQUEST),
    FIELD_STATE_INVALID(50051, "官方资料字段状态异常，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR),
    RELATION_INCONSISTENT(50052, "官方资料关联状态异常，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
