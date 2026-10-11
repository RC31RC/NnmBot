package com.nnm.bot.warframe.constant;

import com.nnm.bot.warframe.model.entity.WarframeCommunityAliasDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityBountyDefinition;
import com.nnm.bot.warframe.model.entity.WarframeCommunityDatasetRecord;
import com.nnm.bot.warframe.model.entity.WarframeCommunityMarketElementDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityMarketComponentDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityMarketRivenTionAliasDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityMarketRivenTionDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityNodesDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRewardPoolDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRivenAnalyseTrendDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRivenTrendDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityStateTranslationDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityTranslationDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityTermDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityArbitrationEssenceDataset;
import com.nnm.bot.warframe.model.entity.WarframeCommunityFishGuide;
import com.nnm.bot.warframe.model.entity.WarframeCommunityMapFeature;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRelic;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRelicDrop;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRelicReward;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRivenUnrollable;
import com.nnm.bot.warframe.model.entity.WarframeCommunityWorldStateRule;
import com.nnm.bot.warframe.model.entity.WarframeArbitrationSchedule;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateFaction;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateFissureModifier;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateLanguage;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateMissionType;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateNode;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateOperationType;
import com.nnm.bot.warframe.model.entity.WarframeWorldStatePersistentEnemy;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateSortie;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateSyndicate;
import com.nnm.bot.warframe.model.entity.WarframeWorldStateUpgradeType;

import java.util.Locale;

/** 社区仓库中可独立刷新和展示的 JSON 数据集。 */
public enum WarframeCommunityDataset {
    BOUNTY_DEFINITION("bounty_definition", "ExportBounties.json", "赏金静态定义",
            WarframeCommunityBountyDefinition.class),
    TRANSLATION("translation", "dict.zh.json / dict.en.json", "完整键语言字典",
            WarframeCommunityTranslationDataset.class),
    ALIAS("alias", "alias.json", "社区别名", WarframeCommunityAliasDataset.class),
    NODES("nodes", "nodes.json", "节点", WarframeCommunityNodesDataset.class),
    REWARD_POOL("reward_pool", "reward_pool.json", "奖励池",
            WarframeCommunityRewardPoolDataset.class),
    STATE_TRANSLATION("state_translation", "state_translation.json", "状态翻译",
            WarframeCommunityStateTranslationDataset.class),
    MARKET_ELEMENT("market_element", "market_element.json", "Market 物品",
            WarframeCommunityMarketElementDataset.class),
    MARKET_COMPONENT("market_component", "market_component.json", "Market 组件映射",
            WarframeCommunityMarketComponentDataset.class),
    MARKET_RIVEN_TION("market_riven_tion", "market_riven_tion.json", "Riven 词条",
            WarframeCommunityMarketRivenTionDataset.class),
    MARKET_RIVEN_TION_ALIAS("market_riven_tion_alias", "market_riven_tion_alias.json",
            "Riven 词条别名", WarframeCommunityMarketRivenTionAliasDataset.class),
    RIVEN_ANALYSE_TREND("riven_analyse_trend", "riven_analyse_trend.json", "Riven 分析趋势",
            WarframeCommunityRivenAnalyseTrendDataset.class),
    RIVEN_TREND("riven_trend", "riven_trend.json", "Riven 趋势",
            WarframeCommunityRivenTrendDataset.class),
    TERM("term", "term.json", "社区术语词库", WarframeCommunityTermDataset.class),
    ARBITRATION_ESSENCE("arbitration_essence", "arbitrationEssence.js", "仲裁精华规则",
            WarframeCommunityArbitrationEssenceDataset.class),
    ARBITRATION_SCHEDULE("arbitration_schedule", "arbys.txt", "仲裁排期",
            WarframeCommunityDatasetRecord.class),
    WORLDSTATE_MISSION_TYPE("worldstate_mission_type", "missionTypes.json", "WorldState 任务类型",
            WarframeWorldStateMissionType.class),
    WORLDSTATE_NODE("worldstate_node", "solNodes.json", "WorldState 星图节点",
            WarframeWorldStateNode.class),
    WORLDSTATE_FACTION("worldstate_faction", "factionsData.json", "WorldState 阵营",
            WarframeWorldStateFaction.class),
    WORLDSTATE_SYNDICATE("worldstate_syndicate", "syndicatesData.json", "WorldState 集团",
            WarframeWorldStateSyndicate.class),
    WORLDSTATE_OPERATION_TYPE("worldstate_operation_type", "operationTypes.json", "WorldState 活动类型",
            WarframeWorldStateOperationType.class),
    WORLDSTATE_UPGRADE_TYPE("worldstate_upgrade_type", "upgradeTypes.json", "WorldState 升级类型",
            WarframeWorldStateUpgradeType.class),
    WORLDSTATE_PERSISTENT_ENEMY("worldstate_persistent_enemy", "persistentEnemyData.json",
            "WorldState 持续敌人", WarframeWorldStatePersistentEnemy.class),
    WORLDSTATE_FISSURE_MODIFIER("worldstate_fissure_modifier", "fissureModifiers.json",
            "WorldState 裂隙变体", WarframeWorldStateFissureModifier.class),
    WORLDSTATE_SORTIE("worldstate_sortie", "sortieData.json",
            "WorldState 突击静态代码簿", WarframeWorldStateSortie.class),
    WORLDSTATE_LANGUAGE("worldstate_language", "languages.json", "WorldState 语言词条",
            WarframeWorldStateLanguage.class),
    FISH_GUIDE("fish_guide", "fish.json", "鱼类攻略", WarframeCommunityFishGuide.class),
    MAP_FEATURE("map_feature", "geo.json", "地图标注", WarframeCommunityMapFeature.class),
    RELIC("relic", "Relics.json", "遗物主数据", WarframeCommunityRelic.class),
    RELIC_REWARD("relic_reward", "Relics.json", "遗物奖励",
            WarframeCommunityRelicReward.class),
    RELIC_DROP("relic_drop", "Relics.json", "遗物掉落", WarframeCommunityRelicDrop.class),
    RIVEN_UNROLLABLE("riven_unrollable", "riven_unrollables.json", "不可展开紫卡词条",
            WarframeCommunityRivenUnrollable.class),
    WORLDSTATE_RULE("worldstate_rule", "state_translation_local.json", "WorldState 静态规则",
            WarframeCommunityWorldStateRule.class);

