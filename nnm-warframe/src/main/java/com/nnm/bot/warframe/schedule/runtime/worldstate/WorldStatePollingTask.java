package com.nnm.bot.warframe.schedule.runtime.worldstate;

import com.nnm.bot.warframe.runtime.service.worldstate.WarframeWorldStateService;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 按配置在每个实例中刷新本地世界状态快照，失败交由调度框架记录。 */
@Component
@ConditionalOnProperty(prefix = "nnm.warframe.pull.polling", name = "enabled", havingValue = "true")
public class WorldStatePollingTask {
    @Resource
    private WarframeWorldStateService worldStateService;

    /**
     * 拉取并发布本实例的最新世界状态；失败直接抛出，保留服务中上次成功的快照。
     */
    @Scheduled(
            cron = "#{@warframePollingProperties.cron}",
            zone = "#{@warframePollingProperties.zone}")
    public void execute() {
        worldStateService.refresh();
    }
}
