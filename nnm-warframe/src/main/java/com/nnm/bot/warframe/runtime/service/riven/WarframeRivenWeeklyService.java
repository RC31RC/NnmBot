package com.nnm.bot.warframe.runtime.service.riven;

import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenWeeklyVO;
import com.nnm.bot.warframe.runtime.pojo.query.riven.WarframeRivenWeeklyQuery;
import com.nnm.bot.warframe.runtime.pojo.vo.riven.WarframeRivenWeeklyManagementVO;

public interface WarframeRivenWeeklyService {
    /**
     * 按给定业务条件查询周紫卡成交统计。
     *
     * @param query 平台、武器、洗练状态及分页条件。
     * @return 当前平台周紫卡成交统计的分页视图。
     */
    WarframeRivenWeeklyVO find(WarframeRivenWeeklyQuery query);

    /**
     * 分页读取已采集快照，管理端查询不触发上游请求。
     *
     * @param query 平台、武器、洗练状态及分页条件。
     * @return 采集状态与当前页统计。
     */
    WarframeRivenWeeklyManagementVO page(WarframeRivenWeeklyQuery query);

    /**
     * 获取当前上游数据并更新本地有效状态。
     */
    void refresh();
}
