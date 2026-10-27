package com.nnm.bot.warframe.persistent.official.pojo.dto.market;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialInputText;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialNullDeserializer;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

/** MARKET_ITEM 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialMarketItemDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 基础内融核心价值；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("baseEndo")
    private BigDecimal baseEndo;

    /**
     * 记录基础内融核心价值是否出现，同时区分未提供与显式空值。
     *
     * @param value 基础内融核心价值，保留协议中的显式空值。
     */
    @JsonSetter("baseEndo")
    public void setBaseEndo(BigDecimal value) {
        this.baseEndo = value;
        markPresent("baseEndo");
    }

    /** 是否支持批量交易；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("bulkTradable")
    private Boolean bulkTradable;

    /**
     * 记录是否支持批量交易是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否支持批量交易，保留协议中的显式空值。
     */
    @JsonSetter("bulkTradable")
    public void setBulkTradable(Boolean value) {
        this.bulkTradable = value;
        markPresent("bulkTradable");
    }

    /** 杜卡德金币数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("ducats")
    private BigDecimal ducats;

    /**
     * 记录杜卡德金币数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 杜卡德金币数量，保留协议中的显式空值。
     */
    @JsonSetter("ducats")
    public void setDucats(BigDecimal value) {
        this.ducats = value;
        markPresent("ducats");
    }

    /** 内融核心倍率；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("endoMultiplier")
    private BigDecimal endoMultiplier;

    /**
     * 记录内融核心倍率是否出现，同时区分未提供与显式空值。
     *
     * @param value 内融核心倍率，保留协议中的显式空值。
     */
    @JsonSetter("endoMultiplier")
    public void setEndoMultiplier(BigDecimal value) {
        this.endoMultiplier = value;
        markPresent("endoMultiplier");
    }

    /** 游戏物品；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("gameItem")
    @JsonDeserialize(using = OfficialNullDeserializer.class)
    private Void gameItem;

    /**
     * 记录游戏物品是否出现，同时区分未提供与显式空值。
     *
     * @param value 游戏物品，保留协议中的显式空值。
     */
    @JsonSetter("gameItem")
    public void setGameItem(Void value) {
        this.gameItem = value;
        markPresent("gameItem");
    }

    /** 游戏物品的图标状态；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("gameItem__iconStatus")
    private String gameItemIconStatus;

    /**
     * 记录游戏物品的图标状态是否出现，同时区分未提供与显式空值。
     *
     * @param value 游戏物品的图标状态，保留协议中的显式空值。
     */
    @JsonSetter("gameItem__iconStatus")
    public void setGameItemIconStatus(String value) {
        this.gameItemIconStatus = value;
        markPresent("gameItem__iconStatus");
    }

    /** 游戏物品的图标地址；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("gameItem__iconUrl")
    private String gameItemIconUrl;

    /**
     * 记录游戏物品的图标地址是否出现，同时区分未提供与显式空值。
     *
     * @param value 游戏物品的图标地址，保留协议中的显式空值。
     */
    @JsonSetter("gameItem__iconUrl")
    public void setGameItemIconUrl(String value) {
        this.gameItemIconUrl = value;
        markPresent("gameItem__iconUrl");
    }

    /** 游戏物品的名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("gameItem__name")
    private String gameItemName;

    /**
     * 记录游戏物品的名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 游戏物品的名称，保留协议中的显式空值。
     */
    @JsonSetter("gameItem__name")
    public void setGameItemName(String value) {
        this.gameItemName = value;
        markPresent("gameItem__name");
    }

    /** 游戏物品的英文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("gameItem__nameEn")
    private String gameItemNameEn;

    /**
     * 记录游戏物品的英文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 游戏物品的英文名称，保留协议中的显式空值。
     */
    @JsonSetter("gameItem__nameEn")
    public void setGameItemNameEn(String value) {
        this.gameItemNameEn = value;
        markPresent("gameItem__nameEn");
    }

    /** 游戏物品的中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("gameItem__nameZh")
    private String gameItemNameZh;

    /**
     * 记录游戏物品的中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 游戏物品的中文名称，保留协议中的显式空值。
     */
    @JsonSetter("gameItem__nameZh")
    public void setGameItemNameZh(String value) {
        this.gameItemNameZh = value;
        markPresent("gameItem__nameZh");
    }

    /** 游戏物品的解析状态；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("gameItem__resolution")
    private String gameItemResolution;

    /**
     * 记录游戏物品的解析状态是否出现，同时区分未提供与显式空值。
     *
     * @param value 游戏物品的解析状态，保留协议中的显式空值。
     */
    @JsonSetter("gameItem__resolution")
    public void setGameItemResolution(String value) {
        this.gameItemResolution = value;
        markPresent("gameItem__resolution");
    }

    /** 游戏物品的唯一标识；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("gameItem__uniqueName")
    private String gameItemUniqueName;

    /**
     * 记录游戏物品的唯一标识是否出现，同时区分未提供与显式空值。
     *
     * @param value 游戏物品的唯一标识，保留协议中的显式空值。
     */
    @JsonSetter("gameItem__uniqueName")
    public void setGameItemUniqueName(String value) {
        this.gameItemUniqueName = value;
        markPresent("gameItem__uniqueName");
    }

    /** 游戏引用；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("gameRef")
    private String gameRef;

    /**
     * 记录游戏引用是否出现，同时区分未提供与显式空值。
     *
     * @param value 游戏引用，保留协议中的显式空值。
     */
    @JsonSetter("gameRef")
    public void setGameRef(String value) {
        this.gameRef = value;
        markPresent("gameRef");
    }

    /** 游戏引用状态；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("gameRefStatus")
    private String gameRefStatus;

    /**
     * 记录游戏引用状态是否出现，同时区分未提供与显式空值。
     *
     * @param value 游戏引用状态，保留协议中的显式空值。
     */
    @JsonSetter("gameRefStatus")
    public void setGameRefStatus(String value) {
        this.gameRefStatus = value;
        markPresent("gameRefStatus");
    }

    /** 图标；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("icon")
    private String icon;

    /**
     * 记录图标是否出现，同时区分未提供与显式空值。
     *
     * @param value 图标，保留协议中的显式空值。
     */
    @JsonSetter("icon")
    public void setIcon(String value) {
        this.icon = value;
        markPresent("icon");
    }

    /** 图标状态；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("iconStatus")
    private String iconStatus;

    /**
     * 记录图标状态是否出现，同时区分未提供与显式空值。
     *
     * @param value 图标状态，保留协议中的显式空值。
     */
    @JsonSetter("iconStatus")
    public void setIconStatus(String value) {
        this.iconStatus = value;
        markPresent("iconStatus");
    }

    /** 图标地址；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("iconUrl")
    private String iconUrl;

    /**
     * 记录图标地址是否出现，同时区分未提供与显式空值。
     *
     * @param value 图标地址，保留协议中的显式空值。
     */
    @JsonSetter("iconUrl")
    public void setIconUrl(String value) {
        this.iconUrl = value;
        markPresent("iconUrl");
    }

    /** 标识；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("id")
    private String id;

    /**
     * 记录标识是否出现，同时区分未提供与显式空值。
     *
     * @param value 标识，保留协议中的显式空值。
     */
    @JsonSetter("id")
    public void setId(String value) {
        this.id = value;
        markPresent("id");
    }

    /** 琥珀星最大镶嵌数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("maxAmberStars")
    private BigDecimal maxAmberStars;

    /**
     * 记录琥珀星最大镶嵌数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 琥珀星最大镶嵌数量，保留协议中的显式空值。
     */
    @JsonSetter("maxAmberStars")
    public void setMaxAmberStars(BigDecimal value) {
        this.maxAmberStars = value;
        markPresent("maxAmberStars");
    }

    /** 青蓝星最大镶嵌数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("maxCyanStars")
    private BigDecimal maxCyanStars;

    /**
     * 记录青蓝星最大镶嵌数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 青蓝星最大镶嵌数量，保留协议中的显式空值。
     */
    @JsonSetter("maxCyanStars")
    public void setMaxCyanStars(BigDecimal value) {
        this.maxCyanStars = value;
        markPresent("maxCyanStars");
    }

    /** 最高等级；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("maxRank")
    private BigDecimal maxRank;

    /**
     * 记录最高等级是否出现，同时区分未提供与显式空值。
     *
     * @param value 最高等级，保留协议中的显式空值。
     */
    @JsonSetter("maxRank")
    public void setMaxRank(BigDecimal value) {
        this.maxRank = value;
        markPresent("maxRank");
    }

    /** 名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("name")
    private String name;

    /**
     * 记录名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 名称，保留协议中的显式空值。
     */
    @JsonSetter("name")
    public void setName(String value) {
        this.name = value;
        markPresent("name");
    }

    /** 英文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("nameEn")
    private String nameEn;

    /**
     * 记录英文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 英文名称，保留协议中的显式空值。
     */
    @JsonSetter("nameEn")
    public void setNameEn(String value) {
        this.nameEn = value;
        markPresent("nameEn");
    }

    /** 记录键；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("recordKey")
    private String recordKey;

    /**
     * 记录记录键是否出现，同时区分未提供与显式空值。
     *
     * @param value 记录键，保留协议中的显式空值。
     */
    @JsonSetter("recordKey")
    public void setRecordKey(String value) {
        this.recordKey = value;
        markPresent("recordKey");
    }

    /** 网址标识；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("slug")
    private String slug;

    /**
     * 记录网址标识是否出现，同时区分未提供与显式空值。
     *
     * @param value 网址标识，保留协议中的显式空值。
     */
    @JsonSetter("slug")
    public void setSlug(String value) {
        this.slug = value;
        markPresent("slug");
    }

    /** 次级图标；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("subIcon")
    private String subIcon;

    /**
     * 记录次级图标是否出现，同时区分未提供与显式空值。
     *
     * @param value 次级图标，保留协议中的显式空值。
     */
    @JsonSetter("subIcon")
    public void setSubIcon(String value) {
        this.subIcon = value;
        markPresent("subIcon");
    }

    /** 子类型列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("subtypes")
    private List<String> subtypes;

    /**
     * 记录子类型列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 子类型列表，保留协议中的显式空值。
     */
    @JsonSetter("subtypes")
    public void setSubtypes(List<String> value) {
        this.subtypes = value;
        markPresent("subtypes");
    }

    /** 标签列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("tags")
    private List<String> tags;

    /**
     * 记录标签列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 标签列表，保留协议中的显式空值。
     */
    @JsonSetter("tags")
    public void setTags(List<String> value) {
        this.tags = value;
        markPresent("tags");
    }

    /** 缩略图；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("thumb")
    private String thumb;

    /**
     * 记录缩略图是否出现，同时区分未提供与显式空值。
     *
     * @param value 缩略图，保留协议中的显式空值。
     */
    @JsonSetter("thumb")
    public void setThumb(String value) {
        this.thumb = value;
        markPresent("thumb");
    }

    /** 缩略图地址；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("thumbUrl")
    private String thumbUrl;

    /**
     * 记录缩略图地址是否出现，同时区分未提供与显式空值。
     *
     * @param value 缩略图地址，保留协议中的显式空值。
     */
    @JsonSetter("thumbUrl")
    public void setThumbUrl(String value) {
        this.thumbUrl = value;
        markPresent("thumbUrl");
    }

    /** 网址标识；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("url_name")
    private String urlName;

    /**
     * 记录网址标识是否出现，同时区分未提供与显式空值。
     *
     * @param value 网址标识，保留协议中的显式空值。
     */
    @JsonSetter("url_name")
    public void setUrlName(String value) {
        this.urlName = value;
        markPresent("url_name");
    }

    /** 是否已入库；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("vaulted")
    private Boolean vaulted;

    /**
     * 记录是否已入库是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否已入库，保留协议中的显式空值。
     */
    @JsonSetter("vaulted")
    public void setVaulted(Boolean value) {
        this.vaulted = value;
        markPresent("vaulted");
    }

    /** 中文图标；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("zhIcon")
    @JsonDeserialize(using = OfficialNullDeserializer.class)
    private Void zhIcon;

    /**
     * 记录中文图标是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文图标，保留协议中的显式空值。
     */
    @JsonSetter("zhIcon")
    public void setZhIcon(Void value) {
        this.zhIcon = value;
        markPresent("zhIcon");
    }

    /** 中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("zhName")
    private String zhName;

    /**
     * 记录中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文名称，保留协议中的显式空值。
     */
    @JsonSetter("zhName")
    public void setZhName(String value) {
        this.zhName = value;
        markPresent("zhName");
    }

    /** 中文次级图标；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("zhSubIcon")
    @JsonDeserialize(using = OfficialNullDeserializer.class)
    private Void zhSubIcon;

    /**
     * 记录中文次级图标是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文次级图标，保留协议中的显式空值。
     */
    @JsonSetter("zhSubIcon")
    public void setZhSubIcon(Void value) {
        this.zhSubIcon = value;
        markPresent("zhSubIcon");
    }

    /** 中文缩略图；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("zhThumb")
    @JsonDeserialize(using = OfficialNullDeserializer.class)
    private Void zhThumb;

    /**
     * 记录中文缩略图是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文缩略图，保留协议中的显式空值。
     */
    @JsonSetter("zhThumb")
    public void setZhThumb(Void value) {
        this.zhThumb = value;
        markPresent("zhThumb");
    }

    /**
     * 提取本记录的常用检索字段，不携带 JPA 受管状态。
     *
     * @return 名称、分类和说明等检索摘要；上游缺省字段保持为空。
     */
    @Override
    public OfficialRecordHeaderDTO header() {
        return new OfficialRecordHeaderDTO(OfficialInputText.first(nameEn, name), OfficialInputText.first(zhName), null, null, null);
    }

    /**
     * 读取上游显式业务键，不使用数组位置生成身份。
     *
     * @return 上游提供的首个稳定业务键；未提供时为空。
     */
    @Override
    public String stableKey() {
        return OfficialInputText.first(recordKey, id, slug);
    }

}
