package com.nnm.bot.warframe.service.impl;

import com.nnm.bot.warframe.model.WorldState;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** Deterministic port of warframe-worldstate-parser cycle models; intervals are [start, end). */
public final class WarframeCycleCalculator {
    private static final long ZARIMAN_FULL_CYCLE_MILLIS = 18_000_000L;
    private static final long ZARIMAN_STATE_MILLIS = 9_000_000L;
    private static final long DUVIRI_FULL_CYCLE_SECONDS = 36_000L;
    private static final long DUVIRI_STATE_SECONDS = 7_200L;
    private static final List<String> DUVIRI_STATES =
            List.of("sorrow", "fear", "joy", "anger", "envy");

    private WarframeCycleCalculator() {}

    public static WorldState.EarthCycle earth(Instant now) {
        long phase = Math.floorMod(now.getEpochSecond(), 28_800);
        return new WorldState.EarthCycle(phase < 14_400,
                Instant.ofEpochSecond(now.getEpochSecond() + 14_400 - phase % 14_400));
    }

    public static WorldState.VallisCycle vallis(Instant now, Instant warmEpoch) {
        long phase = Math.floorMod(now.toEpochMilli() - warmEpoch.toEpochMilli(), 1_600_000);
        boolean warm = phase < 400_000;
        return new WorldState.VallisCycle(warm,
                now.plusMillis((warm ? 400_000 : 1_600_000) - phase));
    }

    public static WorldState.CetusCycle cetus(Instant now, Instant bountyExpiry, Duration maxAge) {
        if (!validBountyAnchor(now, bountyExpiry, maxAge)) {
            return null;
        }
        // Bounty expiry anchors night-end. Extend a recent stale anchor, never use epoch zero.
        Instant anchor = bountyExpiry.truncatedTo(ChronoUnit.MINUTES);
        long phase = Math.floorMod(now.toEpochMilli() - anchor.toEpochMilli(), 9_000_000);
        boolean day = phase < 6_000_000;
        return new WorldState.CetusCycle(day,
                now.plusMillis((day ? 6_000_000 : 9_000_000) - phase));
    }

    public static WorldState.ZarimanCycle zariman(
            Instant now,
            Instant bountyExpiry,
            Duration bountyOffset,
            Instant corpusEpoch,
            Duration maxAge) {
        if (!validBountyAnchor(now, bountyExpiry, maxAge)) {
            return null;
        }
        Instant calculationPoint = bountyExpiry.minus(bountyOffset);
        long elapsed = Math.floorMod(
                calculationPoint.toEpochMilli() - corpusEpoch.toEpochMilli(),
                ZARIMAN_FULL_CYCLE_MILLIS);
        boolean corpus = ZARIMAN_FULL_CYCLE_MILLIS - elapsed > ZARIMAN_STATE_MILLIS;
        // Reference expiry is bounty-minus-offset rounded to the nearest minute.
        // Extend recent stale anchors by whole faction stages, including the exact expiry boundary.
        Instant anchor = calculationPoint.plusSeconds(30).truncatedTo(ChronoUnit.MINUTES);
        long stages = Math.floorDiv(Duration.between(anchor, now).getSeconds(),
                ZARIMAN_STATE_MILLIS / 1_000) + 1;
        Instant expiry = anchor.plusMillis(stages * ZARIMAN_STATE_MILLIS);
        if (Math.floorMod(stages, 2) != 0) {
            corpus = !corpus;
        }
        return new WorldState.ZarimanCycle(corpus ? "corpus" : "grineer", expiry);
    }

    public static WorldState.DuviriCycle duviri(Instant now, Instant epoch) {
        long elapsed = Duration.between(epoch, now).getSeconds();
        long phase = Math.floorMod(elapsed, DUVIRI_FULL_CYCLE_SECONDS);
        int stateIndex = (int) (phase / DUVIRI_STATE_SECONDS);
        long nextStage = Math.floorDiv(elapsed, DUVIRI_STATE_SECONDS) + 1;
        // Keep calibrated seconds: minute truncation expires the previous state 52 seconds early.
        return new WorldState.DuviriCycle(
                DUVIRI_STATES.get(stateIndex),
                epoch.plusSeconds(nextStage * DUVIRI_STATE_SECONDS));
    }

    private static boolean validBountyAnchor(Instant now, Instant expiry, Duration maxAge) {
        return expiry != null && !expiry.isBefore(now.minus(maxAge))
                && !expiry.isAfter(now.plusSeconds(9_000));
    }
}
