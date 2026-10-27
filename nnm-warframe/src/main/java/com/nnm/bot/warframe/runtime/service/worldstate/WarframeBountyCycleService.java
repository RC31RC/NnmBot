package com.nnm.bot.warframe.runtime.service.worldstate;

import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;

/** 补充当前赏金任务，但不修改官方原始 Jobs、Nodes 或轮换时间。 */
public interface WarframeBountyCycleService {
    /**
     * 结合已采纳静态资料补全运行时数据。
     *
     * @param state 已经解析的当前世界状态。
     * @return 已经解析并校验的世界状态快照。
     */
    WorldStateDTO enrich(WorldStateDTO state);
}
