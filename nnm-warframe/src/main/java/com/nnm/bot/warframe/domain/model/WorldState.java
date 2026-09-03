package com.nnm.bot.warframe.domain.model;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

import cn.hutool.core.collection.CollUtil;

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
 *
 * @param alerts 当前警报列表
 * @param fissures 当前虚空裂缝列表
 * @param invasions 当前入侵列表
 * @param sortie 当前突击兼容字段
 * @param cetusCycle 夜灵平原昼夜周期
 * @param vallisCycle 奥布山谷冷热周期
 * @param cambionCycle 魔胎之境周期
 * @param voidTrader 当前奸商兼容字段
 * @param arbitration 当前仲裁任务
 * @param nightwave 夜波任务列表
 * @param timestamp 世界状态时间戳
 * @param sorties 当前突击列表
 * @param voidTraders 当前奸商列表
 * @param earthCycle 地球昼夜周期
 * @param duviriCycle 双衍王境周期
 * @param zarimanCycle 扎里曼周期
 * @param dailyDeals 当前每日交易列表
 * @param events 世界事件列表
 * @param news 新闻列表
 * @param syndicateMissions 集团任务列表
 * @param steelPathOfferings 钢铁之路奖励列表
 * @param activeMissions 当前活动任务列表
 * @param archonHunts 执政官猎杀列表
 * @param metadata 数据来源与新鲜度信息
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
                ObjectUtil.isNull(sortie) ? List.of() : List.of(sortie),
                ObjectUtil.isNull(voidTrader) ? List.of() : List.of(voidTrader),
                null, null, null, List.of(), List.of(), List.of(), List.of(), List.of(),
                CollUtil.isEmpty(fissures) ? List.of() : fissures.stream().filter(Objects::nonNull)
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
        metadata = ObjectUtil.isNull(metadata) ? WorldStateMetadata.unknown(timestamp) : metadata;
    }

    public List<Sortie> allSorties() {
        return sorties.isEmpty() && ObjectUtil.isNotNull(sortie) ? List.of(sortie) : sorties;
    }

    public List<VoidTrader> allVoidTraders() {
        return voidTraders.isEmpty() && ObjectUtil.isNotNull(voidTrader) ? List.of(voidTrader) : voidTraders;
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
        Instant captured = ObjectUtil.isNull(metadata.fetchedAt()) ? timestamp : metadata.fetchedAt();
        return ObjectUtil.isNull(captured) ? Duration.ZERO : Duration.between(captured, now).isNegative()
                ? Duration.ZERO : Duration.between(captured, now);
    }

    private static <T> List<T> immutable(List<T> value) {
        return CollUtil.isEmpty(value) ? List.of() : List.copyOf(value);
    }

    /**
     * 单条警报信息。
     *
     * @param id 警报标识
     * @param mission 任务名称
     * @param reward 奖励摘要
     * @param expiry 结束时间
     * @param activation 开始时间
     * @param node 节点名称
     * @param faction 敌对势力
     * @param minEnemyLevel 最低敌人等级
     * @param maxEnemyLevel 最高敌人等级
     * @param rewardItems 奖励物品列表
     */
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

    /**
     * 单条虚空裂缝信息。
     *
     * @param id 裂缝标识
     * @param node 节点名称
     * @param tier 裂缝等级
     * @param missionType 任务类型
     * @param expiry 结束时间
     * @param activation 开始时间
     * @param hard 是否为钢铁之路裂缝
     * @param voidStorm 是否为虚空风暴
     */
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

    /**
     * 单条入侵信息。
     *
     * @param id 入侵标识
     * @param node 节点名称
     * @param attackerReward 进攻方奖励摘要
     * @param defenderReward 防守方奖励摘要
     * @param completion 完成百分比
     * @param activation 开始时间
     * @param completed 是否已完成
     * @param attackerRewards 进攻方奖励列表
     * @param defenderRewards 防守方奖励列表
     */
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

    /**
     * 每日突击信息。
     *
     * @param id 突击标识
     * @param boss 首领名称
     * @param variants 突击任务列表
     * @param expiry 结束时间
     */
    public record Sortie(String id, String boss, List<SortieVariant> variants, Instant expiry) {
        public Sortie {
            variants = immutable(variants);
        }
    }

    /**
     * 突击中的单个任务节点。
     *
     * @param missionType 任务类型
     * @param modifier 任务限制或变体
     * @param node 节点名称
     */
    public record SortieVariant(String missionType, String modifier, String node) {
    }

    /**
     * 夜灵平原昼夜周期。
     *
     * @param isDay 当前是否为白天
     * @param expiry 当前周期结束时间
     */
    public record CetusCycle(boolean isDay, Instant expiry) {
    }

    /**
     * 奥布山谷冷热周期。
     *
     * @param isWarm 当前是否为暖和
     * @param expiry 当前周期结束时间
     */
    public record VallisCycle(boolean isWarm, Instant expiry) {
    }

    /**
     * 魔胎之境当前周期。
     *
     * @param active 当前周期名称
     * @param expiry 当前周期结束时间
     */
    public record CambionCycle(String active, Instant expiry) {
    }

    /**
     * 地球昼夜周期。
     *
     * @param isDay 当前是否为白天
     * @param expiry 当前周期结束时间
     */
    public record EarthCycle(boolean isDay, Instant expiry) {
    }

    /**
     * 双衍王境当前状态。
     *
     * @param state 当前状态
     * @param expiry 当前状态结束时间
     */
    public record DuviriCycle(String state, Instant expiry) {
    }

    /**
     * 扎里曼当前状态。
     *
     * @param state 当前状态
     * @param expiry 当前状态结束时间
     */
    public record ZarimanCycle(String state, Instant expiry) {
    }

    /**
     * 虚空商人状态及库存。
     *
     * @param location 当前所在节点
     * @param activation 出现时间
     * @param expiry 离开时间
     * @param active 当前是否在场
     * @param inventory 出售库存
     */
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

    /**
     * 虚空商人出售的一件物品。
     *
     * @param item 物品名称
     * @param ducats 杜卡特数量
     * @param credits 现金价格
     */
    public record VoidTraderItem(String item, int ducats, int credits) {
    }

    /**
     * 当前仲裁任务。
     *
     * @param node 节点名称
     * @param type 任务类型
     * @param expiry 结束时间
     */
    public record Arbitration(String node, String type, Instant expiry) {
    }

    /**
     * 夜波单项任务。
     *
     * @param id 任务标识
     * @param title 任务标题
     * @param desc 任务说明
     * @param reputation 声望奖励
     * @param isDaily 是否为每日任务
     */
    public record NightwaveMission(
            String id,
            String title,
            String desc,
            int reputation,
            boolean isDaily
    ) {
    }

    /**
     * 每日交易项目。
     *
     * @param id 交易标识
     * @param item 物品名称
     * @param stock 总库存
     * @param sold 已售数量
     * @param credits 现金价格
     * @param platinum 白金价格
     * @param activation 开始时间
     * @param expiry 结束时间
     */
    public record DailyDeal(String id, String item, int stock, int sold, int credits, int platinum,
                            Instant activation, Instant expiry) {
    }

    /**
     * 世界事件或限时活动。
     *
     * @param id 事件标识
     * @param title 事件标题
     * @param description 事件说明
     * @param activation 开始时间
     * @param expiry 结束时间
     * @param category 事件分类
     * @param node 关联节点
     * @param progress 当前进度
     * @param goal 目标进度
     * @param rewards 奖励列表
     */
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

    /**
     * 新闻与活动目标分开建模，避免混淆官方事件列表与目标列表的不同语义。
     *
     * @param id 新闻标识
     * @param title 新闻标题
     * @param link 新闻链接
     * @param imageUrl 新闻图片地址
     * @param publishedAt 发布时间
     * @param expiry 失效时间
     * @param priority 是否为重点新闻
     */
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

    /**
     * 集团任务。
     *
     * @param id 任务标识
     * @param syndicate 集团名称
     * @param node 节点名称
     * @param activation 开始时间
     * @param expiry 结束时间
     * @param jobs 任务阶段列表
     */
    public record SyndicateMission(String id, String syndicate, String node, Instant activation, Instant expiry,
                                   List<String> jobs) {
        public SyndicateMission {
            jobs = immutable(jobs);
        }
    }

    /**
     * 钢铁之路奖励项目。
     *
     * @param id 奖励标识
     * @param node 节点名称
     * @param reward 奖励内容
     * @param activation 开始时间
     * @param expiry 结束时间
     */
    public record SteelPathOffering(String id, String node, String reward, Instant activation, Instant expiry) {
    }

    /**
     * 可执行的活动任务投影。
     *
     * @param id 任务标识
     * @param node 节点名称
     * @param missionType 任务类型
     * @param tier 裂缝等级
     * @param activation 开始时间
     * @param expiry 结束时间
     * @param hard 是否为钢铁之路任务
     * @param voidStorm 是否为虚空风暴
     */
    public record ActiveMission(String id, String node, String missionType, String tier, Instant activation,
                                Instant expiry, boolean hard, boolean voidStorm) {
        static ActiveMission fromFissure(Fissure fissure) {
            return new ActiveMission(fissure.id(), fissure.node(), fissure.missionType(), fissure.tier(),
                    fissure.activation(), fissure.expiry(), fissure.hard(), fissure.voidStorm());
        }
    }

    /**
     * 采集与质量元数据，避免把陈旧数据伪装成实时数据。
     *
     * @param source 数据来源编码
     * @param fetchedAt 本地抓取时间
     * @param providerTimestamp 上游数据时间戳
     * @param stale 是否已经过期
     * @param schemaVersion 内部模型版本
     * @param contentDigest 原始内容摘要
     */
    public record WorldStateMetadata(
            String source,
            Instant fetchedAt,
            Instant providerTimestamp,
            boolean stale,
            String schemaVersion,
            String contentDigest
    ) {
        public WorldStateMetadata {
            source = StrUtil.isBlank(source) ? "unknown" : source;
            schemaVersion = StrUtil.isBlank(schemaVersion) ? "nnm-world-state-v1" : schemaVersion;
        }

        public static WorldStateMetadata unknown(Instant providerTimestamp) {
            return new WorldStateMetadata("unknown", Instant.now(), providerTimestamp, false,
                    "nnm-world-state-v1", null);
        }
    }
}
