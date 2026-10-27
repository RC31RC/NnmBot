package com.nnm.bot.warframe.persistent.official.constant;

import java.util.Arrays;
import java.util.List;

/** 固定版本成品的业务领域；完整路径与发布契约一致，不传播生产者的采集文件身份。 */
public enum WarframeOfficialDataDomain {
    RECIPE("crafting/recipes.json", "制作配方", false),
    DOJO_RECIPE("crafting/dojo-recipes.json", "道场配方", false),
    ABILITY("equipment/abilities.json", "技能", true),
    ARCHWING("equipment/archwings.json", "Archwing", true),
    ANIMAL("equipment/companions-animals.json", "动物同伴与保育生物", true),
    SENTINELS("equipment/companions-sentinels.json", "机械同伴", true),
    GEAR("equipment/gear.json", "消耗道具", true),
    RAILJACK_WEAPON("equipment/railjack-weapons.json", "九重天武器", true),
    DRONES("equipment/resource-drones.json", "资源无人机", true),
    SHIP("equipment/ships.json", "飞船", true),
    WARFRAME("equipment/warframes.json", "战甲", true),
    WEAPON("equipment/weapons.json", "武器", true),
    AYATAN("items/ayatan-treasures.json", "阿耶檀识珍宝", true),
    BOOSTER("items/boosters.json", "增幅器", true),
    BUNDLE("items/bundles.json", "礼包", true),
    CAPTURA("items/captura-scenes.json", "Captura 场景", true),
    COMPONENT("items/components.json", "制造部件", true),
    CREDIT_BUNDLE("items/credit-bundles.json", "现金礼包", true),
    CUSTOMS("items/customizations.json", "外观定制", true),
    DECORATION("items/decorations.json", "装饰", true),
    FUSION_BUNDLES("items/endo-bundles.json", "内融核心包", true),
    INTERNAL_DEFINITION("items/internal-definitions.json", "内部定义", false),
    MISC("items/misc-items.json", "杂项", true),
    RESOURCES("items/resources.json", "资源", true),
    REWARD_PACK("items/reward-packs.json", "随机奖励包", true),
    SKIN("items/skins.json", "外观", true),
    SONG("items/songs.json", "歌曲", true),
    SUPPLY_DROP("items/supply-drops.json", "补给", true),
    MARKET_ITEM("market/trade-items.json", "市场商品", false),
    ACHIEVEMENT("progression/achievements.json", "成就", false),
    CHALLENGE("progression/challenges.json", "挑战", false),
    INTRINSIC("progression/intrinsics.json", "内源之力", false),
    NIGHTWAVE("progression/nightwave.json", "午夜电波静态定义", false),
    IMAGE("reference/images.json", "图片引用", false),
    ITEM_LOCALIZATION("reference/item-localizations.json", "物品双语映射", false),
    ABILITY_OWNER_LOCALIZATION("reference/ability-owner-localizations.json", "技能拥有者双语映射", false),
    UPGRADE_LOCALIZATION("reference/upgrade-localizations.json", "升级多行双语映射", false),
    LANGUAGE_STRING("reference/language-strings.json", "双语语言字典", false),
    TEXT_ICON("reference/text-icons.json", "文本图标", false),
    BOUNTY("rewards/bounties.json", "赏金定义与奖励池", false),
    DROP_TABLE("rewards/drop-tables.json", "掉落表", false),
    RELIC("rewards/relics.json", "虚空遗物", true),
    REWARD_POOL("rewards/reward-pools.json", "奖励池", false),
    ARCANE("upgrades/arcanes.json", "赋能", true),
    AVIONIC("upgrades/avionics.json", "航电", true),
    FOCUS("upgrades/focus-upgrades.json", "专精", true),
    MOD_SET("upgrades/mod-sets.json", "MOD 套装", true),
    UPGRADE("upgrades/mods.json", "MOD", true),
    RIVEN_ATTRIBUTE("upgrades/riven-attributes.json", "裂罅属性", false),
    RIVEN_DEFINITION("upgrades/riven-definitions.json", "裂罅定义", false),
    RIVEN_WEAPON("upgrades/riven-weapons.json", "裂罅武器完整映射", false),
    CODEX("world/codex.json", "资料库", false),
    ENEMY("world/enemies.json", "敌人", false),
    INBOX("world/inbox-messages.json", "邮件", false),
    KEYS("world/quest-keys.json", "任务钥匙", true),
    REGION("world/star-chart.json", "星图节点", true),
    TILESET("world/tilesets.json", "场景", false),
    VENDOR("world/vendors.json", "商人目录", false),
    ACTIVITY("worldstate/activities.json", "科研与下降静态定义", false),
    BOSS("worldstate/bosses.json", "执刑官", false),
    FACTION("worldstate/factions.json", "阵营", false),
    FISSURE_MODIFIER("worldstate/fissure-modifiers.json", "裂缝", false),
    MISSION_TYPE("worldstate/mission-types.json", "任务类型", false),
    SYNDICATE("worldstate/syndicates.json", "集团", false),
    UPGRADE_TYPE("worldstate/upgrade-types.json", "全局增益", false),
    USAGE_2020("statistics/usage-2020.json", "2020 使用统计", false),
    USAGE_2021("statistics/usage-2021.json", "2021 使用统计", false),
    USAGE_2022("statistics/usage-2022.json", "2022 使用统计", false),
    USAGE_2023("statistics/usage-2023.json", "2023 使用统计", false),
    USAGE_2024("statistics/usage-2024.json", "2024 使用统计", false),
    USAGE_2025("statistics/usage-2025.json", "2025 使用统计", false),
    OTHER("", "未识别领域", false);

