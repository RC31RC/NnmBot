package com.nnm.bot.warframe.constant;

import com.nnm.bot.core.plugin.api.CommandSpecification;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 本模块命令的统一定义：稳定编码、中文名称与排序。
 *
 * <p>
 * 注解必须使用编译期字符串常量，因此保留本类编码字段；对应元数据在同文件的 {@link Definition} 中维护。 Provider 仅负责将此目录注册给 Core，不再维护第二份名称映射。
 * </p>
 */
public final class WarframeCommandCodes {
    public static final String GAME_CODE = "warframe";

    public static final String ALERTS = "alerts";
    public static final String ARBITRATION = "arbitration";
    public static final String ARBITRATION_TABLE = "arbitration-table";
    public static final String TODAY_ARBITRATION = "today-arbitration";
    public static final String EFFICIENT_ARBITRATION = "efficient-arbitration";
    public static final String ARCHON_HUNTS = "archon-hunts";
    public static final String ARCHIMEDEAS = "archimedeas";
    public static final String DEEP_ARCHIMEDEAS = "deep-archimedeas";
    public static final String HEX_ARCHIMEDEAS = "hex-archimedeas";
    public static final String DESCENTS = "descents";
    public static final String CALENDAR_1999 = "calendar-1999";
    public static final String CONCLAVE = "conclave";
    public static final String CONCLAVE_RULES = "conclave-rules";
    public static final String ARCHON_SHARDS = "archon-shards";
    public static final String STEEL_PATH_CATALOG = "steel-path-catalog";
    public static final String EVENT_RULES = "event-rules";
    public static final String TUTORIALS = "tutorials";
    public static final String ARCANE_GUIDES = "arcane-guides";
    public static final String NEWS = "news";
    public static final String FLASH_SALES = "flash-sales";
    public static final String PERSISTENT_ENEMIES = "persistent-enemies";
    public static final String SYNDICATE_MISSIONS = "syndicate-missions";
    public static final String CONSTRUCTION_PROGRESS = "construction-progress";
    public static final String SIMARIS = "simaris";
    public static final String SENTIENT_OUTPOSTS = "sentient-outposts";
    public static final String CLAN_INITIATIVE = "clan-initiative";
    public static final String CIRCUIT_NORMAL = "circuit-normal";
    public static final String CIRCUIT_HARD = "circuit-hard";
    public static final String CALENDAR = "calendar";
    public static final String VAULT_TRADER = "vault-trader";
    public static final String TIMESTAMP = "timestamp";
    public static final String CAVIA = "cavia";
    public static final String CYCLES = "cycles";
    public static final String DAILY_DEALS = "daily-deals";
    public static final String DUVIRI_CYCLE = "duviri-cycle";
    public static final String ENTRATI = "entrati";
    public static final String EVENTS = "events";
    public static final String FISSURES = "fissures";
    public static final String FORUM_LOADOUT = "forum-loadout";
    public static final String GOLD_TRASH = "gold-trash";
    public static final String GLOBAL_UPGRADES = "global-upgrades";
    public static final String INVASIONS = "invasions";
    public static final String LICHES = "liches";
    public static final String MARKET = "market";
    public static final String MARKET_RIVEN = "market-riven";
    public static final String NIGHTWAVE = "nightwave";
    public static final String NIGHTWAVE_DEFINITIONS = "nightwave-definitions";
    public static final String VENDOR_DEFINITIONS = "vendor-definitions";
    public static final String ENEMIES = "enemies";
    public static final String LEGACY_ITEMS = "legacy-items";
    public static final String RIVEN_ATTRIBUTES = "riven-attributes";
    public static final String MARKET_CATALOG = "market-catalog";
    public static final String OSTRONS = "ostrons";
    public static final String PERRIN_SEQUENCE = "perrin-sequence";
    public static final String RELICS = "relics";
    public static final String RIVEN_ANALYSE = "riven-analyse";
    public static final String RIVEN_WEEKLY = "riven-weekly";
    public static final String RIVEN_DEFINITIONS = "riven-definitions";
    public static final String RIVEN_UNROLLABLES = "riven-unrollables";
    public static final String SILVER_TRASH = "silver-trash";
    public static final String SISTERS = "sisters";
    public static final String SOLARIS = "solaris";
    public static final String SORTIES = "sorties";
    public static final String STEEL_FISSURES = "steel-fissures";
    public static final String STEEL_PATH = "steel-path";
    public static final String SUBSCRIBE = "subscribe";
    public static final String WATCH_LIST = "watch-list";
    public static final String TRANSLATION = "translation";
    public static final String UNSUBSCRIBE = "unsubscribe";
    public static final String UPDATE_SISTER = "update-sister";
    public static final String UPDATE_LICHES = "update-liches";
    public static final String UPDATE_PERRIN_SEQUENCE = "update-perrin-sequence";
    public static final String VOID_STORMS = "void-storms";
    public static final String VOID_TRADER = "void-trader";
    public static final String HOLDFASTS = "holdfasts";
    public static final String HEX_BOUNTIES = "hex-bounties";
    public static final String WIKI = "wiki";
    public static final String WARFRAMES = "warframes";
    public static final String ARCHWING = "archwing";
    public static final String WEAPONS = "weapons";
    public static final String ABILITIES = "abilities";
    public static final String MODS = "mods";
    public static final String ARCANES = "arcanes";
    public static final String MOD_SETS = "mod-sets";
    public static final String RECIPES = "recipes";
    public static final String REWARD_POOLS = "reward-pools";
    public static final String SYNTH_TARGETS = "synth-targets";
    public static final String FISH_GUIDE = "fish-guide";
    public static final String MAP_FEATURES = "map-features";
    public static final String RESOURCES = "resources";
    public static final String FISH = "fish";
    public static final String GEAR = "gear";
    public static final String MISC = "misc";
    public static final String QUESTS = "quests";
    public static final String CUSTOMS = "customs";
    public static final String COMPANIONS = "companions";
    public static final String ANIMALS = "animals";
    public static final String DRONES = "drones";
    public static final String FLAVOURS = "flavours";
    public static final String FUSION_BUNDLES = "fusion-bundles";
    public static final String BOOSTERS = "boosters";
    public static final String CREDIT_BUNDLES = "credit-bundles";
    public static final String BUNDLES = "bundles";
    public static final String BOOSTER_PACKS = "booster-packs";
    public static final String BOUNTY_DEFINITIONS = "bounty-definitions";
    public static final String KEYS = "keys";
    public static final String EMAIL_ITEMS = "email-items";
    public static final String ACHIEVEMENTS = "achievements";
    public static final String INTRINSICS = "intrinsics";
    public static final String CODEX_OBJECTS = "codex-objects";
    public static final String CODEX_LORE = "codex-lore";
    public static final String CODEX_SONGS = "codex-songs";
    public static final String CODEX_FIGHTERS = "codex-fighters";
    public static final String VIRTUALS = "virtuals";
    public static final String CHALLENGES = "challenges";
    public static final String SYSTEMS = "systems";
    public static final String TILESETS = "tilesets";
    public static final String ITEM_COMPONENTS = "item-components";
    public static final String ITEM_DROPS = "item-drops";

