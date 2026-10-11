package com.nnm.bot.warframe.model;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

public record ArbitrationScheduleResult(
        List<ArbitrationScheduleEntry> entries, String notice, Instant fetchedAt, ZoneId zone) {
    public ArbitrationScheduleResult {
        entries = List.copyOf(entries);
    }
}
