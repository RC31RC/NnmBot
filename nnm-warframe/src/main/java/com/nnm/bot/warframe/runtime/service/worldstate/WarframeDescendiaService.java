package com.nnm.bot.warframe.runtime.service.worldstate;

import com.nnm.bot.common.error.BusinessException;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.DescendiaRewardSelectionDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;

/** 沉沦之地快照、楼层资料与本周人工确认的统一入口。 */
public interface WarframeDescendiaService {
    /**
     * @param state 当前世界状态快照。
     * @return 供命令和管理端复用的本周楼层与奖励。
     */
    WarframeWorldStateCommandVO project(WorldStateDTO state);

    /**
     * @param selection 当前周的楼层、难度与候选奖励身份。
     * @throws BusinessException 周次已变更或候选已不可用时拒绝写入。
     */
    void confirm(DescendiaRewardSelectionDTO selection);
}
