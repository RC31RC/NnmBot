package com.nnm.bot.warframe.runtime.constant;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/** 动态世界状态只引用已发布静态资源的展示身份。 */
public final class WarframeWorldStateDisplayConstants {
    /** 限时促销只展示到期不超过两周的当前商品。 */
    public static final Duration MAX_FLASH_SALE_REMAINING = Duration.ofDays(14);

    /** 仅保留玩家社区活动与传家宝商城分类，身份以官方分类代码为准。 */
    public static final Set<String> MARKET_CATEGORY_CODES = Set.of("COMMUNITY", "HEIRLOOM");

    private WarframeWorldStateDisplayConstants() {}

    /** 官方制作配方的完整游戏路径前缀。 */

    private static final String CREDIT_CACHE_PREFIX = "/Lotus/Types/PickUps/Credits/";
    private static final String CREDIT_BUNDLE_PREFIX = "/Lotus/Types/StoreItems/CreditBundles/";
    private static final Pattern CREDIT_AMOUNT_NAME = Pattern.compile("[1-9][0-9]*Credits");

    /**
     * 只识别官方现金面额商品，不把同目录的霍币或铂金兑换礼包当作现金匣。
     *
     * @param key 已规范化为非商店路径的官方商品键。
     * @return 属于可用通用现金图标补位的商品身份时为真。
     */
    public static boolean isNumericCreditBundle(String key) {
        if (key == null)
            return false;
        String prefix = key.startsWith(CREDIT_CACHE_PREFIX) ? CREDIT_CACHE_PREFIX
            : key.startsWith(CREDIT_BUNDLE_PREFIX) ? CREDIT_BUNDLE_PREFIX : null;
        return prefix != null && CREDIT_AMOUNT_NAME.matcher(key.substring(prefix.length())).matches();
    }

    /** WFCD 外部日志中标识赤毒任务的类型前缀。 */
    public static final String KUVA_MISSION_PREFIX = "KuvaMission";

    /** Tmp 内按奖励物品身份分组的 QTCC 活动计数和目标键。 */
    public static final String QTCC_COUNT_PREFIX = "QTCCFloofCount:";
    public static final String QTCC_LIMIT_PREFIX = "QTCCFloofLimit:";

    /** 相同业务栏目在命令投影与 DTO 字段中的稳定身份。 */
    private static final Map<String,
        String> SECTION_KEY_ALIASES = Map.ofEntries(Map.entry("archonHunts", "archon-hunts"),
            Map.entry("constructionProgress", "construction-progress"), Map.entry("dailyDeals", "daily-deals"),
            Map.entry("flashSales", "flash-sales"), Map.entry("marketCategories", "in-game-market"),
            Map.entry("globalUpgrades", "global-upgrades"), Map.entry("temporaryActivity", "temporary-activities"),
            Map.entry("steelPathOfferings", "steel-path"), Map.entry("clanInitiatives", "clan-initiative"),
            Map.entry("sentientOutpost", "sentient-outposts"), Map.entry("voidTraders", "void-trader"),
            Map.entry("vaultTraders", "vault-trader"), Map.entry("calendarSeasons", "calendar"),
            Map.entry("syndicateMissions", "syndicate-missions"));

    /** 两种视图共享的栏目顺序；其余专属业务栏位稳定排在末尾。 */
    private static final List<String> SECTION_ORDER =
        List.of("alerts", "sorties", "archon-hunts", "void-trader", "daily-deals", "flash-sales", "in-game-market",
            "global-upgrades", "temporary-activities", "invasions", "fissures", "steel-fissures", "void-storms",
            "steel-path", "cycles", "nightwave", "deep-archimedeas", "hex-archimedeas", "other-archimedeas", "descents",
            "events", "news", "construction-progress", "sentient-outposts", "clan-initiative", "circuit-normal",
            "circuit-hard", "calendar", "vault-trader", "syndicate-missions", "ostrons", "solaris", "entrati");

