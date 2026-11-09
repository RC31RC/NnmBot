package com.nnm.bot.warframe.schedule.runtime.arbitration;

import com.nnm.bot.warframe.runtime.service.arbitration.WarframeArbitrationRefreshService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 各实例定期检查刷新时机，Service 分别控制成功刷新与失败重试的间隔。 */
@Component
public class WarframeArbitrationRefreshTask {
    @Resource
    private WarframeArbitrationRefreshService refresh;

    /**
     * 到期才拉取仲裁；定期检查使短暂失败能够恢复，不改变成功后的长刷新周期。
     */
    @Scheduled(fixedDelayString = "#{@warframeArbitrationSourceProperties.retryInterval.toString()}")
    public void execute() {
        refresh.refresh();
    }
}
