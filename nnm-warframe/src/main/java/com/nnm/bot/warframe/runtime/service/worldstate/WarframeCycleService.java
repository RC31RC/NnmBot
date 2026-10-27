package com.nnm.bot.warframe.runtime.service.worldstate;

import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;

public interface WarframeCycleService {
    /**
     * 解析引用或输入对应的实际业务对象。
     *
     * @param state 已经解析的当前世界状态。
     * @return 已经解析并校验的世界状态快照。
     */
    WorldStateDTO resolve(WorldStateDTO state);
}
