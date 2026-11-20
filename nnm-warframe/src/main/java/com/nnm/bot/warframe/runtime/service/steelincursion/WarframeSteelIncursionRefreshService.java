package com.nnm.bot.warframe.runtime.service.steelincursion;

/** 钢铁侵袭独立来源的周期刷新边界。 */
public interface WarframeSteelIncursionRefreshService {
    /** 到期时获取钢铁侵袭排期并更新本地快照。 */
    void refresh();
}
