package com.nnm.bot.scheduler.task;

import com.nnm.bot.warframe.application.WorldStateService;
import com.nnm.bot.warframe.infrastructure.parser.WorldStateParser;
import com.nnm.bot.warframe.infrastructure.provider.WorldStateProvider;
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

    private final WorldStateProvider worldStateProvider;
    private final WorldStateService worldStateService;
    private final WorldStateParser worldStateParser;

    public WorldStatePollingTask(WorldStateProvider worldStateProvider,
                                 WorldStateService worldStateService,
                                 WorldStateParser worldStateParser,
                                 WarframePollingProperties properties) {
        properties.validate();
        this.worldStateProvider = worldStateProvider;
        this.worldStateService = worldStateService;
        this.worldStateParser = worldStateParser;
    }

    @Scheduled(fixedDelayString = "${nnm.warframe.polling.interval:2m}")
    public void poll() {
        try {
            worldStateProvider.fetch().ifPresent(payload -> {
                worldStateService.processNewState(worldStateParser.parse(
                        payload.body(), payload.source()));
                log.debug("World state fetched, validated and processed successfully");
            });
        } catch (Exception e) {
            log.error("World state polling failed", e);
        }
    }
}
