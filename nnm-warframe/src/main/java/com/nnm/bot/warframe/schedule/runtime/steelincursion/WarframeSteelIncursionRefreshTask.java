package com.nnm.bot.warframe.schedule.runtime.steelincursion;

import com.nnm.bot.warframe.runtime.service.steelincursion.WarframeSteelIncursionRefreshService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 独立拉取 browse.wf 钢铁侵袭排期；需要停用时注释定时任务注解。 */
@Component
public class WarframeSteelIncursionRefreshTask {
    @Resource
    private WarframeSteelIncursionRefreshService refreshService;

    /** 按重试间隔检查是否需要从上游拉取；成功刷新间隔由 Service 门控。 */
    @Scheduled(fixedDelayString = "#{@warframeSteelIncursionProperties.retryInterval.toString()}")
    public void execute() {
        refreshService.refresh();
    }
}
