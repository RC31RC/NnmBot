package com.nnm.bot.warframe.application;

import com.nnm.bot.warframe.domain.ChangeDetector;
import com.nnm.bot.warframe.domain.model.WorldState;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class WorldStateService {

    @Resource
    private ObjectProvider<ChangeDetector<WorldState>> detectorProvider;
    @Resource
    private ApplicationEventPublisher eventPublisher;
    private List<ChangeDetector<WorldState>> detectors = List.of();

    private final AtomicReference<WorldState> currentState = new AtomicReference<>();

    @PostConstruct
    void initializeDetectors() {
        detectors = detectorProvider.orderedStream().toList();
    }

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
