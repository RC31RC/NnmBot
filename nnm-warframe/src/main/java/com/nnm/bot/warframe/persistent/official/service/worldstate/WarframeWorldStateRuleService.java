package com.nnm.bot.warframe.persistent.official.service.worldstate;

import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeStateTranslationDetailsDTO;
import java.util.List;

public interface WarframeWorldStateRuleService {
    /**
     * 读取已采纳的官方世界状态译名规则。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeStateTranslationDetailsDTO> stateTranslations();
}
