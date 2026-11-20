package com.nnm.bot.warframe.runtime.pojo.vo.kuva;

import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.runtime.pojo.dto.kuva.KuvaMissionDTO;
import java.time.Instant;

/** 赤毒任务来源状态及分页快照。 */
public record WarframeKuvaMissionManagementVO(boolean available, boolean stale, String source, Instant fetchedAt,
    PageResult<KuvaMissionDTO> entries, String notice) {
}
