package com.nnm.bot.warframe.persistent.official.pojo.dto.items;

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

/** SKIN 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialSkinDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 额外物品列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("additionalItems")
    private List<String> additionalItems;

    /**
     * 记录额外物品列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 额外物品列表，保留协议中的显式空值。
     */
    @JsonSetter("additionalItems")
    public void setAdditionalItems(List<String> value) {
        this.additionalItems = value;
        markPresent("additionalItems");
    }

    /** 是否始终可用；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("alwaysAvailable")
    private Boolean alwaysAvailable;

    /**
     * 记录是否始终可用是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否始终可用，保留协议中的显式空值。
     */
    @JsonSetter("alwaysAvailable")
    public void setAlwaysAvailable(Boolean value) {
        this.alwaysAvailable = value;
        markPresent("alwaysAvailable");
    }

    /** 基础；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("base")
    private String base;

    /**
     * 记录基础是否出现，同时区分未提供与显式空值。
     *
     * @param value 基础，保留协议中的显式空值。
     */
    @JsonSetter("base")
    public void setBase(String value) {
        this.base = value;
        markPresent("base");
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

    /** 不支持的平台列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("excludedPlatforms")
    private List<String> excludedPlatforms;

    /**
     * 记录不支持的平台列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 不支持的平台列表，保留协议中的显式空值。
     */
    @JsonSetter("excludedPlatforms")
    public void setExcludedPlatforms(List<String> value) {
        this.excludedPlatforms = value;
        markPresent("excludedPlatforms");
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

    /** 是否排除在商店之外；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("excludeFromMarket")
    private Boolean excludeFromMarket;

    /**
     * 记录是否排除在商店之外是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否排除在商店之外，保留协议中的显式空值。
     */
    @JsonSetter("excludeFromMarket")
    public void setExcludeFromMarket(Boolean value) {
        this.excludeFromMarket = value;
        markPresent("excludeFromMarket");
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

    /** 铂金消耗；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("platinumCost")
    private BigDecimal platinumCost;

    /**
     * 记录铂金消耗是否出现，同时区分未提供与显式空值。
     *
     * @param value 铂金消耗，保留协议中的显式空值。
     */
    @JsonSetter("platinumCost")
    public void setPlatinumCost(BigDecimal value) {
        this.platinumCost = value;
        markPresent("platinumCost");
    }

    /** 产品分类；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("productCategory")
    private String productCategory;

    /**
     * 记录产品分类是否出现，同时区分未提供与显式空值。
     *
     * @param value 产品分类，保留协议中的显式空值。
     */
    @JsonSetter("productCategory")
    public void setProductCategory(String value) {
        this.productCategory = value;
        markPresent("productCategory");
    }

    /** 随机强化列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("randomisedUpgrades")
    private List<RandomisedUpgradesDTO> randomisedUpgrades;

    /**
     * 记录随机强化列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 随机强化列表，保留协议中的显式空值。
     */
    @JsonSetter("randomisedUpgrades")
    public void setRandomisedUpgrades(List<RandomisedUpgradesDTO> value) {
        this.randomisedUpgrades = value;
        markPresent("randomisedUpgrades");
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

    /** 需求条件；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("requirement")
    private String requirement;

    /**
     * 记录需求条件是否出现，同时区分未提供与显式空值。
     *
     * @param value 需求条件，保留协议中的显式空值。
     */
    @JsonSetter("requirement")
    public void setRequirement(String value) {
        this.requirement = value;
        markPresent("requirement");
    }

    /** 子程序列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("subroutines")
    private List<String> subroutines;

    /**
     * 记录子程序列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 子程序列表，保留协议中的显式空值。
     */
    @JsonSetter("subroutines")
    public void setSubroutines(List<String> value) {
        this.subroutines = value;
        markPresent("subroutines");
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

    /**
     * 提取本记录的常用检索字段，不携带 JPA 受管状态。
     *
     * @return 名称、分类和说明等检索摘要；上游缺省字段保持为空。
     */
    @Override
    public OfficialRecordHeaderDTO header() {
        return new OfficialRecordHeaderDTO(OfficialInputText.first(nameEn, name), OfficialInputText.first(nameZh), null, OfficialInputText.first(productCategory), OfficialInputText.first(descriptionZh, descriptionEn, description));
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

    /** 官方嵌套对象 RandomisedUpgradesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class RandomisedUpgradesDTO extends OfficialObjectDTO {
        /** 随机强化列表的范围；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("range")
        private List<BigDecimal> range;

        /**
         * 记录随机强化列表的范围是否出现，同时区分未提供与显式空值。
         *
         * @param value 随机强化列表的范围，保留协议中的显式空值。
         */
        @JsonSetter("range")
        public void setRange(List<BigDecimal> value) {
            this.range = value;
            markPresent("range");
        }

        /** 随机强化列表的标签；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("tag")
        private String tag;

        /**
         * 记录随机强化列表的标签是否出现，同时区分未提供与显式空值。
         *
         * @param value 随机强化列表的标签，保留协议中的显式空值。
         */
        @JsonSetter("tag")
        public void setTag(String value) {
            this.tag = value;
            markPresent("tag");
        }

    }

}
