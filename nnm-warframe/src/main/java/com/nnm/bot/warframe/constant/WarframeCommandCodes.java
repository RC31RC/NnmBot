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
    public static final String ARCHON_HUNTS = "archon-hunts";
    public static final String CALENDAR_1999 = "calendar-1999";
    public static final String CYCLES = "cycles";
    public static final String DAILY_DEALS = "daily-deals";
    public static final String DUVIRI_CYCLE = "duviri-cycle";
    public static final String ENTRATI = "entrati";
    public static final String FISSURES = "fissures";
    public static final String FORUM_LOADOUT = "forum-loadout";
    public static final String GOLD_TRASH = "gold-trash";
    public static final String INVASIONS = "invasions";
    public static final String LICHES = "liches";
    public static final String MARKET = "market";
    public static final String MARKET_RIVEN = "market-riven";
    public static final String NIGHTWAVE = "nightwave";
    public static final String OPEN_RELICS = "open-relics";
    public static final String OSTRONS = "ostrons";
    public static final String PERRIN_SEQUENCE = "perrin-sequence";
    public static final String RELICS = "relics";
    public static final String RIVEN_ANALYSE = "riven-analyse";
    public static final String RIVEN_DISPOSITION = "riven-disposition";
    public static final String RIVEN_MARKET = "riven-market";
    public static final String SILVER_TRASH = "silver-trash";
    public static final String SISTERS = "sisters";
    public static final String SOLARIS = "solaris";
    public static final String SORTIES = "sorties";
    public static final String STEEL_FISSURES = "steel-fissures";
    public static final String STEEL_PATH = "steel-path";
    public static final String SUBSCRIBE = "subscribe";
    public static final String TRANSLATION = "translation";
    public static final String UNSUBSCRIBE = "unsubscribe";
    public static final String UPDATE_MARKET_ITEMS = "update-market-items";
    public static final String UPDATE_MARKET_RIVEN = "update-market-riven";
    public static final String UPDATE_SISTER = "update-sister";
    public static final String UPDATE_TRANSLATION = "update-translation";
    public static final String VOID_STORMS = "void-storms";
    public static final String VOID_TRADER = "void-trader";
    public static final String WIKI = "wiki";

    public static final Set<String> ALL = Set.of(
            ALERTS,
            ARBITRATION,
            ARBITRATION_TABLE,
            ARCHON_HUNTS,
            CALENDAR_1999,
            CYCLES,
            DAILY_DEALS,
            DUVIRI_CYCLE,
            ENTRATI,
            FISSURES,
            FORUM_LOADOUT,
            GOLD_TRASH,
            INVASIONS,
            LICHES,
            MARKET,
            MARKET_RIVEN,
            NIGHTWAVE,
            OPEN_RELICS,
            OSTRONS,
            PERRIN_SEQUENCE,
            RELICS,
            RIVEN_ANALYSE,
            RIVEN_DISPOSITION,
            RIVEN_MARKET,
            SILVER_TRASH,
            SISTERS,
            SOLARIS,
            SORTIES,
            STEEL_FISSURES,
            STEEL_PATH,
            SUBSCRIBE,
            TRANSLATION,
            UNSUBSCRIBE,
            UPDATE_MARKET_ITEMS,
            UPDATE_MARKET_RIVEN,
            UPDATE_SISTER,
            UPDATE_TRANSLATION,
            VOID_STORMS,
            VOID_TRADER,
            WIKI);

    private WarframeCommandCodes() {
    }
}