    /**
     * 管理端展示和刷新流程使用的稳定数据集元数据。
     *
     * <p>来源 URL 仍由配置类管理；这里仅登记数据集本身的职责，避免 Service
     * 重新维护一份与数据集注册表脱节的元数据。</p>
     */
    public record Metadata(
            String tableName,
            String sourceType,
            String description,
            String consumerCommands,
            boolean refreshable,
            boolean manageable,
            boolean readOnly) {
    }

    private final String code;
    private final String fileName;
    private final String label;
    private final Class<? extends WarframeCommunityDatasetRecord> entityType;

    WarframeCommunityDataset(
            String code,
            String fileName,
            String label,
            Class<? extends WarframeCommunityDatasetRecord> entityType) {
        this.code = code;
        this.fileName = fileName;
        this.label = label;
        this.entityType = entityType;
    }

    public String code() {
        return code;
    }

    public String fileName() {
        return fileName;
    }

    public String label() {
        return label;
    }

    public Class<? extends WarframeCommunityDatasetRecord> entityType() {
        return entityType;
    }

    public Metadata metadata() {
        return switch (this) {
            case BOUNTY_DEFINITION -> referenceMetadata("动态赏金引用的名称、描述与阶段定义",
                    "希图斯、索拉里斯、英择谛、赏金展示");
            case TRANSLATION -> metadata("wf_community_dataset_translation", "COMMUNITY",
                    "plus 完整路径的中英文语言投影；按路径和语言分别保存。",
                    "wm、zk、wiki、物品解析、WorldState 奖励显示", true, true);
            case ALIAS -> metadata("wf_community_dataset_alias", "COMMUNITY",
                    "社区黑话/别名到标准物品名称的映射。",
                    "wm、zk、wiki、物品解析", true, true);
            case NODES -> metadata("wf_community_dataset_nodes", "COMMUNITY",
                    "星图节点、星系和任务静态字段。",
                    "警报、入侵、仲裁、裂隙、突击、WorldState 节点解析", true, true);
            case REWARD_POOL -> metadata("wf_community_dataset_reward_pool", "COMMUNITY",
                    "赏金和活动奖励池，用于补全官方快照缺失奖励。",
                    "希图斯、索拉里斯、英择谛、赏金展示", true, true);
            case STATE_TRANSLATION -> metadata("wf_community_dataset_state_translation", "COMMUNITY",
                    "WorldState 内部状态、挑战和任务文本翻译。",
                    "电波、赏金、日历、WorldState 引用清洗", true, true);
            case MARKET_ELEMENT -> metadata("wf_community_dataset_market_element", "COMMUNITY",
                    "社区整理的 Market 商品目录，不是元素属性或组件后缀规则。",
                    "已导入资料，尚无命令消费者；wm 当前使用 Market API 商品目录", true, true);
            case MARKET_COMPONENT -> metadata("wf_community_dataset_market_component", "SELF_MAINTAINED",
                    "NnmBot 自维护的中文组件后缀到 Market 英文后缀映射。",
                    "wm 物品组件解析", false, true);
            case MARKET_RIVEN_TION -> metadata("wf_community_dataset_market_riven_tion", "COMMUNITY",
                    "紫卡属性、URL 名称、前后缀和适用范围。",
                    "zk 紫卡属性条件解析与查询", true, true);
            case MARKET_RIVEN_TION_ALIAS -> metadata("wf_community_dataset_market_riven_tion_alias",
                    "COMMUNITY", "紫卡属性中文别名及可配置组合词条。",
                    "zk 紫卡属性、组合词条和任意负面解析", true, true);
            case RIVEN_ANALYSE_TREND -> metadata("wf_community_dataset_riven_analyse_trend",
                    "COMMUNITY", "紫卡分析趋势系数和武器类型。",
                    "已导入资料，尚无命令消费者；紫卡分析尚未实现", true, true);
            case RIVEN_TREND -> metadata("wf_community_dataset_riven_trend", "COMMUNITY",
                    "紫卡倾向变化的时间和前后数值。",
                    "紫卡倾向变动查询", true, true);
            case TERM -> metadata("wf_community_term", "COMMUNITY",
                    "参考项目术语、阵营和静态资料词库；远程 term 来源已停用。",
                    "信条、赤毒、终幕、物品解析和 WorldState 文案", false, true);
            case ARBITRATION_ESSENCE -> metadata("wf_community_arbitration_essence", "COMMUNITY",
                    "仲裁节点对应精华数量的规则。",
                    "仲裁：为当前仲裁节点补充精华数量和效率等级；不是历史或未来仲裁任务表",
                    true, true);
            case ARBITRATION_SCHEDULE -> metadata("wf_arbitration_schedule", "COMMUNITY",
                    "browse.wf 提供的仲裁历史与未来排期；解析后写入独立仲裁排期表。",
                    "仲裁、今日仲裁、高效仲裁、仲裁表", true, false, true);
            case WORLDSTATE_MISSION_TYPE -> referenceMetadata("WorldState 任务类型",
                    "警报、入侵、仲裁、裂隙、突击、平原状态");
            case WORLDSTATE_NODE -> referenceMetadata("WorldState 星图节点",
                    "全部 WorldState 节点类命令");
            case WORLDSTATE_FACTION -> referenceMetadata("WorldState 阵营",
                    "警报、入侵、WorldState 奖励和阵营解析");
            case WORLDSTATE_SYNDICATE -> referenceMetadata("WorldState 集团",
                    "希图斯、索拉里斯、英择谛等集团任务");
            case WORLDSTATE_OPERATION_TYPE -> referenceMetadata("WorldState 活动类型",
                    "入侵、活动和 WorldState 任务解析");
            case WORLDSTATE_UPGRADE_TYPE -> referenceMetadata("WorldState 升级类型",
                    "WorldState 奖励和升级引用解析");
            case WORLDSTATE_PERSISTENT_ENEMY -> referenceMetadata("WorldState 持续敌人",
                    "WorldState 持续敌人引用解析；不是赤毒、信条独立维护数据");
            case WORLDSTATE_FISSURE_MODIFIER -> referenceMetadata("WorldState 裂隙变体",
                    "裂隙和虚空风暴命令");
            case WORLDSTATE_SORTIE -> referenceMetadata("WorldState 突击静态代码簿",
                    "突击和执刑官猎杀命令");
            case WORLDSTATE_LANGUAGE -> referenceMetadata("WorldState 语言词条",
                    "所有 WorldState 内部路径清洗和显示");
            case FISH_GUIDE -> referenceMetadata("鱼类攻略", "已导入资料，尚无命令消费者");
            case MAP_FEATURE -> referenceMetadata("地图标注", "已导入资料，尚无命令消费者");
            case RELIC -> referenceMetadata("遗物主数据", "遗物、金垃圾、银垃圾");
            case RELIC_REWARD -> referenceMetadata("遗物奖励", "遗物、金垃圾、银垃圾");
            case RELIC_DROP -> referenceMetadata("遗物掉落", "已导入资料，尚无命令消费者");
            case RIVEN_UNROLLABLE -> referenceMetadata("不可展开紫卡词条", "已导入资料，尚无命令消费者");
            case WORLDSTATE_RULE -> referenceMetadata("WorldState 静态规则",
                    "已导入资料；运行时引用解析读取专用代码簿，不读取此汇总表");
        };
    }

    private static Metadata metadata(
            String tableName,
            String sourceType,
            String description,
            String consumerCommands,
            boolean refreshable,
            boolean manageable) {
        return new Metadata(tableName, sourceType, description, consumerCommands,
                refreshable, manageable, false);
    }

    private static Metadata metadata(
            String tableName,
            String sourceType,
            String description,
            String consumerCommands,
            boolean refreshable,
            boolean manageable,
            boolean readOnly) {
        return new Metadata(tableName, sourceType, description, consumerCommands,
                refreshable, manageable, readOnly);
    }

    private Metadata referenceMetadata(String description, String consumerCommands) {
        return metadata("wf_community_" + code, "COMMUNITY", description,
                consumerCommands, true, true);
    }

    public static WarframeCommunityDataset parse(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("社区数据集不能为空");
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        for (WarframeCommunityDataset dataset : values()) {
            if (dataset.code.equals(normalized) || dataset.fileName.equals(normalized)
                    || dataset.name().toLowerCase(Locale.ROOT).equals(normalized)) {
                return dataset;
            }
        }
        throw new IllegalArgumentException("不支持的社区数据集：" + value);
    }
}
