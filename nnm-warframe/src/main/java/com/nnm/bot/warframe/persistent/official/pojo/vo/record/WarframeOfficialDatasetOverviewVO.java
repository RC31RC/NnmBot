package com.nnm.bot.warframe.persistent.official.pojo.vo.record;

import java.time.Instant;
import java.util.Map;

/** 单个官方 JSON 的已采纳版本、业务记录数量与文件级信息。 */
public record WarframeOfficialDatasetOverviewVO(String domain, String productPath, String sourceRef, String buildId,
    String adoptedSha256, Instant adoptedAt, long officialCount, long selfMaintainedCount,
    Map<String, Object> metadata) {
}
