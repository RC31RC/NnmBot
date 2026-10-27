package com.nnm.bot.core.schedule.channel;

import com.nnm.bot.core.channel.runtime.ClusterBotRuntime;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 仅负责本实例周期触发；状态与生命周期仍由所属组件维护。 */
@Component
public class BotRuntimeReconciliationTask {
    @Resource
    private ClusterBotRuntime runtime;

    @Scheduled(fixedDelayString = "${nnm.cluster.reconcile-interval:10s}")
    public void execute() {
        runtime.reconcile();
    }
}
