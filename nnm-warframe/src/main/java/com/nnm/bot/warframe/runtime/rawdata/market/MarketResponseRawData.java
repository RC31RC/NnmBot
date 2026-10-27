package com.nnm.bot.warframe.runtime.rawdata.market;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

/** 市场 API 的协议响应；各端点通过泛型指定数据区的具体字段。 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketResponseRawData<T> {
    /** 接口版本标识。 */
    @JsonProperty("apiVersion")
    private String apiVersion;

    /** 接口返回的错误原文，仅用于排障展示，不参与业务解析。 */
    @JsonProperty("error")
    private Object error;

    /** 当前端点的类型化数据区。 */
    @JsonProperty("data")
    private T data;

    /** 旧版端点的明确协议数据区。 */
    @JsonProperty("payload")
    private PayloadRawData payload;

    /** 订单响应顶层内嵌的物品详情。 */
    @JsonProperty("item")
    private ItemRawData item;

    /** 市场各端点的实际数据分区。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PayloadRawData {
        /** 旧版协议内嵌的物品详情。 */
        @JsonProperty("item")
        private ItemRawData item;

        /** 旧版协议的市场订单。 */
        @JsonProperty("orders")
        private List<OrderRawData> orders;

        /** 旧版协议的物品目录。 */
        @JsonProperty("items")
        private List<ItemRawData> items;

        /** 拍卖查询结果。 */
        @JsonProperty("auctions")
        private List<AuctionRawData> auctions;

        /** 按时间段划分的成交统计。 */
        @JsonProperty("statistics_live")
        private StatisticsPeriodsRawData statisticsLive;

        /** 上一小时的杜卡德成交统计。 */
        @JsonProperty("previous_hour")
        private List<DucatRawData> previousHour;

        /** 上一日的杜卡德成交统计。 */
        @JsonProperty("previous_day")
        private List<DucatRawData> previousDay;

    }

    /** 市场物品原始字段。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemRawData {
        /** 市场物品标识。 */
        @JsonProperty("id")
        @JsonAlias({"_id"})
        private String id;

        /** 市场物品的地址标识。 */
        @JsonProperty("slug")
        @JsonAlias({"url_name", "urlName"})
        private String slug;

        /** 对应游戏物品的唯一引用。 */
        @JsonProperty("gameRef")
        @JsonAlias({"game_ref", "uniqueName"})
        private String gameRef;

        /** 物品分类标签。 */
        @JsonProperty("tags")
        private List<String> tags;

        /** 是否为套装主体。 */
        @JsonProperty("setRoot")
        @JsonAlias({"set_root"})
        private Boolean setRoot;

        /** 套装部件的市场标识。 */
        @JsonProperty("setParts")
        @JsonAlias({"set_parts"})
        private List<String> setParts;

        /** 套装内该部件的数量。 */
        @JsonProperty("quantityInSet")
        @JsonAlias({"quantity_in_set"})
        private Integer quantityInSet;

        /** 物品稀有度。 */
        @JsonProperty("rarity")
        private String rarity;

        /** 是否允许批量交易。 */
        @JsonProperty("bulkTradable")
        @JsonAlias({"bulk_tradable"})
        private Boolean bulkTradable;

        /** 可交易物品的子类型。 */
        @JsonProperty("subtypes")
        private List<String> subtypes;

        /** 最高强化等级。 */
        @JsonProperty("maxRank")
        @JsonAlias({"max_rank"})
        private Integer maxRank;

        /** 最高充能次数。 */
        @JsonProperty("maxCharges")
        @JsonAlias({"max_charges"})
        private Integer maxCharges;

        /** 最大琥珀星槽数量。 */
        @JsonProperty("maxAmberStars")
        @JsonAlias({"max_amber_stars"})
        private Integer maxAmberStars;

        /** 最大青蓝星槽数量。 */
        @JsonProperty("maxCyanStars")
        @JsonAlias({"max_cyan_stars"})
        private Integer maxCyanStars;

        /** 基础内融核心价值。 */
        @JsonProperty("baseEndo")
        @JsonAlias({"base_endo"})
        private Integer baseEndo;

        /** 兑换杜卡德金币的数量。 */
        @JsonProperty("ducats")
        private Integer ducats;

        /** 兑换赋能精华的数量。 */
        @JsonProperty("vosfor")
        private Integer vosfor;

        /** 交易要求的段位。 */
        @JsonProperty("reqMasteryRank")
        @JsonAlias({"req_mastery_rank"})
        private Integer reqMasteryRank;

        /** 交易税额。 */
        @JsonProperty("tradingTax")
        @JsonAlias({"trading_tax"})
        private Integer tradingTax;

        /** 等级对应的内融核心倍率。 */
        @JsonProperty("endoMultiplier")
        @JsonAlias({"endo_multiplier"})
        private Double endoMultiplier;

        /** 是否已进入宝库。 */
        @JsonProperty("vaulted")
        private Boolean vaulted;

        /** 是否可以交易。 */
        @JsonProperty("tradable")
        private Boolean tradable;

        /** 按语言代码划分的物品说明。 */
        @JsonProperty("i18n")
        private Map<String, I18nRawData> i18n;

        /** 旧版市场协议的英文名称对象。 */
        @JsonProperty("en")
        private I18nRawData english;

    }

    /** 单个语言的物品文本与图标。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class I18nRawData {
        /**
         * 适配旧版端点实际返回的语言名称字符串。
         *
         * @param name 该语言的物品名称
         * @return 仅提供名称的原始语言对象
         */
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static I18nRawData fromName(String name) {
            I18nRawData value = new I18nRawData();
            value.setName(name);
            return value;
        }

        /** 本地化物品名称。 */
        @JsonProperty("name")
        private String name;

        /** 本地化物品说明。 */
        @JsonProperty("description")
        private String description;

        /** 百科页面地址。 */
        @JsonProperty("wikiLink")
        @JsonAlias({"wiki_link"})
        private String wikiLink;

        /** 完整图标路径。 */
        @JsonProperty("icon")
        private String icon;

        /** 缩略图路径。 */
        @JsonProperty("thumb")
        private String thumb;

        /** 附属图标路径。 */
        @JsonProperty("subIcon")
        @JsonAlias({"sub_icon"})
        private String subIcon;

    }

    /** 市场用户原始字段。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserRawData {
        /** 市场用户标识。 */
        @JsonProperty("id")
        @JsonAlias({"_id"})
        private String id;

        /** 游戏内名称。 */
        @JsonProperty("ingameName")
        @JsonAlias({"ingame_name", "name"})
        private String ingameName;

        /** 用户主页地址标识。 */
        @JsonProperty("slug")
        private String slug;

        /** 用户交易平台。 */
        @JsonProperty("platform")
        private String platform;

        /** 用户语言。 */
        @JsonProperty("locale")
        private String locale;

        /** 用户在线状态。 */
        @JsonProperty("status")
        private String status;

        /** 用户头像路径。 */
        @JsonProperty("avatar")
        private String avatar;

        /** 交易信誉值。 */
        @JsonProperty("reputation")
        private Integer reputation;

        /** 是否启用跨平台交易。 */
        @JsonProperty("crossplay")
        private Boolean crossplay;

        /** 最近在线时间。 */
        @JsonProperty("lastSeen")
        @JsonAlias({"last_seen"})
        private String lastSeen;

        /** 平台提供的活动状态扩展，仅供展示与审计。 */
        @JsonProperty("activity")
        private Map<String,Object> activity;

    }

    /** 订单原始字段。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrderRawData {
        /** 订单标识。 */
        @JsonProperty("id")
        @JsonAlias({"_id"})
        private String id;

        /** 买入或卖出方向。 */
        @JsonProperty("type")
        @JsonAlias({"order_type"})
        private String type;

        /** 订单的铂金价格。 */
        @JsonProperty("platinum")
        private Integer platinum;

        /** 可交易数量。 */
        @JsonProperty("quantity")
        private Integer quantity;

        /** 每次交易的物品数量。 */
        @JsonProperty("perTrade")
        @JsonAlias({"per_trade"})
        private Integer perTrade;

        /** 物品子类型。 */
        @JsonProperty("subtype")
        private String subtype;

        /** 强化等级。 */
        @JsonProperty("rank")
        @JsonAlias({"modRank", "mod_rank"})
        private Integer rank;

        /** 剩余充能次数。 */
        @JsonProperty("charges")
        private Integer charges;

        /** 已镶嵌的琥珀星数量。 */
        @JsonProperty("amberStars")
        @JsonAlias({"amber_stars"})
        private Integer amberStars;

        /** 已镶嵌的青蓝星数量。 */
        @JsonProperty("cyanStars")
        @JsonAlias({"cyan_stars"})
        private Integer cyanStars;

        /** 订单是否公开，未提供时按公开处理。 */
        @JsonProperty("visible")
        private Boolean visible;

        /** 订单创建时间。 */
        @JsonProperty("createdAt")
        @JsonAlias({"created_at"})
        private String createdAt;

        /** 订单最后更新时间。 */
        @JsonProperty("updatedAt")
        @JsonAlias({"updated_at"})
        private String updatedAt;

        /** 物品标识。 */
        @JsonProperty("itemId")
        @JsonAlias({"item_id"})
        private String itemId;

        /** 订单组标识。 */
        @JsonProperty("groupId")
        @JsonAlias({"group_id"})
        private String groupId;

        /** 订单发布者概要。 */
        @JsonProperty("user")
        private UserRawData user;

    }

    /** 拍卖原始字段。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuctionRawData {
        /** 拍卖标识。 */
        @JsonProperty("id")
        private String id;

        /** 拍卖发布者概要。 */
        @JsonProperty("owner")
        private UserRawData owner;

        /** 拍卖物品的实际属性。 */
        @JsonProperty("item")
        private AuctionItemRawData item;

        /** 起拍价。 */
        @JsonProperty("starting_price")
        private Integer startingPrice;

        /** 一口价。 */
        @JsonProperty("buyout_price")
        private Integer buyoutPrice;

        /** 当前最高出价。 */
        @JsonProperty("top_bid")
        private Integer topBid;

        /** 是否直接出售。 */
        @JsonProperty("is_direct_sell")
        private Boolean directSell;

        /** 拍卖创建时间。 */
        @JsonProperty("created")
        private String created;

    }

    /** 拍卖物品原始属性。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuctionItemRawData {
        /** 拍卖物品类别。 */
        @JsonProperty("type")
        private String type;

        /** 适用武器的市场标识。 */
        @JsonProperty("weapon_url_name")
        private String weaponUrlName;

        /** 拍卖物品名称。 */
        @JsonProperty("name")
        private String name;

        /** 物品极性。 */
        @JsonProperty("polarity")
        private String polarity;

        /** 玄骸武器元素。 */
        @JsonProperty("element")
        private String element;

        /** 玄骸性格特征。 */
        @JsonProperty("quirk")
        private String quirk;

        /** 强化等级。 */
        @JsonProperty("mod_rank")
        private Integer modRank;

        /** 洗练次数。 */
        @JsonProperty("re_rolls")
        private Integer rerolls;

        /** 使用要求的段位。 */
        @JsonProperty("mastery_level")
        private Integer masteryLevel;

        /** 玄骸武器加成数值。 */
        @JsonProperty("damage")
        private Integer damage;

        /** 是否携带幻纹。 */
        @JsonProperty("having_ephemera")
        private Boolean ephemera;

        /** 紫卡词条列表。 */
        @JsonProperty("attributes")
        private List<AttributeRawData> attributes;

    }

    /** 紫卡的单条实际属性。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AttributeRawData {
        /** 词条的市场查询标识。 */
        @JsonProperty("url_name")
        private String urlName;

        /** 词条的实际数值。 */
        @JsonProperty("value")
        private BigDecimal value;

        /** 是否为正面词条。 */
        @JsonProperty("positive")
        private Boolean positive;

    }

    /** 市场套装详情。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ItemSetRawData {
        /** 套装标识。 */
        @JsonProperty("id")
        private String id;

        /** 套装物品与部件列表。 */
        @JsonProperty("items")
        private List<ItemRawData> items;

    }

    /** 市场成交统计的时间分区。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatisticsPeriodsRawData {
        /** 最近九十日的成交统计。 */
        @JsonProperty("90days")
        private List<StatisticRawData> days90;

    }

    /** 新版成交统计数据区。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatisticsDataRawData {
        /** 成交统计记录。 */
        @JsonProperty("statistics")
        private List<StatisticRawData> statistics;

    }

    /** 单条市场成交统计。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StatisticRawData {
        /** 该统计记录的时间。 */
        @JsonProperty("datetime")
        private String datetime;

        /** 成交量。 */
        @JsonProperty("volume")
        private Integer volume;

        /** 统计订单方向。 */
        @JsonProperty("order_type")
        private String orderType;

        /** 最低成交价。 */
        @JsonProperty("min_price")
        private BigDecimal minimum;

        /** 最高成交价。 */
        @JsonProperty("max_price")
        private BigDecimal maximum;

        /** 平均成交价。 */
        @JsonProperty("avg_price")
        private BigDecimal average;

        /** 加权平均成交价。 */
        @JsonProperty("wa_price")
        private BigDecimal weightedAverage;

        /** 成交价格中位数。 */
        @JsonProperty("median")
        private BigDecimal median;

        /** 成交物品强化等级。 */
        @JsonProperty("mod_rank")
        private Integer modRank;

    }

    /** 单条杜卡德金币成交统计。 */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DucatRawData {
        /** 市场物品标识。 */
        @JsonProperty("item")
        private String item;

        /** 单件物品的杜卡德金币价值。 */
        @JsonProperty("ducats")
        private Integer ducats;

        /** 按加权价格计算的金币铂金比。 */
        @JsonProperty("ducats_per_platinum_wa")
        private BigDecimal ratio;

        /** 加权平均成交价格。 */
        @JsonProperty("wa_price")
        private BigDecimal weightedPrice;

        /** 参与统计的成交量。 */
        @JsonProperty("volume")
        private Long volume;

        /** 统计记录的时间。 */
        @JsonProperty("datetime")
        private String datetime;

    }

}
