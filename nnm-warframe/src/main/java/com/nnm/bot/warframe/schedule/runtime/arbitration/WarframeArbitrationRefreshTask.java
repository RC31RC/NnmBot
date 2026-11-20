package com.nnm.bot.warframe.schedule.runtime.arbitration;

import com.nnm.bot.warframe.runtime.service.arbitration.WarframeArbitrationRefreshService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 独立拉取 browse.wf 仲裁排期；需要停用时注释定时任务注解。 */
@Component
public class WarframeArbitrationRefreshTask {
    @Resource
    private WarframeArbitrationRefreshService refreshService;

    /** 按重试间隔检查是否需要从上游拉取；成功刷新间隔由 Service 门控。 */
    @Scheduled(fixedDelayString = "#{@warframeArbitrationSourceProperties.retryInterval.toString()}")
    public void execute() {
        refreshService.refresh();
    }
}
