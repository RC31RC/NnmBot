package com.nnm.bot.warframe.constant;

import java.util.Set;

/**
 * Warframe 插件使用的稳定命令编码。
 *
 * <p>这里使用编译期字符串常量而非 enum getter，因为注解属性只能引用编译期常量。</p>
 */
public final class WarframeCommandCodes {

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

    public static final Set<String> ALL = Set.of(
            ALERTS,
            ARBITRATION,
            ARBITRATION_TABLE,
            TODAY_ARBITRATION,
            EFFICIENT_ARBITRATION,
            ARCHON_HUNTS,
            ARCHIMEDEAS,
            DEEP_ARCHIMEDEAS,
            HEX_ARCHIMEDEAS,
            DESCENTS,
            CALENDAR_1999,
            CONCLAVE,
            CONCLAVE_RULES, ARCHON_SHARDS, STEEL_PATH_CATALOG, EVENT_RULES, TUTORIALS, ARCANE_GUIDES,
            NEWS,
            FLASH_SALES,
            PERSISTENT_ENEMIES,
            SYNDICATE_MISSIONS,
            CONSTRUCTION_PROGRESS, SIMARIS, SENTIENT_OUTPOSTS, CLAN_INITIATIVE,
            CIRCUIT_NORMAL, CIRCUIT_HARD, CALENDAR, VAULT_TRADER, TIMESTAMP,
            CAVIA,
            CYCLES,
            DAILY_DEALS,
            DUVIRI_CYCLE,
            ENTRATI,
            EVENTS,
            FISSURES,
            FORUM_LOADOUT,
            GOLD_TRASH,
            GLOBAL_UPGRADES,
            INVASIONS,
            LICHES,
            MARKET,
            MARKET_RIVEN,
            NIGHTWAVE,
            OSTRONS,
            PERRIN_SEQUENCE,
            RELICS,
            RIVEN_ANALYSE,
            RIVEN_WEEKLY,
            RIVEN_DEFINITIONS,
            RIVEN_UNROLLABLES,
            SILVER_TRASH,
            SISTERS,
            SOLARIS,
            SORTIES,
            STEEL_FISSURES,
            STEEL_PATH,
            SUBSCRIBE,
            WATCH_LIST,
            TRANSLATION,
            UNSUBSCRIBE,
            UPDATE_SISTER,
            UPDATE_LICHES,
            UPDATE_PERRIN_SEQUENCE,
            VOID_STORMS,
            VOID_TRADER,
            HOLDFASTS,
            HEX_BOUNTIES,
            WIKI, WARFRAMES, ARCHWING, WEAPONS, ABILITIES, MODS, ARCANES, MOD_SETS, RECIPES, REWARD_POOLS,
            SYNTH_TARGETS, FISH_GUIDE, MAP_FEATURES, RESOURCES, FISH, GEAR, MISC, QUESTS, CUSTOMS,
            ITEM_COMPONENTS, ITEM_DROPS, COMPANIONS, ANIMALS, DRONES, FLAVOURS,
            FUSION_BUNDLES, BOOSTERS, CREDIT_BUNDLES, BUNDLES, BOOSTER_PACKS, BOUNTY_DEFINITIONS, KEYS, EMAIL_ITEMS,
            ACHIEVEMENTS, INTRINSICS, CODEX_OBJECTS, CODEX_LORE, CODEX_SONGS, CODEX_FIGHTERS,
            VIRTUALS, CHALLENGES, SYSTEMS, TILESETS, NIGHTWAVE_DEFINITIONS, VENDOR_DEFINITIONS, ENEMIES,
            LEGACY_ITEMS, RIVEN_ATTRIBUTES, MARKET_CATALOG);

    /**
     * 禁止实例化，仅通过本类的静态方法使用此能力。
     */
    private WarframeCommandCodes() {
    }
}
