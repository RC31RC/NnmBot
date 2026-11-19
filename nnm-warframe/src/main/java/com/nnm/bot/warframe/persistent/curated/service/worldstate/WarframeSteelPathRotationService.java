package com.nnm.bot.warframe.persistent.curated.service.worldstate;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate.SteelPathRotationDTO;
import java.time.Instant;

/** 从已审核数据库轮换表计算某个时刻的钢铁奖励。 */
public interface WarframeSteelPathRotationService {
    /** @param asOf 要计算的 UTC 时刻；@return 完整轮换规则中本周商品，不完整时返回 null。 */
    SteelPathRotationDTO current(Instant asOf);
}
