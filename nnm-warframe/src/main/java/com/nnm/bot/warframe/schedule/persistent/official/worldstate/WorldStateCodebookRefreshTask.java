package com.nnm.bot.warframe.schedule.persistent.official.worldstate;

import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateCodebookService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WorldStateCodebookRefreshTask {
    @Resource
    private WarframeWorldStateCodebookService codebooks;

    /**
     * 获取当前上游数据并更新本地有效状态。
     */
    @Scheduled(fixedDelayString = "5m", initialDelayString = "5m")
    public void refresh() {
        codebooks.reload();
    }
}
