package com.nnm.bot.warframe.runtime.service.worldstate;

import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;

/** 独立结构化任务流提供赤毒任务，任何异常不影响官方世界状态。 */
public interface WarframeKuvaMissionEnrichmentService {
    /** @param official 官方快照；@return 有效任务增量或未更改的官方快照。 */
    WorldStateDTO enrich(WorldStateDTO official);
}
