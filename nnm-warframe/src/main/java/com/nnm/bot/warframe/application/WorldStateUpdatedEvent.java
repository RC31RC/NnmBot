package com.nnm.bot.warframe.application;

import com.nnm.bot.warframe.domain.model.WorldState;

import java.time.Instant;
import java.util.List;

/** 发布给通知、管理端和指标适配器的稳定世界状态事件。 */
public record WorldStateUpdatedEvent(
        WorldState previous,
        WorldState current,
        List<String> changedSections,
        Instant observedAt
) {
    public WorldStateUpdatedEvent {
        changedSections = changedSections == null ? List.of() : List.copyOf(changedSections);
        observedAt = observedAt == null ? Instant.now() : observedAt;
    }
}
