package com.nnm.bot.warframe.persistent.official.service.rewards;

import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeBountyDefinitionDetailsDTO;
import com.nnm.bot.warframe.persistent.official.pojo.vo.rewards.WarframeBountyVO;
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
}
