package com.nnm.bot.warframe.runtime.service.arbitration;

public interface WarframeArbitrationRefreshService {
    /**
     * 获取当前上游数据并更新本地有效状态。
     */
    void refresh();
}
