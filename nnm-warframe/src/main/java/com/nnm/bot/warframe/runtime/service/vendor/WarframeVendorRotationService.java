package com.nnm.bot.warframe.runtime.service.vendor;

import com.nnm.bot.warframe.runtime.pojo.dto.vendor.WarframeVendorObservationDTO;
import com.nnm.bot.warframe.runtime.pojo.dto.worldstate.WorldStateDTO;
import com.nnm.bot.warframe.runtime.pojo.vo.vendor.WarframeWeaponVendorVO;
import com.nnm.bot.warframe.runtime.pojo.vo.worldstate.WarframeWorldStateCommandVO;

/** 使用官方 Vendor 资料计算商人轮换并管理本轮人工确认。 */
public interface WarframeVendorRotationService {
    /** @param state 当前动态快照；@return 钢铁商人的当前奖励或官方候选。 */
    WarframeWorldStateCommandVO steelPath(WorldStateDTO state);

    /** @return 当前四日轮换预测与本机人工填报。 */
    WarframeWeaponVendorVO weapons();

    /**
     * @param observation 商人、轮次、武器及选填属性。
     * @return 更新后的轮换展示。
     */
    WarframeWeaponVendorVO observe(WarframeVendorObservationDTO observation);
}
