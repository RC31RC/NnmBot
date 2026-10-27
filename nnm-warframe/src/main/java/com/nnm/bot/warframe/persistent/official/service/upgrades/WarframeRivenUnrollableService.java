package com.nnm.bot.warframe.persistent.official.service.upgrades;

import com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades.WarframeRivenUnrollableVO;
import java.util.List;

public interface WarframeRivenUnrollableService {
    /**
     * 按给定业务条件查询不可洗紫卡词条定义。
     *
     * @param keyword 筛选使用的关键词。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeRivenUnrollableVO.EntryVO> find(String keyword);
}
