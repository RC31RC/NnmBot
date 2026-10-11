package com.nnm.bot.warframe.constant;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import com.nnm.bot.warframe.model.entity.WarframeCommunityDatasetRecord;

/**
 * 社区静态数据的来源登记。
 *
 * <p>Raw URL 由 {@code application-bot.yml} 配置；枚举只登记稳定代号、数据集和实体映射，
 * 数据库保存代号而不保存易变的 URL。</p>
 */
public enum WarframeCommunitySource {
    PLUS_BOUNTIES("warframe-public-export-plus:ExportBounties.json", "bounty_definition",
            "warframe-public-export-plus"),
    PLUS_TRANSLATION_ZH("warframe-public-export-plus:dict.zh.json", "translation",
            "warframe-public-export-plus 中文语言字典"),
    PLUS_TRANSLATION_EN("warframe-public-export-plus:dict.en.json", "translation",
            "warframe-public-export-plus 英文语言字典"),
    KINGPRIMES_TERM("kingprimes:warframe/term.json", "term", "KingPrimes/DataSource"),
    KINGPRIMES_ALIAS("kingprimes:warframe/alias.json", "alias", "KingPrimes/DataSource"),
    KINGPRIMES_NODES("kingprimes:warframe/nodes.json", "nodes", "KingPrimes/DataSource"),
    KINGPRIMES_REWARD_POOL("kingprimes:warframe/reward_pool.json", "reward_pool", "KingPrimes/DataSource"),
    PLUS_REWARDS("warframe-public-export-plus:ExportRewards.json", "reward_pool",
            "warframe-public-export-plus"),
    WF_INFO_API_REWARDS("warframe-info-api:reward_pool_local.json", "reward_pool", "warframe-info-api"),
    KINGPRIMES_STATE_TRANSLATION("kingprimes:warframe/state_translation.json", "state_translation",
            "KingPrimes/DataSource"),
    KINGPRIMES_MARKET_ELEMENT("kingprimes:warframe/market_element.json", "market_element",
            "KingPrimes/DataSource"),
    KINGPRIMES_MARKET_RIVEN_TION("kingprimes:warframe/market_riven_tion.json", "market_riven_tion",
            "KingPrimes/DataSource"),
    KINGPRIMES_MARKET_RIVEN_TION_ALIAS("kingprimes:warframe/market_riven_tion_alias.json",
            "market_riven_tion_alias", "KingPrimes/DataSource"),
    KINGPRIMES_RIVEN_ANALYSE_TREND("kingprimes:warframe/riven_analyse_trend.json",
            "riven_analyse_trend", "KingPrimes/DataSource"),
    KINGPRIMES_RIVEN_TREND("kingprimes:warframe/riven_trend.json", "riven_trend",
            "KingPrimes/DataSource"),
    WARFRAME_WORLDSTATE_MISSION_TYPE("warframe-worldstate-data:data/zh/missionTypes.json",
            "worldstate_mission_type", "warframe-worldstate-data"),
    WARFRAME_WORLDSTATE_NODE("warframe-worldstate-data:data/zh/solNodes.json",
            "worldstate_node", "warframe-worldstate-data"),
    WARFRAME_WORLDSTATE_FACTION("warframe-worldstate-data:data/zh/factionsData.json",
            "worldstate_faction", "warframe-worldstate-data"),
    WARFRAME_WORLDSTATE_SYNDICATE("warframe-worldstate-data:data/zh/syndicatesData.json",
            "worldstate_syndicate", "warframe-worldstate-data"),
    WARFRAME_WORLDSTATE_OPERATION_TYPE("warframe-worldstate-data:data/zh/operationTypes.json",
            "worldstate_operation_type", "warframe-worldstate-data"),
    WARFRAME_WORLDSTATE_UPGRADE_TYPE("warframe-worldstate-data:data/zh/upgradeTypes.json",
            "worldstate_upgrade_type", "warframe-worldstate-data"),
    WARFRAME_WORLDSTATE_PERSISTENT_ENEMY("warframe-worldstate-data:data/zh/persistentEnemyData.json",
            "worldstate_persistent_enemy", "warframe-worldstate-data"),
    WARFRAME_WORLDSTATE_FISSURE_MODIFIER("warframe-worldstate-data:data/zh/fissureModifiers.json",
            "worldstate_fissure_modifier", "warframe-worldstate-data"),
    WARFRAME_WORLDSTATE_SORTIE("warframe-worldstate-data:data/zh/sortieData.json",
            "worldstate_sortie", "warframe-worldstate-data"),
    WARFRAME_WORLDSTATE_LANGUAGE("warframe-worldstate-data:data/zh/languages.json",
            "worldstate_language", "warframe-worldstate-data"),
    WARFRAME_HUB_FISH("warframe-hub:data/json/fish", "fish_guide", "warframe-hub"),
    WARFRAME_HUB_MAP("warframe-hub:data/json/geo", "map_feature", "warframe-hub"),
    WARFRAME_HUB_FISH_POE("warframe-hub:data/json/fish/poe.json", "fish_guide", "warframe-hub"),
    WARFRAME_HUB_FISH_VALLIS("warframe-hub:data/json/fish/vallis.json", "fish_guide", "warframe-hub"),
    WARFRAME_HUB_FISH_DEIMOS("warframe-hub:data/json/fish/deimos.json", "fish_guide", "warframe-hub"),
    WARFRAME_HUB_GEO_VALLIS_TOROID_CAVE(
            "warframe-hub:data/json/geo/vallis/toroidcave.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_VALLIS_LABELS(
            "warframe-hub:data/json/geo/vallis/labels.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_VALLIS_TOROIDS(
            "warframe-hub:data/json/geo/vallis/toroids.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_VALLIS_FISHING_RECOMMEND(
            "warframe-hub:data/json/geo/vallis/fishing-recommend.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_VALLIS_TOROID_FISH_CAVE(
            "warframe-hub:data/json/geo/vallis/toroidfishcave.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_VALLIS_MEMORY_FRAG(
            "warframe-hub:data/json/geo/vallis/memoryfrag.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_VALLIS_FISH_CAVE(
            "warframe-hub:data/json/geo/vallis/fishcave.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_VALLIS_FISHING(
            "warframe-hub:data/json/geo/vallis/fishing.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_VALLIS_KDRIVE(
            "warframe-hub:data/json/geo/vallis/kdrive.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_VALLIS_SOMACHORD(
            "warframe-hub:data/json/geo/vallis/somachord.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_VALLIS_MINING_RECOMMEND(
            "warframe-hub:data/json/geo/vallis/mining-recommend.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_DEIMOS_LABELS(
            "warframe-hub:data/json/geo/deimos/labels.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_DEIMOS_NECRAMECH(
            "warframe-hub:data/json/geo/deimos/necramech.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_DEIMOS_CAVE(
            "warframe-hub:data/json/geo/deimos/cave.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_DEIMOS_TELEPORTER(
            "warframe-hub:data/json/geo/deimos/teleporter.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_DEIMOS_BOUNTY(
            "warframe-hub:data/json/geo/deimos/bounty.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_DEIMOS_KDRIVE(
            "warframe-hub:data/json/geo/deimos/kdrive.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_PLAINS_LABELS(
            "warframe-hub:data/json/geo/plains/labels.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_PLAINS_LURE(
            "warframe-hub:data/json/geo/plains/lure.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_PLAINS_GRINEER(
            "warframe-hub:data/json/geo/plains/grineer.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_PLAINS_LOREFISH(
            "warframe-hub:data/json/geo/plains/lorefish.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_PLAINS_CAVE(
            "warframe-hub:data/json/geo/plains/cave.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_PLAINS_WISP(
            "warframe-hub:data/json/geo/plains/wisp.json", "map_feature", "warframe-hub"),
    WARFRAME_HUB_GEO_PLAINS_FISHING(
            "warframe-hub:data/json/geo/plains/fishing.json", "map_feature", "warframe-hub"),
    WARFRAME_RELIC_DATA("warframe-relic-data:data/Relics.json", "relic", "warframe-relic-data"),
    WARFRAME_RELIC_REWARD_DATA("warframe-relic-data:data/Relics.json", "relic_reward",
            "warframe-relic-data"),
    WARFRAME_RELIC_DROP_DATA("warframe-relic-data:data/Relics.json", "relic_drop",
            "warframe-relic-data"),
    WARFRAME_PUBLIC_EXPORT_PLUS("warframe-public-export-plus:supplementals/riven_unrollables.json",
            "riven_unrollable", "warframe-public-export-plus"),
    WF_INFO_API_RULE("warframe-info-api:api/dataSource/state_translation_local.json",
            "worldstate_rule", "warframe-info-api"),
    NNMBOT_MARKET_COMPONENT("nnmbot:market_component", "market_component", "NnmBot 自维护规则"),
    WF_INFO_API_ARBITRATION_ESSENCE("warframe-info-api:routes/arbitrationEssence.js",
            "arbitration_essence", "warframe-info-api 精华/小时规则"),
    BROWSE_WF_ARBITRATION("browse.wf:arbys.txt", "arbitration_schedule", "browse.wf");