    /** 强类型命令定义，统一维护编码、中文名称与排序。 */
    @Getter
    @Accessors(fluent = true)
    public enum Definition implements CommandSpecification {
        MARKET(WarframeCommandCodes.MARKET, "市场", 20), WIKI(WarframeCommandCodes.WIKI, "Wiki", 20),
        FORUM_LOADOUT(WarframeCommandCodes.FORUM_LOADOUT, "wf配装", 21),
        ARBITRATION(WarframeCommandCodes.ARBITRATION, "仲裁", 30), ALERTS(WarframeCommandCodes.ALERTS, "警报", 40),
        SORTIES(WarframeCommandCodes.SORTIES, "突击", 41), ARCHON_HUNTS(WarframeCommandCodes.ARCHON_HUNTS, "执刑官猎杀", 42),
        VOID_TRADER(WarframeCommandCodes.VOID_TRADER, "奸商", 43),
        ARBITRATION_TABLE(WarframeCommandCodes.ARBITRATION_TABLE, "仲裁表", 45),
        DAILY_DEALS(WarframeCommandCodes.DAILY_DEALS, "每日特惠", 46),
        TODAY_ARBITRATION(WarframeCommandCodes.TODAY_ARBITRATION, "今日仲裁", 46),
        EFFICIENT_ARBITRATION(WarframeCommandCodes.EFFICIENT_ARBITRATION, "高效仲裁", 47),
        INVASIONS(WarframeCommandCodes.INVASIONS, "入侵", 47), FISSURES(WarframeCommandCodes.FISSURES, "裂隙", 48),
        VOID_STORMS(WarframeCommandCodes.VOID_STORMS, "九重天裂隙", 49),
        STEEL_FISSURES(WarframeCommandCodes.STEEL_FISSURES, "钢铁裂隙", 50),
        STEEL_PATH(WarframeCommandCodes.STEEL_PATH, "钢铁奖励", 51), CYCLES(WarframeCommandCodes.CYCLES, "平原", 52),
        OSTRONS(WarframeCommandCodes.OSTRONS, "希图斯", 53), ENTRATI(WarframeCommandCodes.ENTRATI, "英择谛", 54),
        SOLARIS(WarframeCommandCodes.SOLARIS, "索拉里斯", 55), DUVIRI_CYCLE(WarframeCommandCodes.DUVIRI_CYCLE, "双衍王境", 56),
        NIGHTWAVE(WarframeCommandCodes.NIGHTWAVE, "电波", 57), ARCHIMEDEAS(WarframeCommandCodes.ARCHIMEDEAS, "科研", 58),
        DEEP_ARCHIMEDEAS(WarframeCommandCodes.DEEP_ARCHIMEDEAS, "深层科研", 59),
        HEX_ARCHIMEDEAS(WarframeCommandCodes.HEX_ARCHIMEDEAS, "时光科研", 60),
        UPDATE_SISTER(WarframeCommandCodes.UPDATE_SISTER, "更新信条", 60), CAVIA(WarframeCommandCodes.CAVIA, "科维兽赏金", 61),
        CONCLAVE(WarframeCommandCodes.CONCLAVE, "竞技场", 61), DESCENTS(WarframeCommandCodes.DESCENTS, "深层下降", 61),
        UPDATE_LICHES(WarframeCommandCodes.UPDATE_LICHES, "更新赤毒", 61),
        HOLDFASTS(WarframeCommandCodes.HOLDFASTS, "扎里曼赏金", 62),
        UPDATE_PERRIN_SEQUENCE(WarframeCommandCodes.UPDATE_PERRIN_SEQUENCE, "更新终幕", 62),
        HEX_BOUNTIES(WarframeCommandCodes.HEX_BOUNTIES, "1999赏金", 63),
        RIVEN_WEEKLY(WarframeCommandCodes.RIVEN_WEEKLY, "紫卡周成交统计", 63),
        TRANSLATION(WarframeCommandCodes.TRANSLATION, "翻译", 63), WARFRAMES(WarframeCommandCodes.WARFRAMES, "战甲资料", 63),
        ARCHWING(WarframeCommandCodes.ARCHWING, "Archwing资料", 64), EVENTS(WarframeCommandCodes.EVENTS, "活动", 64),
        MARKET_RIVEN(WarframeCommandCodes.MARKET_RIVEN, "紫卡市场", 64),
        GLOBAL_UPGRADES(WarframeCommandCodes.GLOBAL_UPGRADES, "全局增益", 65), NEWS(WarframeCommandCodes.NEWS, "新闻", 65),
        WEAPONS(WarframeCommandCodes.WEAPONS, "武器资料", 65), ABILITIES(WarframeCommandCodes.ABILITIES, "技能资料", 66),
        FLASH_SALES(WarframeCommandCodes.FLASH_SALES, "促销", 66), LICHES(WarframeCommandCodes.LICHES, "赤毒", 66),
        PERSISTENT_ENEMIES(WarframeCommandCodes.PERSISTENT_ENEMIES, "小小黑", 67),
        SISTERS(WarframeCommandCodes.SISTERS, "信条", 67), MODS(WarframeCommandCodes.MODS, "MOD 资料", 68),
        PERRIN_SEQUENCE(WarframeCommandCodes.PERRIN_SEQUENCE, "佩兰数列", 68),
        SYNDICATE_MISSIONS(WarframeCommandCodes.SYNDICATE_MISSIONS, "集团任务", 68),
        ARCANES(WarframeCommandCodes.ARCANES, "奥术资料", 69), GOLD_TRASH(WarframeCommandCodes.GOLD_TRASH, "金垃圾", 69),
        MOD_SETS(WarframeCommandCodes.MOD_SETS, "MOD 套装", 70),
        SILVER_TRASH(WarframeCommandCodes.SILVER_TRASH, "银垃圾", 70), RECIPES(WarframeCommandCodes.RECIPES, "制作配方", 71),
        RELICS(WarframeCommandCodes.RELICS, "遗物", 71), REWARD_POOLS(WarframeCommandCodes.REWARD_POOLS, "奖励池", 72),
        RIVEN_ANALYSE(WarframeCommandCodes.RIVEN_ANALYSE, "紫卡分析", 73),
        SYNTH_TARGETS(WarframeCommandCodes.SYNTH_TARGETS, "合成目标地点", 73),
        CALENDAR_1999(WarframeCommandCodes.CALENDAR_1999, "1999日历", 74),
        FISH_GUIDE(WarframeCommandCodes.FISH_GUIDE, "鱼类攻略", 74),
        MAP_FEATURES(WarframeCommandCodes.MAP_FEATURES, "开放世界地图标注", 75),
        SUBSCRIBE(WarframeCommandCodes.SUBSCRIBE, "订阅", 75), UNSUBSCRIBE(WarframeCommandCodes.UNSUBSCRIBE, "取消订阅", 76),
        RESOURCES(WarframeCommandCodes.RESOURCES, "资源资料", 77),
        RIVEN_DEFINITIONS(WarframeCommandCodes.RIVEN_DEFINITIONS, "紫卡属性与挑战", 77),
        WATCH_LIST(WarframeCommandCodes.WATCH_LIST, "蹲任务", 77), FISH(WarframeCommandCodes.FISH, "鱼类物品", 78),
        GEAR(WarframeCommandCodes.GEAR, "装备工具", 79),
        CONSTRUCTION_PROGRESS(WarframeCommandCodes.CONSTRUCTION_PROGRESS, "舰队建造", 80),
        MISC(WarframeCommandCodes.MISC, "杂项资料", 80),
        RIVEN_UNROLLABLES(WarframeCommandCodes.RIVEN_UNROLLABLES, "紫卡不可洗属性", 80),
        QUESTS(WarframeCommandCodes.QUESTS, "任务资料", 81), SIMARIS(WarframeCommandCodes.SIMARIS, "西玛里斯", 81),
        CUSTOMS(WarframeCommandCodes.CUSTOMS, "外观资料", 82),
        SENTIENT_OUTPOSTS(WarframeCommandCodes.SENTIENT_OUTPOSTS, "先遣舰", 82),
        CLAN_INITIATIVE(WarframeCommandCodes.CLAN_INITIATIVE, "氏族周任务", 83),
        ITEM_COMPONENTS(WarframeCommandCodes.ITEM_COMPONENTS, "物品组件", 83),
        CIRCUIT_NORMAL(WarframeCommandCodes.CIRCUIT_NORMAL, "普通回环", 84),
        ITEM_DROPS(WarframeCommandCodes.ITEM_DROPS, "物品掉落", 84),
        CIRCUIT_HARD(WarframeCommandCodes.CIRCUIT_HARD, "灵化轮换", 85),
        COMPANIONS(WarframeCommandCodes.COMPANIONS, "同伴资料", 85), ANIMALS(WarframeCommandCodes.ANIMALS, "捕获动物", 86),
        CALENDAR(WarframeCommandCodes.CALENDAR, "实时日历", 86), DRONES(WarframeCommandCodes.DRONES, "无人机资料", 87),
        VAULT_TRADER(WarframeCommandCodes.VAULT_TRADER, "宝库商人", 87),
        FLAVOURS(WarframeCommandCodes.FLAVOURS, "展示风格", 88), TIMESTAMP(WarframeCommandCodes.TIMESTAMP, "世界时间", 88),
        FUSION_BUNDLES(WarframeCommandCodes.FUSION_BUNDLES, "融合包", 89),
        BOOSTERS(WarframeCommandCodes.BOOSTERS, "增幅器", 90),
        CREDIT_BUNDLES(WarframeCommandCodes.CREDIT_BUNDLES, "信用点礼包", 91),
        BUNDLES(WarframeCommandCodes.BUNDLES, "礼包内容", 92),
        BOOSTER_PACKS(WarframeCommandCodes.BOOSTER_PACKS, "随机礼包", 93),
        BOUNTY_DEFINITIONS(WarframeCommandCodes.BOUNTY_DEFINITIONS, "赏金定义", 94),
        KEYS(WarframeCommandCodes.KEYS, "钥匙链", 95), EMAIL_ITEMS(WarframeCommandCodes.EMAIL_ITEMS, "邮件定义", 96),
        ACHIEVEMENTS(WarframeCommandCodes.ACHIEVEMENTS, "成就定义", 97),
        VIRTUALS(WarframeCommandCodes.VIRTUALS, "虚拟实体", 98), CHALLENGES(WarframeCommandCodes.CHALLENGES, "挑战定义", 99),
        SYSTEMS(WarframeCommandCodes.SYSTEMS, "星系定义", 100), TILESETS(WarframeCommandCodes.TILESETS, "场景定义", 101),
        INTRINSICS(WarframeCommandCodes.INTRINSICS, "内源之力", 102),
        CODEX_OBJECTS(WarframeCommandCodes.CODEX_OBJECTS, "图鉴物件", 103),
        CODEX_LORE(WarframeCommandCodes.CODEX_LORE, "图鉴故事碎片", 104),
        CODEX_SONGS(WarframeCommandCodes.CODEX_SONGS, "图鉴歌曲", 105),
        CODEX_FIGHTERS(WarframeCommandCodes.CODEX_FIGHTERS, "Frame Fighter 碎片", 106),
        NIGHTWAVE_DEFINITIONS(WarframeCommandCodes.NIGHTWAVE_DEFINITIONS, "电波静态定义", 107),
        VENDOR_DEFINITIONS(WarframeCommandCodes.VENDOR_DEFINITIONS, "商人静态定义", 108),
        ENEMIES(WarframeCommandCodes.ENEMIES, "敌人资料", 109),
        LEGACY_ITEMS(WarframeCommandCodes.LEGACY_ITEMS, "历史物品目录", 110),
        RIVEN_ATTRIBUTES(WarframeCommandCodes.RIVEN_ATTRIBUTES, "历史紫卡属性编码", 111),
        MARKET_CATALOG(WarframeCommandCodes.MARKET_CATALOG, "历史 Market 商品", 113),
        CONCLAVE_RULES(WarframeCommandCodes.CONCLAVE_RULES, "武形秘仪规则", 120),
        ARCHON_SHARDS(WarframeCommandCodes.ARCHON_SHARDS, "源力石", 121),
        STEEL_PATH_CATALOG(WarframeCommandCodes.STEEL_PATH_CATALOG, "钢铁奖励目录", 122),
        EVENT_RULES(WarframeCommandCodes.EVENT_RULES, "活动显示规则", 123),
        TUTORIALS(WarframeCommandCodes.TUTORIALS, "教程", 124),
        ARCANE_GUIDES(WarframeCommandCodes.ARCANE_GUIDES, "奥术攻略", 125);

        private final String code;
        private final String displayName;
        private final int order;

        /**
         * 定义命令的稳定编码、中文名称和排序。
         *
         * @param code 稳定命令编码
         * @param displayName 默认中文名称
         * @param order 展示及匹配优先级
         */
        Definition(String code, String displayName, int order) {
            this.code = code;
            this.displayName = displayName;
            this.order = order;
        }
    }

    /** 启动时构建一次不可变索引，禁止重复编码，不在每次匹配时遍历枚举。 */
    public static final Map<String, Definition> DEFINITIONS =
        Arrays.stream(Definition.values()).collect(Collectors.toUnmodifiableMap(Definition::code, Function.identity()));

    public static final Set<String> ALL = DEFINITIONS.keySet();

    /** 禁止实例化，仅使用本类的稳定定义。 */
    private WarframeCommandCodes() {}
}
