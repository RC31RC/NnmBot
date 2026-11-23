package com.nnm.bot.warframe.persistent.curated.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 社区数据预览与上传的明确错误码。 */
@Getter
@RequiredArgsConstructor
public enum WarframeCommunityErrorCode implements ErrorCode {
    DESCENDIA_POOL_NOT_FOUND(400946, "沉沦之地映射必须引用已采纳且来源名称一致的掉落池", HttpStatus.BAD_REQUEST),
    PREVIEW_INDEX_READ_FAILED(503921, "社区预览索引无法解析", HttpStatus.SERVICE_UNAVAILABLE),
    PREVIEW_INDEX_SAVE_FAILED(503922, "社区预览索引无法保存", HttpStatus.SERVICE_UNAVAILABLE),
    UPLOAD_READ_FAILED(400921, "上传内容无法读取", HttpStatus.BAD_REQUEST),
    PREVIEW_ENCODE_FAILED(503923, "预览编码失败", HttpStatus.SERVICE_UNAVAILABLE),
    PREVIEW_CONTENT_READ_FAILED(503924, "预览内容无法读取", HttpStatus.SERVICE_UNAVAILABLE),
    RIVEN_ATTRIBUTES_UNAVAILABLE(400922, "请先拉取并采纳官方裂罅属性定义，再维护紫卡别名与组合", HttpStatus.BAD_REQUEST),
    RIVEN_ALIAS_INVALID(400923, "紫卡别名必须选择一至三个不同的官方属性", HttpStatus.BAD_REQUEST),
    APPLY_SELECTION_REQUIRED(400924, "请逐条选择需要应用的新增或修改", HttpStatus.BAD_REQUEST),
    PREVIEW_RECORDS_INVALID(400925, "导入内容包含无效记录，请修正后重新生成预览", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
