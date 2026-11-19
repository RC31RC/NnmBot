package com.nnm.bot.warframe.persistent.official.constant;

import com.nnm.bot.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/** 官方文件和版本拉取故障，对外不暴露内部路径和上游响应。 */
@Getter
@RequiredArgsConstructor
public enum OfficialSourceErrorCode implements ErrorCode {
    TAG_PAGE_LIMIT_EXCEEDED(502101, "数据源版本列表超出配置上限", HttpStatus.BAD_GATEWAY),
    LOCAL_FILE_UNAVAILABLE(502102, "本地官方压缩文件不可用", HttpStatus.BAD_GATEWAY),
    DOWNLOAD_FAILED(502103, "官方压缩文件下载失败", HttpStatus.BAD_GATEWAY),
    CONTENT_ENCODING_UNSUPPORTED(502104, "官方压缩文件响应编码不受支持", HttpStatus.BAD_GATEWAY),
    FILE_IO_FAILED(502105, "官方压缩文件读写失败", HttpStatus.BAD_GATEWAY),
    TEMP_FILE_CLEANUP_FAILED(502106, "官方下载临时文件清理失败", HttpStatus.BAD_GATEWAY),
    VERSION_RESPONSE_INVALID(502107, "GitHub 数据源版本响应不合法", HttpStatus.BAD_GATEWAY),
    ALL_SOURCES_FAILED(502108, "官方数据来源暂时不可用", HttpStatus.BAD_GATEWAY),
    LOCAL_COMMIT_UNAVAILABLE(502109, "本地数据源版本不可用", HttpStatus.BAD_GATEWAY),
    LOCAL_VERSION_FAILED(502110, "解析本地数据源版本失败", HttpStatus.BAD_GATEWAY),
    LOCAL_VERSION_INTERRUPTED(502111, "解析本地数据源版本被中断", HttpStatus.BAD_GATEWAY),
    LOCAL_REF_REQUIRED(400971, "本地数据源只接受配置的 local-ref", HttpStatus.BAD_REQUEST),
    TAG_WHITESPACE_INVALID(400972, "数据源版本不能包含首尾空格", HttpStatus.BAD_REQUEST),
    TAG_FORMAT_INVALID(400973, "官方数据刷新必须使用 UTC 秒级 yyyyMMddHHmmss tag", HttpStatus.BAD_REQUEST),
    TAG_REFERENCE_INVALID(400974, "指定版本不是数据源 tag", HttpStatus.BAD_REQUEST),
    TAG_COMMIT_INVALID(400975, "tag 不能解析为 Git commit", HttpStatus.BAD_REQUEST),
    MANIFEST_FILE_MISSING(400976, "manifest 中缺少待下载的官方 JSON 文件", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
