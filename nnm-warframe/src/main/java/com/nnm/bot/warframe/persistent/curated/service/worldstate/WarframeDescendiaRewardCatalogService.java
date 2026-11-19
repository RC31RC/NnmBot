package com.nnm.bot.warframe.persistent.curated.service.worldstate;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate.DescendiaRewardGroupDTO;
import java.util.List;

/** 从数据库读取人工审核的楼层奖池，不从运行时 JSON 回退。 */
public interface WarframeDescendiaRewardCatalogService {
    /** @return 所有已入库的候选奖池及可核实的固定奖励。 */
    List<DescendiaRewardGroupDTO> findAll();
}
