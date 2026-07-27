package com.nnm.bot.scheduler.task;

import com.nnm.bot.warframe.application.WorldStateService;
import com.nnm.bot.warframe.infrastructure.client.WarframeApiClient;
import com.nnm.bot.scheduler.config.WarframePollingProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(
        prefix = "nnm.warframe.polling",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class WorldStatePollingTask {

    private final WarframeApiClient apiClient;
    private final WorldStateService worldStateService;

    public WorldStatePollingTask(WarframeApiClient apiClient,
                                 WorldStateService worldStateService,
                                 WarframePollingProperties properties) {
        properties.validate();
        this.apiClient = apiClient;
        this.worldStateService = worldStateService;
    }

    @Scheduled(fixedDelayString = "${nnm.warframe.polling.interval:2m}")
    public void poll() {
        try {
            String json = apiClient.fetchWorldState();
            if (json != null) {
                // TODO: parse JSON to WorldState and process
                log.debug("World state fetched successfully");
            }
        } catch (Exception e) {
            log.error("World state polling failed", e);
        }
    }
}
