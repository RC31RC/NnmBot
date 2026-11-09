package com.nnm.bot.warframe.persistent.official.service.rewards;

import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeBountyDefinitionDetailsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeBountyVO;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface WarframeBountyDefinitionService {
    /**
     * 按给定业务条件查询赏金静态定义。
     *
     * @param jobType 任务类型。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    Optional<WarframeBountyDefinitionDetailsDTO> find(String jobType);

    /**
     * 按给定筛选条件分页读取赏金静态定义。
     *
     * @param keyword 筛选使用的关键词。
     * @param page 从一开始的页码。
     * @return 赏金静态定义的分页视图。
     */
    WarframeBountyVO page(String keyword, int page);

    /**
     * 对本次动态数据的赏金类型统一批量读取静态定义。
     *
     * @param jobTypes 当前动态赏金类型。
     * @return 已找到的类型及定义，不包含缺失占位值。
     */
    Map<String, WarframeBountyDefinitionDetailsDTO> findAll(Collection<String> jobTypes);
}
