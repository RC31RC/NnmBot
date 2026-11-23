package com.nnm.bot.warframe.schedule.runtime.kuva;

import com.nnm.bot.warframe.runtime.service.kuva.WarframeKuvaMissionService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 赤毒来源独立轮询，不依赖官方 PHP 世界状态刷新；需要停用时注释定时任务注解。 */
@Component
public class KuvaMissionPollingTask {
    @Resource
    private WarframeKuvaMissionService kuvaMissionService;

    /** 按独立配置刷新赤毒任务内存快照。 */
    // @Scheduled(fixedDelayString = "#{@warframeKuvaMissionProperties.interval.toString()}")
    public void execute() {
        kuvaMissionService.refresh();
    }
}
