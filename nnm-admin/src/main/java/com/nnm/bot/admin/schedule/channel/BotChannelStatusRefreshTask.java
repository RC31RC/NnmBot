package com.nnm.bot.admin.schedule.channel;

import com.nnm.bot.admin.event.channel.BotChannelStatusBroadcaster;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 仅负责本实例周期触发；状态与生命周期仍由所属组件维护。 */
@Component
public class BotChannelStatusRefreshTask {
    @Resource
    private BotChannelStatusBroadcaster broadcaster;

    @Scheduled(fixedDelayString = "${nnm.cache.bot.status-refresh:2s}")
    public void execute() {
        broadcaster.refresh();
    }
}
