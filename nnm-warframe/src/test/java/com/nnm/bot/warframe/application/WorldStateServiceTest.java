package com.nnm.bot.warframe.application;

import com.nnm.bot.warframe.domain.ChangeDetector;
import com.nnm.bot.warframe.domain.model.WorldState;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class WorldStateServiceTest {

    @Test
    void publishesInitialAndChangedSnapshotsWithoutSharingMutableLists() {
        ChangeDetector<WorldState> detector = new ChangeDetector<>() {
            @Override
            public boolean hasChanged(WorldState oldState, WorldState newState) {
                return !oldState.alerts().equals(newState.alerts());
            }

            @Override
            public String type() {
                return "alerts";
            }
        };
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
        WorldStateService service = new WorldStateService(List.of(detector), publisher);
        WorldState initial = state(List.of());
        WorldState updated = state(List.of(new WorldState.Alert("a1", "Capture", "Credits", null)));

        service.processNewState(initial);
        service.processNewState(updated);

        ArgumentCaptor<WorldStateUpdatedEvent> events = ArgumentCaptor.forClass(WorldStateUpdatedEvent.class);
        verify(publisher, times(2)).publishEvent(events.capture());
        assertThat(events.getAllValues().getFirst().previous()).isNull();
        assertThat(events.getAllValues().get(1).changedSections()).containsExactly("alerts");
        assertThat(service.getCurrentState()).isSameAs(updated);
    }

    private WorldState state(List<WorldState.Alert> alerts) {
        return new WorldState(alerts, List.of(), List.of(), null, null, null, null,
                null, null, List.of(), Instant.parse("2026-07-29T00:00:00Z"));
    }
}
