package com.nnm.bot.warframe.runtime.service.kuva;

import com.nnm.bot.warframe.runtime.pojo.query.kuva.WarframeKuvaMissionQuery;
import com.nnm.bot.warframe.runtime.pojo.vo.kuva.WarframeKuvaMissionManagementVO;

/** 独立赤毒任务快照的刷新和管理端查询边界。 */
public interface WarframeKuvaMissionService {
    /** 拉取任务流并原子替换最近一次成功快照；失败时保留旧快照。 */
    void refresh();

    /** @param query 关键词和分页条件。 @return 当前赤毒任务快照及采集状态。 */
    WarframeKuvaMissionManagementVO page(WarframeKuvaMissionQuery query);
}