    private final String code;
    private final String datasetCode;
    private final String description;

    WarframeCommunitySource(String code, String datasetCode, String description) {
        this.code = code;
        this.datasetCode = datasetCode;
        this.description = description;
    }

    public String code() {
        return code;
    }

    public String datasetCode() {
        return datasetCode;
    }

    public String description() {
        return description;
    }

    /** 仲裁排期使用独立业务表，但沿用社区数据集刷新、预览和合并流程。 */
    public boolean isDedicatedSource() {
        return false;
    }

    public Class<? extends WarframeCommunityDatasetRecord> entityType() {
        return WarframeCommunityDataset.parse(datasetCode).entityType();
    }

    public static String defaultCode(String datasetCode) {
        List<String> codes = forDataset(datasetCode).stream()
                .map(WarframeCommunitySource::code)
                .distinct()
                .toList();
        if (codes.isEmpty()) {
            throw new IllegalArgumentException("未登记社区数据来源：" + datasetCode);
        }
        if (codes.size() != 1) {
            throw new IllegalArgumentException(
                    "数据集存在多个来源，必须由调用方明确来源：" + datasetCode);
        }
        return codes.getFirst();
    }

    public static List<WarframeCommunitySource> forDataset(String datasetCode) {
        return Arrays.stream(values())
                .filter(source -> source.datasetCode.equals(datasetCode))
                .toList();
    }

