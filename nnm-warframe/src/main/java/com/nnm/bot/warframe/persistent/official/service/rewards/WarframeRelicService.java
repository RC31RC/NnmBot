package com.nnm.bot.warframe.persistent.official.service.rewards;

import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeRelicDetailsDTO;
import java.util.List;

public interface WarframeRelicService {
    /**
     * 按名称或奖励关键词查询遗物及各精炼等级的奖励详情。
     *
     * @param keyword 筛选使用的关键词。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeRelicDetailsDTO> findDetails(String keyword);
}
