package com.nnm.bot.warframe.schedule.runtime.riven;

import com.nnm.bot.warframe.runtime.config.riven.WarframeRivenWeeklyProperties;
import com.nnm.bot.warframe.runtime.service.riven.WarframeRivenWeeklyService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 在每个实例中刷新外部紫卡周统计快照。 */
@Component
public class WarframeRivenWeeklyRefreshTask {

    @Resource
    private WarframeRivenWeeklyService weeklyService;
    @Resource
    private WarframeRivenWeeklyProperties properties;

    /**
     * 按配置拉取最新的紫卡周统计数据。
     */
    @Scheduled(
            initialDelayString = "#{@warframeRivenWeeklyProperties.initialDelay.toString()}",
            fixedDelayString = "#{@warframeRivenWeeklyProperties.refreshCheckInterval.toString()}")
    public void refresh() {
        if (properties.isEnabled()) {
            weeklyService.refresh();
        }
    }
}
