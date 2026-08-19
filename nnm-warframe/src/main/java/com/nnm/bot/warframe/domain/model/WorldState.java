package com.nnm.bot.warframe.domain.model;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * NnmBot 自己维护的 Warframe 世界状态模型。
 *
 * <p>这个模型不暴露任何上游 SDK 类型。外部 API 的字段变化由解析器和映射器
 * 吸收，业务层只依赖稳定的 Warframe 概念。前 11 个字段保留了早期 NnmBot
 * API 的兼容构造器，新字段用于覆盖机器人业务所需的主要世界状态和数据质量信息。</p>
 */
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
        Instant timestamp,
        List<Sortie> sorties,
        List<VoidTrader> voidTraders,
        EarthCycle earthCycle,
        DuviriCycle duviriCycle,
        ZarimanCycle zarimanCycle,
        List<DailyDeal> dailyDeals,
        List<WorldEvent> events,
        List<NewsItem> news,
        List<SyndicateMission> syndicateMissions,
        List<SteelPathOffering> steelPathOfferings,
        List<ActiveMission> activeMissions,
        List<Sortie> archonHunts,
        WorldStateMetadata metadata
) {

    /** 兼容原始 NnmBot 的精简构造器。 */
    public WorldState(
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
        this(alerts, fissures, invasions, sortie, cetusCycle, vallisCycle, cambionCycle,
                voidTrader, arbitration, nightwave, timestamp,
                sortie == null ? List.of() : List.of(sortie),
                voidTrader == null ? List.of() : List.of(voidTrader),
                null, null, null, List.of(), List.of(), List.of(), List.of(), List.of(),
                fissures == null ? List.of() : fissures.stream().filter(Objects::nonNull)
                        .map(ActiveMission::fromFissure).toList(),
                List.of(),
                WorldStateMetadata.unknown(timestamp));
    }

    public WorldState {
        alerts = immutable(alerts);
        fissures = immutable(fissures);
        invasions = immutable(invasions);
        nightwave = immutable(nightwave);
        sorties = immutable(sorties);
        voidTraders = immutable(voidTraders);
        dailyDeals = immutable(dailyDeals);
        events = immutable(events);
        news = immutable(news);
        syndicateMissions = immutable(syndicateMissions);
        steelPathOfferings = immutable(steelPathOfferings);
        activeMissions = immutable(activeMissions);
        archonHunts = immutable(archonHunts);
        metadata = metadata == null ? WorldStateMetadata.unknown(timestamp) : metadata;
    }

    public List<Sortie> allSorties() {
        return sorties.isEmpty() && sortie != null ? List.of(sortie) : sorties;
    }

    public List<VoidTrader> allVoidTraders() {
        return voidTraders.isEmpty() && voidTrader != null ? List.of(voidTrader) : voidTraders;
    }

    public List<ActiveMission> allActiveMissions() {
        return activeMissions.isEmpty()
                ? fissures.stream().map(ActiveMission::fromFissure).toList()
                : activeMissions;
    }

    public boolean stale() {
        return metadata.stale();
    }

    public Duration ageAt(Instant now) {
        Objects.requireNonNull(now, "now must not be null");
        Instant captured = metadata.fetchedAt() == null ? timestamp : metadata.fetchedAt();
        return captured == null ? Duration.ZERO : Duration.between(captured, now).isNegative()
                ? Duration.ZERO : Duration.between(captured, now);
    }

    private static <T> List<T> immutable(List<T> value) {
        return value == null ? List.of() : List.copyOf(value);
    }

    public record Alert(
            String id,
            String mission,
            String reward,
            Instant expiry,
            Instant activation,
            String node,
            String faction,
            Integer minEnemyLevel,
            Integer maxEnemyLevel,
            List<String> rewardItems
    ) {
        public Alert(String id, String mission, String reward, Instant expiry) {
            this(id, mission, reward, expiry, null, null, null, null, null, List.of());
        }

        public Alert {
            rewardItems = immutable(rewardItems);
        }
    }

    public record Fissure(
            String id,
            String node,
            String tier,
            String missionType,
            Instant expiry,
            Instant activation,
            boolean hard,
            boolean voidStorm
    ) {
        public Fissure(String id, String node, String tier, String missionType, Instant expiry) {
            this(id, node, tier, missionType, expiry, null, false, false);
        }
    }

    public record Invasion(
            String id,
            String node,
            String attackerReward,
            String defenderReward,
            float completion,
            Instant activation,
            boolean completed,
            List<String> attackerRewards,
            List<String> defenderRewards
    ) {
        public Invasion(String id, String node, String attackerReward, String defenderReward, float completion) {
            this(id, node, attackerReward, defenderReward, completion, null, false, List.of(), List.of());
        }

        public Invasion {
            attackerRewards = immutable(attackerRewards);
            defenderRewards = immutable(defenderRewards);
        }
    }

    public record Sortie(String id, String boss, List<SortieVariant> variants, Instant expiry) {
        public Sortie {
            variants = immutable(variants);
        }
    }

    public record SortieVariant(String missionType, String modifier, String node) {
    }

    public record CetusCycle(boolean isDay, Instant expiry) {
    }

    public record VallisCycle(boolean isWarm, Instant expiry) {
    }

    public record CambionCycle(String active, Instant expiry) {
    }

    public record EarthCycle(boolean isDay, Instant expiry) {
    }

    public record DuviriCycle(String state, Instant expiry) {
    }

    public record ZarimanCycle(String state, Instant expiry) {
    }

    public record VoidTrader(
            String location,
            Instant activation,
            Instant expiry,
            boolean active,
            List<VoidTraderItem> inventory
    ) {
        public VoidTrader {
            inventory = immutable(inventory);
        }
    }

    public record VoidTraderItem(String item, int ducats, int credits) {
    }

    public record Arbitration(String node, String type, Instant expiry) {
    }

    public record NightwaveMission(
            String id,
            String title,
            String desc,
            int reputation,
            boolean isDaily
    ) {
    }

    public record DailyDeal(String id, String item, int stock, int sold, int credits, int platinum,
                            Instant activation, Instant expiry) {
    }

    public record WorldEvent(
            String id,
            String title,
            String description,
            Instant activation,
            Instant expiry,
            String category,
            String node,
            Double progress,
            Double goal,
            List<String> rewards
    ) {
        public WorldEvent {
            rewards = immutable(rewards);
        }
    }

    /** 新闻与活动目标分开建模，避免混淆官方事件列表与目标列表的不同语义。 */
    public record NewsItem(
            String id,
            String title,
            String link,
            String imageUrl,
            Instant publishedAt,
            Instant expiry,
            boolean priority
    ) {
    }

    public record SyndicateMission(String id, String syndicate, String node, Instant activation, Instant expiry,
                                   List<String> jobs) {
        public SyndicateMission {
            jobs = immutable(jobs);
        }
    }

    public record SteelPathOffering(String id, String node, String reward, Instant activation, Instant expiry) {
    }

    public record ActiveMission(String id, String node, String missionType, String tier, Instant activation,
                                Instant expiry, boolean hard, boolean voidStorm) {
        static ActiveMission fromFissure(Fissure fissure) {
            return new ActiveMission(fissure.id(), fissure.node(), fissure.missionType(), fissure.tier(),
                    fissure.activation(), fissure.expiry(), fissure.hard(), fissure.voidStorm());
        }
    }

    /** 采集与质量元数据，避免把陈旧数据伪装成实时数据。 */
    public record WorldStateMetadata(
            String source,
            Instant fetchedAt,
            Instant providerTimestamp,
            boolean stale,
            String schemaVersion,
            String contentDigest
    ) {
        public WorldStateMetadata {
            source = source == null || source.isBlank() ? "unknown" : source;
            schemaVersion = schemaVersion == null || schemaVersion.isBlank() ? "nnm-world-state-v1" : schemaVersion;
        }

        public static WorldStateMetadata unknown(Instant providerTimestamp) {
            return new WorldStateMetadata("unknown", Instant.now(), providerTimestamp, false,
                    "nnm-world-state-v1", null);
        }
    }
}
