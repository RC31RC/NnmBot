package com.nnm.bot.warframe.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WarframeManagementErrorCode implements ErrorCode {

    INVALID_COMMUNITY_DATASET(400, "纯自维护数据集不在白名单内", HttpStatus.BAD_REQUEST),
    COMMUNITY_INVALID_INPUT(400, "纯自维护输入不符合数据集字段契约", HttpStatus.BAD_REQUEST),
    COMMUNITY_PREVIEW_NOT_FOUND(404, "纯自维护预览不存在或已过期", HttpStatus.NOT_FOUND),
    COMMUNITY_PREVIEW_CONFLICT(409, "所选记录在预览后已变化，请重新预览", HttpStatus.CONFLICT),
    COMMUNITY_SOURCE_REJECTED(400, "raw URL 不符合协议、域名或网络地址限制", HttpStatus.BAD_REQUEST),
    ITEM_NOT_FOUND(404, "Warframe 物品不存在", HttpStatus.NOT_FOUND),
    ALIAS_NOT_FOUND(404, "Warframe 物品别名不存在", HttpStatus.NOT_FOUND),
    ALIAS_DUPLICATED(400, "该物品已存在相同别名", HttpStatus.BAD_REQUEST),
    INVALID_OFFICIAL_DATA_DOMAIN(400, "Warframe 官方数据领域不合法", HttpStatus.BAD_REQUEST),
    INVALID_IMPORT_FILE(400, "Warframe 导入文件格式不正确", HttpStatus.BAD_REQUEST),
    IMPORT_FILE_TOO_LARGE(413, "Warframe 导入文件过大", HttpStatus.CONTENT_TOO_LARGE),
    DATASET_UNAVAILABLE(502, "Warframe 官方数据源拉取失败", HttpStatus.BAD_GATEWAY),
    DATASET_INVALID(400, "Warframe 官方数据包校验失败", HttpStatus.BAD_REQUEST),
    DATASET_VERSION_INVALID(400, "请选择有效的数据源 tag 或完整 commit", HttpStatus.BAD_REQUEST),
    DATASET_PREVIEW_CAPACITY(409, "待审核官方快照已达到容量上限，请先完成审核或等待预览过期", HttpStatus.CONFLICT),
    DATASET_PREVIEW_NOT_FOUND(404, "官方数据预览不存在或已过期", HttpStatus.NOT_FOUND),
    DATASET_PREVIEW_CONFLICT(409, "官方数据基线已变化，请重新生成预览", HttpStatus.CONFLICT),
    OFFICIAL_CONVERSION_CONFIRM_REQUIRED(400, "请先确认将官方记录转为自维护整树", HttpStatus.BAD_REQUEST),
    SELF_MAINTAINED_OVERWRITE_CONFIRM_REQUIRED(40931, "同身份自维护整树已存在，请确认覆盖", HttpStatus.CONFLICT),
    OFFICIAL_DATA_EMPTY(502, "官方 PublicExport 没有可用物品数据", HttpStatus.BAD_GATEWAY),
    OFFICIAL_DATA_REFRESH_FAILED(502, "官方 PublicExport 刷新失败", HttpStatus.BAD_GATEWAY),
    EXTERNAL_DATA_SOURCE_UNAVAILABLE(502, "Warframe 外部数据来源拉取失败", HttpStatus.BAD_GATEWAY),
    COMMUNITY_PREVIEW_CACHE_UNAVAILABLE(503, "纯自维护预览缓存不可用", HttpStatus.SERVICE_UNAVAILABLE),
    ARBITRATION_PREVIEW_NOT_FOUND(404, "仲裁排期预览不存在或已经过期", HttpStatus.NOT_FOUND),
    ARBITRATION_PREVIEW_INVALID(400, "仲裁排期预览记录不合法", HttpStatus.BAD_REQUEST),
    WORLD_STATE_REFRESH_FAILED(502, "Warframe 世界数据刷新失败", HttpStatus.BAD_GATEWAY);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
