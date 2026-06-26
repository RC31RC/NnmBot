package com.nnm.bot.warframe.application;

import com.nnm.bot.warframe.domain.ChangeDetector;
import com.nnm.bot.warframe.domain.model.WorldState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorldStateService {

    private final List<ChangeDetector<WorldState>> detectors;
    private final ApplicationEventPublisher eventPublisher;

    private volatile WorldState currentState;

    public void processNewState(WorldState newState) {
        WorldState oldState = this.currentState;
        this.currentState = newState;

        if (oldState == null) {
            log.info("Initial world state loaded");
            return;
        }

        for (ChangeDetector<WorldState> detector : detectors) {
            try {
                if (detector.hasChanged(oldState, newState)) {
                    log.debug("Change detected by: {}", detector.type());
                }
            } catch (Exception e) {
                log.error("Detector [{}] failed", detector.type(), e);
            }
        }
    }

    public WorldState getCurrentState() {
        return currentState;
    }
}
