package com.nnm.bot.warframe.runtime.pojo.dto.worldstate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * NnmBot 自己维护的 Warframe 世界状态模型。
 *
 * <p>这个模型不暴露任何上游 SDK 类型。外部 API 的字段变化由类型化输入和映射器
 * 吸收，业务层依赖稳定的任务、奖励、周期与数据质量字段。集合在构造时复制，
 * 避免调用方修改共享快照；该模型不携带 JPA 受管状态。</p>
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
 * @param buildLabel 当前游戏版本标签
 * @param globalUpgrades 当前全局增益
 * @param flashSales 当前闪购
 * @param persistentEnemies 当前持续敌人
 * @param metadata 数据来源与新鲜度信息
 * @param archimedeas 当前科研任务
 * @param descents 当前深层下降周轮换
 * @param conclaveChallenges 当前武形秘仪挑战
 * @param constructionProgress 巨人战舰与豺狼舰队建造进度
 * @param simaris Simaris 公共研究状态
 * @param sentientOutpost Sentient 前哨状态
 * @param clanInitiatives 氏族每周行动
 * @param circuitChoices 当前回廊可选装备
 * @param calendarSeasons 1999 日历赛季
 * @param vaultTraders Prime 宝库商人
 * @param weeklyChallenges 每周挑战
 * @param darkSectors 黑暗地带控制状态
 */
