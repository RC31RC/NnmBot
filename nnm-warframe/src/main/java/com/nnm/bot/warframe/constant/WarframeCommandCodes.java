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
    public static final String OSTRONS = "ostrons";
    public static final String PERRIN_SEQUENCE = "perrin-sequence";
    public static final String RELICS = "relics";
    public static final String RIVEN_ANALYSE = "riven-analyse";
    public static final String RIVEN_DISPOSITION = "riven-disposition";
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
            RIVEN_DISPOSITION,
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
            WIKI);

    private WarframeCommandCodes() {
    }
}
