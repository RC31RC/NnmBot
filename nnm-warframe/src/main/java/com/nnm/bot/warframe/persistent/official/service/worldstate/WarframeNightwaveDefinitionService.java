package com.nnm.bot.warframe.persistent.official.service.worldstate;

import com.nnm.bot.warframe.persistent.official.constant.WarframeCatalogConstants.NightwaveSection;
import com.nnm.bot.warframe.persistent.official.pojo.vo.worldstate.WarframeNightwaveDefinitionVO;

public interface WarframeNightwaveDefinitionService {
    /**
     * 按查询条件筛选午夜电波静态定义并返回分页结果。
     *
     * @param keyword 筛选使用的关键词。
     * @param section 分区。
     * @param page 从一开始的页码。
     * @return 午夜电波静态定义的分页视图。
     */
    WarframeNightwaveDefinitionVO query(String keyword, NightwaveSection section, int page);
}
