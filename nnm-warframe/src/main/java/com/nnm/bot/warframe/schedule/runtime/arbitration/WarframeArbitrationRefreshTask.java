package com.nnm.bot.warframe.schedule.runtime.arbitration;

import com.nnm.bot.warframe.runtime.service.arbitration.WarframeArbitrationRefreshService;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 各实例定时刷新自身的仲裁排期快照，数据保存在本实例内存。 */
@Component
@ConditionalOnProperty(prefix = "nnm.warframe.pull.arbitration", name = "enabled", havingValue = "true")
public class WarframeArbitrationRefreshTask {
    @Resource
    private WarframeArbitrationRefreshService refresh;

    /**
     * 执行当前任务入口的业务操作。
     */
    @Scheduled(
            cron = "#{@warframeArbitrationPullProperties.cron}",
            zone = "#{@warframeArbitrationPullProperties.zone}")
    public void execute() {
        refresh.refresh();
    }
}
