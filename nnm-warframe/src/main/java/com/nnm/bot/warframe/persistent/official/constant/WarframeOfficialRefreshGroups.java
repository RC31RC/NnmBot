package com.nnm.bot.warframe.persistent.official.constant;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** 依字段矩阵扩展一次关联刷新，文件 hash 决定其中哪些内容需要重新解析。 */
public final class WarframeOfficialRefreshGroups {
    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private WarframeOfficialRefreshGroups() {
    }

    private static final Map<String, Set<String>> GROUPS = Map.ofEntries(
            Map.entry("crafting/dojo-recipes.json", Set.of("CRAFTING_REWARDS")),
            Map.entry("crafting/recipes.json", Set.of("CRAFTING_REWARDS")),
            Map.entry("equipment/abilities.json", Set.of("CATALOG_INDEX")),
            Map.entry("equipment/archwings.json", Set.of("CATALOG_INDEX")),
            Map.entry("equipment/companions-animals.json", Set.of("CATALOG_INDEX")),
            Map.entry("equipment/companions-sentinels.json", Set.of("CATALOG_INDEX")),
            Map.entry("equipment/gear.json", Set.of("CATALOG_INDEX")),
            Map.entry("equipment/railjack-weapons.json", Set.of("CATALOG_INDEX")),
            Map.entry("equipment/resource-drones.json", Set.of("CATALOG_INDEX")),
            Map.entry("equipment/ships.json", Set.of("CATALOG_INDEX")),
            Map.entry("equipment/warframes.json", Set.of("CATALOG_INDEX")),
            Map.entry("equipment/weapons.json", Set.of("CATALOG_INDEX","RIVEN_MARKET_CATALOG")),
            Map.entry("items/ayatan-treasures.json", Set.of("CATALOG_INDEX")),
            Map.entry("items/boosters.json", Set.of("CATALOG_INDEX")),
            Map.entry("items/bundles.json", Set.of("CATALOG_INDEX")),
            Map.entry("items/captura-scenes.json", Set.of("CATALOG_INDEX")),
            Map.entry("items/components.json", Set.of("CATALOG_INDEX")),
            Map.entry("items/credit-bundles.json", Set.of("CATALOG_INDEX")),
            Map.entry("items/customizations.json", Set.of("CATALOG_INDEX")),
            Map.entry("items/decorations.json", Set.of("CATALOG_INDEX")),
            Map.entry("items/endo-bundles.json", Set.of("CATALOG_INDEX")),
            Map.entry("items/internal-definitions.json", Set.of("CATALOG_INDEX")),
            Map.entry("items/misc-items.json", Set.of("CATALOG_INDEX")),
            Map.entry("items/resources.json", Set.of("CATALOG_INDEX")),
            Map.entry("items/reward-packs.json", Set.of("CATALOG_INDEX")),
            Map.entry("items/skins.json", Set.of("CATALOG_INDEX")),
            Map.entry("items/songs.json", Set.of("CATALOG_INDEX")),
            Map.entry("items/supply-drops.json", Set.of("CATALOG_INDEX")),
            Map.entry("market/trade-items.json", Set.of("CATALOG_INDEX","RIVEN_MARKET_CATALOG")),
            Map.entry("progression/achievements.json", Set.of("FILE_LOCAL")),
            Map.entry("progression/challenges.json", Set.of("FILE_LOCAL")),
            Map.entry("progression/intrinsics.json", Set.of("FILE_LOCAL")),
            Map.entry("progression/nightwave.json", Set.of("WORLDSTATE_CODEBOOK")),
            Map.entry("reference/images.json", Set.of("CATALOG_INDEX")),
            Map.entry("reference/item-localizations.json", Set.of("CATALOG_INDEX")),
            Map.entry("reference/language-strings.json", Set.of("CATALOG_INDEX")),
            Map.entry("reference/ability-owner-localizations.json", Set.of("CATALOG_INDEX")),
            Map.entry("reference/upgrade-localizations.json", Set.of("CATALOG_INDEX")),
            Map.entry("reference/text-icons.json", Set.of("CATALOG_INDEX")),
            Map.entry("rewards/bounties.json", Set.of("CRAFTING_REWARDS")),
            Map.entry("rewards/drop-tables.json", Set.of("CRAFTING_REWARDS")),
            Map.entry("rewards/relics.json", Set.of("CRAFTING_REWARDS")),
            Map.entry("rewards/reward-pools.json", Set.of("CRAFTING_REWARDS")),
            Map.entry("statistics/usage-2020.json", Set.of("FILE_LOCAL")),
            Map.entry("statistics/usage-2021.json", Set.of("FILE_LOCAL")),
            Map.entry("statistics/usage-2022.json", Set.of("FILE_LOCAL")),
            Map.entry("statistics/usage-2023.json", Set.of("FILE_LOCAL")),
            Map.entry("statistics/usage-2024.json", Set.of("FILE_LOCAL")),
            Map.entry("statistics/usage-2025.json", Set.of("FILE_LOCAL")),
            Map.entry("upgrades/arcanes.json", Set.of("CATALOG_INDEX")),
            Map.entry("upgrades/avionics.json", Set.of("CATALOG_INDEX")),
            Map.entry("upgrades/focus-upgrades.json", Set.of("CATALOG_INDEX")),
            Map.entry("upgrades/mod-sets.json", Set.of("CATALOG_INDEX")),
            Map.entry("upgrades/mods.json", Set.of("CATALOG_INDEX")),
            Map.entry("upgrades/riven-attributes.json", Set.of("CATALOG_INDEX","RIVEN_MARKET_CATALOG")),
            Map.entry("upgrades/riven-definitions.json", Set.of("CATALOG_INDEX","RIVEN_MARKET_CATALOG")),
            Map.entry("upgrades/riven-weapons.json", Set.of("CATALOG_INDEX","RIVEN_MARKET_CATALOG")),
            Map.entry("world/codex.json", Set.of("WORLDSTATE_CODEBOOK")),
            Map.entry("world/enemies.json", Set.of("WORLDSTATE_CODEBOOK")),
            Map.entry("world/inbox-messages.json", Set.of("WORLDSTATE_CODEBOOK")),
            Map.entry("world/quest-keys.json", Set.of("WORLDSTATE_CODEBOOK")),
            Map.entry("world/star-chart.json", Set.of("WORLDSTATE_CODEBOOK")),
            Map.entry("world/tilesets.json", Set.of("WORLDSTATE_CODEBOOK")),
            Map.entry("world/vendors.json", Set.of("WORLDSTATE_CODEBOOK")),
            Map.entry("worldstate/activities.json", Set.of("WORLDSTATE_CODEBOOK")),
            Map.entry("worldstate/bosses.json", Set.of("WORLDSTATE_CODEBOOK")),
            Map.entry("worldstate/factions.json", Set.of("WORLDSTATE_CODEBOOK")),
            Map.entry("worldstate/fissure-modifiers.json", Set.of("WORLDSTATE_CODEBOOK")),
            Map.entry("worldstate/mission-types.json", Set.of("WORLDSTATE_CODEBOOK")),
            Map.entry("worldstate/syndicates.json", Set.of("WORLDSTATE_CODEBOOK")),
            Map.entry("worldstate/upgrade-types.json", Set.of("WORLDSTATE_CODEBOOK")));

    /**
     * 取得当前合同声明的业务路径。
     *
     * @param requested 请求的。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public static Set<String> paths(String requested) {
        Set<String> groups = GROUPS.get(requested);
        if (groups == null) throw new IllegalArgumentException("未登记的官方文件：" + requested);
        return GROUPS.entrySet().stream().filter(entry -> entry.getKey().equals(requested)
                        || entry.getValue().stream().anyMatch(groups::contains))
                .map(Map.Entry::getKey).collect(Collectors.toUnmodifiableSet());
    }
}
