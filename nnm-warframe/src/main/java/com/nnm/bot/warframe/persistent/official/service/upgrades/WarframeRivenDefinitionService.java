package com.nnm.bot.warframe.persistent.official.service.upgrades;

import com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades.WarframeRivenDefinitionVO;
import java.util.List;

public interface WarframeRivenDefinitionService {
    /**
     * 按给定业务条件查询紫卡静态定义。
     *
     * @param keyword 筛选使用的关键词。
     * @param mode 模式。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeRivenDefinitionVO.EntryVO> find(String keyword, String mode);
}
