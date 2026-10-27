package com.nnm.bot.warframe.persistent.official.service.reference;

import com.nnm.bot.warframe.constant.WarframeCommandCatalog.Category;
import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeDefinitionVO;

public interface WarframeDefinitionService {
    /**
     * 按查询条件筛选官方基础定义并返回分页结果。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param keyword 筛选使用的关键词。
     * @param page 从一开始的页码。
     * @return 官方基础定义的分页视图。
     */
    WarframeDefinitionVO query(Category dataset, String keyword, int page);
}