    public static String sourceCodes(String datasetCode) {
        return forDataset(datasetCode).stream()
                .map(WarframeCommunitySource::code)
                .distinct()
                .collect(Collectors.joining(", "));
    }

    /**
     * 聚合管理端展示用的来源说明，并保持枚举登记顺序。
     *
     * <p>来源代号和说明一起展示，避免多个参考项目使用同一业务表时失去追溯信息。</p>
     */
    public static String descriptionText(String datasetCode) {
        return forDataset(datasetCode).stream()
                .map(source -> source.code() + " (" + source.description() + ")")
                .distinct()
                .collect(Collectors.joining("; "));
    }

    public static WarframeCommunitySource parse(String value) {
        String normalized = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(source -> source.code.toLowerCase(Locale.ROOT).equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未登记社区数据来源：" + value));
    }

    /**
     * 按数据集解析来源。多个数据集可以共用同一个上游文件，但来源身份仍必须
     * 保留各自的数据集代号，不能按枚举顺序选取第一个匹配项。
     */
    public static WarframeCommunitySource parse(String datasetCode, String value) {
        String normalizedDataset = datasetCode.trim().equalsIgnoreCase("arbitration_schedule")
                ? "arbitration_schedule" : WarframeCommunityDataset.parse(datasetCode).code();
        String normalizedSource = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        return forDataset(normalizedDataset).stream()
                .filter(source -> source.code.toLowerCase(Locale.ROOT).equals(normalizedSource))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "数据集未登记社区数据来源：" + datasetCode + " / " + value));
    }
}
