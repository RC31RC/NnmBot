package com.nnm.bot.warframe.task;

import com.nnm.bot.warframe.application.WarframeWorldStateService;
import com.nnm.bot.warframe.messaging.producer.WarframeWorldStateRefreshMessageProducer;
import com.nnm.bot.warframe.task.config.WarframePollingProperties;
import io.github.rdt.spring.annotation.RdtScheduled;
import io.github.rdt.schedule.RdtScheduledTask;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;

@Slf4j
//@RdtScheduled(
//        id = "warframe-worldstate-poll-v1",
//        displayName = "Warframe 世界数据刷新",
//        cron = "${nnm.warframe.polling.cron:0 */2 * * * *}"
//)
public class WorldStatePollingTask implements RdtScheduledTask {

    @Resource
    private WarframeWorldStateService worldStateService;
    @Resource
    private WarframePollingProperties properties;
    @Resource
    private ObjectProvider<WarframeWorldStateRefreshMessageProducer> refreshProducer;

    @PostConstruct
    void validateProperties() {
        properties.validate();
    }

    @Override
    public void execute() {
        try {
            WarframeWorldStateRefreshMessageProducer producer =
                    refreshProducer == null ? null : refreshProducer.getIfAvailable();
            if (producer != null) {
                producer.publishRefreshRequest();
                return;
            }
            // 统一走 refresh，保留原始 JSON，管理端才能展示未建模的动态字段。
            worldStateService.refresh();
            log.debug("World state fetched, validated and processed successfully");
        } catch (Exception e) {
            log.error("World state polling failed", e);
            throw new IllegalStateException("World state polling failed", e);
        }
    }
}
