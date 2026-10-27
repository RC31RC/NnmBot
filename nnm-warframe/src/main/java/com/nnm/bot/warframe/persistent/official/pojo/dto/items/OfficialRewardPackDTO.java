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

/** REWARD_PACK 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialRewardPackDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 是否允许重复奖励；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("canGiveDuplicates")
    private Boolean canGiveDuplicates;

    /**
     * 记录是否允许重复奖励是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否允许重复奖励，保留协议中的显式空值。
     */
    @JsonSetter("canGiveDuplicates")
    public void setCanGiveDuplicates(Boolean value) {
        this.canGiveDuplicates = value;
        markPresent("canGiveDuplicates");
    }

    /** 组成部件列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("components")
    private List<ComponentsDTO> components;

    /**
     * 记录组成部件列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 组成部件列表，保留协议中的显式空值。
     */
    @JsonSetter("components")
    public void setComponents(List<ComponentsDTO> value) {
        this.components = value;
        markPresent("components");
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

    /** 每次抽取的稀有度权重；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("rarityWeightsPerRoll")
    private List<RarityWeightsPerRollDTO> rarityWeightsPerRoll;

    /**
     * 记录每次抽取的稀有度权重是否出现，同时区分未提供与显式空值。
     *
     * @param value 每次抽取的稀有度权重，保留协议中的显式空值。
     */
    @JsonSetter("rarityWeightsPerRoll")
    public void setRarityWeightsPerRoll(List<RarityWeightsPerRollDTO> value) {
        this.rarityWeightsPerRoll = value;
        markPresent("rarityWeightsPerRoll");
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
        return null;
    }

    /** 官方嵌套对象 ComponentsItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ComponentsItemReferencesDTO extends OfficialObjectDTO {
        /** 组成部件列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录组成部件列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 组成部件列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 组成部件列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录组成部件列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 组成部件列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 组成部件列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录组成部件列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 组成部件列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 组成部件列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录组成部件列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 组成部件列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 组成部件列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录组成部件列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 组成部件列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 组成部件列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录组成部件列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 组成部件列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 组成部件列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录组成部件列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 组成部件列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 组成部件列表的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录组成部件列表的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 组成部件列表的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 组成部件列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录组成部件列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 组成部件列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 组成部件列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录组成部件列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 组成部件列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 ComponentsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ComponentsDTO extends OfficialObjectDTO {
        /** 组成部件列表的数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("Amount")
        private BigDecimal amount;

        /**
         * 记录组成部件列表的数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 组成部件列表的数量，保留协议中的显式空值。
         */
        @JsonSetter("Amount")
        public void setAmount(BigDecimal value) {
            this.amount = value;
            markPresent("Amount");
        }

        /** 组成部件列表的物品；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("Item")
        private String item;

        /**
         * 记录组成部件列表的物品是否出现，同时区分未提供与显式空值。
         *
         * @param value 组成部件列表的物品，保留协议中的显式空值。
         */
        @JsonSetter("Item")
        public void setItem(String value) {
            this.item = value;
            markPresent("Item");
        }

        /** 组成部件列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<ComponentsItemReferencesDTO> itemReferences;

        /**
         * 记录组成部件列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 组成部件列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<ComponentsItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 组成部件列表的保底概率增长率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("PityIncreaseRate")
        private BigDecimal pityIncreaseRate;

        /**
         * 记录组成部件列表的保底概率增长率是否出现，同时区分未提供与显式空值。
         *
         * @param value 组成部件列表的保底概率增长率，保留协议中的显式空值。
         */
        @JsonSetter("PityIncreaseRate")
        public void setPityIncreaseRate(BigDecimal value) {
            this.pityIncreaseRate = value;
            markPresent("PityIncreaseRate");
        }

        /** 组成部件列表的概率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("Probability")
        private BigDecimal probability;

        /**
         * 记录组成部件列表的概率是否出现，同时区分未提供与显式空值。
         *
         * @param value 组成部件列表的概率，保留协议中的显式空值。
         */
        @JsonSetter("Probability")
        public void setProbability(BigDecimal value) {
            this.probability = value;
            markPresent("Probability");
        }

        /** 组成部件列表的稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("Rarity")
        private String rarity;

        /**
         * 记录组成部件列表的稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 组成部件列表的稀有度，保留协议中的显式空值。
         */
        @JsonSetter("Rarity")
        public void setRarity(String value) {
            this.rarity = value;
            markPresent("Rarity");
        }

    }

    /** 官方嵌套对象 RarityWeightsPerRollDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class RarityWeightsPerRollDTO extends OfficialObjectDTO {
        /** 每次抽取的稀有度权重的普通稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("COMMON")
        private BigDecimal common;

        /**
         * 记录每次抽取的稀有度权重的普通稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 每次抽取的稀有度权重的普通稀有度，保留协议中的显式空值。
         */
        @JsonSetter("COMMON")
        public void setCommon(BigDecimal value) {
            this.common = value;
            markPresent("COMMON");
        }

        /** 每次抽取的稀有度权重的传奇稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("LEGENDARY")
        private BigDecimal legendary;

        /**
         * 记录每次抽取的稀有度权重的传奇稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 每次抽取的稀有度权重的传奇稀有度，保留协议中的显式空值。
         */
        @JsonSetter("LEGENDARY")
        public void setLegendary(BigDecimal value) {
            this.legendary = value;
            markPresent("LEGENDARY");
        }

        /** 每次抽取的稀有度权重的稀有稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("RARE")
        private BigDecimal rare;

        /**
         * 记录每次抽取的稀有度权重的稀有稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 每次抽取的稀有度权重的稀有稀有度，保留协议中的显式空值。
         */
        @JsonSetter("RARE")
        public void setRare(BigDecimal value) {
            this.rare = value;
            markPresent("RARE");
        }

        /** 每次抽取的稀有度权重的罕见稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("UNCOMMON")
        private BigDecimal uncommon;

        /**
         * 记录每次抽取的稀有度权重的罕见稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 每次抽取的稀有度权重的罕见稀有度，保留协议中的显式空值。
         */
        @JsonSetter("UNCOMMON")
        public void setUncommon(BigDecimal value) {
            this.uncommon = value;
            markPresent("UNCOMMON");
        }

    }

}
