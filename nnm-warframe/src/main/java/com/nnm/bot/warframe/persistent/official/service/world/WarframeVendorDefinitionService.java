package com.nnm.bot.warframe.persistent.official.service.world;

import com.nnm.bot.warframe.persistent.official.constant.WarframeCatalogConstants.VendorSection;
import com.nnm.bot.warframe.persistent.official.pojo.vo.world.WarframeVendorDefinitionVO;

public interface WarframeVendorDefinitionService {
    /**
     * 按查询条件筛选商人静态定义并返回分页结果。
     *
     * @param keyword 筛选使用的关键词。
     * @param section 分区。
     * @param page 从一开始的页码。
     * @return 商人静态定义的分页视图。
     */
    WarframeVendorDefinitionVO query(String keyword, VendorSection section, int page);
}
