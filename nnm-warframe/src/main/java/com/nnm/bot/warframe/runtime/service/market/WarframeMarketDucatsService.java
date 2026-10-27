package com.nnm.bot.warframe.runtime.service.market;

import com.nnm.bot.warframe.runtime.pojo.vo.market.WarframeDucatRankingVO;
import java.util.Optional;

public interface WarframeMarketDucatsService {
    /**
     * 按业务规则生成成交统计排行。
     *
     * @param gold 金档。
     * @param keyword 筛选使用的关键词。
     * @param page 从一开始的页码。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    Optional<WarframeDucatRankingVO> ranking(boolean gold, String keyword, int page);
}
