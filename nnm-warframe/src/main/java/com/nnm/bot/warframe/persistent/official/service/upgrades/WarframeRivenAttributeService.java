package com.nnm.bot.warframe.persistent.official.service.upgrades;

import com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades.WarframeRivenAttributeDTO;
import java.util.List;

public interface WarframeRivenAttributeService {
    /**
     * 读取已采纳的官方紫卡属性定义，供词条解析与数值分析使用。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeRivenAttributeDTO> definitions();
}