    private final String productPath;
    private final String label;
    private final boolean itemIndex;

    /**
     * 构造官方数据业务领域，并保存初始化参数。
     *
     * @param productPath 官方 JSON 文件在发布包内的相对路径。
     * @param label 标签标题。
     * @param itemIndex 物品索引。
     */
    WarframeOfficialDataDomain(String productPath, String label, boolean itemIndex) {
        this.productPath = productPath;
        this.label = label;
        this.itemIndex = itemIndex;
    }

    /**
     * 读取当前官方 JSON 在应用中的稳定领域代码。
     * @return 官方文件在应用中的稳定领域代码。
     */
    public String domainCode() {
        return name();
    }

    /**
     * 读取当前官方 JSON 在发布包内的相对路径。
     * @return 官方 JSON 在发布包内的相对路径。
     */
    public String productPath() {
        return productPath;
    }

    /**
     * 读取当前代码对应的展示名称。
     *
     * @return 当前代码对应的展示名称对应的文本。
     */
    public String label() {
        return label;
    }

    /**
     * 判断是否满足物品索引条件。
     *
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    public boolean itemIndex() {
        return itemIndex;
    }

    /**
     * 取得当前合同声明的业务路径。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public static List<String> paths() {
        return Arrays.stream(values()).filter(domain -> domain != OTHER)
                .map(WarframeOfficialDataDomain::productPath).sorted().toList();
    }

    /**
     * 按发布包相对路径查找官方 JSON 领域，未登记路径归为未知领域。
     * @param path 当前文件或业务资源的路径。
     * @return 游戏资料官方数据业务领域对象。
     */
    public static WarframeOfficialDataDomain fromPath(String path) {
        return Arrays.stream(values()).filter(domain -> domain != OTHER && domain.productPath().equals(path))
                .findFirst().orElse(null);
    }

    /**
     * 按应用领域代码查找官方 JSON 领域，未登记代码归为未知领域。
     * @param code 当前数据集或业务对象的稳定代码。
     * @return 游戏资料官方数据业务领域对象。
     */
    public static WarframeOfficialDataDomain fromCode(String code) {
        return Arrays.stream(values()).filter(domain -> domain.name().equalsIgnoreCase(code))
                .findFirst().orElse(OTHER);
    }

    /**
     * 列出所有已登记官方 JSON 的稳定领域代码。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public static List<String> knownCodes() {
        return Arrays.stream(values()).filter(domain -> domain != OTHER)
                .map(WarframeOfficialDataDomain::domainCode).toList();
    }

    /**
     * 列出需要加入物品搜索索引的官方数据领域。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    public static List<String> itemIndexCodes() {
        return Arrays.stream(values()).filter(domain -> domain != OTHER && domain.itemIndex())
                .map(WarframeOfficialDataDomain::domainCode).toList();
    }
}