    /** 跨模式展示名统一的业务栏目。 */
    private static final Map<String,
        String> SECTION_TITLES = Map.ofEntries(Map.entry("alerts", "警报"), Map.entry("sorties", "突击"),
            Map.entry("archon-hunts", "执刑官猎杀"), Map.entry("void-trader", "奸商"), Map.entry("daily-deals", "每日特惠"),
            Map.entry("flash-sales", "限时促销"), Map.entry("in-game-market", "商城分类"), Map.entry("global-upgrades", "全局增益"),
            Map.entry("temporary-activities", "社区活动与寻呼机"), Map.entry("invasions", "入侵"), Map.entry("fissures", "裂隙"),
            Map.entry("steel-fissures", "钢铁裂隙"), Map.entry("void-storms", "九重天裂隙"), Map.entry("steel-path", "钢铁奖励"),
            Map.entry("cycles", "平原"), Map.entry("nightwave", "电波"), Map.entry("deep-archimedeas", "深层科研"),
            Map.entry("hex-archimedeas", "时光科研"), Map.entry("other-archimedeas", "其他科研"), Map.entry("descents", "沉沦之地"),
            Map.entry("events", "世界事件"), Map.entry("news", "新闻"), Map.entry("construction-progress", "舰队建造进度"),
            Map.entry("sentient-outposts", "感知者先遣舰"), Map.entry("clan-initiative", "氏族周任务"),
            Map.entry("circuit-normal", "普通回廊"), Map.entry("circuit-hard", "灵化轮换"), Map.entry("calendar", "实时日历"),
            Map.entry("vault-trader", "Prime 宝库商人"), Map.entry("syndicate-missions", "集团任务"),
            Map.entry("ostrons", "希图斯"), Map.entry("solaris", "索拉里斯"), Map.entry("entrati", "英择谛"));

    /** DTO 中仅为兼容或由其他业务栏位完整呈现的重复字段。 */
    public static final Set<String> DUPLICATE_WORLD_STATE_FIELDS = Set.of("sortie", "voidTrader", "activeMissions");

    /** 应合并到平原周期栏位的 DTO 字段。 */
    public static final Set<String> CYCLE_WORLD_STATE_FIELDS =
        Set.of("cetusCycle", "vallisCycle", "cambionCycle", "earthCycle", "duviriCycle", "zarimanCycle");

    /** @param key 当前投影或 DTO 字段中的栏目键；@return 跨模式稳定栏目键。 */
    public static String sectionKey(String key) {
        return SECTION_KEY_ALIASES.getOrDefault(key, key);
    }

    /** @return 两种世界状态视图共用的业务栏目目录。 */
    public static List<String> sectionKeys() {
        return SECTION_ORDER;
    }

    /** @param key 稳定栏目键；@return 当前栏目在导航中的顺序位置。 */
    public static int sectionOrder(String key) {
        String canonicalKey = sectionKey(key);
        if (canonicalKey.startsWith("bounty-cycle-"))
            return SECTION_ORDER.indexOf("deep-archimedeas") - 1;
        int order = SECTION_ORDER.indexOf(canonicalKey);
        return order < 0 ? Integer.MAX_VALUE : order;
    }

    /**
     * 解析跨模式共用的栏目名；独有业务栏位保留来源投影给出的标题。
     *
     * @param key 稳定栏目键。
     * @param fallback 来源投影给出的可读标题。
     * @return 管理端展示使用的栏目标题。
     */
    public static String sectionTitle(String key, String fallback) {
        return SECTION_TITLES.getOrDefault(sectionKey(key), fallback);
    }

    /** 日历事件只展示有可用任务或物品信息的类型。 */
    public enum CalendarEvent {
        CHALLENGE("CET_CHALLENGE"), REWARD("CET_REWARD");

        private final String code;

        CalendarEvent(String code) {
            this.code = code;
        }

        /** @param value 上游事件类型；@return 当前事件属于此业务类型时为真。 */
        public boolean matches(String value) {
            return code.equals(value);
        }
    }
}
