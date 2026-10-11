package com.nnm.bot.warframe.model;

import java.time.Instant;

public record ArbitrationScheduleEntry(
        String id, Instant activation, Instant expiry, String node, String missionType,
        String enemy, String planet, String boundsJson) {}
