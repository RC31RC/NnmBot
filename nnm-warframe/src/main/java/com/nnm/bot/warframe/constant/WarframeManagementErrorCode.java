package com.nnm.bot.warframe.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WarframeManagementErrorCode implements ErrorCode {

    ITEM_NOT_FOUND(404, "Warframe 物品不存在", HttpStatus.NOT_FOUND),
    ALIAS_NOT_FOUND(404, "Warframe 物品别名不存在", HttpStatus.NOT_FOUND),
    SOURCE_KEY_DUPLICATED(400, "Warframe 数据源标识已存在", HttpStatus.BAD_REQUEST),
    ALIAS_DUPLICATED(400, "该物品已存在相同别名", HttpStatus.BAD_REQUEST),
    INVALID_SOURCE_TYPE(400, "Warframe 数据来源类型不合法", HttpStatus.BAD_REQUEST),
    INVALID_OFFICIAL_DATA_DOMAIN(400, "Warframe 官方数据领域不合法", HttpStatus.BAD_REQUEST),
    OFFICIAL_DATA_READ_ONLY(400, "Warframe 官方数据只允许通过刷新维护", HttpStatus.BAD_REQUEST),
    COMMUNITY_DATASET_READ_ONLY(
            400,
            "Warframe 社区外部同步数据为只读数据，只能通过数据刷新维护",
            HttpStatus.BAD_REQUEST),
    INVALID_IMPORT_FILE(400, "Warframe 导入文件格式不正确", HttpStatus.BAD_REQUEST),
    IMPORT_FILE_TOO_LARGE(413, "Warframe 导入文件过大", HttpStatus.CONTENT_TOO_LARGE),
    INVALID_FORUM_FILE(400, "论坛附件格式不受支持", HttpStatus.BAD_REQUEST),
    FORUM_FILE_TOO_LARGE(413, "论坛附件大小超过限制", HttpStatus.CONTENT_TOO_LARGE),
    FORUM_FILE_STORAGE_FAILED(500, "论坛附件存储失败", HttpStatus.INTERNAL_SERVER_ERROR),
    FORUM_ARTICLE_OPERATION_DENIED(
            403,
            "权限不足，不能维护同级或更高级用户创建的文章",
            HttpStatus.FORBIDDEN),
    OFFICIAL_DATA_EMPTY(502, "官方 PublicExport 没有可用物品数据", HttpStatus.BAD_GATEWAY),
    OFFICIAL_DATA_REFRESH_FAILED(502, "官方 PublicExport 刷新失败", HttpStatus.BAD_GATEWAY),
    COMMUNITY_DATA_SOURCE_UNAVAILABLE(502, "Warframe 社区数据来源拉取失败", HttpStatus.BAD_GATEWAY),
    COMMUNITY_PREVIEW_CACHE_UNAVAILABLE(
            503, "Warframe 社区数据预览缓存不可用", HttpStatus.SERVICE_UNAVAILABLE),
    COMMUNITY_SNAPSHOT_NOT_FOUND(404, "社区数据快照不存在", HttpStatus.NOT_FOUND),
    COMMUNITY_SNAPSHOT_NOT_PENDING(400, "社区数据快照已经处理，不能重复审批", HttpStatus.BAD_REQUEST),
    COMMUNITY_PREVIEW_NOT_FOUND(404, "社区数据预览不存在或已经过期", HttpStatus.NOT_FOUND),
    COMMUNITY_PREVIEW_ALREADY_MERGED(400, "社区数据预览已经合并，不能重复提交", HttpStatus.BAD_REQUEST),
    ARBITRATION_PREVIEW_NOT_FOUND(404, "仲裁排期预览不存在或已经过期", HttpStatus.NOT_FOUND),
    ARBITRATION_PREVIEW_INVALID(400, "仲裁排期预览记录不合法", HttpStatus.BAD_REQUEST),
    COMMUNITY_DATASET_BUSY(409, "该社区数据集正在刷新或合并", HttpStatus.CONFLICT),
    WORLD_STATE_REFRESH_FAILED(502, "Warframe 世界数据刷新失败", HttpStatus.BAD_GATEWAY);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
