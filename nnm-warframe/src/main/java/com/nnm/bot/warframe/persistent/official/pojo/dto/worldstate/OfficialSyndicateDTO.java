package com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate;

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

/** SYNDICATE 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialSyndicateDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 集团立场关系；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("alignments")
    private AlignmentsDTO alignments;

    /**
     * 记录集团立场关系是否出现，同时区分未提供与显式空值。
     *
     * @param value 集团立场关系，保留协议中的显式空值。
     */
    @JsonSetter("alignments")
    public void setAlignments(AlignmentsDTO value) {
        this.alignments = value;
        markPresent("alignments");
    }

    /** 背景颜色的数值；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("backgroundColour__value")
    private String backgroundColourValue;

    /**
     * 记录背景颜色的数值是否出现，同时区分未提供与显式空值。
     *
     * @param value 背景颜色的数值，保留协议中的显式空值。
     */
    @JsonSetter("backgroundColour__value")
    public void setBackgroundColourValue(String value) {
        this.backgroundColourValue = value;
        markPresent("backgroundColour__value");
    }

    /** 颜色的数值；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("colour__value")
    private String colourValue;

    /**
     * 记录颜色的数值是否出现，同时区分未提供与显式空值。
     *
     * @param value 颜色的数值，保留协议中的显式空值。
     */
    @JsonSetter("colour__value")
    public void setColourValue(String value) {
        this.colourValue = value;
        markPresent("colour__value");
    }

    /** 每日挑战列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("dailyChallenges")
    private List<String> dailyChallenges;

    /**
     * 记录每日挑战列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 每日挑战列表，保留协议中的显式空值。
     */
    @JsonSetter("dailyChallenges")
    public void setDailyChallenges(List<String> value) {
        this.dailyChallenges = value;
        markPresent("dailyChallenges");
    }

    /** 每日购买限额分组；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("dailyLimitBin")
    private String dailyLimitBin;

    /**
     * 记录每日购买限额分组是否出现，同时区分未提供与显式空值。
     *
     * @param value 每日购买限额分组，保留协议中的显式空值。
     */
    @JsonSetter("dailyLimitBin")
    public void setDailyLimitBin(String value) {
        this.dailyLimitBin = value;
        markPresent("dailyLimitBin");
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

    /** 集团兑换商品列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("favours")
    private List<FavoursDTO> favours;

    /**
     * 记录集团兑换商品列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 集团兑换商品列表，保留协议中的显式空值。
     */
    @JsonSetter("favours")
    public void setFavours(List<FavoursDTO> value) {
        this.favours = value;
        markPresent("favours");
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

    /** 入门奖励；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("initiationReward")
    private String initiationReward;

    /**
     * 记录入门奖励是否出现，同时区分未提供与显式空值。
     *
     * @param value 入门奖励，保留协议中的显式空值。
     */
    @JsonSetter("initiationReward")
    public void setInitiationReward(String value) {
        this.initiationReward = value;
        markPresent("initiationReward");
    }

    /** 入门献祭要求的现金；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("initiationSacrifice__credits")
    private BigDecimal initiationSacrificeCredits;

    /**
     * 记录入门献祭要求的现金是否出现，同时区分未提供与显式空值。
     *
     * @param value 入门献祭要求的现金，保留协议中的显式空值。
     */
    @JsonSetter("initiationSacrifice__credits")
    public void setInitiationSacrificeCredits(BigDecimal value) {
        this.initiationSacrificeCredits = value;
        markPresent("initiationSacrifice__credits");
    }

    /** 入门献祭要求的物品列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("initiationSacrifice__items")
    private List<InitiationSacrificeItemsDTO> initiationSacrificeItems;

    /**
     * 记录入门献祭要求的物品列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 入门献祭要求的物品列表，保留协议中的显式空值。
     */
    @JsonSetter("initiationSacrifice__items")
    public void setInitiationSacrificeItems(List<InitiationSacrificeItemsDTO> value) {
        this.initiationSacrificeItems = value;
        markPresent("initiationSacrifice__items");
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

    /** 集团奖章列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("medallions")
    private List<MedallionsDTO> medallions;

    /**
     * 记录集团奖章列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 集团奖章列表，保留协议中的显式空值。
     */
    @JsonSetter("medallions")
    public void setMedallions(List<MedallionsDTO> value) {
        this.medallions = value;
        markPresent("medallions");
    }

    /** 奖章声望是否受每日上限限制；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("medallionsCappedByDailyLimit")
    private Boolean medallionsCappedByDailyLimit;

    /**
     * 记录奖章声望是否受每日上限限制是否出现，同时区分未提供与显式空值。
     *
     * @param value 奖章声望是否受每日上限限制，保留协议中的显式空值。
     */
    @JsonSetter("medallionsCappedByDailyLimit")
    public void setMedallionsCappedByDailyLimit(Boolean value) {
        this.medallionsCappedByDailyLimit = value;
        markPresent("medallionsCappedByDailyLimit");
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

    /** 集团等级称号列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("titles")
    private List<TitlesDTO> titles;

    /**
     * 记录集团等级称号列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 集团等级称号列表，保留协议中的显式空值。
     */
    @JsonSetter("titles")
    public void setTitles(List<TitlesDTO> value) {
        this.titles = value;
        markPresent("titles");
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

    /** 每周挑战列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("weeklyChallenges")
    private List<String> weeklyChallenges;

    /**
     * 记录每周挑战列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 每周挑战列表，保留协议中的显式空值。
     */
    @JsonSetter("weeklyChallenges")
    public void setWeeklyChallenges(List<String> value) {
        this.weeklyChallenges = value;
        markPresent("weeklyChallenges");
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
        return OfficialInputText.first(uniqueName);
    }

    /** 官方嵌套对象 AlignmentsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class AlignmentsDTO extends OfficialObjectDTO {
        /** 集团立场关系的均衡仲裁者；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ArbitersSyndicate")
        private BigDecimal arbitersSyndicate;

        /**
         * 记录集团立场关系的均衡仲裁者是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团立场关系的均衡仲裁者，保留协议中的显式空值。
         */
        @JsonSetter("ArbitersSyndicate")
        public void setArbitersSyndicate(BigDecimal value) {
            this.arbitersSyndicate = value;
            markPresent("ArbitersSyndicate");
        }

        /** 集团立场关系的中枢苏达；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("CephalonSudaSyndicate")
        private BigDecimal cephalonSudaSyndicate;

        /**
         * 记录集团立场关系的中枢苏达是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团立场关系的中枢苏达，保留协议中的显式空值。
         */
        @JsonSetter("CephalonSudaSyndicate")
        public void setCephalonSudaSyndicate(BigDecimal value) {
            this.cephalonSudaSyndicate = value;
            markPresent("CephalonSudaSyndicate");
        }

        /** 集团立场关系的新世间；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("NewLokaSyndicate")
        private BigDecimal newLokaSyndicate;

        /**
         * 记录集团立场关系的新世间是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团立场关系的新世间，保留协议中的显式空值。
         */
        @JsonSetter("NewLokaSyndicate")
        public void setNewLokaSyndicate(BigDecimal value) {
            this.newLokaSyndicate = value;
            markPresent("NewLokaSyndicate");
        }

        /** 集团立场关系的佩兰数列；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("PerrinSyndicate")
        private BigDecimal perrinSyndicate;

        /**
         * 记录集团立场关系的佩兰数列是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团立场关系的佩兰数列，保留协议中的显式空值。
         */
        @JsonSetter("PerrinSyndicate")
        public void setPerrinSyndicate(BigDecimal value) {
            this.perrinSyndicate = value;
            markPresent("PerrinSyndicate");
        }

        /** 集团立场关系的血色面纱；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("RedVeilSyndicate")
        private BigDecimal redVeilSyndicate;

        /**
         * 记录集团立场关系的血色面纱是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团立场关系的血色面纱，保留协议中的显式空值。
         */
        @JsonSetter("RedVeilSyndicate")
        public void setRedVeilSyndicate(BigDecimal value) {
            this.redVeilSyndicate = value;
            markPresent("RedVeilSyndicate");
        }

        /** 集团立场关系的钢铁防线；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("SteelMeridianSyndicate")
        private BigDecimal steelMeridianSyndicate;

        /**
         * 记录集团立场关系的钢铁防线是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团立场关系的钢铁防线，保留协议中的显式空值。
         */
        @JsonSetter("SteelMeridianSyndicate")
        public void setSteelMeridianSyndicate(BigDecimal value) {
            this.steelMeridianSyndicate = value;
            markPresent("SteelMeridianSyndicate");
        }

    }

    /** 官方嵌套对象 FavoursItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class FavoursItemReferencesDTO extends OfficialObjectDTO {
        /** 集团兑换商品列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录集团兑换商品列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团兑换商品列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 集团兑换商品列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录集团兑换商品列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团兑换商品列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 集团兑换商品列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录集团兑换商品列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团兑换商品列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 集团兑换商品列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录集团兑换商品列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团兑换商品列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 集团兑换商品列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录集团兑换商品列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团兑换商品列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 集团兑换商品列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录集团兑换商品列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团兑换商品列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 集团兑换商品列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录集团兑换商品列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团兑换商品列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 集团兑换商品列表的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录集团兑换商品列表的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团兑换商品列表的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 集团兑换商品列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录集团兑换商品列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团兑换商品列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 集团兑换商品列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录集团兑换商品列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团兑换商品列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 FavoursDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class FavoursDTO extends OfficialObjectDTO {
        /** 集团兑换商品列表的现金消耗；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("creditsCost")
        private BigDecimal creditsCost;

        /**
         * 记录集团兑换商品列表的现金消耗是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团兑换商品列表的现金消耗，保留协议中的显式空值。
         */
        @JsonSetter("creditsCost")
        public void setCreditsCost(BigDecimal value) {
            this.creditsCost = value;
            markPresent("creditsCost");
        }

        /** 集团兑换商品列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<FavoursItemReferencesDTO> itemReferences;

        /**
         * 记录集团兑换商品列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团兑换商品列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<FavoursItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 集团兑换商品列表的晋级奖励；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rankUpReward")
        private Boolean rankUpReward;

        /**
         * 记录集团兑换商品列表的晋级奖励是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团兑换商品列表的晋级奖励，保留协议中的显式空值。
         */
        @JsonSetter("rankUpReward")
        public void setRankUpReward(Boolean value) {
            this.rankUpReward = value;
            markPresent("rankUpReward");
        }

        /** 集团兑换商品列表的所需等级；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("requiredLevel")
        private BigDecimal requiredLevel;

        /**
         * 记录集团兑换商品列表的所需等级是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团兑换商品列表的所需等级，保留协议中的显式空值。
         */
        @JsonSetter("requiredLevel")
        public void setRequiredLevel(BigDecimal value) {
            this.requiredLevel = value;
            markPresent("requiredLevel");
        }

        /** 集团兑换商品列表的声望消耗；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("standingCost")
        private BigDecimal standingCost;

        /**
         * 记录集团兑换商品列表的声望消耗是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团兑换商品列表的声望消耗，保留协议中的显式空值。
         */
        @JsonSetter("standingCost")
        public void setStandingCost(BigDecimal value) {
            this.standingCost = value;
            markPresent("standingCost");
        }

        /** 集团兑换商品列表的商店物品；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("storeItem")
        private String storeItem;

        /**
         * 记录集团兑换商品列表的商店物品是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团兑换商品列表的商店物品，保留协议中的显式空值。
         */
        @JsonSetter("storeItem")
        public void setStoreItem(String value) {
            this.storeItem = value;
            markPresent("storeItem");
        }

    }

    /** 官方嵌套对象 InitiationSacrificeItemsItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class InitiationSacrificeItemsItemReferencesDTO extends OfficialObjectDTO {
        /** 入门献祭要求的物品列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录入门献祭要求的物品列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 入门献祭要求的物品列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 入门献祭要求的物品列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录入门献祭要求的物品列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 入门献祭要求的物品列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 入门献祭要求的物品列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录入门献祭要求的物品列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 入门献祭要求的物品列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 入门献祭要求的物品列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录入门献祭要求的物品列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 入门献祭要求的物品列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 入门献祭要求的物品列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录入门献祭要求的物品列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 入门献祭要求的物品列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 入门献祭要求的物品列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录入门献祭要求的物品列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 入门献祭要求的物品列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 入门献祭要求的物品列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录入门献祭要求的物品列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 入门献祭要求的物品列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 入门献祭要求的物品列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录入门献祭要求的物品列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 入门献祭要求的物品列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 入门献祭要求的物品列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录入门献祭要求的物品列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 入门献祭要求的物品列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 InitiationSacrificeItemsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class InitiationSacrificeItemsDTO extends OfficialObjectDTO {
        /** 入门献祭要求的物品列表的物品数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ItemCount")
        private BigDecimal itemCount;

        /**
         * 记录入门献祭要求的物品列表的物品数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 入门献祭要求的物品列表的物品数量，保留协议中的显式空值。
         */
        @JsonSetter("ItemCount")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("ItemCount");
        }

        /** 入门献祭要求的物品列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<InitiationSacrificeItemsItemReferencesDTO> itemReferences;

        /**
         * 记录入门献祭要求的物品列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 入门献祭要求的物品列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<InitiationSacrificeItemsItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 入门献祭要求的物品列表的物品类型路径；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ItemType")
        private String itemType;

        /**
         * 记录入门献祭要求的物品列表的物品类型路径是否出现，同时区分未提供与显式空值。
         *
         * @param value 入门献祭要求的物品列表的物品类型路径，保留协议中的显式空值。
         */
        @JsonSetter("ItemType")
        public void setItemType(String value) {
            this.itemType = value;
            markPresent("ItemType");
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

    /** 官方嵌套对象 MedallionsItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class MedallionsItemReferencesDTO extends OfficialObjectDTO {
        /** 集团奖章列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录集团奖章列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团奖章列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 集团奖章列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录集团奖章列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团奖章列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 集团奖章列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录集团奖章列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团奖章列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 集团奖章列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录集团奖章列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团奖章列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 集团奖章列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录集团奖章列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团奖章列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 集团奖章列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录集团奖章列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团奖章列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 集团奖章列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录集团奖章列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团奖章列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 集团奖章列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录集团奖章列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团奖章列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 集团奖章列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录集团奖章列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团奖章列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 MedallionsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class MedallionsDTO extends OfficialObjectDTO {
        /** 集团奖章列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<MedallionsItemReferencesDTO> itemReferences;

        /**
         * 记录集团奖章列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团奖章列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<MedallionsItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 集团奖章列表的物品类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemType")
        private String itemType;

        /**
         * 记录集团奖章列表的物品类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团奖章列表的物品类型，保留协议中的显式空值。
         */
        @JsonSetter("itemType")
        public void setItemType(String value) {
            this.itemType = value;
            markPresent("itemType");
        }

        /** 集团奖章列表的声望；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("standing")
        private BigDecimal standing;

        /**
         * 记录集团奖章列表的声望是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团奖章列表的声望，保留协议中的显式空值。
         */
        @JsonSetter("standing")
        public void setStanding(BigDecimal value) {
            this.standing = value;
            markPresent("standing");
        }

    }

    /** 官方嵌套对象 TitlesItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class TitlesItemReferencesDTO extends OfficialObjectDTO {
        /** 集团等级称号列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录集团等级称号列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 集团等级称号列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录集团等级称号列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 集团等级称号列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录集团等级称号列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 集团等级称号列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录集团等级称号列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 集团等级称号列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录集团等级称号列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 集团等级称号列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录集团等级称号列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 集团等级称号列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录集团等级称号列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 集团等级称号列表的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录集团等级称号列表的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 集团等级称号列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录集团等级称号列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 集团等级称号列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录集团等级称号列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 TitlesRewardItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class TitlesRewardItemReferencesDTO extends OfficialObjectDTO {
        /** 集团等级称号列表的奖励的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录集团等级称号列表的奖励的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的奖励的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 集团等级称号列表的奖励的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录集团等级称号列表的奖励的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的奖励的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 集团等级称号列表的奖励的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录集团等级称号列表的奖励的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的奖励的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 集团等级称号列表的奖励的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录集团等级称号列表的奖励的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的奖励的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 集团等级称号列表的奖励的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录集团等级称号列表的奖励的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的奖励的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 集团等级称号列表的奖励的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录集团等级称号列表的奖励的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的奖励的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 集团等级称号列表的奖励的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录集团等级称号列表的奖励的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的奖励的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 集团等级称号列表的奖励的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录集团等级称号列表的奖励的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的奖励的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 集团等级称号列表的奖励的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录集团等级称号列表的奖励的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的奖励的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 集团等级称号列表的奖励的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录集团等级称号列表的奖励的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的奖励的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 TitlesSacrificeItemsItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class TitlesSacrificeItemsItemReferencesDTO extends OfficialObjectDTO {
        /** 集团等级称号列表的献祭的物品列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录集团等级称号列表的献祭的物品列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的献祭的物品列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 集团等级称号列表的献祭的物品列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录集团等级称号列表的献祭的物品列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的献祭的物品列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 集团等级称号列表的献祭的物品列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录集团等级称号列表的献祭的物品列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的献祭的物品列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 集团等级称号列表的献祭的物品列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录集团等级称号列表的献祭的物品列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的献祭的物品列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 集团等级称号列表的献祭的物品列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录集团等级称号列表的献祭的物品列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的献祭的物品列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 集团等级称号列表的献祭的物品列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录集团等级称号列表的献祭的物品列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的献祭的物品列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 集团等级称号列表的献祭的物品列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录集团等级称号列表的献祭的物品列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的献祭的物品列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 集团等级称号列表的献祭的物品列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录集团等级称号列表的献祭的物品列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的献祭的物品列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 集团等级称号列表的献祭的物品列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录集团等级称号列表的献祭的物品列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的献祭的物品列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 TitlesSacrificeItemsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class TitlesSacrificeItemsDTO extends OfficialObjectDTO {
        /** 集团等级称号列表的献祭的物品列表的物品数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ItemCount")
        private BigDecimal itemCount;

        /**
         * 记录集团等级称号列表的献祭的物品列表的物品数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的献祭的物品列表的物品数量，保留协议中的显式空值。
         */
        @JsonSetter("ItemCount")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("ItemCount");
        }

        /** 集团等级称号列表的献祭的物品列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<TitlesSacrificeItemsItemReferencesDTO> itemReferences;

        /**
         * 记录集团等级称号列表的献祭的物品列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的献祭的物品列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<TitlesSacrificeItemsItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 集团等级称号列表的献祭的物品列表的物品类型路径；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ItemType")
        private String itemType;

        /**
         * 记录集团等级称号列表的献祭的物品列表的物品类型路径是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的献祭的物品列表的物品类型路径，保留协议中的显式空值。
         */
        @JsonSetter("ItemType")
        public void setItemType(String value) {
            this.itemType = value;
            markPresent("ItemType");
        }

    }

    /** 官方嵌套对象 TitlesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class TitlesDTO extends OfficialObjectDTO {
        /** 集团等级称号列表的描述；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("description")
        private String description;

        /**
         * 记录集团等级称号列表的描述是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的描述，保留协议中的显式空值。
         */
        @JsonSetter("description")
        public void setDescription(String value) {
            this.description = value;
            markPresent("description");
        }

        /** 集团等级称号列表的英文描述；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("descriptionEn")
        private String descriptionEn;

        /**
         * 记录集团等级称号列表的英文描述是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的英文描述，保留协议中的显式空值。
         */
        @JsonSetter("descriptionEn")
        public void setDescriptionEn(String value) {
            this.descriptionEn = value;
            markPresent("descriptionEn");
        }

        /** 集团等级称号列表的中文描述；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("descriptionZh")
        private String descriptionZh;

        /**
         * 记录集团等级称号列表的中文描述是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的中文描述，保留协议中的显式空值。
         */
        @JsonSetter("descriptionZh")
        public void setDescriptionZh(String value) {
            this.descriptionZh = value;
            markPresent("descriptionZh");
        }

        /** 集团等级称号列表的图标；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("icon")
        private String icon;

        /**
         * 记录集团等级称号列表的图标是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的图标，保留协议中的显式空值。
         */
        @JsonSetter("icon")
        public void setIcon(String value) {
            this.icon = value;
            markPresent("icon");
        }

        /** 集团等级称号列表的图标资源；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconAsset")
        private String iconAsset;

        /**
         * 记录集团等级称号列表的图标资源是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的图标资源，保留协议中的显式空值。
         */
        @JsonSetter("iconAsset")
        public void setIconAsset(String value) {
            this.iconAsset = value;
            markPresent("iconAsset");
        }

        /** 集团等级称号列表的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录集团等级称号列表的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 集团等级称号列表的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录集团等级称号列表的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 集团等级称号列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<TitlesItemReferencesDTO> itemReferences;

        /**
         * 记录集团等级称号列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<TitlesItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 集团等级称号列表的等级；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("level")
        private BigDecimal level;

        /**
         * 记录集团等级称号列表的等级是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的等级，保留协议中的显式空值。
         */
        @JsonSetter("level")
        public void setLevel(BigDecimal value) {
            this.level = value;
            markPresent("level");
        }

        /** 集团等级称号列表的声望上限；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("maxStanding")
        private BigDecimal maxStanding;

        /**
         * 记录集团等级称号列表的声望上限是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的声望上限，保留协议中的显式空值。
         */
        @JsonSetter("maxStanding")
        public void setMaxStanding(BigDecimal value) {
            this.maxStanding = value;
            markPresent("maxStanding");
        }

        /** 集团等级称号列表的最低声望；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("minStanding")
        private BigDecimal minStanding;

        /**
         * 记录集团等级称号列表的最低声望是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的最低声望，保留协议中的显式空值。
         */
        @JsonSetter("minStanding")
        public void setMinStanding(BigDecimal value) {
            this.minStanding = value;
            markPresent("minStanding");
        }

        /** 集团等级称号列表的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录集团等级称号列表的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 集团等级称号列表的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录集团等级称号列表的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 集团等级称号列表的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录集团等级称号列表的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 集团等级称号列表的奖励的物品数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("reward__ItemCount")
        private BigDecimal rewardItemCount;

        /**
         * 记录集团等级称号列表的奖励的物品数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的奖励的物品数量，保留协议中的显式空值。
         */
        @JsonSetter("reward__ItemCount")
        public void setRewardItemCount(BigDecimal value) {
            this.rewardItemCount = value;
            markPresent("reward__ItemCount");
        }

        /** 集团等级称号列表的奖励的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("reward__itemReferences")
        private List<TitlesRewardItemReferencesDTO> rewardItemReferences;

        /**
         * 记录集团等级称号列表的奖励的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的奖励的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("reward__itemReferences")
        public void setRewardItemReferences(List<TitlesRewardItemReferencesDTO> value) {
            this.rewardItemReferences = value;
            markPresent("reward__itemReferences");
        }

        /** 集团等级称号列表的奖励的物品类型路径；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("reward__ItemType")
        private String rewardItemType;

        /**
         * 记录集团等级称号列表的奖励的物品类型路径是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的奖励的物品类型路径，保留协议中的显式空值。
         */
        @JsonSetter("reward__ItemType")
        public void setRewardItemType(String value) {
            this.rewardItemType = value;
            markPresent("reward__ItemType");
        }

        /** 集团等级称号列表的献祭的现金；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("sacrifice__credits")
        private BigDecimal sacrificeCredits;

        /**
         * 记录集团等级称号列表的献祭的现金是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的献祭的现金，保留协议中的显式空值。
         */
        @JsonSetter("sacrifice__credits")
        public void setSacrificeCredits(BigDecimal value) {
            this.sacrificeCredits = value;
            markPresent("sacrifice__credits");
        }

        /** 集团等级称号列表的献祭的物品列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("sacrifice__items")
        private List<TitlesSacrificeItemsDTO> sacrificeItems;

        /**
         * 记录集团等级称号列表的献祭的物品列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的献祭的物品列表，保留协议中的显式空值。
         */
        @JsonSetter("sacrifice__items")
        public void setSacrificeItems(List<TitlesSacrificeItemsDTO> value) {
            this.sacrificeItems = value;
            markPresent("sacrifice__items");
        }

        /** 集团等级称号列表的商店物品奖励；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("storeItemReward")
        private String storeItemReward;

        /**
         * 记录集团等级称号列表的商店物品奖励是否出现，同时区分未提供与显式空值。
         *
         * @param value 集团等级称号列表的商店物品奖励，保留协议中的显式空值。
         */
        @JsonSetter("storeItemReward")
        public void setStoreItemReward(String value) {
            this.storeItemReward = value;
            markPresent("storeItemReward");
        }

    }

}