public record WorldStateDTO(
        List<AlertDTO> alerts,
        List<FissureDTO> fissures,
        List<InvasionDTO> invasions,
        SortieDTO sortie,
        CetusCycleDTO cetusCycle,
        VallisCycleDTO vallisCycle,
        CambionCycleDTO cambionCycle,
        VoidTraderDTO voidTrader,
        ArbitrationDTO arbitration,
        List<NightwaveMissionDTO> nightwave,
        Instant timestamp,
        List<SortieDTO> sorties,
        List<VoidTraderDTO> voidTraders,
        EarthCycleDTO earthCycle,
        DuviriCycleDTO duviriCycle,
        ZarimanCycleDTO zarimanCycle,
        List<DailyDealDTO> dailyDeals,
        List<WorldEventDTO> events,
        List<NewsItemDTO> news,
        List<SyndicateMissionDTO> syndicateMissions,
        List<SteelPathOfferingDTO> steelPathOfferings,
        List<ActiveMissionDTO> activeMissions,
        List<SortieDTO> archonHunts,
        String buildLabel,
        List<GlobalUpgradeDTO> globalUpgrades,
        List<FlashSaleDTO> flashSales,
        List<PersistentEnemyDTO> persistentEnemies,
        WorldStateMetadataDTO metadata,
        List<ArchimedeaDTO> archimedeas,
        List<ConclaveChallengeDTO> conclaveChallenges,
        ConstructionProgressDTO constructionProgress,
        SimarisDTO simaris,
        SentientOutpostDTO sentientOutpost,
        List<ClanInitiativeDTO> clanInitiatives,
        List<CircuitChoiceDTO> circuitChoices,
        List<CalendarSeasonDTO> calendarSeasons,
        List<VaultTraderDTO> vaultTraders,
        List<WeeklyChallengeDTO> weeklyChallenges,
        List<DarkSectorDTO> darkSectors,
        List<DescentDTO> descents
) {

    /**
     * 使用本机周期推算结果构建新快照，保留其他分区。
     *
     * @param cetus 希图斯。
     * @param vallis 奥布山谷。
     * @param cambion 魔胎之境。
     * @param earth 地球。
     * @param duviri 双衍王境。
     * @param zariman 扎里曼。
     * @return 已经解析并校验的世界状态快照。
     */
    public WorldStateDTO withLocalCycles(CetusCycleDTO cetus, VallisCycleDTO vallis,
                                     CambionCycleDTO cambion, EarthCycleDTO earth,
                                     DuviriCycleDTO duviri, ZarimanCycleDTO zariman) {
        return new WorldStateDTO(alerts, fissures, invasions, sortie, cetus, vallis, cambion,
                voidTrader, arbitration, nightwave, timestamp, sorties, voidTraders,
                earth, duviri, zariman, dailyDeals, events, news, syndicateMissions,
                steelPathOfferings, activeMissions, archonHunts, buildLabel, globalUpgrades,
                flashSales, persistentEnemies, metadata, archimedeas, conclaveChallenges,
                constructionProgress, simaris, sentientOutpost, clanInitiatives, circuitChoices,
                calendarSeasons, vaultTraders, weeklyChallenges, darkSectors, descents);
    }

    /**
     * 替换已补全的辛迪加任务，保留其他分区。
     *
     * @param missions 任务集合。
     * @return 已经解析并校验的世界状态快照。
     */
    public WorldStateDTO withSyndicateMissions(List<SyndicateMissionDTO> missions) {
        return new WorldStateDTO(alerts, fissures, invasions, sortie, cetusCycle, vallisCycle, cambionCycle,
                voidTrader, arbitration, nightwave, timestamp, sorties, voidTraders,
                earthCycle, duviriCycle, zarimanCycle, dailyDeals, events, news, missions,
                steelPathOfferings, activeMissions, archonHunts, buildLabel, globalUpgrades,
                flashSales, persistentEnemies, metadata, archimedeas, conclaveChallenges,
                constructionProgress, simaris, sentientOutpost, clanInitiatives, circuitChoices,
                calendarSeasons, vaultTraders, weeklyChallenges, darkSectors, descents);
    }

    /**
     * 兼容增加官方顶层字段前的完整构造器。
     *
     * @param alerts 警报集合。
     * @param fissures 裂隙集合。
     * @param invasions 入侵集合。
     * @param sortie 突击。
     * @param cetusCycle 希图斯周期。
     * @param vallisCycle 奥布山谷周期。
     * @param cambionCycle 魔胎之境周期。
     * @param voidTrader 虚空商人。
     * @param arbitration 仲裁。
     * @param nightwave 夜波集合。
     * @param timestamp 时间戳。
     * @param sorties 突击集合。
     * @param voidTraders 虚空商人集合。
     * @param earthCycle 地球周期。
     * @param duviriCycle 双衍王境周期。
     * @param zarimanCycle 扎里曼周期。
     * @param dailyDeals 每日特惠集合。
     * @param events 事件集合。
     * @param news 新闻集合。
     * @param syndicateMissions 集团任务集合。
     * @param steelPathOfferings 钢铁路径供品集合。
     * @param activeMissions 活动任务集合。
     * @param archonHunts 执刑官狩猎集合。
     * @param metadata 元数据。
     */
    public WorldStateDTO(
            List<AlertDTO> alerts,
            List<FissureDTO> fissures,
            List<InvasionDTO> invasions,
            SortieDTO sortie,
            CetusCycleDTO cetusCycle,
            VallisCycleDTO vallisCycle,
            CambionCycleDTO cambionCycle,
            VoidTraderDTO voidTrader,
            ArbitrationDTO arbitration,
            List<NightwaveMissionDTO> nightwave,
            Instant timestamp,
            List<SortieDTO> sorties,
            List<VoidTraderDTO> voidTraders,
            EarthCycleDTO earthCycle,
            DuviriCycleDTO duviriCycle,
            ZarimanCycleDTO zarimanCycle,
            List<DailyDealDTO> dailyDeals,
            List<WorldEventDTO> events,
            List<NewsItemDTO> news,
            List<SyndicateMissionDTO> syndicateMissions,
            List<SteelPathOfferingDTO> steelPathOfferings,
            List<ActiveMissionDTO> activeMissions,
            List<SortieDTO> archonHunts,
            WorldStateMetadataDTO metadata
    ) {
        this(alerts, fissures, invasions, sortie, cetusCycle, vallisCycle, cambionCycle,
                voidTrader, arbitration, nightwave, timestamp, sorties, voidTraders,
                earthCycle, duviriCycle, zarimanCycle, dailyDeals, events, news,
                syndicateMissions, steelPathOfferings, activeMissions, archonHunts,
                null, List.of(), List.of(), List.of(), metadata, List.of(), List.of(),
                null, null, null, List.of(), List.of(), List.of(), List.of(),
                List.of(), List.of(), List.of());
    }

    /**
     * 兼容原始 NnmBot 的精简构造器。
     *
     * @param alerts 警报集合。
     * @param fissures 裂隙集合。
     * @param invasions 入侵集合。
     * @param sortie 突击。
     * @param cetusCycle 希图斯周期。
     * @param vallisCycle 奥布山谷周期。
     * @param cambionCycle 魔胎之境周期。
     * @param voidTrader 虚空商人。
     * @param arbitration 仲裁。
     * @param nightwave 夜波集合。
     * @param timestamp 时间戳。
     */
    public WorldStateDTO(
            List<AlertDTO> alerts,
            List<FissureDTO> fissures,
            List<InvasionDTO> invasions,
            SortieDTO sortie,
            CetusCycleDTO cetusCycle,
            VallisCycleDTO vallisCycle,
            CambionCycleDTO cambionCycle,
            VoidTraderDTO voidTrader,
            ArbitrationDTO arbitration,
            List<NightwaveMissionDTO> nightwave,
            Instant timestamp
    ) {
        this(alerts, fissures, invasions, sortie, cetusCycle, vallisCycle, cambionCycle,
                voidTrader, arbitration, nightwave, timestamp,
                ObjectUtil.isNull(sortie) ? List.of() : List.of(sortie),
                ObjectUtil.isNull(voidTrader) ? List.of() : List.of(voidTrader),
                null, null, null, List.of(), List.of(), List.of(), List.of(), List.of(),
                CollUtil.isEmpty(fissures) ? List.of() : fissures.stream().filter(Objects::nonNull)
                        .map(ActiveMissionDTO::fromFissure).toList(),
                List.of(),
                null, List.of(), List.of(), List.of(),
                WorldStateMetadataDTO.unknown(timestamp), List.of(), List.of(),
                null, null, null, List.of(), List.of(), List.of(), List.of(),
                List.of(), List.of(), List.of());
    }

    /**
     * 统一缺省值并复制集合，保证运行时快照可安全共享。
     *
     * @param alerts 警报集合。
     * @param fissures 裂隙集合。
     * @param invasions 入侵集合。
     * @param sortie 突击。
     * @param cetusCycle 希图斯周期。
     * @param vallisCycle 奥布山谷周期。
     * @param cambionCycle 魔胎之境周期。
     * @param voidTrader 虚空商人。
     * @param arbitration 仲裁。
     * @param nightwave 夜波集合。
     * @param timestamp 时间戳。
     * @param sorties 突击集合。
     * @param voidTraders 虚空商人集合。
     * @param earthCycle 地球周期。
     * @param duviriCycle 双衍王境周期。
     * @param zarimanCycle 扎里曼周期。
     * @param dailyDeals 每日特惠集合。
     * @param events 事件集合。
     * @param news 新闻集合。
     * @param syndicateMissions 集团任务集合。
     * @param steelPathOfferings 钢铁路径供品集合。
     * @param activeMissions 活动任务集合。
     * @param archonHunts 执刑官狩猎集合。
     * @param buildLabel 构造标签标题。
     * @param globalUpgrades 全局升级集合。
     * @param flashSales 限时促销集合。
     * @param persistentEnemies 常驻敌人集合。
     * @param metadata 元数据。
     * @param archimedeas 科研集合。
     * @param conclaveChallenges 武形秘仪挑战集合。
     * @param constructionProgress 建设进度。
     * @param simaris Simaris。
     * @param sentientOutpost Sentient前哨站。
     * @param clanInitiatives 氏族倡议集合。
     * @param circuitChoices 回环选项集合。
     * @param calendarSeasons 日历赛季集合。
     * @param vaultTraders 宝库商人集合。
     * @param weeklyChallenges 每周挑战集合。
     * @param darkSectors 黑暗黑暗地带集合。
     * @param descents 恶堕集合。
     */
    public WorldStateDTO {
        // 统一缺省值，保证 JSON 往返转换后记录相等性与访问器结果一致。
        buildLabel = StrUtil.emptyIfNull(buildLabel);
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
        globalUpgrades = immutable(globalUpgrades);
        flashSales = immutable(flashSales);
        persistentEnemies = immutable(persistentEnemies);
        archimedeas = immutable(archimedeas);
        conclaveChallenges = immutable(conclaveChallenges);
        clanInitiatives = immutable(clanInitiatives);
        circuitChoices = immutable(circuitChoices);
        calendarSeasons = immutable(calendarSeasons);
        vaultTraders = immutable(vaultTraders);
        weeklyChallenges = immutable(weeklyChallenges);
        darkSectors = immutable(darkSectors);
        descents = immutable(descents);
        metadata = ObjectUtil.isNull(metadata) ? WorldStateMetadataDTO.unknown(timestamp) : metadata;
    }

    /**
     * 读取当前突击列表，单条输入按列表返回。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public List<SortieDTO> allSorties() {
        return CollUtil.isEmpty(sorties) && ObjectUtil.isNotNull(sortie) ? List.of(sortie) : sorties;
    }

    /**
     * 读取当前奸商列表，单条输入按列表返回。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public List<VoidTraderDTO> allVoidTraders() {
        return CollUtil.isEmpty(voidTraders) && ObjectUtil.isNotNull(voidTrader) ? List.of(voidTrader) : voidTraders;
    }

    /**
     * 读取活动任务；缺少活动任务时由裂缝构造任务投影。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public List<ActiveMissionDTO> allActiveMissions() {
        return CollUtil.isEmpty(activeMissions)
                ? fissures.stream().map(ActiveMissionDTO::fromFissure).toList()
                : activeMissions;
    }

    /**
     * 读取采集元数据标记的过期状态。
     *
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    public boolean stale() {
        return metadata.stale();
    }

    /**
     * 当前版本标签，官方响应缺少该字段时返回空字符串。
     *
     * @return 构造标签标题对应的文本。
     */
    public String buildLabel() {
        return StrUtil.emptyIfNull(buildLabel);
    }

    /**
     * 计算快照在指定时刻的年龄，时钟回退时按零处理。
     *
     * @param now 本次操作使用的当前时间。
     * @return 时长对象。
     */
    public Duration ageAt(Instant now) {
        Objects.requireNonNull(now, "now must not be null");
        Instant captured = ObjectUtil.isNull(metadata.fetchedAt()) ? timestamp : metadata.fetchedAt();
        return ObjectUtil.isNull(captured) ? Duration.ZERO : Duration.between(captured, now).isNegative()
                ? Duration.ZERO : Duration.between(captured, now);
    }

    /**
     * 将可空列表复制为不可修改列表，防止共享快照被调用方改写。
     *
     * @param value 待校验或转换的成员集合。
     * @return 按当前条件筛选或转换后的数据集合。
     */
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
     * @param rewardItems 奖励物品及数量
     */
    public record AlertDTO(
            String id,
            String mission,
            String reward,
            Instant expiry,
            Instant activation,
            String node,
            String faction,
            Integer minEnemyLevel,
            Integer maxEnemyLevel,
            List<AlertRewardDTO> rewardItems
    ) {
        /**
         * 按已提供的任务字段构造对象，其余字段使用明确缺省值。
         *
         * @param id 目标记录的本地主键或协议标识。
         * @param mission 任务。
         * @param reward 奖励。
         * @param expiry 到期。
         */
        public AlertDTO(String id, String mission, String reward, Instant expiry) {
            this(id, mission, reward, expiry, null, null, null, null, null, List.of());
        }

        /**
         * 统一缺省值并复制集合，保证运行时快照可安全共享。
         *
         * @param id 目标记录的本地主键或协议标识。
         * @param mission 任务。
         * @param reward 奖励。
         * @param expiry 到期。
         * @param activation 开始时间。
         * @param node 节点。
         * @param faction 阵营。
         * @param minEnemyLevel 最小敌人等级。
         * @param maxEnemyLevel 最大敌人等级。
         * @param rewardItems 奖励物品集合。
         */
        public AlertDTO {
            rewardItems = immutable(rewardItems);
        }
    }

    /**
     * 警报奖励物品。
     *
     * @param item 物品内部标识或已翻译名称
     * @param count 数量；未提供时为空
     */
    public record AlertRewardDTO(String item, Integer count) {
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
    public record FissureDTO(
            String id,
            String node,
            String tier,
            String missionType,
            Instant expiry,
            Instant activation,
            boolean hard,
            boolean voidStorm
    ) {
        /**
         * 按已提供的任务字段构造对象，其余字段使用明确缺省值。
         *
         * @param id 目标记录的本地主键或协议标识。
         * @param node 节点。
         * @param tier 层级。
         * @param missionType 任务类型。
         * @param expiry 到期。
         */
        public FissureDTO(String id, String node, String tier, String missionType, Instant expiry) {
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
    public record InvasionDTO(
            String id,
            String node,
            String attackerReward,
            String defenderReward,
            float completion,
            Instant activation,
            boolean completed,
            List<InvasionRewardDTO> attackerRewards,
            List<InvasionRewardDTO> defenderRewards
    ) {
        /**
         * 按已提供的任务字段构造对象，其余字段使用明确缺省值。
         *
         * @param id 目标记录的本地主键或协议标识。
         * @param node 节点。
         * @param attackerReward 进攻方奖励。
         * @param defenderReward 防守方奖励。
         * @param completion 完成。
         */
        public InvasionDTO(String id, String node, String attackerReward, String defenderReward, float completion) {
            this(id, node, attackerReward, defenderReward, completion, null, false, List.of(), List.of());
        }

        /**
         * 统一缺省值并复制集合，保证运行时快照可安全共享。
         *
         * @param id 目标记录的本地主键或协议标识。
         * @param node 节点。
         * @param attackerReward 进攻方奖励。
         * @param defenderReward 防守方奖励。
         * @param completion 完成。
         * @param activation 开始时间。
         * @param completed 已完成。
         * @param attackerRewards 进攻方奖励集合。
         * @param defenderRewards 防守方奖励集合。
         */
        public InvasionDTO {
            attackerRewards = immutable(attackerRewards);
            defenderRewards = immutable(defenderRewards);
        }
    }

    /**
     * 入侵任务的结构化奖励。
     *
     * @param item 物品内部标识或已解析名称
     * @param count 数量；上游未提供时为空
     */
    public record InvasionRewardDTO(String item, Integer count) {
    }

    /**
     * 每日突击信息。
     *
     * @param id 突击标识
     * @param boss 首领名称
     * @param variants 突击任务列表
     * @param expiry 结束时间
     */
    public record SortieDTO(String id, String boss, List<SortieVariantDTO> variants, Instant expiry) {
        /**
         * 统一缺省值并复制集合，保证运行时快照可安全共享。
         *
         * @param id 目标记录的本地主键或协议标识。
         * @param boss 首领。
         * @param variants 变体集合。
         * @param expiry 到期。
         */
        public SortieDTO {
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
    public record SortieVariantDTO(String missionType, String modifier, String node) {
    }

    /**
     * 夜灵平原昼夜周期。
     *
     * @param isDay 当前是否为白天
     * @param expiry 当前周期结束时间
     */
    public record CetusCycleDTO(boolean isDay, Instant expiry) {
    }

    /**
     * 奥布山谷冷热周期。
     *
     * @param isWarm 当前是否为暖和
     * @param expiry 当前周期结束时间
     */
    public record VallisCycleDTO(boolean isWarm, Instant expiry) {
    }

    /**
     * 魔胎之境当前周期。
     *
     * @param active 当前周期名称
     * @param expiry 当前周期结束时间
     */
    public record CambionCycleDTO(String active, Instant expiry) {
    }

    /**
     * 地球昼夜周期。
     *
     * @param isDay 当前是否为白天
     * @param expiry 当前周期结束时间
     */
    public record EarthCycleDTO(boolean isDay, Instant expiry) {
    }

    /**
     * 双衍王境当前状态。
     *
     * @param state 当前状态
     * @param expiry 当前状态结束时间
     */
    public record DuviriCycleDTO(String state, Instant expiry) {
    }

    /**
     * 扎里曼当前状态。
     *
     * @param state 当前状态
     * @param expiry 当前状态结束时间
     */
    public record ZarimanCycleDTO(String state, Instant expiry) {
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
    public record VoidTraderDTO(
            String location,
            Instant activation,
            Instant expiry,
            boolean active,
            List<VoidTraderItemDTO> inventory
    ) {
        /**
         * 统一缺省值并复制集合，保证运行时快照可安全共享。
         *
         * @param location 位置。
         * @param activation 开始时间。
         * @param expiry 到期。
         * @param active 活动。
         * @param inventory 库存集合。
         */
        public VoidTraderDTO {
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
    public record VoidTraderItemDTO(String item, int ducats, int credits) {
    }

    /**
     * 当前仲裁任务。
     *
     * @param node 节点名称
     * @param type 任务类型
     * @param expiry 结束时间
     */
    public record ArbitrationDTO(String node, String type, Instant expiry) {
    }

    /**
     * 夜波单项任务。
     *
     * @param id 任务标识
     * @param challenge 上游 Challenge 引用
     * @param title 任务标题
     * @param desc 任务说明
     * @param reputation 声望奖励
     * @param isDaily 是否为每日任务
     */
    public record NightwaveMissionDTO(
            String id,
            String challenge,
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
    public record DailyDealDTO(String id, String item, int stock, int sold, int credits, int platinum,
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
     * @param rewards 结构化物品奖励列表
     * @param rewardCredits 现金奖励
     */
    public record WorldEventDTO(
            String id,
            String title,
            String description,
            Instant activation,
            Instant expiry,
            String category,
            String node,
            Double progress,
            Double goal,
            List<WorldEventRewardDTO> rewards,
            Integer rewardCredits
    ) {
        /**
         * 统一缺省值并复制集合，保证运行时快照可安全共享。
         *
         * @param id 目标记录的本地主键或协议标识。
         * @param title 标题。
         * @param description 说明。
         * @param activation 开始时间。
         * @param expiry 到期。
         * @param category 分类。
         * @param node 节点。
         * @param progress 进度。
         * @param goal 目标。
         * @param rewards 奖励集合。
         * @param rewardCredits 奖励现金。
         */
        public WorldEventDTO {
            rewards = immutable(rewards);
        }
    }

    /**
     * 世界活动的单项奖励。
     *
     * @param item 物品标识
     * @param count 数量；未指定时为 {@code null}
     */
    public record WorldEventRewardDTO(String item, Integer count) {
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
    public record NewsItemDTO(
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
     * @param jobs 赏金列表，每份赏金包含自己的声望阶段
     * @param nodes 官方任务节点列表
     * @param supplementalJobs 补充数据源给出的当前赏金任务
     * @param staticRewardPools 官方静态候选奖励池
     * @param supplementStatus 赏金补充数据的解析状态
     */
    public record SyndicateMissionDTO(String id, String syndicate, String node, Instant activation, Instant expiry,
                                   List<SyndicateJobDTO> jobs, List<String> nodes,
                                   List<BountyCycleJobDTO> supplementalJobs,
                                   List<BountyStaticPoolDTO> staticRewardPools, String supplementStatus) {
        /**
         * 统一缺省值并复制集合，保证运行时快照可安全共享。
         *
         * @param id 目标记录的本地主键或协议标识。
         * @param syndicate 集团。
         * @param node 节点。
         * @param activation 开始时间。
         * @param expiry 到期。
         * @param jobs 任务集合。
         * @param nodes 节点集合。
         * @param supplementalJobs 补充任务集合。
         * @param staticRewardPools 静态奖励奖池集合。
         * @param supplementStatus 补充状态。
         */
        public SyndicateMissionDTO {
            jobs = immutable(jobs);
            nodes = immutable(nodes);
            supplementalJobs = immutable(supplementalJobs);
            staticRewardPools = immutable(staticRewardPools);
        }
    }

    /**
     * 社区当前赏金任务与官方原始 Jobs、Nodes 分开保存，不能冒充官方字段。
     *
     * @param node 任务节点标识
     * @param nodeName 任务节点展示名称
     * @param systemName 所在星球或星系名称
     * @param missionType 任务类型名称
     * @param challenge 挑战内部标识
     * @param challengeName 挑战名称
     * @param challengeDescription 填充动态参数后的挑战说明
     * @param ally 支援角色内部标识
     * @param allyName 支援角色名称
     * @param resolutionStatus 引用与静态资料的匹配状态
     * @param missionTypeKey 任务类型原始键
     * @param tier 当前任务等级
     * @param totalTiers 任务等级总数
     */
    public record BountyCycleJobDTO(String node, String nodeName, String systemName, String missionType,
                                String challenge, String challengeName, String challengeDescription,
                                String ally, String allyName, String resolutionStatus,
                                String missionTypeKey, int tier, int totalTiers) {
    }

    /**
     * 成品的静态候选池，不表示当前任务已确定会抽中其中某项奖励。
     *
     * @param id 记录标识
     * @param name 展示名称
     * @param rewards 候选奖励列表
     */
    public record BountyStaticPoolDTO(String id, String name, List<String> rewards) {
        /**
         * 统一缺省值并复制集合，保证运行时快照可安全共享。
         *
         * @param id 目标记录的本地主键或协议标识。
         * @param name 当前业务对象的名称。
         * @param rewards 奖励集合。
         */
        public BountyStaticPoolDTO {
            rewards = immutable(rewards);
        }
    }

    /**
     * 官方赏金的完整动态参数；名称、说明及奖励详情通过官方静态数据补全。
     *
     * @param jobType 赏金任务类型内部路径
     * @param type 类型代码或已解析名称
     * @param rewardPoolReference 官方奖励池引用路径
     * @param rewardPool 已解析的奖励明细
     * @param minEnemyLevel 最低敌人等级
     * @param maxEnemyLevel 最高敌人等级
     * @param standingStages 各阶段声望数量
     * @param minMR 最低段位要求
     * @param isVault 是否为隔离库任务
     * @param locationTag 位置标记
     * @param expiry 结束时间；上游未提供时为空
     * @param timeBound 适用周期限制
     */
    public record SyndicateJobDTO(
            String jobType,
            String type,
            String rewardPoolReference,
            List<SyndicateRewardDTO> rewardPool,
            Integer minEnemyLevel,
            Integer maxEnemyLevel,
            List<Integer> standingStages,
            Integer minMR,
            boolean isVault,
            String locationTag,
            Instant expiry,
            String timeBound
    ) {
        /**
         * 统一缺省值并复制集合，保证运行时快照可安全共享。
         *
         * @param jobType 任务类型。
         * @param type 目标业务类型。
         * @param rewardPoolReference 奖励奖池引用。
         * @param rewardPool 奖励奖池集合。
         * @param minEnemyLevel 最小敌人等级。
         * @param maxEnemyLevel 最大敌人等级。
         * @param standingStages 声望阶段集合。
         * @param minMR 最小段位。
         * @param isVault 判断宝库。
         * @param locationTag 位置标签。
         * @param expiry 到期。
         * @param timeBound 时间边界。
         */
        public SyndicateJobDTO {
            rewardPool = immutable(rewardPool);
            standingStages = immutable(standingStages);
        }
    }

    /**
     * 已解析的上游赏金奖励，概率和数量不从名称猜测。
     *
     * @param item 物品内部标识或已解析名称
     * @param count 数量；上游未提供时为空
     * @param chance 奖励出现概率
     * @param rarity 稀有度
     */
    public record SyndicateRewardDTO(String item, Integer count, Double chance, String rarity) {
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
    public record SteelPathOfferingDTO(String id, String node, String reward, Instant activation, Instant expiry) {
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
    public record ActiveMissionDTO(String id, String node, String missionType, String tier, Instant activation,
                                Instant expiry, boolean hard, boolean voidStorm) {
        /**
         * 将裂缝转换为共用的活动任务投影。
         *
         * @param fissure 裂隙。
         * @return 活动任务对象。
         */
        static ActiveMissionDTO fromFissure(FissureDTO fissure) {
            return new ActiveMissionDTO(fissure.id(), fissure.node(), fissure.missionType(), fissure.tier(),
                    fissure.activation(), fissure.expiry(), fissure.hard(), fissure.voidStorm());
        }
    }

    /**
     * 官方 GlobalUpgrades 的稳定领域投影。
     *
     * @param id 记录标识
     * @param upgradeType 全局增益类型
     * @param operationType 增益数值的运算类型
     * @param value 增益数值
     * @param activation 开始时间；上游未提供时为空
     * @param expiry 结束时间；上游未提供时为空
     */
    public record GlobalUpgradeDTO(
            String id,
            String upgradeType,
            String operationType,
            Double value,
            Instant activation,
            Instant expiry
    ) {
    }

    /**
     * 官方 FlashSales 的稳定领域投影。
     *
     * @param id 记录标识
     * @param uniqueName 物品内部唯一路径
     * @param discount 折扣比例
     * @param regularOverride 覆盖后的普通货币价格
     * @param premiumOverride 覆盖后的白金价格
     * @param shownInMarket 是否在商城显示
     * @param featured 是否为精选项目
     * @param popular 是否为热门项目
     * @param activation 开始时间；上游未提供时为空
     * @param expiry 结束时间；上游未提供时为空
     */
    public record FlashSaleDTO(
            String id,
            String uniqueName,
            Double discount,
            Integer regularOverride,
            Integer premiumOverride,
            boolean shownInMarket,
            boolean featured,
            boolean popular,
            Instant activation,
            Instant expiry
    ) {
    }

    /**
     * 官方 PersistentEnemies 的稳定领域投影。
     *
     * @param id 记录标识
     * @param agentType 追猎敌人类型
     * @param locationTag 位置标记
     * @param rank 敌人等级
     * @param healthPercent 剩余生命比例
     * @param fleeDamage 触发撤退的伤害阈值
     * @param region 所在区域
     * @param lastDiscoveredTime 最近发现时间
     * @param lastDiscoveredLocation 最近发现位置
     * @param discovered 是否已被发现
     * @param usingTicketing 是否使用票券机制
     */
    public record PersistentEnemyDTO(
            String id,
            String agentType,
            String locationTag,
            Integer rank,
            Double healthPercent,
            Double fleeDamage,
            String region,
            Instant lastDiscoveredTime,
            String lastDiscoveredLocation,
            boolean discovered,
            boolean usingTicketing
    ) {
    }

    /**
     * 官方 Conquests 的科研周期及其任务条件。
     *
     * @param id 记录标识
     * @param type 类型代码或已解析名称
     * @param typeKey 类型原始键
     * @param missions 任务列表
     * @param personalModifiers 个人限制条件
     * @param activation 开始时间；上游未提供时为空
     * @param expiry 结束时间；上游未提供时为空
     */
    public record ArchimedeaDTO(
            String id,
            String type,
            String typeKey,
            List<ArchimedeaMissionDTO> missions,
            List<String> personalModifiers,
            Instant activation,
            Instant expiry
    ) {
        /**
         * 统一缺省值并复制集合，保证运行时快照可安全共享。
         *
         * @param id 目标记录的本地主键或协议标识。
         * @param type 目标业务类型。
         * @param typeKey 类型键。
         * @param missions 任务集合。
         * @param personalModifiers 个人词缀集合。
         * @param activation 开始时间。
         * @param expiry 到期。
         */
        public ArchimedeaDTO {
            missions = immutable(missions);
            personalModifiers = immutable(personalModifiers);
        }
    }

    /**
     * 科研中的任务、偏差和风险，保留上游 key 供后续词典维护。
     *
     * @param faction 敌对阵营名称
     * @param factionKey 敌对阵营原始键
     * @param missionType 任务类型名称
     * @param missionTypeKey 任务类型原始键
     * @param deviation 任务偏差条件
     * @param risks 风险条件列表
     */
    public record ArchimedeaMissionDTO(
            String faction,
            String factionKey,
            String missionType,
            String missionTypeKey,
            String deviation,
            List<String> risks
    ) {
        /**
         * 统一缺省值并复制集合，保证运行时快照可安全共享。
         *
         * @param faction 阵营。
         * @param factionKey 阵营键。
         * @param missionType 任务类型。
         * @param missionTypeKey 任务类型键。
         * @param deviation 偏差。
         * @param risks 风险集合。
         */
        public ArchimedeaMissionDTO {
            risks = immutable(risks);
        }
    }

    /**
     * 官方 PVPChallengeInstances 的当前 Conclave 挑战。
     *
     * @param id 记录标识
     * @param mode 挑战所属模式
     * @param category 所属分类
     * @param categoryKey 挑战分类原始键
     * @param title 展示标题
     * @param description 展示说明
     * @param amount 目标数量
     * @param standing 声望奖励
     * @param daily 是否为每日挑战
     * @param rootChallenge 是否为根分类挑战
     * @param activation 开始时间；上游未提供时为空
     * @param expiry 结束时间；上游未提供时为空
     */
    public record ConclaveChallengeDTO(
            String id,
            String mode,
            String category,
            String categoryKey,
            String title,
            String description,
            Integer amount,
            Integer standing,
            boolean daily,
            boolean rootChallenge,
            Instant activation,
            Instant expiry
    ) {
    }

    /**
     * 官方各建造项目的当前进度。
     *
     * @param fomorian 巨人战舰建造进度
     * @param razorback 豺狼舰队建造进度
     * @param unknown 官方第三项建造进度，保留原始含义
     */
    public record ConstructionProgressDTO(Double fomorian, Double razorback, Double unknown) {}

    /**
     * 公共 Sanctuary 研究目标，不是玩家个人每日扫描任务。
     *
     * @param target 目标内部标识或名称
     * @param active 当前是否激活
     */
    public record SimarisDTO(String target, boolean active) {}

    /**
     * Tmp.sfn 只给节点；没有明确时间源时不补造起止时间。
     *
     * @param node 任务节点标识
     * @param activation 开始时间；上游未提供时为空
     * @param expiry 结束时间；上游未提供时为空
     */
    public record SentientOutpostDTO(String node, Instant activation, Instant expiry) {}

    /**
     * 氏族每周行动及其奖励。
     *
     * @param week 周轮次
     * @param region 所在区域
     * @param rewards 候选奖励列表
     */
    public record ClanInitiativeDTO(Integer week, String region, List<ClanRewardDTO> rewards) {
        /**
         * 统一缺省值并复制集合，保证运行时快照可安全共享。
         *
         * @param week 周。
         * @param region 星区。
         * @param rewards 奖励集合。
         */
        public ClanInitiativeDTO { rewards = immutable(rewards); }
    }

    /**
     * 氏族行动的单项奖励。
     *
     * @param item 物品内部标识或已解析名称
     * @param count 数量；上游未提供时为空
     * @param points 氏族积分
     * @param claimed 是否已领取
     */
    public record ClanRewardDTO(String item, Integer count, Integer points, Boolean claimed) {}

    /**
     * 当前回廊分类及装备选项。
     *
     * @param category 所属分类
     * @param choices 可选装备列表
     */
    public record CircuitChoiceDTO(String category, List<String> choices) {
        /**
         * 统一缺省值并复制集合，保证运行时快照可安全共享。
         *
         * @param category 分类。
         * @param choices 选项集合。
         */
        public CircuitChoiceDTO { choices = immutable(choices); }
    }

    /**
     * 1999 日历赛季及日期事件。
     *
     * @param season 赛季标识
     * @param iteration 赛季迭代号
     * @param days 日历日期列表
     */
    public record CalendarSeasonDTO(String season, Integer iteration, List<CalendarDayDTO> days) {
        /**
         * 统一缺省值并复制集合，保证运行时快照可安全共享。
         *
         * @param season 赛季。
         * @param iteration 迭代次数。
         * @param days 日集合。
         */
        public CalendarSeasonDTO { days = immutable(days); }
    }

    /**
     * 游戏内单日事件。
     *
     * @param date 游戏内日期，不推断为现实日期
     * @param events 当日事件列表
     */
    public record CalendarDayDTO(String date, List<CalendarEventDTO> events) {
        /**
         * 统一缺省值并复制集合，保证运行时快照可安全共享。
         *
         * @param date 日期。
         * @param events 事件集合。
         */
        public CalendarDayDTO { events = immutable(events); }
    }

    /**
     * 日历中的任务、奖励或对话事件。
     *
     * @param type 类型代码或已解析名称
     * @param title 展示标题
     * @param description 展示说明
     * @param reward 奖励内部标识或摘要
     * @param dialogueName 对话角色名称
     * @param dialogueConvo 对话内容引用
     */
    public record CalendarEventDTO(String type, String title, String description, String reward,
                                String dialogueName, String dialogueConvo) {}

    /**
     * 宝库商人价格字段未声明币种，不能复用奸商 DTO 标成杜卡德金币。
     *
     * @param location 商人所在节点
     * @param activation 开始时间；上游未提供时为空
     * @param expiry 结束时间；上游未提供时为空
     * @param inventory 当前库存
     */
    public record VaultTraderDTO(String location, Instant activation, Instant expiry,
                               List<VaultProductDTO> inventory) {
        /**
         * 统一缺省值并复制集合，保证运行时快照可安全共享。
         *
         * @param location 位置。
         * @param activation 开始时间。
         * @param expiry 到期。
         * @param inventory 库存集合。
         */
        public VaultTraderDTO { inventory = immutable(inventory); }
    }

    /**
     * Prime 宝库商人的单件商品。
     *
     * @param item 物品内部标识或已解析名称
     * @param primePrice 官方 Prime 价格；不推断币种
     * @param regularPrice 官方普通价格；不推断币种
     */
    public record VaultProductDTO(String item, Integer primePrice, Integer regularPrice) {}

    /**
     * 官方 WeeklyChallenges 的周期挑战；参考解析器仅做原始字段映射。
     *
     * @param id 记录标识
     * @param activation 开始时间；上游未提供时为空
     * @param expiry 结束时间；上游未提供时为空
     * @param challenges 挑战列表
     */
    public record WeeklyChallengeDTO(
            String id,
            Instant activation,
            Instant expiry,
            List<WeeklyChallengeItemDTO> challenges
    ) {
        /**
         * 统一缺省值并复制集合，保证运行时快照可安全共享。
         *
         * @param id 目标记录的本地主键或协议标识。
         * @param activation 开始时间。
         * @param expiry 到期。
         * @param challenges 挑战集合。
         */
        public WeeklyChallengeDTO {
            challenges = immutable(challenges);
        }
    }

    /**
     * 每周挑战的目标条件。
     *
     * @param type 类型代码或已解析名称
     * @param minimumEnemyLevel 最低敌人等级
     * @param requiredCount 完成所需数量
     * @param progressIndicatorFrequency 进度提示频率
     * @param damageType 伤害类型
     * @param target 目标内部标识或名称
     */
    public record WeeklyChallengeItemDTO(
            String type,
            Integer minimumEnemyLevel,
            Integer requiredCount,
            Integer progressIndicatorFrequency,
            String damageType,
            String target
    ) {}

    /**
     * 官方 BadlandNodes 的暗域状态摘要。
     *
     * @param id 记录标识
     * @param defender 防守方名称
     * @param attacker 进攻方名称
     * @param strengthRemaining 剩余强度
     * @param maxStrength 最大强度
     * @param battlePay 战斗报酬
     * @param missionNode 战斗任务节点
     * @param activation 开始时间；上游未提供时为空
     * @param expiry 结束时间；上游未提供时为空
     */
    public record DarkSectorDTO(
            String id,
            String defender,
            String attacker,
            Double strengthRemaining,
            Double maxStrength,
            Integer battlePay,
            String missionNode,
            Instant activation,
            Instant expiry
    ) {}

    /**
     * 官方 Descents 的每周深层下降轮换；挑战内保留原始键供后续代码簿解析。
     *
     * @param seed 官方随机种子，使用 Long 保留完整值
     * @param challenges 挑战列表
     */
    public record DescentDTO(Long seed, List<DescentChallengeDTO> challenges) {
        /**
         * 统一缺省值并复制集合，保证运行时快照可安全共享。
         *
         * @param seed 随机种子。
         * @param challenges 挑战集合。
         */
        public DescentDTO {
            challenges = immutable(challenges);
        }
    }

    /**
     * 深层下降的单项挑战与规则。
     *
     * @param index 挑战序号
     * @param type 类型代码或已解析名称
     * @param challenge 挑战内部标识
     * @param level 挑战等级原始键
     * @param specs 特殊规则原始键列表
     * @param auras 光环效果原始键列表
     */
    public record DescentChallengeDTO(
            Integer index,
            String type,
            String challenge,
            String level,
            List<String> specs,
            List<String> auras
    ) {
        /**
         * 统一缺省值并复制集合，保证运行时快照可安全共享。
         *
         * @param index 待读取的成员下标。
         * @param type 目标业务类型。
         * @param challenge 挑战。
         * @param level 等级。
         * @param specs 规格集合。
         * @param auras 光环集合。
         */
        public DescentChallengeDTO {
            specs = immutable(specs);
            auras = immutable(auras);
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
    public record WorldStateMetadataDTO(
            String source,
            Instant fetchedAt,
            Instant providerTimestamp,
            boolean stale,
            String schemaVersion,
            String contentDigest
    ) {
        /**
         * 统一缺省值并复制集合，保证运行时快照可安全共享。
         *
         * @param source 当前数据来源标识。
         * @param fetchedAt 拉取时间时刻。
         * @param providerTimestamp 上游提供方时间戳。
         * @param stale 过期。
         * @param schemaVersion 字段合同版本。
         * @param contentDigest 内容摘要。
         */
        public WorldStateMetadataDTO {
            source = StrUtil.isBlank(source) ? "unknown" : source;
            schemaVersion = StrUtil.isBlank(schemaVersion) ? "nnm-world-state-v1" : schemaVersion;
        }

        /**
         * 为缺少采集信息的状态创建明确的未知来源元数据。
         *
         * @param providerTimestamp 上游提供方时间戳。
         * @return 世界状态元数据对象。
         */
        public static WorldStateMetadataDTO unknown(Instant providerTimestamp) {
            return new WorldStateMetadataDTO("unknown", Instant.now(), providerTimestamp, false,
                    "nnm-world-state-v1", null);
        }
    }
}
