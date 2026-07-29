package com.nnm.bot.warframe.application;

import com.nnm.bot.warframe.domain.ChangeDetector;
import com.nnm.bot.warframe.domain.model.WorldState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorldStateService {

    private final List<ChangeDetector<WorldState>> detectors;
    private final ApplicationEventPublisher eventPublisher;

    private final AtomicReference<WorldState> currentState = new AtomicReference<>();

    public void processNewState(WorldState newState) {
        Objects.requireNonNull(newState, "newState must not be null");
        WorldState oldState = currentState.getAndSet(newState);

        if (oldState == null) {
            log.info("Initial world state loaded");
            eventPublisher.publishEvent(new WorldStateUpdatedEvent(null, newState, List.of(), null));
            return;
        }

        LinkedHashSet<String> changedSections = new LinkedHashSet<>();
        for (ChangeDetector<WorldState> detector : detectors) {
            String detectorType = detectorType(detector);
            try {
                if (detector.hasChanged(oldState, newState)) {
                    changedSections.add(detectorType);
                    log.debug("Change detected by: {}", detectorType);
                }
            } catch (Exception e) {
                log.error("Detector [{}] failed", detectorType, e);
            }
        }
        eventPublisher.publishEvent(new WorldStateUpdatedEvent(oldState, newState,
                List.copyOf(changedSections), null));
    }

    public WorldState getCurrentState() {
        return currentState.get();
    }

    private String detectorType(ChangeDetector<WorldState> detector) {
        try {
            String type = detector.type();
            return type == null || type.isBlank() ? detector.getClass().getName() : type.trim();
        } catch (Exception e) {
            return detector.getClass().getName();
        }
    }
}
