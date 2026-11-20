package com.nnm.bot.warframe.runtime.service.arbitration;

public interface WarframeArbitrationRefreshService {
    /**
     * 到期时获取当前上游数据并更新本地快照。
     */
    void refresh();
}
