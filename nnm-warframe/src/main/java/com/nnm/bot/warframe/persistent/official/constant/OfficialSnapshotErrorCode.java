package com.nnm.bot.warframe.persistent.official.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 官方发布包清单、文件完整性和逐记录结构的稳定错误码。 */
@Getter
@RequiredArgsConstructor
public enum OfficialSnapshotErrorCode implements ErrorCode {
    MANIFEST_TRAILING_JSON(400320, "manifest 后存在额外 JSON 文档", HttpStatus.BAD_REQUEST),
    MANIFEST_EMPTY(400321, "manifest 不能为空", HttpStatus.BAD_REQUEST),
    MANIFEST_VERSION_UNSUPPORTED(400322, "不支持的 manifest schemaVersion", HttpStatus.BAD_REQUEST),
    MANIFEST_PRODUCER_INVALID(400323, "不合法的成品 producer", HttpStatus.BAD_REQUEST),
    MANIFEST_BUILD_ID_INVALID(400324, "缺少有效 buildId", HttpStatus.BAD_REQUEST),
    MANIFEST_CREATED_AT_MISSING(400325, "缺少构建时间", HttpStatus.BAD_REQUEST),
    MANIFEST_FILE_COUNT_INVALID(400326, "成品文件数量不合法", HttpStatus.BAD_REQUEST),
    MANIFEST_FILE_EMPTY(400327, "成品条目不能为空", HttpStatus.BAD_REQUEST),
    FILE_UNREGISTERED(400328, "未登记的业务文件", HttpStatus.BAD_REQUEST),
    FILE_PATH_DUPLICATE(400329, "重复成品路径", HttpStatus.BAD_REQUEST),
    FILE_DIGEST_INVALID(400330, "成品 SHA-256 不合法", HttpStatus.BAD_REQUEST),
    FILE_SIZE_LIMIT(400331, "成品字节数超限", HttpStatus.BAD_REQUEST),
    FILE_MEDIA_TYPE_INVALID(400332, "成品媒体类型不合法", HttpStatus.BAD_REQUEST),
    FILE_RECORD_COUNT_INVALID(400333, "记录数不合法", HttpStatus.BAD_REQUEST),
    MANIFEST_REQUIRED_FILES_MISSING(400334, "缺少必需业务文件", HttpStatus.BAD_REQUEST),
    MANIFEST_INVALID(400335, "manifest 不是有效发布清单", HttpStatus.BAD_REQUEST),
    FILE_SIZE_MISMATCH(400336, "成品字节数不匹配", HttpStatus.BAD_REQUEST),
    FILE_DIGEST_MISMATCH(400337, "成品 SHA-256 不匹配", HttpStatus.BAD_REQUEST),
    FILE_ROOT_INVALID(400338, "成品根结构不匹配", HttpStatus.BAD_REQUEST),
    FILE_TRUNCATED(400339, "成品 JSON 提前结束", HttpStatus.BAD_REQUEST),
    FILE_WRAPPER_FIELD_INVALID(400340, "成品字段结构不匹配", HttpStatus.BAD_REQUEST),
    FILE_RECORD_COUNT_MISMATCH(400341, "成品记录数不匹配", HttpStatus.BAD_REQUEST),
    FILE_WRAPPER_MISSING(400342, "成品缺少必需结构", HttpStatus.BAD_REQUEST),
    FILE_TRAILING_JSON(400343, "成品包含尾随 JSON", HttpStatus.BAD_REQUEST),
    FILE_READ_FAILED(400344, "成品 JSON 无法读取", HttpStatus.BAD_REQUEST),
    RECORD_COLLECTION_INVALID(400345, "记录集合结构不合法", HttpStatus.BAD_REQUEST),
    RECORD_COLLECTION_TRUNCATED(400346, "记录集合提前结束", HttpStatus.BAD_REQUEST),
    REWARD_COLLECTION_TRUNCATED(400347, "掉落集合提前结束", HttpStatus.BAD_REQUEST),
    FILE_PATH_INVALID(400348, "不合法的完整业务路径", HttpStatus.BAD_REQUEST),
    MANIFEST_SIZE_INVALID(400349, "manifest 压缩字节数不合法", HttpStatus.BAD_REQUEST),
    FILE_MISSING(400350, "成品 JSON 不存在", HttpStatus.BAD_REQUEST),
    MANIFEST_FILE_SET_MISMATCH(400351, "manifest 文件集合与 NnmBot 实体契约不一致，拒绝部分采纳", HttpStatus.BAD_REQUEST),
    GROUP_ROOT_INVALID(400352, "分组文件必须为对象", HttpStatus.BAD_REQUEST),
    WRAPPED_ROOT_INVALID(400353, "包装文件必须为对象", HttpStatus.BAD_REQUEST),
    WRAPPED_FIELD_INVALID(400354, "文件字段结构无效", HttpStatus.BAD_REQUEST),
    SYSTEMS_ARRAY_REQUIRED(400355, "systems 必须是数组", HttpStatus.BAD_REQUEST),
    RECORD_ARRAY_REQUIRED(400356, "业务集合必须为数组", HttpStatus.BAD_REQUEST),
    RECORD_OBJECT_REQUIRED(400357, "业务记录必须为对象", HttpStatus.BAD_REQUEST),
    RECORD_KEY_MISSING(400358, "成品缺少明确的稳定业务键", HttpStatus.BAD_REQUEST),
    USAGE_ID_MISSING(400359, "统计缺少身份字段", HttpStatus.BAD_REQUEST),
    RECORD_DICTIONARY_REQUIRED(400360, "业务集合必须为字典", HttpStatus.BAD_REQUEST),
    RECORD_DICTIONARY_MEMBER_MISSING(400361, "字典成员缺少名称", HttpStatus.BAD_REQUEST),
    DICTIONARY_KEY_MISMATCH(400362, "字典键与显式业务键不一致", HttpStatus.BAD_REQUEST),
    NIGHTWAVE_REWARDS_INVALID(400363, "夜波奖励必须为数组", HttpStatus.BAD_REQUEST),
    NIGHTWAVE_REWARD_KEY_MISSING(400364, "夜波奖励缺少物品业务键", HttpStatus.BAD_REQUEST),
    NIGHTWAVE_REWARD_KEY_DUPLICATE(400365, "夜波奖励存在同键冲突", HttpStatus.BAD_REQUEST),
    RECORD_KEY_TOO_LONG(400366, "成品业务键长度无效", HttpStatus.BAD_REQUEST),
    RECORD_KEY_DUPLICATE(400367, "成品存在重复业务键", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
