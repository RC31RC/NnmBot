package com.nnm.bot.warframe.persistent.official.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 官方资料命令的可预期输入错误。 */
@Getter
@RequiredArgsConstructor
public enum OfficialCommandErrorCode implements ErrorCode {
    GUIDE_OPTION_INVALID(400101, "攻略参数不合法", HttpStatus.BAD_REQUEST),
    GUIDE_OPTION_DUPLICATE(400102, "攻略参数不合法或重复", HttpStatus.BAD_REQUEST),
    QUERY_TOO_LONG(400103, "查询内容过长，请缩短到 200 字以内", HttpStatus.BAD_REQUEST),
    WORLD_INVALID(400104, "开放世界参数不合法", HttpStatus.BAD_REQUEST),
    MAP_LAYER_INVALID(400105, "地图图层参数不合法", HttpStatus.BAD_REQUEST),
    GUIDE_PAGE_INVALID(400106, "页码必须为正整数", HttpStatus.BAD_REQUEST),
    RELATION_OPTION_INVALID(400107, "仅支持末尾 --type 和 --page 参数，不能重复", HttpStatus.BAD_REQUEST),
    RELATION_TYPE_INVALID(400108, "物品类型不支持组件或掉落查询", HttpStatus.BAD_REQUEST),
    RELATION_PAGE_INVALID(400109, "页码必须为有效的正整数", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
