package com.nnm.bot.scheduler.task;

import com.nnm.bot.warframe.application.WorldStateService;
import com.nnm.bot.warframe.infrastructure.client.WarframeApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorldStatePollingTask {

    private final WarframeApiClient apiClient;
    private final WorldStateService worldStateService;

    @Scheduled(fixedDelayString = "${warframe.polling.interval-ms:120000}")
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
