package com.nnm.bot.core.schedule.channel;

import com.nnm.bot.core.channel.topology.BotTopologyStore;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 仅负责本实例周期触发；状态与生命周期仍由所属组件维护。 */
@Component
public class BotTopologyRefreshTask {
    @Resource
    private BotTopologyStore topology;

    @Scheduled(fixedDelayString = "${nnm.cache.bot.topology-refresh:60s}")
    public void execute() {
        topology.refresh();
    }
}
