package com.nnm.bot.warframe.runtime.service.worldstate;

import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;

/** 限时补全入侵双方的动态任务类型，不改变官方主任务。 */
public interface WarframeInvasionEnrichmentService {
    /** @param official 已绑定的官方快照；@return 可用时附带任务类型，否则原样返回。 */
    WorldStateDTO enrich(WorldStateDTO official);
}
