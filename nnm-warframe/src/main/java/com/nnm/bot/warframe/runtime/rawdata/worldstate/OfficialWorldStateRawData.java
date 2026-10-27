package com.nnm.bot.warframe.runtime.rawdata.worldstate;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.jackson.OfficialIdDeserializer;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.jackson.OfficialInstantDeserializer;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.jackson.OfficialLongDeserializer;
import com.nnm.bot.warframe.runtime.rawdata.worldstate.jackson.OfficialRewardDeserializer;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.List;

/**
 * Warframe 官方 WorldState 原始数据的类型化输入模型。
 * 未识别字段由 Jackson 忽略；排障原文和顶层字段名由运行时快照单独保留。
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfficialWorldStateRawData {

    /** 官方服务器生成时间，官方协议使用 Unix 秒。 */
    @JsonProperty("Time")
    @JsonAlias({"timestamp", "time", "serverTime"})
    @JsonDeserialize(using = OfficialLongDeserializer.class)
    private Long timestamp;

    /** 当前客户端构建标识。 */
    @JsonProperty("BuildLabel")
    @JsonAlias({"buildLabel"})
    private String buildLabel;

    /** 活动警报列表。 */
    @JsonProperty("Alerts")
    @JsonAlias({"alerts"})
    private List<AlertRawData> alerts;

    /** 普通活动任务列表。 */
    @JsonProperty("ActiveMissions")
    @JsonAlias({"activeMissions"})
    private List<ActiveMissionRawData> activeMissions;

    /** 普通虚空裂缝列表。 */
    @JsonProperty("fissures")
    @JsonAlias("Fissures")
    private List<ActiveMissionRawData> fissures;

    /** 虚空风暴列表。 */
    @JsonProperty("VoidStorms")
    @JsonAlias({"voidStorms"})
    private List<ActiveMissionRawData> voidStorms;

    /** 入侵列表。 */
    @JsonProperty("Invasions")
    @JsonAlias({"invasions"})
    private List<InvasionRawData> invasions;

    /** 突击列表。 */
    @JsonProperty("Sorties")
    @JsonAlias({"sorties", "sortie", "Sortie"})
    private List<SortieRawData> sorties;

    /** 执政官狩猎列表。 */
    @JsonProperty("LiteSorties")
    @JsonAlias({"archonHunts", "ArchonHunts", "archonHunt", "liteSorties"})
    private List<SortieRawData> archonHunts;

    /** 夜灵平原周期。 */
    @JsonProperty("cetusCycle")
    @JsonAlias("CetusCycle")
    private CycleRawData cetusCycle;

    /** 奥布山谷周期。 */
    @JsonProperty("vallisCycle")
    @JsonAlias("VallisCycle")
    private CycleRawData vallisCycle;

    /** 魔胎之境周期。 */
    @JsonProperty("cambionCycle")
    @JsonAlias("CambionCycle")
    private CycleRawData cambionCycle;

    /** 虚空奸商列表。 */
    @JsonProperty("VoidTraders")
    @JsonAlias({"voidTraders", "voidTrader", "VoidTrader"})
    private List<VoidTraderRawData> voidTraders;

    /** 当前仲裁任务。 */
    @JsonProperty("arbitration")
    @JsonAlias("Arbitration")
    private ArbitrationRawData arbitration;

    /** 夜波赛季及挑战。 */
    @JsonProperty("SeasonInfo")
    @JsonAlias({"nightwave", "Nightwave", "seasonInfo"})
    private SeasonRawData nightwave;

    /** 地球昼夜周期。 */
    @JsonProperty("earthCycle")
    @JsonAlias("EarthCycle")
    private CycleRawData earthCycle;

    /** 双衍王境周期。 */
    @JsonProperty("duviriCycle")
    @JsonAlias({"DuviriCycle", "duvalierCycle"})
    private CycleRawData duviriCycle;

    /** 扎里曼周期。 */
    @JsonProperty("zarimanCycle")
    @JsonAlias("ZarimanCycle")
    private CycleRawData zarimanCycle;

    /** 每日特惠。 */
    @JsonProperty("DailyDeals")
    @JsonAlias({"dailyDeals"})
    private List<DailyDealRawData> dailyDeals;

    /** 官方限时活动目标。 */
    @JsonProperty("Goals")
    @JsonAlias({"goals", "events"})
    private List<WorldEventRawData> goals;

    /** 官方新闻和公告。 */
    @JsonProperty("Events")
    @JsonAlias({"news", "News"})
    private List<NewsRawData> news;

    /** 辛迪加任务。 */
    @JsonProperty("SyndicateMissions")
    @JsonAlias({"syndicateMissions"})
    private List<SyndicateMissionRawData> syndicateMissions;

    /** 钢铁之路奖励。 */
    @JsonProperty("steelPathOfferings")
    @JsonAlias("SteelPathOfferings")
    private List<SteelPathOfferingRawData> steelPathOfferings;

    /** 全局增益。 */
    @JsonProperty("GlobalUpgrades")
    @JsonAlias({"globalUpgrades"})
    private List<GlobalUpgradeRawData> globalUpgrades;

    /** 商城闪购。 */
    @JsonProperty("FlashSales")
    @JsonAlias({"flashSales"})
    private List<FlashSaleRawData> flashSales;

    /** 持续追猎敌人。 */
    @JsonProperty("PersistentEnemies")
    @JsonAlias({"persistentEnemies"})
    private List<PersistentEnemyRawData> persistentEnemies;

    /** 深潜/执政官类周常征服任务。 */
    @JsonProperty("Conquests")
    @JsonAlias({"conquests", "archimedeas", "Archimedeas"})
    private List<ConquestRawData> conquests;

    /** PVP 挑战。 */
    @JsonProperty("PVPChallengeInstances")
    @JsonAlias({"pvpChallengeInstances", "conclaveChallenges", "ConclaveChallenges"})
    private List<ConclaveChallengeRawData> pvpChallengeInstances;

    /** 建造项目进度，官方以有序数值数组提供。 */
    @JsonProperty("ProjectPct")
    @JsonAlias({"projectPct"})
    private List<Double> projectPct;

    /** 建造项目进度的具名协议形式。 */
    @JsonProperty("constructionProgress")
    private ConstructionProgressRawData constructionProgress;

    /** Simaris 图书馆状态。 */
    @JsonProperty("LibraryInfo")
    @JsonAlias({"libraryInfo"})
    private LibraryInfoRawData libraryInfo;

    /** Simaris 图书馆状态的精简协议形式。 */
    @JsonProperty("simaris")
    private SimarisRawData simaris;

    /** Sentient 舰队前哨状态。 */
    @JsonProperty("sentientOutposts")
    @JsonAlias("SentientOutposts")
    private SentientOutpostRawData sentientOutposts;

    /** 官方以 JSON 字符串内嵌的临时扩展状态。 */
    @JsonProperty("Tmp")
    @JsonAlias({"tmp"})
    private String tmp;

    /** 氏族每周行动及奖励。 */
    @JsonProperty("WeeklyVaultBonusRewards")
    @JsonAlias({"weeklyVaultBonusRewards", "clanWeeklyInitiative"})
    private List<ClanInitiativeRawData> weeklyVaultBonusRewards;

    /** 无尽任务经验计划。 */
    @JsonProperty("EndlessXpSchedule")
    @JsonAlias({"endlessXpSchedule"})
    private List<EndlessXpScheduleRawData> endlessXpSchedule;

    /** 1999 日历赛季。 */
    @JsonProperty("KnownCalendarSeasons")
    @JsonAlias({"knownCalendarSeasons", "calendar"})
    private List<CalendarSeasonRawData> knownCalendarSeasons;

    /** Prime Vault 商人。 */
    @JsonProperty("PrimeVaultTraders")
    @JsonAlias({"primeVaultTraders"})
    private List<VoidTraderRawData> primeVaultTraders;

    /** 每周挑战。 */
    @JsonProperty("WeeklyChallenges")
    @JsonAlias({"weeklyChallenges"})
    private List<WeeklyChallengeRawData> weeklyChallenges;

    /** 黑暗地带控制状态。 */
    @JsonProperty("BadlandNodes")
    @JsonAlias({"darkSectors"})
    private List<DarkSectorRawData> darkSectors;

    /** 深渊之塔挑战。 */
    @JsonProperty("Descents")
    @JsonAlias({"descents"})
    private List<DescentRawData> descents;

    /** 类型化动态记录的公共字段，具体分区通过各自的子类限定字段结构。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public abstract static class TimedRawData {
        /** 官方 Mongo 标识。 */
        @JsonProperty("_id")
        @JsonAlias({"id"})
        @JsonDeserialize(using = OfficialIdDeserializer.class)
        private String id;

        /** 官方激活时间。 */
        @JsonProperty("Activation")
        @JsonAlias({"activation", "Date", "date", "StartDate", "startDate"})
        @JsonDeserialize(using = OfficialInstantDeserializer.class)
        private Instant activation;

        /** 官方结束时间。 */
        @JsonProperty("Expiry")
        @JsonAlias({"expiry", "ExpiryDate", "expiryDate", "EndDate", "endDate", "expiration"})
        @JsonDeserialize(using = OfficialInstantDeserializer.class)
        private Instant expiry;
    }

    /** 警报分区输入。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AlertRawData extends TimedRawData {
        /** 警报任务信息。 */
        @JsonProperty("MissionInfo")
        @JsonAlias({"missionInfo"})
        private MissionInfoRawData missionInfo;

        /** 官方分组标签。 */
        @JsonProperty("Tag")
        @JsonAlias({"tag"})
        private String tag;

        /** 警报图标。 */
        @JsonProperty("Icon")
        @JsonAlias({"icon"})
        private String icon;
    }

    /** 动态任务的静态代码引用与奖励信息。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MissionInfoRawData {
        /** 任务类型代码。 */
        @JsonProperty("missionType")
        @JsonAlias("MissionType")
        private String missionType;

        /** 敌对阵营代码。 */
        @JsonProperty("faction")
        @JsonAlias("Faction")
        private String faction;

        /** 星图节点代码。 */
        @JsonProperty("location")
        @JsonAlias({"node", "Node"})
        private String location;

        /** 最低敌人等级。 */
        @JsonProperty("minEnemyLevel")
        @JsonAlias("MinEnemyLevel")
        private Integer minEnemyLevel;

        /** 最高敌人等级。 */
        @JsonProperty("maxEnemyLevel")
        @JsonAlias("MaxEnemyLevel")
        private Integer maxEnemyLevel;

        /** 任务奖励结构。 */
        @JsonProperty("missionReward")
        @JsonAlias("reward")
        @JsonDeserialize(using = OfficialRewardDeserializer.class)
        private RewardRawData missionReward;

        /** 任务描述语言键。 */
        @JsonProperty("descText")
        private String descText;

        /** 敌人等级描述。 */
        @JsonProperty("levelOverride")
        private String levelOverride;

        /** 敌人配置路径。 */
        @JsonProperty("enemySpec")
        private String enemySpec;

        /** 无尽任务轮次数。 */
        @JsonProperty("maxWaveNum")
        private Integer maxWaveNum;

        /** 任务种子。 */
        @JsonProperty("seed")
        private Long seed;
    }

    /** 活动任务和裂缝任务输入。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ActiveMissionRawData extends TimedRawData {
        /** 星图节点。 */
        @JsonProperty("Node")
        @JsonAlias({"node", "location"})
        private String node;

        /** 任务类型代码。 */
        @JsonProperty("MissionType")
        @JsonAlias({"missionType"})
        private String missionType;

        /** 阵营代码。 */
        @JsonProperty("faction")
        private String faction;

        /** 裂缝等级或风暴类型。 */
        @JsonProperty("Modifier")
        @JsonAlias({"tier", "modifier", "activeMissionTier", "ActiveMissionTier"})
        private String tier;

        /** 是否为钢铁之路任务。 */
        @JsonProperty("Hard")
        @JsonAlias({"hard", "isHard"})
        private Boolean hard;

        /** 是否为虚空风暴任务。 */
        @JsonProperty("voidStorm")
        @JsonAlias({"voidStorms", "isVoidStorm"})
        private Boolean voidStorm;

        /** 官方任务附加信息。 */
        @JsonProperty("missionInfo")
        private MissionInfoRawData missionInfo;
    }

    /** 入侵输入。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InvasionRawData extends TimedRawData {
        /** 星图节点。 */
        @JsonProperty("Node")
        @JsonAlias({"node", "location"})
        private String node;

        /** 当前争夺进度。 */
        @JsonProperty("completion")
        @JsonAlias({"completionRate", "Completion"})
        private Double completion;

        /** 双方净争夺数值。 */
        @JsonProperty("Count")
        @JsonAlias({"count"})
        private Double count;

        /** 入侵目标数值。 */
        @JsonProperty("Goal")
        @JsonAlias({"goal"})
        private Double goal;

        /** 入侵是否结束。 */
        @JsonProperty("Completed")
        @JsonAlias({"completed"})
        private Boolean completed;

        /** 攻击方任务信息。 */
        @JsonProperty("AttackerMissionInfo")
        @JsonAlias({"attackerMissionInfo"})
        private MissionInfoRawData attackerMissionInfo;

        /** 防守方任务信息。 */
        @JsonProperty("DefenderMissionInfo")
        @JsonAlias({"defenderMissionInfo"})
        private MissionInfoRawData defenderMissionInfo;

        /** 攻击方奖励。 */
        @JsonProperty("AttackerReward")
        @JsonAlias({"attackerReward"})
        @JsonDeserialize(using = OfficialRewardDeserializer.class)
        private RewardRawData attackerReward;

        /** 防守方奖励。 */
        @JsonProperty("DefenderReward")
        @JsonAlias({"defenderReward"})
        @JsonDeserialize(using = OfficialRewardDeserializer.class)
        private RewardRawData defenderReward;
    }

    /** 突击和执政官狩猎输入。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SortieRawData extends TimedRawData {
        /** 首领代码。 */
        @JsonProperty("Boss")
        @JsonAlias({"boss", "bossName"})
        private String boss;

        /** 突击任务变体列表。 */
        @JsonProperty("Variants")
        @JsonAlias({"variants", "missions", "Missions"})
        private List<SortieVariantRawData> variants;
    }

    /** 突击中的单个任务变体。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SortieVariantRawData {
        /** 任务类型代码。 */
        @JsonProperty("missionType")
        @JsonAlias("type")
        private String missionType;

        /** 任务修正代码。 */
        @JsonProperty("modifier")
        @JsonAlias({"modifierType", "ModifierType", "modifierDescription"})
        private String modifier;

        /** 星图节点。 */
        @JsonProperty("node")
        @JsonAlias("location")
        private String node;
    }

    /** 奖励及奖励物品的官方结构。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RewardRawData {
        /** 直接奖励物品路径。 */
        @JsonProperty("items")
        private List<String> items;

        /** 带数量的奖励物品。 */
        @JsonProperty("countedItems")
        private List<CountedItemRawData> countedItems;

        /** 信用点奖励。 */
        @JsonProperty("credits")
        private Integer credits;

        /** 官方可读奖励摘要。 */
        @JsonProperty("asString")
        @JsonAlias({"description", "string"})
        private String asString;
    }

    /** 带数量奖励物品。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CountedItemRawData {
        /** 物品静态路径。 */
        @JsonProperty("ItemType")
        @JsonAlias({"itemType", "item", "name", "uniqueName", "type"})
        private String itemType;

        /** 物品数量。 */
        @JsonProperty("ItemCount")
        @JsonAlias({"itemCount", "count"})
        private Integer itemCount;
    }

    /** 虚空奸商和 Prime Vault 商人输入。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VoidTraderRawData extends TimedRawData {
        /** 商人所在节点。 */
        @JsonProperty("Node")
        @JsonAlias({"location", "node"})
        private String location;

        /** 商人是否已经离开。 */
        @JsonProperty("completed")
        @JsonAlias("Completed")
        private Boolean completed;

        /** 普通库存列表。 */
        @JsonProperty("inventory")
        private List<VoidTraderItemRawData> inventory;

        /** 官方 Manifest 库存列表。 */
        @JsonProperty("Manifest")
        @JsonAlias({"manifest"})
        private List<VoidTraderItemRawData> manifest;
    }

    /** 商人库存中的物品和价格。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VoidTraderItemRawData {
        /** 物品静态路径。 */
        @JsonProperty("ItemType")
        @JsonAlias({"item", "itemType", "uniqueName"})
        private String item;

        /** 杜卡德金币价格。 */
        @JsonProperty("PrimePrice")
        @JsonAlias({"ducats"})
        private Integer ducats;

        /** 信用点价格。 */
        @JsonProperty("RegularPrice")
        @JsonAlias({"credits"})
        private Integer credits;
    }

    /** 单个周期状态。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CycleRawData {
        /** 当前周期或状态名称。 */
        @JsonProperty("state")
        @JsonAlias({"active", "State"})
        private String state;

        /** 是否为白昼。 */
        @JsonProperty("isDay")
        @JsonAlias("day")
        private Boolean isDay;

        /** 是否处于奥布山谷暖和周期。 */
        @JsonProperty("isWarm")
        @JsonAlias("warm")
        private Boolean isWarm;

        /** 周期截止时间。 */
        @JsonProperty("expiry")
        @JsonAlias("expiration")
        @JsonDeserialize(using = OfficialInstantDeserializer.class)
        private Instant expiry;
    }

    /** 仲裁任务。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ArbitrationRawData extends TimedRawData {
        /** 节点名称。 */
        @JsonProperty("node")
        private String node;

        /** 任务类型。 */
        @JsonProperty("type")
        @JsonAlias("missionType")
        private String type;

        /** 敌人阵营。 */
        @JsonProperty("enemy")
        @JsonAlias("faction")
        private String enemy;
    }

    /** 夜波赛季和挑战。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SeasonRawData {
        /** 活动挑战列表。 */
        @JsonProperty("ActiveChallenges")
        @JsonAlias({"missions", "activeChallenges"})
        private List<NightwaveChallengeRawData> missions;
    }

    /** 夜波挑战。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NightwaveChallengeRawData {
        /** 挑战标识。 */
        @JsonProperty("_id")
        @JsonAlias({"id"})
        @JsonDeserialize(using = OfficialIdDeserializer.class)
        private String id;

        /** 挑战代码。 */
        @JsonProperty("Challenge")
        @JsonAlias({"challenge"})
        private String challenge;

        /** 挑战名称。 */
        @JsonProperty("title")
        @JsonAlias("name")
        private String title;

        /** 挑战说明。 */
        @JsonProperty("desc")
        @JsonAlias("description")
        private String desc;

        /** 声望奖励。 */
        @JsonProperty("reputation")
        @JsonAlias("standing")
        private Integer reputation;

        /** 是否为每日挑战。 */
        @JsonProperty("Daily")
        @JsonAlias({"isDaily", "daily"})
        private Boolean isDaily;
    }

    /** 每日特惠。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DailyDealRawData extends TimedRawData {
        /** 特惠物品路径。 */
        @JsonProperty("StoreItem")
        @JsonAlias({"item", "itemType", "storeItem"})
        private String item;

        /** 总库存。 */
        @JsonProperty("AmountTotal")
        @JsonAlias({"stock", "total", "amountTotal"})
        private Integer stock;

        /** 已售数量。 */
        @JsonProperty("AmountSold")
        @JsonAlias({"sold", "amountSold"})
        private Integer sold;

        /** 信用点价格。 */
        @JsonProperty("OriginalPrice")
        @JsonAlias({"credits", "regularPrice", "originalPrice"})
        private Integer credits;

        /** 白金价格。 */
        @JsonProperty("SalePrice")
        @JsonAlias({"platinum", "primePrice", "salePrice"})
        private Integer platinum;
    }

    /** 官方限时活动目标。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WorldEventRawData extends TimedRawData {
        /** 活动描述键。 */
        @JsonProperty("Desc")
        @JsonAlias({"description", "desc", "title", "name"})
        private String description;

        /** 活动提示。 */
        @JsonProperty("tooltip")
        @JsonAlias({"toolTip", "Tooltip"})
        private String tooltip;

        /** 活动类型。 */
        @JsonProperty("Tag")
        @JsonAlias({"category", "tag", "type"})
        private String category;

        /** 活动节点。 */
        @JsonProperty("Node")
        @JsonAlias({"node", "location"})
        private String node;

        /** 当前活动进度。 */
        @JsonProperty("Count")
        @JsonAlias({"currentScore", "healthPct", "count"})
        private Double currentScore;

        /** 活动目标进度。 */
        @JsonProperty("Goal")
        @JsonAlias({"maximumScore", "goal"})
        private Double maximumScore;

        /** 活动奖励。 */
        @JsonProperty("Reward")
        @JsonAlias({"reward"})
        @JsonDeserialize(using = OfficialRewardDeserializer.class)
        private RewardRawData reward;
    }

    /** 官方新闻或公告。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NewsRawData extends TimedRawData {
        /** 多语言标题消息。 */
        @JsonProperty("Messages")
        @JsonAlias({"messages"})
        private List<LocalizedMessageRawData> messages;

        /** 新闻跳转链接。 */
        @JsonProperty("Prop")
        @JsonAlias({"link", "prop"})
        private String link;

        /** 新闻图片。 */
        @JsonProperty("Icon")
        @JsonAlias({"imageLink", "imageUrl", "image", "icon"})
        private String imageLink;

        /** 是否置顶。 */
        @JsonProperty("Priority")
        @JsonAlias({"priority"})
        private Boolean priority;

        /** 新闻标题或消息。 */
        @JsonProperty("title")
        @JsonAlias({"name", "message"})
        private String title;

        /** 新闻多语言标题。 */
        @JsonProperty("translations")
        private TranslationsRawData translations;

    }

    /** 新闻标题的单语言文本。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LocalizedMessageRawData {
        /** 语言代码。 */
        @JsonProperty("LanguageCode")
        @JsonAlias({"languageCode"})
        private String languageCode;

        /** 标题或正文。 */
        @JsonProperty("Message")
        @JsonAlias({"message"})
        private String message;
    }

    /** 辛迪加任务。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SyndicateMissionRawData extends TimedRawData {
        /** 辛迪加代码。 */
        @JsonProperty("Tag")
        @JsonAlias({"tag", "syndicate"})
        private String tag;

        /** 可执行节点。 */
        @JsonProperty("Nodes")
        @JsonAlias({"nodes"})
        private List<String> nodes;

        /** 赏金任务列表。 */
        @JsonProperty("Jobs")
        @JsonAlias({"jobs"})
        private List<SyndicateJobRawData> jobs;
    }

    /** 辛迪加赏金任务。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SyndicateJobRawData {
        /** 任务静态路径。 */
        @JsonProperty("jobType")
        private String jobType;

        /** 赏金任务的已解析名称。 */
        @JsonProperty("type")
        private String type;

        /** 赏金所在区域的语言键。 */
        @JsonProperty("locationTag")
        @JsonAlias("location")
        private String locationTag;

        /** 奖励池静态路径或列表。 */
        @JsonProperty("rewards")
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<String> rewards;

        /** 最低敌人等级。 */
        @JsonProperty("minEnemyLevel")
        private Integer minEnemyLevel;

        /** 最高敌人等级。 */
        @JsonProperty("maxEnemyLevel")
        private Integer maxEnemyLevel;

        /** 直接提供的敌人等级范围。 */
        @JsonProperty("enemyLevels")
        private List<Integer> enemyLevels;

        /** 各阶段声望奖励。 */
        @JsonProperty("xpAmounts")
        @JsonAlias("standingStages")
        private List<Integer> xpAmounts;

        /** 最低段位要求。 */
        @JsonProperty("masteryReq")
        @JsonAlias("minMR")
        private Integer masteryReq;

        /** 是否为虚空任务。 */
        @JsonProperty("isVault")
        private Boolean isVault;

        /** 自维护赏金的奖励池文本。 */
        @JsonProperty("rewardPool")
        private List<String> rewardPool;

        /** 自维护赏金的结构化掉落。 */
        @JsonProperty("rewardPoolDrops")
        private List<SyndicateRewardRawData> rewardPoolDrops;
    }

    /** 自维护赏金中的结构化奖励掉落。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SyndicateRewardRawData {
        /** 奖励物品名称。 */
        @JsonProperty("item")
        private String item;

        /** 奖励数量。 */
        @JsonProperty("count")
        private Integer count;

        /** 掉落概率。 */
        @JsonProperty("chance")
        private Double chance;

        /** 奖励稀有度。 */
        @JsonProperty("rarity")
        private String rarity;
    }

    /** 其他动态分区均使用强类型且允许忽略新增上游字段的输入对象。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SteelPathOfferingRawData extends TimedRawData {
        /** 奖励静态路径。 */
        @JsonProperty("item")
        @JsonAlias({"Item", "itemType", "uniqueName"})
        private String item;

        /** 杜卡德金币价格。 */
        @JsonProperty("ducats")
        @JsonAlias("PrimePrice")
        private Integer ducats;

        /** 信用点价格。 */
        @JsonProperty("credits")
        @JsonAlias("RegularPrice")
        private Integer credits;
    }

    /** 全局增益。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GlobalUpgradeRawData extends TimedRawData {
        /** 增益类型。 */
        @JsonProperty("UpgradeType")
        @JsonAlias({"upgradeType", "type"})
        private String upgradeType;

        /** 增益应用方式。 */
        @JsonProperty("OperationType")
        @JsonAlias({"operationType"})
        private String operationType;

        /** 增益数值。 */
        @JsonProperty("Value")
        @JsonAlias({"value"})
        private Double value;

        /** 影响阵营。 */
        @JsonProperty("faction")
        @JsonAlias("Faction")
        private String faction;
    }

    /** 商城闪购。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FlashSaleRawData extends TimedRawData {
        /** 商品静态路径。 */
        @JsonProperty("TypeName")
        @JsonAlias({"item", "Item", "storeItem", "UniqueName", "ItemType"})
        private String item;

        /** 商品名称。 */
        @JsonProperty("title")
        @JsonAlias("Title")
        private String title;

        /** 折扣比例。 */
        @JsonProperty("Discount")
        @JsonAlias({"discount"})
        private Double discount;

        /** 信用点价格。 */
        @JsonProperty("RegularOverride")
        @JsonAlias({"credits", "regularPrice", "RegularPrice"})
        private Integer credits;

        /** 白金价格。 */
        @JsonProperty("PremiumOverride")
        @JsonAlias({"platinum", "salePrice", "PrimePrice"})
        private Integer platinum;

        /** 商品是否在商城展示。 */
        @JsonProperty("ShowInMarket")
        @JsonAlias({"shownInMarket"})
        private Boolean shownInMarket;

        /** 是否为精选商品。 */
        @JsonProperty("Featured")
        @JsonAlias({"featured"})
        private Boolean featured;

        /** 是否为热门商品。 */
        @JsonProperty("Popular")
        @JsonAlias({"popular"})
        private Boolean popular;
    }

    /** 持续追猎敌人。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PersistentEnemyRawData extends TimedRawData {
        /** 敌人静态路径。 */
        @JsonProperty("AgentType")
        @JsonAlias({"agentType", "type"})
        private String agentType;

        /** 敌人等级。 */
        @JsonProperty("Rank")
        @JsonAlias({"rank"})
        private Integer rank;

        /** 是否已击败。 */
        @JsonProperty("Discovered")
        @JsonAlias({"discovered", "isDiscovered", "isDead"})
        private Boolean discovered;

        /** 敌人展示标签。 */
        @JsonProperty("LocTag")
        @JsonAlias({"locationTag", "locTag"})
        private String locationTag;

        /** 当前生命百分比。 */
        @JsonProperty("HealthPercent")
        @JsonAlias({"healthPercent"})
        private Double healthPercent;

        /** 逃离所需伤害。 */
        @JsonProperty("FleeDamage")
        @JsonAlias({"fleeDamage"})
        private Double fleeDamage;

        /** 当前所在区域。 */
        @JsonProperty("Region")
        @JsonAlias({"region"})
        private String region;

        /** 最近发现时间。 */
        @JsonProperty("LastDiscoveredTime")
        @JsonAlias({"lastDiscoveredTime"})
        @JsonDeserialize(using = OfficialInstantDeserializer.class)
        private Instant lastDiscoveredTime;

        /** 所在节点。 */
        @JsonProperty("LastDiscoveredLocation")
        @JsonAlias({"lastDiscoveredLocation", "node"})
        private String lastDiscoveredLocation;

        /** 是否使用任务票据。 */
        @JsonProperty("UseTicketing")
        @JsonAlias({"usingTicketing", "useTicketing"})
        private Boolean usingTicketing;
    }

    /** Conquest/Archimedea 轮换。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConquestRawData extends TimedRawData {
        /** 轮换类型代码。 */
        @JsonProperty("Type")
        @JsonAlias({"type"})
        private String type;

        /** 轮换任务。 */
        @JsonProperty("Missions")
        @JsonAlias({"missions"})
        private List<ConquestMissionRawData> missions;

        /** 全局变量列表。 */
        @JsonProperty("Variables")
        @JsonAlias({"variables"})
        private List<String> variables;

        /** 随机种子。 */
        @JsonProperty("RandomSeed")
        @JsonAlias({"randomSeed", "RandSeed"})
        @JsonDeserialize(using = OfficialLongDeserializer.class)
        private Long randomSeed;
    }

    /** Conquest 中的单个任务。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConquestMissionRawData {
        /** 敌对阵营。 */
        @JsonProperty("faction")
        private String faction;

        /** 任务类型。 */
        @JsonProperty("missionType")
        private String missionType;

        /** 普通和钢铁之路难度配置。 */
        @JsonProperty("difficulties")
        private List<ConquestDifficultyRawData> difficulties;

        /** 直接提供的任务偏差。 */
        @JsonProperty("deviation")
        @JsonAlias({"Deviation"})
        private String deviation;

        /** 直接提供的任务风险。 */
        @JsonProperty("risks")
        @JsonAlias({"Risks"})
        private List<String> risks;

    }

    /** Conquest 难度及修正条件。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConquestDifficultyRawData {
        /** 难度类型。 */
        @JsonProperty("type")
        private String type;

        /** 任务偏差。 */
        @JsonProperty("deviation")
        private String deviation;

        /** 风险修正列表。 */
        @JsonProperty("risks")
        private List<String> risks;
    }

    /** PVP 挑战。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConclaveChallengeRawData extends TimedRawData {
        /** 挑战类型。 */
        @JsonProperty("PVPMode")
        @JsonAlias({"mode", "Mode", "type"})
        private String mode;

        /** 挑战名称。 */
        @JsonProperty("challengeTypeRefID")
        @JsonAlias({"title", "Title"})
        private String title;

        /** 挑战目标次数。 */
        @JsonProperty("requiredCount")
        @JsonAlias("RequiredCount")
        private Integer requiredCount;

        /** 挑战进度次数。 */
        @JsonProperty("progress")
        @JsonAlias("Progress")
        private Integer progress;

        /** 挑战分类代码。 */
        @JsonProperty("Category")
        @JsonAlias({"category"})
        private String category;

        /** 挑战参数。 */
        @JsonProperty("params")
        private List<ConclaveParameterRawData> params;

        /** 子挑战。 */
        @JsonProperty("subChallenges")
        private List<ConclaveChallengeRawData> subChallenges;
    }

    /** Conclave 挑战参数。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConclaveParameterRawData {
        /** 参数名。 */
        @JsonProperty("n")
        private String name;

        /** 参数值。 */
        @JsonProperty("v")
        private Integer value;
    }

    /** 建造进度的命名结构。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConstructionProgressRawData {
        /** Fomorian 建造进度。 */
        @JsonProperty("fomorianProgress")
        private Double fomorianProgress;

        /** Razorback 建造进度。 */
        @JsonProperty("razorbackProgress")
        private Double razorbackProgress;

        /** 其他建造进度。 */
        @JsonProperty("unknownProgress")
        private Double unknownProgress;
    }

    /** Simaris 官方数据。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LibraryInfoRawData {
        /** 最近完成的目标静态路径。 */
        @JsonProperty("LastCompletedTargetType")
        @JsonAlias({"lastCompletedTargetType"})
        private String lastCompletedTargetType;
    }

    /** Simaris 简化状态。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SimarisRawData {
        /** 当前扫描目标。 */
        @JsonProperty("target")
        private String target;

        /** 目标是否有效。 */
        @JsonProperty("isTargetActive")
        private Boolean targetActive;
    }

    /** Sentient 前哨。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SentientOutpostRawData extends TimedRawData {
        /** 前哨任务节点。 */
        @JsonProperty("node")
        @JsonAlias("location")
        private String node;
    }

    /** 氏族每周行动。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ClanInitiativeRawData {
        /** 行动名称或类型。 */
        @JsonProperty("name")
        @JsonAlias({"Name", "type"})
        private String name;

        /** 行动奖励列表。 */
        @JsonProperty("Rewards")
        @JsonAlias({"rewards"})
        private List<ClanRewardRawData> rewards;

        /** 行动分数。 */
        @JsonProperty("score")
        @JsonAlias({"Score", "points"})
        private Integer score;

        /** 行动周数。 */
        @JsonProperty("WeekCount")
        @JsonAlias({"weekCount"})
        private Integer weekCount;

        /** 本周行动所属区域。 */
        @JsonProperty("BonusRegion")
        @JsonAlias({"bonusRegion"})
        private String bonusRegion;
    }

    /** 氏族行动奖励。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ClanRewardRawData {
        /** 奖励物品路径。 */
        @JsonProperty("Reward")
        @JsonAlias({"reward"})
        private String reward;

        /** 奖励数量。 */
        @JsonProperty("ItemCount")
        @JsonAlias({"itemCount"})
        private Integer itemCount;

        /** 领取所需分数。 */
        @JsonProperty("PointThreshold")
        @JsonAlias({"pointThreshold"})
        private Integer pointThreshold;

        /** 奖励是否已经领取。 */
        @JsonProperty("RewardClaimed")
        @JsonAlias({"rewardClaimed"})
        private Boolean rewardClaimed;
    }

    /** 日历赛季。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CalendarSeasonRawData extends TimedRawData {
        /** 赛季名称。 */
        @JsonProperty("Season")
        @JsonAlias({"season"})
        private String season;

        /** 年度轮次。 */
        @JsonProperty("YearIteration")
        @JsonAlias({"yearIteration"})
        private Integer yearIteration;

        /** 赛季每日活动。 */
        @JsonProperty("Days")
        @JsonAlias({"days"})
        private List<CalendarDayRawData> days;
    }

    /** 日历中的一天。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CalendarDayRawData {
        /** 年内日序号。 */
        @JsonProperty("day")
        private Integer day;

        /** 当日活动。 */
        @JsonProperty("events")
        private List<CalendarEventRawData> events;

        /** 显式日历日期。 */
        @JsonProperty("date")
        private String date;

    }

    /** 日历活动。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CalendarEventRawData {
        /** 日历活动类型。 */
        @JsonProperty("type")
        private String type;

        /** 挑战静态路径。 */
        @JsonProperty("challenge")
        private String challenge;

        /** 增益静态路径。 */
        @JsonProperty("upgrade")
        private String upgrade;

        /** 奖励静态路径。 */
        @JsonProperty("uniqueName")
        @JsonAlias("reward")
        private String uniqueName;

        /** 活动说明。 */
        @JsonProperty("description")
        private String description;

        /** 对话名称。 */
        @JsonProperty("dialogueName")
        private String dialogueName;

        /** 对话内容键。 */
        @JsonProperty("dialogueConvo")
        private String dialogueConvo;
    }

    /** 每周挑战集合。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeeklyChallengeRawData extends TimedRawData {
        /** 挑战列表。 */
        @JsonProperty("Challenges")
        @JsonAlias({"challenges"})
        private List<WeeklyChallengeItemRawData> challenges;
    }

    /** 单个每周挑战。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeeklyChallengeItemRawData {
        /** 挑战类型。 */
        @JsonProperty("Type")
        @JsonAlias({"type"})
        private String type;

        /** 最低敌人等级。 */
        @JsonProperty("MinimumEnemyLevel")
        @JsonAlias({"minimumEnemyLevel"})
        private Integer minimumEnemyLevel;

        /** 需要完成次数。 */
        @JsonProperty("RequiredCount")
        @JsonAlias({"requiredCount", "requiredAmount"})
        private Integer requiredCount;

        /** 进度更新频率。 */
        @JsonProperty("ProgressIndicatorFreq")
        @JsonAlias({"progressIndicatorFreq", "progressAmount"})
        private Integer progressIndicatorFreq;

        /** 指定伤害类型。 */
        @JsonProperty("DamageType")
        @JsonAlias({"damageType"})
        private String damageType;

        /** 目标敌人类型。 */
        @JsonProperty("VictimType")
        @JsonAlias({"victimType", "target"})
        private String victimType;

        /** 挑战脚本参数。 */
        @JsonProperty("Script")
        @JsonAlias({"script"})
        private WeeklyChallengeScriptRawData script;

    }

    /** 黑暗地带控制方信息。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DarkSectorRawData {
        /** 节点标识。 */
        @JsonProperty("id")
        @JsonAlias({"_id", "Node", "node"})
        @JsonDeserialize(using = OfficialIdDeserializer.class)
        private String id;

        /** 防守方数据。 */
        @JsonProperty("DefenderInfo")
        @JsonAlias({"defenderInfo"})
        private DarkSectorDefenderRawData defenderInfo;
    }

    /** 黑暗地带防守方数据。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DarkSectorDefenderRawData {
        /** 防守方名称。 */
        @JsonProperty("Name")
        @JsonAlias({"name"})
        private String name;

        /** 攻击方名称。 */
        @JsonProperty("Attacker")
        @JsonAlias({"attacker"})
        private String attacker;

        /** 剩余控制强度。 */
        @JsonProperty("StrengthRemaining")
        @JsonAlias({"strengthRemaining"})
        private Double strengthRemaining;

        /** 最大控制强度。 */
        @JsonProperty("MaxStrength")
        @JsonAlias({"maxStrength"})
        private Double maxStrength;

        /** 战斗报酬。 */
        @JsonProperty("MissionBattlePay")
        @JsonAlias({"missionBattlePay"})
        private Integer missionBattlePay;

        /** 战斗任务信息。 */
        @JsonProperty("MissionInfo")
        @JsonAlias({"missionInfo"})
        private MissionInfoRawData missionInfo;

        /** 部署开始时间。 */
        @JsonProperty("DeploymentActivationTime")
        @JsonAlias({"deploymentActivationTime"})
        @JsonDeserialize(using = OfficialInstantDeserializer.class)
        private Instant deploymentActivationTime;

        /** 部署结束时间。 */
        @JsonProperty("DeploymentExpiryTime")
        @JsonAlias({"deploymentExpiryTime"})
        @JsonDeserialize(using = OfficialInstantDeserializer.class)
        private Instant deploymentExpiryTime;
    }

    /** 深渊之塔轮换。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DescentRawData extends TimedRawData {
        /** 轮换随机种子。 */
        @JsonProperty("RandSeed")
        @JsonAlias({"randSeed"})
        private Long randSeed;

        /** 轮换挑战列表。 */
        @JsonProperty("Challenges")
        @JsonAlias({"challenges"})
        private List<DescentChallengeRawData> challenges;
    }

    /** 深渊之塔中的单个挑战。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DescentChallengeRawData {
        /** 挑战序号。 */
        @JsonProperty("Index")
        @JsonAlias({"index"})
        private Integer index;

        /** 挑战类型。 */
        @JsonProperty("Type")
        @JsonAlias({"type"})
        private String type;

        /** 挑战规则。 */
        @JsonProperty("Challenge")
        @JsonAlias({"challenge"})
        private String challenge;

        /** 关卡路径。 */
        @JsonProperty("Level")
        @JsonAlias({"level"})
        private String level;

        /** 敌人配置路径。 */
        @JsonProperty("Specs")
        @JsonAlias({"specs"})
        private List<String> specs;

        /** 光环配置路径。 */
        @JsonProperty("Auras")
        @JsonAlias({"auras"})
        private List<String> auras;
    }

    /** 无尽任务经验计划中的 Circuit 奖励选择。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EndlessXpScheduleRawData extends TimedRawData {
        /** Circuit 分类和奖励列表。 */
        @JsonProperty("CategoryChoices")
        @JsonAlias({"categoryChoices"})
        private List<CircuitChoiceRawData> categoryChoices;
    }

    /** Circuit 单个分类选择。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CircuitChoiceRawData {
        /** 分类代码。 */
        @JsonProperty("Category")
        @JsonAlias({"category"})
        private String category;

        /** 奖励选择。 */
        @JsonProperty("Choices")
        @JsonAlias({"choices"})
        private List<String> choices;
    }

    /** 官方 Tmp 字符串中内嵌的 JSON 结构。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TmpRawData {
        /** Sentient 前哨的节点序号。 */
        @JsonProperty("sfn")
        private Integer sentientFleetNode;
    }

    /** 新闻协议中的已知语言标题，未支持语言仍可从快照原文排查。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TranslationsRawData {
        /** 简体中文标题。 */
        @JsonProperty("zh")
        @JsonAlias("zh-hans")
        private String chinese;

        /** 英文标题。 */
        @JsonProperty("en")
        private String english;
    }

    /** 每周挑战脚本中的目标阵营。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeeklyChallengeScriptRawData {
        /** 目标阵营代码。 */
        @JsonProperty("_faction")
        private String faction;
    }
}
