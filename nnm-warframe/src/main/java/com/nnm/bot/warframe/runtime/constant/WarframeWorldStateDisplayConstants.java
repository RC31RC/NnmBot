package com.nnm.bot.warframe.runtime.constant;

import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import java.time.Duration;
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
    public static final String RECIPE_PREFIX = "/Lotus/Types/Recipes/";

    /** 官方现金匣独立记录缺图时使用已采纳 IMAGE 表中的通用现金图标。 */
    public static final String GENERIC_CREDIT_ICON = "/Lotus/Interface/Icons/StoreIcons/Currency/GenericCreditIcon.png";
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

    /** 无原始物品路径的奖励只在名称与已采纳官方记录完全吻合时补图。 */
    public enum UnkeyedRewardReference {
        ENDO(WarframeOfficialDataDomain.FUSION_BUNDLES, "/Lotus/Upgrades/Mods/FusionBundles/AlertFusionBundleLarge"),
        KUVA(WarframeOfficialDataDomain.RESOURCES, "/Lotus/Types/Items/MiscItems/Kuva"),
        STEEL_ESSENCE(WarframeOfficialDataDomain.RESOURCES, "/Lotus/Types/Items/MiscItems/SteelEssence");

        private final WarframeOfficialDataDomain domain;
        private final String key;

        UnkeyedRewardReference(WarframeOfficialDataDomain domain, String key) {
            this.domain = domain;
            this.key = key;
        }

        /** @return 官方记录所属的数据集。 */
        public WarframeOfficialDataDomain domain() {
            return domain;
        }

        /** @return 用于校验名称和图标的完整官方记录身份。 */
        public String key() {
            return key;
        }
    }

    /** WFCD 外部日志中标识赤毒任务的类型前缀。 */
    public static final String KUVA_MISSION_PREFIX = "KuvaMission";

    /** Tmp 内按奖励物品身份分组的 QTCC 活动计数和目标键。 */
    public static final String QTCC_COUNT_PREFIX = "QTCCFloofCount:";
    public static final String QTCC_LIMIT_PREFIX = "QTCCFloofLimit:";

    /** 官方物品本地化表中 Umbra Forma 蓝图缺图时，对应成品的已采纳记录可提供图标。 */
    public static final String UMBRA_FORMA_BLUEPRINT = "/Lotus/Types/Recipes/Components/UmbraFormaBlueprint";
    public static final String UMBRA_FORMA_STORE_BLUEPRINT =
        "/Lotus/StoreItems/Types/Recipes/Components/UmbraFormaBlueprint";
    public static final String UMBRA_FORMA_PRODUCT = "/Lotus/Types/Items/MiscItems/FormaUmbra";

    /** 官方 reference/images.json 内的平原景观图片路径。 */
    public static final Map<String, String> OPEN_WORLD_IMAGES =
        Map.of("夜灵平原", "/Lotus/Interface/Icons/StoreIcons/Captura/PlainsOfEidolonLandscape.png", "福尔图娜",
            "/Lotus/Interface/Icons/StoreIcons/Captura/VallisLandscape.png", "魔胎之境",
            "/Lotus/Interface/Icons/StoreIcons/Captura/DeimosCambionDriftLandscape.png");

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
