package com.nnm.bot.warframe.persistent.official.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 官方文件采纳的持久状态错误，与上游协议故障及内部不变量区分。 */
@Getter
@RequiredArgsConstructor
public enum OfficialApprovalErrorCode implements ErrorCode {
    PARTIAL_APPLY_FAILED(40932, "官方审批部分文件入库失败；已完成的文件保持可见，请查看文件进度", HttpStatus.CONFLICT),
    APPLY_STATE_INCONSISTENT(50011, "官方审批状态不完整，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR),
    SAVED_SNAPSHOT_CORRUPTED(50012, "官方审批快照无法读取，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR),
    PREPARATION_RETRYABLE(50330, "官方审批准备未完成，后台将重试", HttpStatus.SERVICE_UNAVAILABLE),
    APPLY_WAIT_INTERRUPTED(50331, "官方审批等待已中断，入库任务仍在后台继续处理", HttpStatus.SERVICE_UNAVAILABLE),
    FIELD_DIFF_SERIALIZATION_FAILED(50013, "官方审批字段差异序列化失败，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
