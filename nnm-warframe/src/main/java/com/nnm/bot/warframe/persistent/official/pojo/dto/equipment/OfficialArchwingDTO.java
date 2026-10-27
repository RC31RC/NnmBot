package com.nnm.bot.warframe.persistent.official.pojo.dto.equipment;

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

/** ARCHWING 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialArchwingDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 技能；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("abilities")
    private List<AbilitiesDTO> abilities;

    /**
     * 记录技能是否出现，同时区分未提供与显式空值。
     *
     * @param value 技能，保留协议中的显式空值。
     */
    @JsonSetter("abilities")
    public void setAbilities(List<AbilitiesDTO> value) {
        this.abilities = value;
        markPresent("abilities");
    }

    /** 护甲；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("armor")
    private BigDecimal armor;

    /**
     * 记录护甲是否出现，同时区分未提供与显式空值。
     *
     * @param value 护甲，保留协议中的显式空值。
     */
    @JsonSetter("armor")
    public void setArmor(BigDecimal value) {
        this.armor = value;
        markPresent("armor");
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

    /** 生命值；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("health")
    private BigDecimal health;

    /**
     * 记录生命值是否出现，同时区分未提供与显式空值。
     *
     * @param value 生命值，保留协议中的显式空值。
     */
    @JsonSetter("health")
    public void setHealth(BigDecimal value) {
        this.health = value;
        markPresent("health");
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

    /** 所需段位；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("masteryReq")
    private BigDecimal masteryReq;

    /**
     * 记录所需段位是否出现，同时区分未提供与显式空值。
     *
     * @param value 所需段位，保留协议中的显式空值。
     */
    @JsonSetter("masteryReq")
    public void setMasteryReq(BigDecimal value) {
        this.masteryReq = value;
        markPresent("masteryReq");
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

    /** 所属对象类型；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("ownerType")
    private String ownerType;

    /**
     * 记录所属对象类型是否出现，同时区分未提供与显式空值。
     *
     * @param value 所属对象类型，保留协议中的显式空值。
     */
    @JsonSetter("ownerType")
    public void setOwnerType(String value) {
        this.ownerType = value;
        markPresent("ownerType");
    }

    /** 父级名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("parentName")
    private String parentName;

    /**
     * 记录父级名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 父级名称，保留协议中的显式空值。
     */
    @JsonSetter("parentName")
    public void setParentName(String value) {
        this.parentName = value;
        markPresent("parentName");
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

    /** 能量上限；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("power")
    private BigDecimal power;

    /**
     * 记录能量上限是否出现，同时区分未提供与显式空值。
     *
     * @param value 能量上限，保留协议中的显式空值。
     */
    @JsonSetter("power")
    public void setPower(BigDecimal value) {
        this.power = value;
        markPresent("power");
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

    /** 护盾；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("shield")
    private BigDecimal shield;

    /**
     * 记录护盾是否出现，同时区分未提供与显式空值。
     *
     * @param value 护盾，保留协议中的显式空值。
     */
    @JsonSetter("shield")
    public void setShield(BigDecimal value) {
        this.shield = value;
        markPresent("shield");
    }

    /** 冲刺速度；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("sprintSpeed")
    private BigDecimal sprintSpeed;

    /**
     * 记录冲刺速度是否出现，同时区分未提供与显式空值。
     *
     * @param value 冲刺速度，保留协议中的显式空值。
     */
    @JsonSetter("sprintSpeed")
    public void setSprintSpeed(BigDecimal value) {
        this.sprintSpeed = value;
        markPresent("sprintSpeed");
    }

    /** 耐力；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("stamina")
    private BigDecimal stamina;

    /**
     * 记录耐力是否出现，同时区分未提供与显式空值。
     *
     * @param value 耐力，保留协议中的显式空值。
     */
    @JsonSetter("stamina")
    public void setStamina(BigDecimal value) {
        this.stamina = value;
        markPresent("stamina");
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

    /** 变体类型；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("variantType")
    private String variantType;

    /**
     * 记录变体类型是否出现，同时区分未提供与显式空值。
     *
     * @param value 变体类型，保留协议中的显式空值。
     */
    @JsonSetter("variantType")
    public void setVariantType(String value) {
        this.variantType = value;
        markPresent("variantType");
    }

    /**
     * 提取本记录的常用检索字段，不携带 JPA 受管状态。
     *
     * @return 名称、分类和说明等检索摘要；上游缺省字段保持为空。
     */
    @Override
    public OfficialRecordHeaderDTO header() {
        return new OfficialRecordHeaderDTO(OfficialInputText.first(nameEn, name), OfficialInputText.first(nameZh), OfficialInputText.first(parentName), OfficialInputText.first(productCategory), OfficialInputText.first(descriptionZh, descriptionEn, description));
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

    /** 官方嵌套对象 AbilitiesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class AbilitiesDTO extends OfficialObjectDTO {
        /** 技能的技能图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("abilityIconUrl")
        private String abilityIconUrl;

        /**
         * 记录技能的技能图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的技能图标地址，保留协议中的显式空值。
         */
        @JsonSetter("abilityIconUrl")
        public void setAbilityIconUrl(String value) {
            this.abilityIconUrl = value;
            markPresent("abilityIconUrl");
        }

        /** 技能的技能名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("abilityName")
        private String abilityName;

        /**
         * 记录技能的技能名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的技能名称，保留协议中的显式空值。
         */
        @JsonSetter("abilityName")
        public void setAbilityName(String value) {
            this.abilityName = value;
            markPresent("abilityName");
        }

        /** 技能的技能中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("abilityNameZh")
        private String abilityNameZh;

        /**
         * 记录技能的技能中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的技能中文名称，保留协议中的显式空值。
         */
        @JsonSetter("abilityNameZh")
        public void setAbilityNameZh(String value) {
            this.abilityNameZh = value;
            markPresent("abilityNameZh");
        }

        /** 技能的技能唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("abilityUniqueName")
        private String abilityUniqueName;

        /**
         * 记录技能的技能唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的技能唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("abilityUniqueName")
        public void setAbilityUniqueName(String value) {
            this.abilityUniqueName = value;
            markPresent("abilityUniqueName");
        }

        /** 技能的描述；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("description")
        private String description;

        /**
         * 记录技能的描述是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的描述，保留协议中的显式空值。
         */
        @JsonSetter("description")
        public void setDescription(String value) {
            this.description = value;
            markPresent("description");
        }

        /** 技能的英文描述；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("descriptionEn")
        private String descriptionEn;

        /**
         * 记录技能的英文描述是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的英文描述，保留协议中的显式空值。
         */
        @JsonSetter("descriptionEn")
        public void setDescriptionEn(String value) {
            this.descriptionEn = value;
            markPresent("descriptionEn");
        }

        /** 技能的中文描述；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("descriptionZh")
        private String descriptionZh;

        /**
         * 记录技能的中文描述是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的中文描述，保留协议中的显式空值。
         */
        @JsonSetter("descriptionZh")
        public void setDescriptionZh(String value) {
            this.descriptionZh = value;
            markPresent("descriptionZh");
        }

        /** 技能的持续能量消耗；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("energyConsumptionOverTime")
        private BigDecimal energyConsumptionOverTime;

        /**
         * 记录技能的持续能量消耗是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的持续能量消耗，保留协议中的显式空值。
         */
        @JsonSetter("energyConsumptionOverTime")
        public void setEnergyConsumptionOverTime(BigDecimal value) {
            this.energyConsumptionOverTime = value;
            markPresent("energyConsumptionOverTime");
        }

        /** 技能的启动所需能量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("energyRequiredToActivate")
        private BigDecimal energyRequiredToActivate;

        /**
         * 记录技能的启动所需能量是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的启动所需能量，保留协议中的显式空值。
         */
        @JsonSetter("energyRequiredToActivate")
        public void setEnergyRequiredToActivate(BigDecimal value) {
            this.energyRequiredToActivate = value;
            markPresent("energyRequiredToActivate");
        }

        /** 技能的图标；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("icon")
        private String icon;

        /**
         * 记录技能的图标是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的图标，保留协议中的显式空值。
         */
        @JsonSetter("icon")
        public void setIcon(String value) {
            this.icon = value;
            markPresent("icon");
        }

        /** 技能的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录技能的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 技能的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录技能的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 技能的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录技能的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 技能的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录技能的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 技能的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录技能的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 技能的记录键；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("recordKey")
        private String recordKey;

        /**
         * 记录技能的记录键是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的记录键，保留协议中的显式空值。
         */
        @JsonSetter("recordKey")
        public void setRecordKey(String value) {
            this.recordKey = value;
            markPresent("recordKey");
        }

        /** 技能的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录技能的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 技能的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
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

}
