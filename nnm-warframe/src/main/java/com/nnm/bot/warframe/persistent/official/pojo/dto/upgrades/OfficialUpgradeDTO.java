package com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialInputText;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

/** UPGRADE 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialUpgradeDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 可用挑战列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("availableChallenges")
    private List<AvailableChallengesDTO> availableChallenges;

    /**
     * 记录可用挑战列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 可用挑战列表，保留协议中的显式空值。
     */
    @JsonSetter("availableChallenges")
    public void setAvailableChallenges(List<AvailableChallengesDTO> value) {
        this.availableChallenges = value;
        markPresent("availableChallenges");
    }

    /** 头像地址；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("avatarUrl")
    private String avatarUrl;

    /**
     * 记录头像地址是否出现，同时区分未提供与显式空值。
     *
     * @param value 头像地址，保留协议中的显式空值。
     */
    @JsonSetter("avatarUrl")
    public void setAvatarUrl(String value) {
        this.avatarUrl = value;
        markPresent("avatarUrl");
    }

    /** 基础容量消耗；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("baseDrain")
    private BigDecimal baseDrain;

    /**
     * 记录基础容量消耗是否出现，同时区分未提供与显式空值。
     *
     * @param value 基础容量消耗，保留协议中的显式空值。
     */
    @JsonSetter("baseDrain")
    public void setBaseDrain(BigDecimal value) {
        this.baseDrain = value;
        markPresent("baseDrain");
    }

    /** 是否为资料库隐藏条目；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("codexSecret")
    private Boolean codexSecret;

    /**
     * 记录是否为资料库隐藏条目是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否为资料库隐藏条目，保留协议中的显式空值。
     */
    @JsonSetter("codexSecret")
    public void setCodexSecret(Boolean value) {
        this.codexSecret = value;
        markPresent("codexSecret");
    }

    /** 兼容对象；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("compat")
    private String compat;

    /**
     * 记录兼容对象是否出现，同时区分未提供与显式空值。
     *
     * @param value 兼容对象，保留协议中的显式空值。
     */
    @JsonSetter("compat")
    public void setCompat(String value) {
        this.compat = value;
        markPresent("compat");
    }

    /** 兼容性标签列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("compatibilityTags")
    private List<String> compatibilityTags;

    /**
     * 记录兼容性标签列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 兼容性标签列表，保留协议中的显式空值。
     */
    @JsonSetter("compatibilityTags")
    public void setCompatibilityTags(List<String> value) {
        this.compatibilityTags = value;
        markPresent("compatibilityTags");
    }

    /** 兼容物品列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("compatibleItems")
    private List<String> compatibleItems;

    /**
     * 记录兼容物品列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 兼容物品列表，保留协议中的显式空值。
     */
    @JsonSetter("compatibleItems")
    public void setCompatibleItems(List<String> value) {
        this.compatibleItems = value;
        markPresent("compatibleItems");
    }

    /** 兼容对象名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("compatName")
    private String compatName;

    /**
     * 记录兼容对象名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 兼容对象名称，保留协议中的显式空值。
     */
    @JsonSetter("compatName")
    public void setCompatName(String value) {
        this.compatName = value;
        markPresent("compatName");
    }

    /** 兼容对象中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("compatNameZh")
    private String compatNameZh;

    /**
     * 记录兼容对象中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 兼容对象中文名称，保留协议中的显式空值。
     */
    @JsonSetter("compatNameZh")
    public void setCompatNameZh(String value) {
        this.compatNameZh = value;
        markPresent("compatNameZh");
    }

    /** 描述；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("description")
    private String description;

    /**
     * 记录描述是否出现，同时区分未提供与显式空值。
     *
     * @param value 描述，保留协议中的显式空值。
     */
    @JsonSetter("description")
    public void setDescription(String value) {
        this.description = value;
        markPresent("description");
    }

    /** 英文描述；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("descriptionEn")
    private String descriptionEn;

    /**
     * 记录英文描述是否出现，同时区分未提供与显式空值。
     *
     * @param value 英文描述，保留协议中的显式空值。
     */
    @JsonSetter("descriptionEn")
    public void setDescriptionEn(String value) {
        this.descriptionEn = value;
        markPresent("descriptionEn");
    }

    /** 分行描述列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("descriptionLines")
    private List<DescriptionLinesDTO> descriptionLines;

    /**
     * 记录分行描述列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 分行描述列表，保留协议中的显式空值。
     */
    @JsonSetter("descriptionLines")
    public void setDescriptionLines(List<DescriptionLinesDTO> value) {
        this.descriptionLines = value;
        markPresent("descriptionLines");
    }

    /** 中文描述；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("descriptionZh")
    private String descriptionZh;

    /**
     * 记录中文描述是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文描述，保留协议中的显式空值。
     */
    @JsonSetter("descriptionZh")
    public void setDescriptionZh(String value) {
        this.descriptionZh = value;
        markPresent("descriptionZh");
    }

    /** 是否排除在资料库之外；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("excludeFromCodex")
    private Boolean excludeFromCodex;

    /**
     * 记录是否排除在资料库之外是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否排除在资料库之外，保留协议中的显式空值。
     */
    @JsonSetter("excludeFromCodex")
    public void setExcludeFromCodex(Boolean value) {
        this.excludeFromCodex = value;
        markPresent("excludeFromCodex");
    }

    /** 融合等级上限；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("fusionLimit")
    private BigDecimal fusionLimit;

    /**
     * 记录融合等级上限是否出现，同时区分未提供与显式空值。
     *
     * @param value 融合等级上限，保留协议中的显式空值。
     */
    @JsonSetter("fusionLimit")
    public void setFusionLimit(BigDecimal value) {
        this.fusionLimit = value;
        markPresent("fusionLimit");
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

    /** 图标资源；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("iconAsset")
    private String iconAsset;

    /**
     * 记录图标资源是否出现，同时区分未提供与显式空值。
     *
     * @param value 图标资源，保留协议中的显式空值。
     */
    @JsonSetter("iconAsset")
    public void setIconAsset(String value) {
        this.iconAsset = value;
        markPresent("iconAsset");
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

    /** 不兼容标签列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("incompatibilityTags")
    private List<String> incompatibilityTags;

    /**
     * 记录不兼容标签列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 不兼容标签列表，保留协议中的显式空值。
     */
    @JsonSetter("incompatibilityTags")
    public void setIncompatibilityTags(List<String> value) {
        this.incompatibilityTags = value;
        markPresent("incompatibilityTags");
    }

    /** 首次推出时间；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("introducedAt")
    private BigDecimal introducedAt;

    /**
     * 记录首次推出时间是否出现，同时区分未提供与显式空值。
     *
     * @param value 首次推出时间，保留协议中的显式空值。
     */
    @JsonSetter("introducedAt")
    public void setIntroducedAt(BigDecimal value) {
        this.introducedAt = value;
        markPresent("introducedAt");
    }

    /** 是否为非必要物品；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("isFrivolous")
    private Boolean isFrivolous;

    /**
     * 记录是否为非必要物品是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否为非必要物品，保留协议中的显式空值。
     */
    @JsonSetter("isFrivolous")
    public void setIsFrivolous(Boolean value) {
        this.isFrivolous = value;
        markPresent("isFrivolous");
    }

    /** 是否为初始物品；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("isStarter")
    private Boolean isStarter;

    /**
     * 记录是否为初始物品是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否为初始物品，保留协议中的显式空值。
     */
    @JsonSetter("isStarter")
    public void setIsStarter(Boolean value) {
        this.isStarter = value;
        markPresent("isStarter");
    }

    /** 是否为功能类物品；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("isUtility")
    private Boolean isUtility;

    /**
     * 记录是否为功能类物品是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否为功能类物品，保留协议中的显式空值。
     */
    @JsonSetter("isUtility")
    public void setIsUtility(Boolean value) {
        this.isUtility = value;
        markPresent("isUtility");
    }

    /** 物品引用；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("itemReferences")
    private List<ItemReferencesDTO> itemReferences;

    /**
     * 记录物品引用是否出现，同时区分未提供与显式空值。
     *
     * @param value 物品引用，保留协议中的显式空值。
     */
    @JsonSetter("itemReferences")
    public void setItemReferences(List<ItemReferencesDTO> value) {
        this.itemReferences = value;
        markPresent("itemReferences");
    }

    /** 套装振幅晶体配置；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("modSet")
    private String modSet;

    /**
     * 记录套装振幅晶体配置是否出现，同时区分未提供与显式空值。
     *
     * @param value 套装振幅晶体配置，保留协议中的显式空值。
     */
    @JsonSetter("modSet")
    public void setModSet(String value) {
        this.modSet = value;
        markPresent("modSet");
    }

    /** 套装振幅晶体加成列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("modSetValues")
    private List<BigDecimal> modSetValues;

    /**
     * 记录套装振幅晶体加成列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 套装振幅晶体加成列表，保留协议中的显式空值。
     */
    @JsonSetter("modSetValues")
    public void setModSetValues(List<BigDecimal> value) {
        this.modSetValues = value;
        markPresent("modSetValues");
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

    /** 名称本地化键；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("nameKey")
    private String nameKey;

    /**
     * 记录名称本地化键是否出现，同时区分未提供与显式空值。
     *
     * @param value 名称本地化键，保留协议中的显式空值。
     */
    @JsonSetter("nameKey")
    public void setNameKey(String value) {
        this.nameKey = value;
        markPresent("nameKey");
    }

    /** 中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("nameZh")
    private String nameZh;

    /**
     * 记录中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文名称，保留协议中的显式空值。
     */
    @JsonSetter("nameZh")
    public void setNameZh(String value) {
        this.nameZh = value;
        markPresent("nameZh");
    }

    /** 极性；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("polarity")
    private String polarity;

    /**
     * 记录极性是否出现，同时区分未提供与显式空值。
     *
     * @param value 极性，保留协议中的显式空值。
     */
    @JsonSetter("polarity")
    public void setPolarity(String value) {
        this.polarity = value;
        markPresent("polarity");
    }

    /** 等级配置列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("ranks")
    private List<RanksDTO> ranks;

    /**
     * 记录等级配置列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 等级配置列表，保留协议中的显式空值。
     */
    @JsonSetter("ranks")
    public void setRanks(List<RanksDTO> value) {
        this.ranks = value;
        markPresent("ranks");
    }

    /** 稀有度；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("rarity")
    private String rarity;

    /**
     * 记录稀有度是否出现，同时区分未提供与显式空值。
     *
     * @param value 稀有度，保留协议中的显式空值。
     */
    @JsonSetter("rarity")
    public void setRarity(String value) {
        this.rarity = value;
        markPresent("rarity");
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

    /** 子类型；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("subtype")
    private String subtype;

    /**
     * 记录子类型是否出现，同时区分未提供与显式空值。
     *
     * @param value 子类型，保留协议中的显式空值。
     */
    @JsonSetter("subtype")
    public void setSubtype(String value) {
        this.subtype = value;
        markPresent("subtype");
    }

    /** 可交易；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("tradable")
    private Boolean tradable;

    /**
     * 记录可交易是否出现，同时区分未提供与显式空值。
     *
     * @param value 可交易，保留协议中的显式空值。
     */
    @JsonSetter("tradable")
    public void setTradable(Boolean value) {
        this.tradable = value;
        markPresent("tradable");
    }

    /** 类型；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("type")
    private String type;

    /**
     * 记录类型是否出现，同时区分未提供与显式空值。
     *
     * @param value 类型，保留协议中的显式空值。
     */
    @JsonSetter("type")
    public void setType(String value) {
        this.type = value;
        markPresent("type");
    }

    /** 唯一标识；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("uniqueName")
    private String uniqueName;

    /**
     * 记录唯一标识是否出现，同时区分未提供与显式空值。
     *
     * @param value 唯一标识，保留协议中的显式空值。
     */
    @JsonSetter("uniqueName")
    public void setUniqueName(String value) {
        this.uniqueName = value;
        markPresent("uniqueName");
    }

    /** 强化条目列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("upgradeEntries")
    private List<UpgradeEntriesDTO> upgradeEntries;

    /**
     * 记录强化条目列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 强化条目列表，保留协议中的显式空值。
     */
    @JsonSetter("upgradeEntries")
    public void setUpgradeEntries(List<UpgradeEntriesDTO> value) {
        this.upgradeEntries = value;
        markPresent("upgradeEntries");
    }

    /** 强化类型；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("upgradeType")
    private String upgradeType;

    /**
     * 记录强化类型是否出现，同时区分未提供与显式空值。
     *
     * @param value 强化类型，保留协议中的显式空值。
     */
    @JsonSetter("upgradeType")
    public void setUpgradeType(String value) {
        this.upgradeType = value;
        markPresent("upgradeType");
    }

    /**
     * 提取本记录的常用检索字段，不携带 JPA 受管状态。
     *
     * @return 名称、分类和说明等检索摘要；上游缺省字段保持为空。
     */
    @Override
    public OfficialRecordHeaderDTO header() {
        return new OfficialRecordHeaderDTO(OfficialInputText.first(nameEn, name), OfficialInputText.first(nameZh), null, null, OfficialInputText.first(descriptionZh, descriptionEn, description));
    }

    /**
     * 读取上游显式业务键，不使用数组位置生成身份。
     *
     * @return 上游提供的首个稳定业务键；未提供时为空。
     */
    @Override
    public String stableKey() {
        return OfficialInputText.first(recordKey, uniqueName);
    }

    /** 官方嵌套对象 AvailableChallengesComplicationsItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class AvailableChallengesComplicationsItemReferencesDTO extends OfficialObjectDTO {
        /** 可用挑战列表的挑战附加条件列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录可用挑战列表的挑战附加条件列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的挑战附加条件列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 可用挑战列表的挑战附加条件列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录可用挑战列表的挑战附加条件列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的挑战附加条件列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 可用挑战列表的挑战附加条件列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录可用挑战列表的挑战附加条件列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的挑战附加条件列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 可用挑战列表的挑战附加条件列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录可用挑战列表的挑战附加条件列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的挑战附加条件列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 可用挑战列表的挑战附加条件列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录可用挑战列表的挑战附加条件列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的挑战附加条件列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 可用挑战列表的挑战附加条件列表的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录可用挑战列表的挑战附加条件列表的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的挑战附加条件列表的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 可用挑战列表的挑战附加条件列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录可用挑战列表的挑战附加条件列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的挑战附加条件列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 可用挑战列表的挑战附加条件列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录可用挑战列表的挑战附加条件列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的挑战附加条件列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 AvailableChallengesComplicationsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class AvailableChallengesComplicationsDTO extends OfficialObjectDTO {
        /** 可用挑战列表的挑战附加条件列表的描述；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("description")
        private String description;

        /**
         * 记录可用挑战列表的挑战附加条件列表的描述是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的挑战附加条件列表的描述，保留协议中的显式空值。
         */
        @JsonSetter("description")
        public void setDescription(String value) {
            this.description = value;
            markPresent("description");
        }

        /** 可用挑战列表的挑战附加条件列表的完整名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("fullName")
        private String fullName;

        /**
         * 记录可用挑战列表的挑战附加条件列表的完整名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的挑战附加条件列表的完整名称，保留协议中的显式空值。
         */
        @JsonSetter("fullName")
        public void setFullName(String value) {
            this.fullName = value;
            markPresent("fullName");
        }

        /** 可用挑战列表的挑战附加条件列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<AvailableChallengesComplicationsItemReferencesDTO> itemReferences;

        /**
         * 记录可用挑战列表的挑战附加条件列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的挑战附加条件列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<AvailableChallengesComplicationsItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 可用挑战列表的挑战附加条件列表的覆盖标签；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("overrideTag")
        private String overrideTag;

        /**
         * 记录可用挑战列表的挑战附加条件列表的覆盖标签是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的挑战附加条件列表的覆盖标签，保留协议中的显式空值。
         */
        @JsonSetter("overrideTag")
        public void setOverrideTag(String value) {
            this.overrideTag = value;
            markPresent("overrideTag");
        }

    }

    /** 官方嵌套对象 AvailableChallengesItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class AvailableChallengesItemReferencesDTO extends OfficialObjectDTO {
        /** 可用挑战列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录可用挑战列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 可用挑战列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录可用挑战列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 可用挑战列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录可用挑战列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 可用挑战列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录可用挑战列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 可用挑战列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录可用挑战列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 可用挑战列表的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录可用挑战列表的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 可用挑战列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录可用挑战列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 可用挑战列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录可用挑战列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 AvailableChallengesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class AvailableChallengesDTO extends OfficialObjectDTO {
        /** 可用挑战列表的挑战附加条件列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("complications")
        private List<AvailableChallengesComplicationsDTO> complications;

        /**
         * 记录可用挑战列表的挑战附加条件列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的挑战附加条件列表，保留协议中的显式空值。
         */
        @JsonSetter("complications")
        public void setComplications(List<AvailableChallengesComplicationsDTO> value) {
            this.complications = value;
            markPresent("complications");
        }

        /** 可用挑战列表的描述；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("description")
        private String description;

        /**
         * 记录可用挑战列表的描述是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的描述，保留协议中的显式空值。
         */
        @JsonSetter("description")
        public void setDescription(String value) {
            this.description = value;
            markPresent("description");
        }

        /** 可用挑战列表的完整名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("fullName")
        private String fullName;

        /**
         * 记录可用挑战列表的完整名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的完整名称，保留协议中的显式空值。
         */
        @JsonSetter("fullName")
        public void setFullName(String value) {
            this.fullName = value;
            markPresent("fullName");
        }

        /** 可用挑战列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<AvailableChallengesItemReferencesDTO> itemReferences;

        /**
         * 记录可用挑战列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 可用挑战列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<AvailableChallengesItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

    }

    /** 官方嵌套对象 DescriptionLinesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class DescriptionLinesDTO extends OfficialObjectDTO {
        /** 分行描述列表的文本行序号；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("lineIndex")
        private BigDecimal lineIndex;

        /**
         * 记录分行描述列表的文本行序号是否出现，同时区分未提供与显式空值。
         *
         * @param value 分行描述列表的文本行序号，保留协议中的显式空值。
         */
        @JsonSetter("lineIndex")
        public void setLineIndex(BigDecimal value) {
            this.lineIndex = value;
            markPresent("lineIndex");
        }

        /** 分行描述列表的文本；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("text")
        private String text;

        /**
         * 记录分行描述列表的文本是否出现，同时区分未提供与显式空值。
         *
         * @param value 分行描述列表的文本，保留协议中的显式空值。
         */
        @JsonSetter("text")
        public void setText(String value) {
            this.text = value;
            markPresent("text");
        }

        /** 分行描述列表的中文文本；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("zhText")
        private String zhText;

        /**
         * 记录分行描述列表的中文文本是否出现，同时区分未提供与显式空值。
         *
         * @param value 分行描述列表的中文文本，保留协议中的显式空值。
         */
        @JsonSetter("zhText")
        public void setZhText(String value) {
            this.zhText = value;
            markPresent("zhText");
        }

    }

    /** 官方嵌套对象 ItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ItemReferencesDTO extends OfficialObjectDTO {
        /** 物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 RanksStatsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class RanksStatsDTO extends OfficialObjectDTO {
        /** 等级配置列表的属性统计的文本行序号；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("lineIndex")
        private BigDecimal lineIndex;

        /**
         * 记录等级配置列表的属性统计的文本行序号是否出现，同时区分未提供与显式空值。
         *
         * @param value 等级配置列表的属性统计的文本行序号，保留协议中的显式空值。
         */
        @JsonSetter("lineIndex")
        public void setLineIndex(BigDecimal value) {
            this.lineIndex = value;
            markPresent("lineIndex");
        }

        /** 等级配置列表的属性统计的文本；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("text")
        private String text;

        /**
         * 记录等级配置列表的属性统计的文本是否出现，同时区分未提供与显式空值。
         *
         * @param value 等级配置列表的属性统计的文本，保留协议中的显式空值。
         */
        @JsonSetter("text")
        public void setText(String value) {
            this.text = value;
            markPresent("text");
        }

        /** 等级配置列表的属性统计的中文文本；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("zhText")
        private String zhText;

        /**
         * 记录等级配置列表的属性统计的中文文本是否出现，同时区分未提供与显式空值。
         *
         * @param value 等级配置列表的属性统计的中文文本，保留协议中的显式空值。
         */
        @JsonSetter("zhText")
        public void setZhText(String value) {
            this.zhText = value;
            markPresent("zhText");
        }

    }

    /** 官方嵌套对象 RanksDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class RanksDTO extends OfficialObjectDTO {
        /** 等级配置列表的等级；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rank")
        private BigDecimal rank;

        /**
         * 记录等级配置列表的等级是否出现，同时区分未提供与显式空值。
         *
         * @param value 等级配置列表的等级，保留协议中的显式空值。
         */
        @JsonSetter("rank")
        public void setRank(BigDecimal value) {
            this.rank = value;
            markPresent("rank");
        }

        /** 等级配置列表的属性统计；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("stats")
        private List<RanksStatsDTO> stats;

        /**
         * 记录等级配置列表的属性统计是否出现，同时区分未提供与显式空值。
         *
         * @param value 等级配置列表的属性统计，保留协议中的显式空值。
         */
        @JsonSetter("stats")
        public void setStats(List<RanksStatsDTO> value) {
            this.stats = value;
            markPresent("stats");
        }

    }

    /** 官方嵌套对象 UpgradeEntriesUpgradeValuesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class UpgradeEntriesUpgradeValuesDTO extends OfficialObjectDTO {
        /** 强化条目列表的强化数值列表的本地化文本标签；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("locTag")
        private String locTag;

        /**
         * 记录强化条目列表的强化数值列表的本地化文本标签是否出现，同时区分未提供与显式空值。
         *
         * @param value 强化条目列表的强化数值列表的本地化文本标签，保留协议中的显式空值。
         */
        @JsonSetter("locTag")
        public void setLocTag(String value) {
            this.locTag = value;
            markPresent("locTag");
        }

        /** 强化条目列表的强化数值列表的是否反转数值符号；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("reverseValueSymbol")
        private Boolean reverseValueSymbol;

        /**
         * 记录强化条目列表的强化数值列表的是否反转数值符号是否出现，同时区分未提供与显式空值。
         *
         * @param value 强化条目列表的强化数值列表的是否反转数值符号，保留协议中的显式空值。
         */
        @JsonSetter("reverseValueSymbol")
        public void setReverseValueSymbol(Boolean value) {
            this.reverseValueSymbol = value;
            markPresent("reverseValueSymbol");
        }

        /** 强化条目列表的强化数值列表的数值；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("value")
        private BigDecimal value;

        /**
         * 记录强化条目列表的强化数值列表的数值是否出现，同时区分未提供与显式空值。
         *
         * @param value 强化条目列表的强化数值列表的数值，保留协议中的显式空值。
         */
        @JsonSetter("value")
        public void setValue(BigDecimal value) {
            this.value = value;
            markPresent("value");
        }

    }

    /** 官方嵌套对象 UpgradeEntriesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class UpgradeEntriesDTO extends OfficialObjectDTO {
        /** 强化条目列表的前缀标签；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("prefixTag")
        private String prefixTag;

        /**
         * 记录强化条目列表的前缀标签是否出现，同时区分未提供与显式空值。
         *
         * @param value 强化条目列表的前缀标签，保留协议中的显式空值。
         */
        @JsonSetter("prefixTag")
        public void setPrefixTag(String value) {
            this.prefixTag = value;
            markPresent("prefixTag");
        }

        /** 强化条目列表的后缀标签；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("suffixTag")
        private String suffixTag;

        /**
         * 记录强化条目列表的后缀标签是否出现，同时区分未提供与显式空值。
         *
         * @param value 强化条目列表的后缀标签，保留协议中的显式空值。
         */
        @JsonSetter("suffixTag")
        public void setSuffixTag(String value) {
            this.suffixTag = value;
            markPresent("suffixTag");
        }

        /** 强化条目列表的标签；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("tag")
        private String tag;

        /**
         * 记录强化条目列表的标签是否出现，同时区分未提供与显式空值。
         *
         * @param value 强化条目列表的标签，保留协议中的显式空值。
         */
        @JsonSetter("tag")
        public void setTag(String value) {
            this.tag = value;
            markPresent("tag");
        }

        /** 强化条目列表的强化数值列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("upgradeValues")
        private List<UpgradeEntriesUpgradeValuesDTO> upgradeValues;

        /**
         * 记录强化条目列表的强化数值列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 强化条目列表的强化数值列表，保留协议中的显式空值。
         */
        @JsonSetter("upgradeValues")
        public void setUpgradeValues(List<UpgradeEntriesUpgradeValuesDTO> value) {
            this.upgradeValues = value;
            markPresent("upgradeValues");
        }

    }

}
