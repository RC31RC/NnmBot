package com.nnm.bot.warframe.runtime.service.riven;

import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenWeeklyVO;

public interface WarframeRivenWeeklyService {
    /**
     * 按给定业务条件查询周紫卡成交统计。
     *
     * @param platform 市场或官方接口使用的平台代码。
     * @param query 当前业务的查询条件。
     * @param rerolled 已洗练。
     * @param page 从一开始的页码。
     * @return 当前平台周紫卡成交统计的分页视图。
     */
    WarframeRivenWeeklyVO find(String platform, String query, Boolean rerolled, int page);

    /**
     * 获取当前上游数据并更新本地有效状态。
     */
    void refresh();
}
