package com.nnm.bot.warframe.persistent.official.service.items;

import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.vo.items.WarframeBundleVO;

public interface WarframeBundleService {
    /**
     * 按给定筛选条件分页读取组合包定义。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param keyword 筛选使用的关键词。
     * @param page 从一开始的页码。
     * @return 组合包定义的分页视图。
     */
    WarframeBundleVO page(Category dataset, String keyword, int page);
}
