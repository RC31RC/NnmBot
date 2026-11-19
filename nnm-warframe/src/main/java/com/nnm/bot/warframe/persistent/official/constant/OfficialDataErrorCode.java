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
    RELATION_INCONSISTENT(50052, "官方资料关联状态异常，请联系管理员", HttpStatus.INTERNAL_SERVER_ERROR),
    MAINTENANCE_TYPE_INVALID(400193, "维护类别不合法", HttpStatus.BAD_REQUEST),
    INPUT_TYPE_UNREGISTERED(400195, "官方数据集尚未登记类型化字段合同", HttpStatus.BAD_REQUEST),
    RECORD_EMPTY(400196, "官方记录必须是非空对象", HttpStatus.BAD_REQUEST),
    INTEGRITY_FIELD_INVALID(400197, "官方压缩文件完整性字段不合法", HttpStatus.BAD_REQUEST),
    FIELD_TYPE_CONTRACT_INVALID(400198, "官方字段类型不符合已建模合同", HttpStatus.BAD_REQUEST),
    RECORD_MODEL_MISMATCH(400199, "请求的具体字段模型与所属文件不一致", HttpStatus.BAD_REQUEST),
    RECORD_JSON_INVALID(400200, "官方记录 JSON 不符合所属文件的字段合同", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
