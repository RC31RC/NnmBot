package com.nnm.bot.warframe.task;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * RDT 未启用时的本地调度兜底。
 *
 * <p>分布式模式下，刷新入口由 RDT 集群任务触发；本类只在明确关闭分布式能力时
 * 负责单节点运行，避免同一刷新逻辑被本地调度和 RDT 同时触发。</p>
 */
@Component
@ConditionalOnProperty(prefix = "nnm.distributed", name = "enabled", havingValue = "false")
public class LocalPollingTasks {
    @Resource
    private ObjectProvider<WorldStatePollingTask> worldState;

    @Scheduled(
            cron = "${nnm.warframe.polling.cron:0 */2 * * * *}",
            zone = "${nnm.warframe.polling.zone:Asia/Shanghai}"
    )
    public void poll() {
        worldState.ifAvailable(WorldStatePollingTask::execute);
    }

}
