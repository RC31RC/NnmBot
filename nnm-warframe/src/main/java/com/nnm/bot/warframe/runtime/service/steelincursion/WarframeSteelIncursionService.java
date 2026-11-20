package com.nnm.bot.warframe.runtime.service.steelincursion;

import com.nnm.bot.warframe.runtime.pojo.query.steelincursion.WarframeSteelIncursionQuery;
import com.nnm.bot.warframe.runtime.pojo.vo.steelincursion.WarframeSteelIncursionManagementVO;

/** 钢铁侵袭内存快照的管理端查询边界。 */
public interface WarframeSteelIncursionService {
    /** @param query 时间、关键词和分页条件。 @return 当前钢铁侵袭快照及采集状态。 */
    WarframeSteelIncursionManagementVO page(WarframeSteelIncursionQuery query);
}
