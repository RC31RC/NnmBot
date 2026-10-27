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

/** RAILJACK_WEAPON 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialRailjackWeaponDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 精准度；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("accuracy")
    private BigDecimal accuracy;

    /**
     * 记录精准度是否出现，同时区分未提供与显式空值。
     *
     * @param value 精准度，保留协议中的显式空值。
     */
    @JsonSetter("accuracy")
    public void setAccuracy(BigDecimal value) {
        this.accuracy = value;
        markPresent("accuracy");
    }

    /** 行为配置列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("behaviours")
    private List<BehavioursDTO> behaviours;

    /**
     * 记录行为配置列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 行为配置列表，保留协议中的显式空值。
     */
    @JsonSetter("behaviours")
    public void setBehaviours(List<BehavioursDTO> value) {
        this.behaviours = value;
        markPresent("behaviours");
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

    /** 现金消耗；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("creditsCost")
    private BigDecimal creditsCost;

    /**
     * 记录现金消耗是否出现，同时区分未提供与显式空值。
     *
     * @param value 现金消耗，保留协议中的显式空值。
     */
    @JsonSetter("creditsCost")
    public void setCreditsCost(BigDecimal value) {
        this.creditsCost = value;
        markPresent("creditsCost");
    }

    /** 暴击几率；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("criticalChance")
    private BigDecimal criticalChance;

    /**
     * 记录暴击几率是否出现，同时区分未提供与显式空值。
     *
     * @param value 暴击几率，保留协议中的显式空值。
     */
    @JsonSetter("criticalChance")
    public void setCriticalChance(BigDecimal value) {
        this.criticalChance = value;
        markPresent("criticalChance");
    }

    /** 暴击倍率；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("criticalMultiplier")
    private BigDecimal criticalMultiplier;

    /**
     * 记录暴击倍率是否出现，同时区分未提供与显式空值。
     *
     * @param value 暴击倍率，保留协议中的显式空值。
     */
    @JsonSetter("criticalMultiplier")
    public void setCriticalMultiplier(BigDecimal value) {
        this.criticalMultiplier = value;
        markPresent("criticalMultiplier");
    }

    /** 单发伤害；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("damagePerShot")
    private List<BigDecimal> damagePerShot;

    /**
     * 记录单发伤害是否出现，同时区分未提供与显式空值。
     *
     * @param value 单发伤害，保留协议中的显式空值。
     */
    @JsonSetter("damagePerShot")
    public void setDamagePerShot(List<BigDecimal> value) {
        this.damagePerShot = value;
        markPresent("damagePerShot");
    }

    /** 预装强化列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("defaultUpgrades")
    private List<DefaultUpgradesDTO> defaultUpgrades;

    /**
     * 记录预装强化列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 预装强化列表，保留协议中的显式空值。
     */
    @JsonSetter("defaultUpgrades")
    public void setDefaultUpgrades(List<DefaultUpgradesDTO> value) {
        this.defaultUpgrades = value;
        markPresent("defaultUpgrades");
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

    /** 射速；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("fireRate")
    private BigDecimal fireRate;

    /**
     * 记录射速是否出现，同时区分未提供与显式空值。
     *
     * @param value 射速，保留协议中的显式空值。
     */
    @JsonSetter("fireRate")
    public void setFireRate(BigDecimal value) {
        this.fireRate = value;
        markPresent("fireRate");
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

    /** 弹匣容量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("magazineSize")
    private BigDecimal magazineSize;

    /**
     * 记录弹匣容量是否出现，同时区分未提供与显式空值。
     *
     * @param value 弹匣容量，保留协议中的显式空值。
     */
    @JsonSetter("magazineSize")
    public void setMagazineSize(BigDecimal value) {
        this.magazineSize = value;
        markPresent("magazineSize");
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

    /** 多重射击；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("multishot")
    private BigDecimal multishot;

    /**
     * 记录多重射击是否出现，同时区分未提供与显式空值。
     *
     * @param value 多重射击，保留协议中的显式空值。
     */
    @JsonSetter("multishot")
    public void setMultishot(BigDecimal value) {
        this.multishot = value;
        markPresent("multishot");
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

    /** 噪音级别；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("noise")
    private String noise;

    /**
     * 记录噪音级别是否出现，同时区分未提供与显式空值。
     *
     * @param value 噪音级别，保留协议中的显式空值。
     */
    @JsonSetter("noise")
    public void setNoise(String value) {
        this.noise = value;
        markPresent("noise");
    }

    /** 裂罅倾向值；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("omegaAttenuation")
    private BigDecimal omegaAttenuation;

    /**
     * 记录裂罅倾向值是否出现，同时区分未提供与显式空值。
     *
     * @param value 裂罅倾向值，保留协议中的显式空值。
     */
    @JsonSetter("omegaAttenuation")
    public void setOmegaAttenuation(BigDecimal value) {
        this.omegaAttenuation = value;
        markPresent("omegaAttenuation");
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

    /** 异常触发几率；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("procChance")
    private BigDecimal procChance;

    /**
     * 记录异常触发几率是否出现，同时区分未提供与显式空值。
     *
     * @param value 异常触发几率，保留协议中的显式空值。
     */
    @JsonSetter("procChance")
    public void setProcChance(BigDecimal value) {
        this.procChance = value;
        markPresent("procChance");
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

    /** 装填时间；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("reloadTime")
    private BigDecimal reloadTime;

    /**
     * 记录装填时间是否出现，同时区分未提供与显式空值。
     *
     * @param value 装填时间，保留协议中的显式空值。
     */
    @JsonSetter("reloadTime")
    public void setReloadTime(BigDecimal value) {
        this.reloadTime = value;
        markPresent("reloadTime");
    }

    /** 槽位；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("slot")
    private BigDecimal slot;

    /**
     * 记录槽位是否出现，同时区分未提供与显式空值。
     *
     * @param value 槽位，保留协议中的显式空值。
     */
    @JsonSetter("slot")
    public void setSlot(BigDecimal value) {
        this.slot = value;
        markPresent("slot");
    }

    /** 总伤害；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("totalDamage")
    private BigDecimal totalDamage;

    /**
     * 记录总伤害是否出现，同时区分未提供与显式空值。
     *
     * @param value 总伤害，保留协议中的显式空值。
     */
    @JsonSetter("totalDamage")
    public void setTotalDamage(BigDecimal value) {
        this.totalDamage = value;
        markPresent("totalDamage");
    }

    /** 触发条件；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("trigger")
    private String trigger;

    /**
     * 记录触发条件是否出现，同时区分未提供与显式空值。
     *
     * @param value 触发条件，保留协议中的显式空值。
     */
    @JsonSetter("trigger")
    public void setTrigger(String value) {
        this.trigger = value;
        markPresent("trigger");
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

    /** 官方嵌套对象 BehavioursDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class BehavioursDTO extends OfficialObjectDTO {
        /** 行为配置列表的连发配置的数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("burst__count")
        private BigDecimal burstCount;

        /**
         * 记录行为配置列表的连发配置的数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的连发配置的数量，保留协议中的显式空值。
         */
        @JsonSetter("burst__count")
        public void setBurstCount(BigDecimal value) {
            this.burstCount = value;
            markPresent("burst__count");
        }

        /** 行为配置列表的连发配置的延迟时间；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("burst__delay")
        private BigDecimal burstDelay;

        /**
         * 记录行为配置列表的连发配置的延迟时间是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的连发配置的延迟时间，保留协议中的显式空值。
         */
        @JsonSetter("burst__delay")
        public void setBurstDelay(BigDecimal value) {
            this.burstDelay = value;
            markPresent("burst__delay");
        }

        /** 行为配置列表的单次开火迭代次数；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("fireIterations")
        private BigDecimal fireIterations;

        /**
         * 记录行为配置列表的单次开火迭代次数是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的单次开火迭代次数，保留协议中的显式空值。
         */
        @JsonSetter("fireIterations")
        public void setFireIterations(BigDecimal value) {
            this.fireIterations = value;
            markPresent("fireIterations");
        }

        /** 行为配置列表的冲击伤害的电击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_ELECTRICITY")
        private BigDecimal impactDtElectricity;

        /**
         * 记录行为配置列表的冲击伤害的电击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的电击伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_ELECTRICITY")
        public void setImpactDtElectricity(BigDecimal value) {
            this.impactDtElectricity = value;
            markPresent("impact__DT_ELECTRICITY");
        }

        /** 行为配置列表的冲击伤害的火焰伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_FIRE")
        private BigDecimal impactDtFire;

        /**
         * 记录行为配置列表的冲击伤害的火焰伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的火焰伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_FIRE")
        public void setImpactDtFire(BigDecimal value) {
            this.impactDtFire = value;
            markPresent("impact__DT_FIRE");
        }

        /** 行为配置列表的冲击伤害的冲击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_IMPACT")
        private BigDecimal impactDtImpact;

        /**
         * 记录行为配置列表的冲击伤害的冲击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的冲击伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_IMPACT")
        public void setImpactDtImpact(BigDecimal value) {
            this.impactDtImpact = value;
            markPresent("impact__DT_IMPACT");
        }

        /** 行为配置列表的冲击伤害的穿刺伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_PUNCTURE")
        private BigDecimal impactDtPuncture;

        /**
         * 记录行为配置列表的冲击伤害的穿刺伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的穿刺伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_PUNCTURE")
        public void setImpactDtPuncture(BigDecimal value) {
            this.impactDtPuncture = value;
            markPresent("impact__DT_PUNCTURE");
        }

        /** 行为配置列表的冲击伤害的切割伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_SLASH")
        private BigDecimal impactDtSlash;

        /**
         * 记录行为配置列表的冲击伤害的切割伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的切割伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_SLASH")
        public void setImpactDtSlash(BigDecimal value) {
            this.impactDtSlash = value;
            markPresent("impact__DT_SLASH");
        }

        /** 行为配置列表的冲击伤害的异常触发几率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__procChance")
        private BigDecimal impactProcChance;

        /**
         * 记录行为配置列表的冲击伤害的异常触发几率是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的异常触发几率，保留协议中的显式空值。
         */
        @JsonSetter("impact__procChance")
        public void setImpactProcChance(BigDecimal value) {
            this.impactProcChance = value;
            markPresent("impact__procChance");
        }

        /** 行为配置列表的投射物的攻击的电击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_ELECTRICITY")
        private BigDecimal projectileAttackDtElectricity;

        /**
         * 记录行为配置列表的投射物的攻击的电击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的电击伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_ELECTRICITY")
        public void setProjectileAttackDtElectricity(BigDecimal value) {
            this.projectileAttackDtElectricity = value;
            markPresent("projectile__attack__DT_ELECTRICITY");
        }

        /** 行为配置列表的投射物的攻击的冰冻伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_FREEZE")
        private BigDecimal projectileAttackDtFreeze;

        /**
         * 记录行为配置列表的投射物的攻击的冰冻伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的冰冻伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_FREEZE")
        public void setProjectileAttackDtFreeze(BigDecimal value) {
            this.projectileAttackDtFreeze = value;
            markPresent("projectile__attack__DT_FREEZE");
        }

        /** 行为配置列表的投射物的攻击的冲击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_IMPACT")
        private BigDecimal projectileAttackDtImpact;

        /**
         * 记录行为配置列表的投射物的攻击的冲击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的冲击伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_IMPACT")
        public void setProjectileAttackDtImpact(BigDecimal value) {
            this.projectileAttackDtImpact = value;
            markPresent("projectile__attack__DT_IMPACT");
        }

        /** 行为配置列表的投射物的攻击的毒素伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_POISON")
        private BigDecimal projectileAttackDtPoison;

        /**
         * 记录行为配置列表的投射物的攻击的毒素伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的毒素伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_POISON")
        public void setProjectileAttackDtPoison(BigDecimal value) {
            this.projectileAttackDtPoison = value;
            markPresent("projectile__attack__DT_POISON");
        }

        /** 行为配置列表的投射物的攻击的穿刺伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_PUNCTURE")
        private BigDecimal projectileAttackDtPuncture;

        /**
         * 记录行为配置列表的投射物的攻击的穿刺伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的穿刺伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_PUNCTURE")
        public void setProjectileAttackDtPuncture(BigDecimal value) {
            this.projectileAttackDtPuncture = value;
            markPresent("projectile__attack__DT_PUNCTURE");
        }

        /** 行为配置列表的投射物的攻击的切割伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_SLASH")
        private BigDecimal projectileAttackDtSlash;

        /**
         * 记录行为配置列表的投射物的攻击的切割伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的切割伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_SLASH")
        public void setProjectileAttackDtSlash(BigDecimal value) {
            this.projectileAttackDtSlash = value;
            markPresent("projectile__attack__DT_SLASH");
        }

        /** 行为配置列表的投射物的攻击的异常触发几率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__procChance")
        private BigDecimal projectileAttackProcChance;

        /**
         * 记录行为配置列表的投射物的攻击的异常触发几率是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的异常触发几率，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__procChance")
        public void setProjectileAttackProcChance(BigDecimal value) {
            this.projectileAttackProcChance = value;
            markPresent("projectile__attack__procChance");
        }

        /** 行为配置列表的状态名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("stateName")
        private String stateName;

        /**
         * 记录行为配置列表的状态名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的状态名称，保留协议中的显式空值。
         */
        @JsonSetter("stateName")
        public void setStateName(String value) {
            this.stateName = value;
            markPresent("stateName");
        }

        /** 行为配置列表的状态英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("stateNameEn")
        private String stateNameEn;

        /**
         * 记录行为配置列表的状态英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的状态英文名称，保留协议中的显式空值。
         */
        @JsonSetter("stateNameEn")
        public void setStateNameEn(String value) {
            this.stateNameEn = value;
            markPresent("stateNameEn");
        }

        /** 行为配置列表的状态中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("stateNameZh")
        private String stateNameZh;

        /**
         * 记录行为配置列表的状态中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的状态中文名称，保留协议中的显式空值。
         */
        @JsonSetter("stateNameZh")
        public void setStateNameZh(String value) {
            this.stateNameZh = value;
            markPresent("stateNameZh");
        }

    }

    /** 官方嵌套对象 DefaultUpgradesItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class DefaultUpgradesItemReferencesDTO extends OfficialObjectDTO {
        /** 预装强化列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录预装强化列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 预装强化列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 预装强化列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录预装强化列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 预装强化列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 预装强化列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录预装强化列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 预装强化列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 预装强化列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录预装强化列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 预装强化列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 预装强化列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录预装强化列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 预装强化列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 预装强化列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录预装强化列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 预装强化列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 预装强化列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录预装强化列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 预装强化列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 预装强化列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录预装强化列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 预装强化列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 预装强化列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录预装强化列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 预装强化列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 DefaultUpgradesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class DefaultUpgradesDTO extends OfficialObjectDTO {
        /** 预装强化列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<DefaultUpgradesItemReferencesDTO> itemReferences;

        /**
         * 记录预装强化列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 预装强化列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<DefaultUpgradesItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 预装强化列表的物品类型路径；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ItemType")
        private String itemType;

        /**
         * 记录预装强化列表的物品类型路径是否出现，同时区分未提供与显式空值。
         *
         * @param value 预装强化列表的物品类型路径，保留协议中的显式空值。
         */
        @JsonSetter("ItemType")
        public void setItemType(String value) {
            this.itemType = value;
            markPresent("ItemType");
        }

        /** 预装强化列表的槽位；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("Slot")
        private BigDecimal slot;

        /**
         * 记录预装强化列表的槽位是否出现，同时区分未提供与显式空值。
         *
         * @param value 预装强化列表的槽位，保留协议中的显式空值。
         */
        @JsonSetter("Slot")
        public void setSlot(BigDecimal value) {
            this.slot = value;
            markPresent("Slot");
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
