package com.nnm.bot.warframe.runtime.pojo.vo.steelincursion;

import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.runtime.pojo.dto.steelincursion.SteelIncursionEntryDTO;
import java.time.Instant;

/** 钢铁侵袭采集状态及分页任务列表。 */
public record WarframeSteelIncursionManagementVO(boolean available, boolean stale, String source, Instant fetchedAt,
    Instant rangeFrom, Instant rangeUntil, PageResult<SteelIncursionEntryDTO> entries, String notice) {
}
