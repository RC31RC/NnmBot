package com.nnm.bot.warframe.domain.model;

import java.time.Instant;
import java.util.List;

public record WorldState(
        List<Alert> alerts,
        List<Fissure> fissures,
        List<Invasion> invasions,
        Sortie sortie,
        CetusCycle cetusCycle,
        VallisCycle vallisCycle,
        CambionCycle cambionCycle,
        VoidTrader voidTrader,
        Arbitration arbitration,
        List<NightwaveMission> nightwave,
        Instant timestamp
) {
    public record Alert(String id, String mission, String reward, Instant expiry) {}
    public record Fissure(String id, String node, String tier, String missionType, Instant expiry) {}
    public record Invasion(String id, String node, String attackerReward, String defenderReward, float completion) {}
    public record Sortie(String id, String boss, List<SortieVariant> variants, Instant expiry) {}
    public record SortieVariant(String missionType, String modifier, String node) {}
    public record CetusCycle(boolean isDay, Instant expiry) {}
    public record VallisCycle(boolean isWarm, Instant expiry) {}
    public record CambionCycle(String active, Instant expiry) {}
    public record VoidTrader(String location, Instant activation, Instant expiry, boolean active, List<VoidTraderItem> inventory) {}
    public record VoidTraderItem(String item, int ducats, int credits) {}
    public record Arbitration(String node, String type, Instant expiry) {}
    public record NightwaveMission(String id, String title, String desc, int reputation, boolean isDaily) {}
}
