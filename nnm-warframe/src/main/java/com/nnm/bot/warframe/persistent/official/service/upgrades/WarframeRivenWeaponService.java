package com.nnm.bot.warframe.persistent.official.service.upgrades;

import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.WarframeRivenWeaponDTO;
import java.util.Map;

public interface WarframeRivenWeaponService {
    /**
     * 按规范化武器名称建立紫卡武器索引，保留重名候选。
     *
     * @return 以本方法约定的业务键组织的数据映射。
     */
    Map<String, WarframeRivenWeaponDTO> index();
}
