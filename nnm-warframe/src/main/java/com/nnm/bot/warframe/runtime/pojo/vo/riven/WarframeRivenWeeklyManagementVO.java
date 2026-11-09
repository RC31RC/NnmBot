package com.nnm.bot.warframe.runtime.pojo.vo.riven;

import com.nnm.bot.common.response.PageResult;
import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenWeeklyVO.EntryVO;
import java.time.Instant;

/** 周成交管理视图；查询只读取已采集快照，不同步访问上游。 */
public record WarframeRivenWeeklyManagementVO(boolean available, boolean stale, String source, String platform,
    Instant fetchedAt, PageResult<EntryVO> entries, String notice) {
}
