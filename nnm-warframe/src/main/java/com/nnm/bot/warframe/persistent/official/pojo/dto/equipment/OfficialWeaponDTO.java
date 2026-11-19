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

/** WEAPON 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialWeaponDTO extends OfficialObjectDTO implements OfficialRecordDTO {
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

    /** 刺刀关联武器类型；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("bayonetOtherWeaponType")
    private String bayonetOtherWeaponType;

    /**
     * 记录刺刀关联武器类型是否出现，同时区分未提供与显式空值。
     *
     * @param value 刺刀关联武器类型，保留协议中的显式空值。
     */
    @JsonSetter("bayonetOtherWeaponType")
    public void setBayonetOtherWeaponType(String value) {
        this.bayonetOtherWeaponType = value;
        markPresent("bayonetOtherWeaponType");
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

    /** 格挡角度；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("blockingAngle")
    private BigDecimal blockingAngle;

    /**
     * 记录格挡角度是否出现，同时区分未提供与显式空值。
     *
     * @param value 格挡角度，保留协议中的显式空值。
     */
    @JsonSetter("blockingAngle")
    public void setBlockingAngle(BigDecimal value) {
        this.blockingAngle = value;
        markPresent("blockingAngle");
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

    /** 连击持续时间；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("comboDuration")
    private BigDecimal comboDuration;

    /**
     * 记录连击持续时间是否出现，同时区分未提供与显式空值。
     *
     * @param value 连击持续时间，保留协议中的显式空值。
     */
    @JsonSetter("comboDuration")
    public void setComboDuration(BigDecimal value) {
        this.comboDuration = value;
        markPresent("comboDuration");
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

    /** 捐赠声望加成；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("donationStandingBonus")
    private BigDecimal donationStandingBonus;

    /**
     * 记录捐赠声望加成是否出现，同时区分未提供与显式空值。
     *
     * @param value 捐赠声望加成，保留协议中的显式空值。
     */
    @JsonSetter("donationStandingBonus")
    public void setDonationStandingBonus(BigDecimal value) {
        this.donationStandingBonus = value;
        markPresent("donationStandingBonus");
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

    /** 穿透衰减；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("followThrough")
    private BigDecimal followThrough;

    /**
     * 记录穿透衰减是否出现，同时区分未提供与显式空值。
     *
     * @param value 穿透衰减，保留协议中的显式空值。
     */
    @JsonSetter("followThrough")
    public void setFollowThrough(BigDecimal value) {
        this.followThrough = value;
        markPresent("followThrough");
    }

    /** 枪械类型；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("gunType")
    private String gunType;

    /**
     * 记录枪械类型是否出现，同时区分未提供与显式空值。
     *
     * @param value 枪械类型，保留协议中的显式空值。
     */
    @JsonSetter("gunType")
    public void setGunType(String value) {
        this.gunType = value;
        markPresent("gunType");
    }

    /** 重击伤害；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("heavyAttackDamage")
    private BigDecimal heavyAttackDamage;

    /**
     * 记录重击伤害是否出现，同时区分未提供与显式空值。
     *
     * @param value 重击伤害，保留协议中的显式空值。
     */
    @JsonSetter("heavyAttackDamage")
    public void setHeavyAttackDamage(BigDecimal value) {
        this.heavyAttackDamage = value;
        markPresent("heavyAttackDamage");
    }

    /** 重型震地攻击伤害；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("heavySlamAttack")
    private BigDecimal heavySlamAttack;

    /**
     * 记录重型震地攻击伤害是否出现，同时区分未提供与显式空值。
     *
     * @param value 重型震地攻击伤害，保留协议中的显式空值。
     */
    @JsonSetter("heavySlamAttack")
    public void setHeavySlamAttack(BigDecimal value) {
        this.heavySlamAttack = value;
        markPresent("heavySlamAttack");
    }

    /** 重型震地范围伤害；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("heavySlamRadialDamage")
    private BigDecimal heavySlamRadialDamage;

    /**
     * 记录重型震地范围伤害是否出现，同时区分未提供与显式空值。
     *
     * @param value 重型震地范围伤害，保留协议中的显式空值。
     */
    @JsonSetter("heavySlamRadialDamage")
    public void setHeavySlamRadialDamage(BigDecimal value) {
        this.heavySlamRadialDamage = value;
        markPresent("heavySlamRadialDamage");
    }

    /** 重型震地半径；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("heavySlamRadius")
    private BigDecimal heavySlamRadius;

    /**
     * 记录重型震地半径是否出现，同时区分未提供与显式空值。
     *
     * @param value 重型震地半径，保留协议中的显式空值。
     */
    @JsonSetter("heavySlamRadius")
    public void setHeavySlamRadius(BigDecimal value) {
        this.heavySlamRadius = value;
        markPresent("heavySlamRadius");
    }

    /** 收纳位置分类；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("holsterCategory")
    private String holsterCategory;

    /**
     * 记录收纳位置分类是否出现，同时区分未提供与显式空值。
     *
     * @param value 收纳位置分类，保留协议中的显式空值。
     */
    @JsonSetter("holsterCategory")
    public void setHolsterCategory(String value) {
        this.holsterCategory = value;
        markPresent("holsterCategory");
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

    /** 等级上限；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("maxLevelCap")
    private BigDecimal maxLevelCap;

    /**
     * 记录等级上限是否出现，同时区分未提供与显式空值。
     *
     * @param value 等级上限，保留协议中的显式空值。
     */
    @JsonSetter("maxLevelCap")
    public void setMaxLevelCap(BigDecimal value) {
        this.maxLevelCap = value;
        markPresent("maxLevelCap");
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

    /** 是否仅可购买一次；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("oneTimePurchasable")
    private Boolean oneTimePurchasable;

    /**
     * 记录是否仅可购买一次是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否仅可购买一次，保留协议中的显式空值。
     */
    @JsonSetter("oneTimePurchasable")
    public void setOneTimePurchasable(Boolean value) {
        this.oneTimePurchasable = value;
        markPresent("oneTimePurchasable");
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

    /** 部件类型；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("partType")
    private String partType;

    /**
     * 记录部件类型是否出现，同时区分未提供与显式空值。
     *
     * @param value 部件类型，保留协议中的显式空值。
     */
    @JsonSetter("partType")
    public void setPartType(String value) {
        this.partType = value;
        markPresent("partType");
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

    /** 圣装裂罅倾向值；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("primeOmegaAttenuation")
    private BigDecimal primeOmegaAttenuation;

    /**
     * 记录圣装裂罅倾向值是否出现，同时区分未提供与显式空值。
     *
     * @param value 圣装裂罅倾向值，保留协议中的显式空值。
     */
    @JsonSetter("primeOmegaAttenuation")
    public void setPrimeOmegaAttenuation(BigDecimal value) {
        this.primeOmegaAttenuation = value;
        markPresent("primeOmegaAttenuation");
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

    /** 范围；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("range")
    private BigDecimal range;

    /**
     * 记录范围是否出现，同时区分未提供与显式空值。
     *
     * @param value 范围，保留协议中的显式空值。
     */
    @JsonSetter("range")
    public void setRange(BigDecimal value) {
        this.range = value;
        markPresent("range");
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

    /** 守护；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("sentinel")
    private Boolean sentinel;

    /**
     * 记录守护是否出现，同时区分未提供与显式空值。
     *
     * @param value 守护，保留协议中的显式空值。
     */
    @JsonSetter("sentinel")
    public void setSentinel(Boolean value) {
        this.sentinel = value;
        markPresent("sentinel");
    }

    /** 震地攻击伤害；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("slamAttack")
    private BigDecimal slamAttack;

    /**
     * 记录震地攻击伤害是否出现，同时区分未提供与显式空值。
     *
     * @param value 震地攻击伤害，保留协议中的显式空值。
     */
    @JsonSetter("slamAttack")
    public void setSlamAttack(BigDecimal value) {
        this.slamAttack = value;
        markPresent("slamAttack");
    }

    /** 震地范围伤害；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("slamRadialDamage")
    private BigDecimal slamRadialDamage;

    /**
     * 记录震地范围伤害是否出现，同时区分未提供与显式空值。
     *
     * @param value 震地范围伤害，保留协议中的显式空值。
     */
    @JsonSetter("slamRadialDamage")
    public void setSlamRadialDamage(BigDecimal value) {
        this.slamRadialDamage = value;
        markPresent("slamRadialDamage");
    }

    /** 震地半径；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("slamRadius")
    private BigDecimal slamRadius;

    /**
     * 记录震地半径是否出现，同时区分未提供与显式空值。
     *
     * @param value 震地半径，保留协议中的显式空值。
     */
    @JsonSetter("slamRadius")
    public void setSlamRadius(BigDecimal value) {
        this.slamRadius = value;
        markPresent("slamRadius");
    }

    /** 滑行攻击伤害；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("slideAttack")
    private BigDecimal slideAttack;

    /**
     * 记录滑行攻击伤害是否出现，同时区分未提供与显式空值。
     *
     * @param value 滑行攻击伤害，保留协议中的显式空值。
     */
    @JsonSetter("slideAttack")
    public void setSlideAttack(BigDecimal value) {
        this.slideAttack = value;
        markPresent("slideAttack");
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

    /** 蓄力时间；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("windUp")
    private BigDecimal windUp;

    /**
     * 记录蓄力时间是否出现，同时区分未提供与显式空值。
     *
     * @param value 蓄力时间，保留协议中的显式空值。
     */
    @JsonSetter("windUp")
    public void setWindUp(BigDecimal value) {
        this.windUp = value;
        markPresent("windUp");
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

        /** 行为配置列表的蓄力投射物的攻击的腐蚀伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__DT_CORROSIVE")
        private BigDecimal chargedProjectileAttackDtCorrosive;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的腐蚀伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的腐蚀伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__DT_CORROSIVE")
        public void setChargedProjectileAttackDtCorrosive(BigDecimal value) {
            this.chargedProjectileAttackDtCorrosive = value;
            markPresent("chargedProjectile__attack__DT_CORROSIVE");
        }

        /** 行为配置列表的蓄力投射物的攻击的电击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__DT_ELECTRICITY")
        private BigDecimal chargedProjectileAttackDtElectricity;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的电击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的电击伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__DT_ELECTRICITY")
        public void setChargedProjectileAttackDtElectricity(BigDecimal value) {
            this.chargedProjectileAttackDtElectricity = value;
            markPresent("chargedProjectile__attack__DT_ELECTRICITY");
        }

        /** 行为配置列表的蓄力投射物的攻击的爆炸伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__DT_EXPLOSION")
        private BigDecimal chargedProjectileAttackDtExplosion;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的爆炸伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的爆炸伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__DT_EXPLOSION")
        public void setChargedProjectileAttackDtExplosion(BigDecimal value) {
            this.chargedProjectileAttackDtExplosion = value;
            markPresent("chargedProjectile__attack__DT_EXPLOSION");
        }

        /** 行为配置列表的蓄力投射物的攻击的火焰伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__DT_FIRE")
        private BigDecimal chargedProjectileAttackDtFire;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的火焰伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的火焰伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__DT_FIRE")
        public void setChargedProjectileAttackDtFire(BigDecimal value) {
            this.chargedProjectileAttackDtFire = value;
            markPresent("chargedProjectile__attack__DT_FIRE");
        }

        /** 行为配置列表的蓄力投射物的攻击的冰冻伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__DT_FREEZE")
        private BigDecimal chargedProjectileAttackDtFreeze;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的冰冻伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的冰冻伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__DT_FREEZE")
        public void setChargedProjectileAttackDtFreeze(BigDecimal value) {
            this.chargedProjectileAttackDtFreeze = value;
            markPresent("chargedProjectile__attack__DT_FREEZE");
        }

        /** 行为配置列表的蓄力投射物的攻击的冲击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__DT_IMPACT")
        private BigDecimal chargedProjectileAttackDtImpact;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的冲击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的冲击伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__DT_IMPACT")
        public void setChargedProjectileAttackDtImpact(BigDecimal value) {
            this.chargedProjectileAttackDtImpact = value;
            markPresent("chargedProjectile__attack__DT_IMPACT");
        }

        /** 行为配置列表的蓄力投射物的攻击的磁力伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__DT_MAGNETIC")
        private BigDecimal chargedProjectileAttackDtMagnetic;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的磁力伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的磁力伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__DT_MAGNETIC")
        public void setChargedProjectileAttackDtMagnetic(BigDecimal value) {
            this.chargedProjectileAttackDtMagnetic = value;
            markPresent("chargedProjectile__attack__DT_MAGNETIC");
        }

        /** 行为配置列表的蓄力投射物的攻击的穿刺伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__DT_PUNCTURE")
        private BigDecimal chargedProjectileAttackDtPuncture;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的穿刺伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的穿刺伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__DT_PUNCTURE")
        public void setChargedProjectileAttackDtPuncture(BigDecimal value) {
            this.chargedProjectileAttackDtPuncture = value;
            markPresent("chargedProjectile__attack__DT_PUNCTURE");
        }

        /** 行为配置列表的蓄力投射物的攻击的辐射伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__DT_RADIATION")
        private BigDecimal chargedProjectileAttackDtRadiation;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的辐射伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的辐射伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__DT_RADIATION")
        public void setChargedProjectileAttackDtRadiation(BigDecimal value) {
            this.chargedProjectileAttackDtRadiation = value;
            markPresent("chargedProjectile__attack__DT_RADIATION");
        }

        /** 行为配置列表的蓄力投射物的攻击的切割伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__DT_SLASH")
        private BigDecimal chargedProjectileAttackDtSlash;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的切割伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的切割伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__DT_SLASH")
        public void setChargedProjectileAttackDtSlash(BigDecimal value) {
            this.chargedProjectileAttackDtSlash = value;
            markPresent("chargedProjectile__attack__DT_SLASH");
        }

        /** 行为配置列表的蓄力投射物的攻击的异常触发几率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__attack__procChance")
        private BigDecimal chargedProjectileAttackProcChance;

        /**
         * 记录行为配置列表的蓄力投射物的攻击的异常触发几率是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的攻击的异常触发几率，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__attack__procChance")
        public void setChargedProjectileAttackProcChance(BigDecimal value) {
            this.chargedProjectileAttackProcChance = value;
            markPresent("chargedProjectile__attack__procChance");
        }

        /** 行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的爆炸伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__embedDeathAttack__DT_EXPLOSION")
        private BigDecimal chargedProjectileEmbedDeathAttackDtExplosion;

        /**
         * 记录行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的爆炸伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的爆炸伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__embedDeathAttack__DT_EXPLOSION")
        public void setChargedProjectileEmbedDeathAttackDtExplosion(BigDecimal value) {
            this.chargedProjectileEmbedDeathAttackDtExplosion = value;
            markPresent("chargedProjectile__embedDeathAttack__DT_EXPLOSION");
        }

        /** 行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的辐射伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__embedDeathAttack__DT_RADIATION")
        private BigDecimal chargedProjectileEmbedDeathAttackDtRadiation;

        /**
         * 记录行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的辐射伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的辐射伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__embedDeathAttack__DT_RADIATION")
        public void setChargedProjectileEmbedDeathAttackDtRadiation(BigDecimal value) {
            this.chargedProjectileEmbedDeathAttackDtRadiation = value;
            markPresent("chargedProjectile__embedDeathAttack__DT_RADIATION");
        }

        /** 行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的病毒伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__embedDeathAttack__DT_VIRAL")
        private BigDecimal chargedProjectileEmbedDeathAttackDtViral;

        /**
         * 记录行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的病毒伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的病毒伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__embedDeathAttack__DT_VIRAL")
        public void setChargedProjectileEmbedDeathAttackDtViral(BigDecimal value) {
            this.chargedProjectileEmbedDeathAttackDtViral = value;
            markPresent("chargedProjectile__embedDeathAttack__DT_VIRAL");
        }

        /** 行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的异常触发几率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__embedDeathAttack__procChance")
        private BigDecimal chargedProjectileEmbedDeathAttackProcChance;

        /**
         * 记录行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的异常触发几率是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的嵌入目标后死亡触发攻击的异常触发几率，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__embedDeathAttack__procChance")
        public void setChargedProjectileEmbedDeathAttackProcChance(BigDecimal value) {
            this.chargedProjectileEmbedDeathAttackProcChance = value;
            markPresent("chargedProjectile__embedDeathAttack__procChance");
        }

        /** 行为配置列表的蓄力投射物的爆炸攻击的腐蚀伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__explosiveAttack__DT_CORROSIVE")
        private BigDecimal chargedProjectileExplosiveAttackDtCorrosive;

        /**
         * 记录行为配置列表的蓄力投射物的爆炸攻击的腐蚀伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的爆炸攻击的腐蚀伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__explosiveAttack__DT_CORROSIVE")
        public void setChargedProjectileExplosiveAttackDtCorrosive(BigDecimal value) {
            this.chargedProjectileExplosiveAttackDtCorrosive = value;
            markPresent("chargedProjectile__explosiveAttack__DT_CORROSIVE");
        }

        /** 行为配置列表的蓄力投射物的爆炸攻击的爆炸伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__explosiveAttack__DT_EXPLOSION")
        private BigDecimal chargedProjectileExplosiveAttackDtExplosion;

        /**
         * 记录行为配置列表的蓄力投射物的爆炸攻击的爆炸伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的爆炸攻击的爆炸伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__explosiveAttack__DT_EXPLOSION")
        public void setChargedProjectileExplosiveAttackDtExplosion(BigDecimal value) {
            this.chargedProjectileExplosiveAttackDtExplosion = value;
            markPresent("chargedProjectile__explosiveAttack__DT_EXPLOSION");
        }

        /** 行为配置列表的蓄力投射物的爆炸攻击的火焰伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__explosiveAttack__DT_FIRE")
        private BigDecimal chargedProjectileExplosiveAttackDtFire;

        /**
         * 记录行为配置列表的蓄力投射物的爆炸攻击的火焰伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的爆炸攻击的火焰伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__explosiveAttack__DT_FIRE")
        public void setChargedProjectileExplosiveAttackDtFire(BigDecimal value) {
            this.chargedProjectileExplosiveAttackDtFire = value;
            markPresent("chargedProjectile__explosiveAttack__DT_FIRE");
        }

        /** 行为配置列表的蓄力投射物的爆炸攻击的冰冻伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__explosiveAttack__DT_FREEZE")
        private BigDecimal chargedProjectileExplosiveAttackDtFreeze;

        /**
         * 记录行为配置列表的蓄力投射物的爆炸攻击的冰冻伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的爆炸攻击的冰冻伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__explosiveAttack__DT_FREEZE")
        public void setChargedProjectileExplosiveAttackDtFreeze(BigDecimal value) {
            this.chargedProjectileExplosiveAttackDtFreeze = value;
            markPresent("chargedProjectile__explosiveAttack__DT_FREEZE");
        }

        /** 行为配置列表的蓄力投射物的爆炸攻击的穿刺伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__explosiveAttack__DT_PUNCTURE")
        private BigDecimal chargedProjectileExplosiveAttackDtPuncture;

        /**
         * 记录行为配置列表的蓄力投射物的爆炸攻击的穿刺伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的爆炸攻击的穿刺伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__explosiveAttack__DT_PUNCTURE")
        public void setChargedProjectileExplosiveAttackDtPuncture(BigDecimal value) {
            this.chargedProjectileExplosiveAttackDtPuncture = value;
            markPresent("chargedProjectile__explosiveAttack__DT_PUNCTURE");
        }

        /** 行为配置列表的蓄力投射物的爆炸攻击的辐射伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__explosiveAttack__DT_RADIATION")
        private BigDecimal chargedProjectileExplosiveAttackDtRadiation;

        /**
         * 记录行为配置列表的蓄力投射物的爆炸攻击的辐射伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的爆炸攻击的辐射伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__explosiveAttack__DT_RADIATION")
        public void setChargedProjectileExplosiveAttackDtRadiation(BigDecimal value) {
            this.chargedProjectileExplosiveAttackDtRadiation = value;
            markPresent("chargedProjectile__explosiveAttack__DT_RADIATION");
        }

        /** 行为配置列表的蓄力投射物的爆炸攻击的切割伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__explosiveAttack__DT_SLASH")
        private BigDecimal chargedProjectileExplosiveAttackDtSlash;

        /**
         * 记录行为配置列表的蓄力投射物的爆炸攻击的切割伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的爆炸攻击的切割伤害，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__explosiveAttack__DT_SLASH")
        public void setChargedProjectileExplosiveAttackDtSlash(BigDecimal value) {
            this.chargedProjectileExplosiveAttackDtSlash = value;
            markPresent("chargedProjectile__explosiveAttack__DT_SLASH");
        }

        /** 行为配置列表的蓄力投射物的爆炸攻击的异常触发几率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chargedProjectile__explosiveAttack__procChance")
        private BigDecimal chargedProjectileExplosiveAttackProcChance;

        /**
         * 记录行为配置列表的蓄力投射物的爆炸攻击的异常触发几率是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的蓄力投射物的爆炸攻击的异常触发几率，保留协议中的显式空值。
         */
        @JsonSetter("chargedProjectile__explosiveAttack__procChance")
        public void setChargedProjectileExplosiveAttackProcChance(BigDecimal value) {
            this.chargedProjectileExplosiveAttackProcChance = value;
            markPresent("chargedProjectile__explosiveAttack__procChance");
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

        /** 行为配置列表的冲击伤害的腐蚀伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_CORROSIVE")
        private BigDecimal impactDtCorrosive;

        /**
         * 记录行为配置列表的冲击伤害的腐蚀伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的腐蚀伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_CORROSIVE")
        public void setImpactDtCorrosive(BigDecimal value) {
            this.impactDtCorrosive = value;
            markPresent("impact__DT_CORROSIVE");
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

        /** 行为配置列表的冲击伤害的爆炸伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_EXPLOSION")
        private BigDecimal impactDtExplosion;

        /**
         * 记录行为配置列表的冲击伤害的爆炸伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的爆炸伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_EXPLOSION")
        public void setImpactDtExplosion(BigDecimal value) {
            this.impactDtExplosion = value;
            markPresent("impact__DT_EXPLOSION");
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

        /** 行为配置列表的冲击伤害的冰冻伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_FREEZE")
        private BigDecimal impactDtFreeze;

        /**
         * 记录行为配置列表的冲击伤害的冰冻伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的冰冻伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_FREEZE")
        public void setImpactDtFreeze(BigDecimal value) {
            this.impactDtFreeze = value;
            markPresent("impact__DT_FREEZE");
        }

        /** 行为配置列表的冲击伤害的毒气伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_GAS")
        private BigDecimal impactDtGas;

        /**
         * 记录行为配置列表的冲击伤害的毒气伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的毒气伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_GAS")
        public void setImpactDtGas(BigDecimal value) {
            this.impactDtGas = value;
            markPresent("impact__DT_GAS");
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

        /** 行为配置列表的冲击伤害的磁力伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_MAGNETIC")
        private BigDecimal impactDtMagnetic;

        /**
         * 记录行为配置列表的冲击伤害的磁力伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的磁力伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_MAGNETIC")
        public void setImpactDtMagnetic(BigDecimal value) {
            this.impactDtMagnetic = value;
            markPresent("impact__DT_MAGNETIC");
        }

        /** 行为配置列表的冲击伤害的毒素伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_POISON")
        private BigDecimal impactDtPoison;

        /**
         * 记录行为配置列表的冲击伤害的毒素伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的毒素伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_POISON")
        public void setImpactDtPoison(BigDecimal value) {
            this.impactDtPoison = value;
            markPresent("impact__DT_POISON");
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

        /** 行为配置列表的冲击伤害的辐射伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_RADIATION")
        private BigDecimal impactDtRadiation;

        /**
         * 记录行为配置列表的冲击伤害的辐射伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的辐射伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_RADIATION")
        public void setImpactDtRadiation(BigDecimal value) {
            this.impactDtRadiation = value;
            markPresent("impact__DT_RADIATION");
        }

        /** 行为配置列表的冲击伤害的感知伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_SENTIENT")
        private BigDecimal impactDtSentient;

        /**
         * 记录行为配置列表的冲击伤害的感知伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的感知伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_SENTIENT")
        public void setImpactDtSentient(BigDecimal value) {
            this.impactDtSentient = value;
            markPresent("impact__DT_SENTIENT");
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

        /** 行为配置列表的冲击伤害的病毒伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("impact__DT_VIRAL")
        private BigDecimal impactDtViral;

        /**
         * 记录行为配置列表的冲击伤害的病毒伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的冲击伤害的病毒伤害，保留协议中的显式空值。
         */
        @JsonSetter("impact__DT_VIRAL")
        public void setImpactDtViral(BigDecimal value) {
            this.impactDtViral = value;
            markPresent("impact__DT_VIRAL");
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

        /** 行为配置列表的投射物的攻击的腐蚀伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_CORROSIVE")
        private BigDecimal projectileAttackDtCorrosive;

        /**
         * 记录行为配置列表的投射物的攻击的腐蚀伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的腐蚀伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_CORROSIVE")
        public void setProjectileAttackDtCorrosive(BigDecimal value) {
            this.projectileAttackDtCorrosive = value;
            markPresent("projectile__attack__DT_CORROSIVE");
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

        /** 行为配置列表的投射物的攻击的爆炸伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_EXPLOSION")
        private BigDecimal projectileAttackDtExplosion;

        /**
         * 记录行为配置列表的投射物的攻击的爆炸伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的爆炸伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_EXPLOSION")
        public void setProjectileAttackDtExplosion(BigDecimal value) {
            this.projectileAttackDtExplosion = value;
            markPresent("projectile__attack__DT_EXPLOSION");
        }

        /** 行为配置列表的投射物的攻击的火焰伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_FIRE")
        private BigDecimal projectileAttackDtFire;

        /**
         * 记录行为配置列表的投射物的攻击的火焰伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的火焰伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_FIRE")
        public void setProjectileAttackDtFire(BigDecimal value) {
            this.projectileAttackDtFire = value;
            markPresent("projectile__attack__DT_FIRE");
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

        /** 行为配置列表的投射物的攻击的磁力伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_MAGNETIC")
        private BigDecimal projectileAttackDtMagnetic;

        /**
         * 记录行为配置列表的投射物的攻击的磁力伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的磁力伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_MAGNETIC")
        public void setProjectileAttackDtMagnetic(BigDecimal value) {
            this.projectileAttackDtMagnetic = value;
            markPresent("projectile__attack__DT_MAGNETIC");
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

        /** 行为配置列表的投射物的攻击的辉光伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_RADIANT")
        private BigDecimal projectileAttackDtRadiant;

        /**
         * 记录行为配置列表的投射物的攻击的辉光伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的辉光伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_RADIANT")
        public void setProjectileAttackDtRadiant(BigDecimal value) {
            this.projectileAttackDtRadiant = value;
            markPresent("projectile__attack__DT_RADIANT");
        }

        /** 行为配置列表的投射物的攻击的辐射伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_RADIATION")
        private BigDecimal projectileAttackDtRadiation;

        /**
         * 记录行为配置列表的投射物的攻击的辐射伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的辐射伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_RADIATION")
        public void setProjectileAttackDtRadiation(BigDecimal value) {
            this.projectileAttackDtRadiation = value;
            markPresent("projectile__attack__DT_RADIATION");
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

        /** 行为配置列表的投射物的攻击的病毒伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__attack__DT_VIRAL")
        private BigDecimal projectileAttackDtViral;

        /**
         * 记录行为配置列表的投射物的攻击的病毒伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的攻击的病毒伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__attack__DT_VIRAL")
        public void setProjectileAttackDtViral(BigDecimal value) {
            this.projectileAttackDtViral = value;
            markPresent("projectile__attack__DT_VIRAL");
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

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的腐蚀伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__DT_CORROSIVE")
        private BigDecimal projectileEmbedDeathAttackDtCorrosive;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的腐蚀伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的腐蚀伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__DT_CORROSIVE")
        public void setProjectileEmbedDeathAttackDtCorrosive(BigDecimal value) {
            this.projectileEmbedDeathAttackDtCorrosive = value;
            markPresent("projectile__embedDeathAttack__DT_CORROSIVE");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的电击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__DT_ELECTRICITY")
        private BigDecimal projectileEmbedDeathAttackDtElectricity;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的电击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的电击伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__DT_ELECTRICITY")
        public void setProjectileEmbedDeathAttackDtElectricity(BigDecimal value) {
            this.projectileEmbedDeathAttackDtElectricity = value;
            markPresent("projectile__embedDeathAttack__DT_ELECTRICITY");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的爆炸伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__DT_EXPLOSION")
        private BigDecimal projectileEmbedDeathAttackDtExplosion;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的爆炸伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的爆炸伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__DT_EXPLOSION")
        public void setProjectileEmbedDeathAttackDtExplosion(BigDecimal value) {
            this.projectileEmbedDeathAttackDtExplosion = value;
            markPresent("projectile__embedDeathAttack__DT_EXPLOSION");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的火焰伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__DT_FIRE")
        private BigDecimal projectileEmbedDeathAttackDtFire;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的火焰伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的火焰伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__DT_FIRE")
        public void setProjectileEmbedDeathAttackDtFire(BigDecimal value) {
            this.projectileEmbedDeathAttackDtFire = value;
            markPresent("projectile__embedDeathAttack__DT_FIRE");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的冰冻伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__DT_FREEZE")
        private BigDecimal projectileEmbedDeathAttackDtFreeze;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的冰冻伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的冰冻伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__DT_FREEZE")
        public void setProjectileEmbedDeathAttackDtFreeze(BigDecimal value) {
            this.projectileEmbedDeathAttackDtFreeze = value;
            markPresent("projectile__embedDeathAttack__DT_FREEZE");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的毒气伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__DT_GAS")
        private BigDecimal projectileEmbedDeathAttackDtGas;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的毒气伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的毒气伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__DT_GAS")
        public void setProjectileEmbedDeathAttackDtGas(BigDecimal value) {
            this.projectileEmbedDeathAttackDtGas = value;
            markPresent("projectile__embedDeathAttack__DT_GAS");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的冲击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__DT_IMPACT")
        private BigDecimal projectileEmbedDeathAttackDtImpact;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的冲击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的冲击伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__DT_IMPACT")
        public void setProjectileEmbedDeathAttackDtImpact(BigDecimal value) {
            this.projectileEmbedDeathAttackDtImpact = value;
            markPresent("projectile__embedDeathAttack__DT_IMPACT");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的毒素伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__DT_POISON")
        private BigDecimal projectileEmbedDeathAttackDtPoison;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的毒素伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的毒素伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__DT_POISON")
        public void setProjectileEmbedDeathAttackDtPoison(BigDecimal value) {
            this.projectileEmbedDeathAttackDtPoison = value;
            markPresent("projectile__embedDeathAttack__DT_POISON");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的穿刺伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__DT_PUNCTURE")
        private BigDecimal projectileEmbedDeathAttackDtPuncture;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的穿刺伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的穿刺伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__DT_PUNCTURE")
        public void setProjectileEmbedDeathAttackDtPuncture(BigDecimal value) {
            this.projectileEmbedDeathAttackDtPuncture = value;
            markPresent("projectile__embedDeathAttack__DT_PUNCTURE");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的辐射伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__DT_RADIATION")
        private BigDecimal projectileEmbedDeathAttackDtRadiation;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的辐射伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的辐射伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__DT_RADIATION")
        public void setProjectileEmbedDeathAttackDtRadiation(BigDecimal value) {
            this.projectileEmbedDeathAttackDtRadiation = value;
            markPresent("projectile__embedDeathAttack__DT_RADIATION");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的切割伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__DT_SLASH")
        private BigDecimal projectileEmbedDeathAttackDtSlash;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的切割伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的切割伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__DT_SLASH")
        public void setProjectileEmbedDeathAttackDtSlash(BigDecimal value) {
            this.projectileEmbedDeathAttackDtSlash = value;
            markPresent("projectile__embedDeathAttack__DT_SLASH");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的病毒伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__DT_VIRAL")
        private BigDecimal projectileEmbedDeathAttackDtViral;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的病毒伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的病毒伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__DT_VIRAL")
        public void setProjectileEmbedDeathAttackDtViral(BigDecimal value) {
            this.projectileEmbedDeathAttackDtViral = value;
            markPresent("projectile__embedDeathAttack__DT_VIRAL");
        }

        /** 行为配置列表的投射物的嵌入目标后死亡触发攻击的异常触发几率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__embedDeathAttack__procChance")
        private BigDecimal projectileEmbedDeathAttackProcChance;

        /**
         * 记录行为配置列表的投射物的嵌入目标后死亡触发攻击的异常触发几率是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的嵌入目标后死亡触发攻击的异常触发几率，保留协议中的显式空值。
         */
        @JsonSetter("projectile__embedDeathAttack__procChance")
        public void setProjectileEmbedDeathAttackProcChance(BigDecimal value) {
            this.projectileEmbedDeathAttackProcChance = value;
            markPresent("projectile__embedDeathAttack__procChance");
        }

        /** 行为配置列表的投射物的爆炸攻击的腐蚀伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_CORROSIVE")
        private BigDecimal projectileExplosiveAttackDtCorrosive;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的腐蚀伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的腐蚀伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_CORROSIVE")
        public void setProjectileExplosiveAttackDtCorrosive(BigDecimal value) {
            this.projectileExplosiveAttackDtCorrosive = value;
            markPresent("projectile__explosiveAttack__DT_CORROSIVE");
        }

        /** 行为配置列表的投射物的爆炸攻击的电击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_ELECTRICITY")
        private BigDecimal projectileExplosiveAttackDtElectricity;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的电击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的电击伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_ELECTRICITY")
        public void setProjectileExplosiveAttackDtElectricity(BigDecimal value) {
            this.projectileExplosiveAttackDtElectricity = value;
            markPresent("projectile__explosiveAttack__DT_ELECTRICITY");
        }

        /** 行为配置列表的投射物的爆炸攻击的爆炸伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_EXPLOSION")
        private BigDecimal projectileExplosiveAttackDtExplosion;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的爆炸伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的爆炸伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_EXPLOSION")
        public void setProjectileExplosiveAttackDtExplosion(BigDecimal value) {
            this.projectileExplosiveAttackDtExplosion = value;
            markPresent("projectile__explosiveAttack__DT_EXPLOSION");
        }

        /** 行为配置列表的投射物的爆炸攻击的火焰伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_FIRE")
        private BigDecimal projectileExplosiveAttackDtFire;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的火焰伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的火焰伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_FIRE")
        public void setProjectileExplosiveAttackDtFire(BigDecimal value) {
            this.projectileExplosiveAttackDtFire = value;
            markPresent("projectile__explosiveAttack__DT_FIRE");
        }

        /** 行为配置列表的投射物的爆炸攻击的冰冻伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_FREEZE")
        private BigDecimal projectileExplosiveAttackDtFreeze;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的冰冻伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的冰冻伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_FREEZE")
        public void setProjectileExplosiveAttackDtFreeze(BigDecimal value) {
            this.projectileExplosiveAttackDtFreeze = value;
            markPresent("projectile__explosiveAttack__DT_FREEZE");
        }

        /** 行为配置列表的投射物的爆炸攻击的毒气伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_GAS")
        private BigDecimal projectileExplosiveAttackDtGas;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的毒气伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的毒气伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_GAS")
        public void setProjectileExplosiveAttackDtGas(BigDecimal value) {
            this.projectileExplosiveAttackDtGas = value;
            markPresent("projectile__explosiveAttack__DT_GAS");
        }

        /** 行为配置列表的投射物的爆炸攻击的冲击伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_IMPACT")
        private BigDecimal projectileExplosiveAttackDtImpact;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的冲击伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的冲击伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_IMPACT")
        public void setProjectileExplosiveAttackDtImpact(BigDecimal value) {
            this.projectileExplosiveAttackDtImpact = value;
            markPresent("projectile__explosiveAttack__DT_IMPACT");
        }

        /** 行为配置列表的投射物的爆炸攻击的毒素伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_POISON")
        private BigDecimal projectileExplosiveAttackDtPoison;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的毒素伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的毒素伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_POISON")
        public void setProjectileExplosiveAttackDtPoison(BigDecimal value) {
            this.projectileExplosiveAttackDtPoison = value;
            markPresent("projectile__explosiveAttack__DT_POISON");
        }

        /** 行为配置列表的投射物的爆炸攻击的穿刺伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_PUNCTURE")
        private BigDecimal projectileExplosiveAttackDtPuncture;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的穿刺伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的穿刺伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_PUNCTURE")
        public void setProjectileExplosiveAttackDtPuncture(BigDecimal value) {
            this.projectileExplosiveAttackDtPuncture = value;
            markPresent("projectile__explosiveAttack__DT_PUNCTURE");
        }

        /** 行为配置列表的投射物的爆炸攻击的辐射伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_RADIATION")
        private BigDecimal projectileExplosiveAttackDtRadiation;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的辐射伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的辐射伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_RADIATION")
        public void setProjectileExplosiveAttackDtRadiation(BigDecimal value) {
            this.projectileExplosiveAttackDtRadiation = value;
            markPresent("projectile__explosiveAttack__DT_RADIATION");
        }

        /** 行为配置列表的投射物的爆炸攻击的切割伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_SLASH")
        private BigDecimal projectileExplosiveAttackDtSlash;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的切割伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的切割伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_SLASH")
        public void setProjectileExplosiveAttackDtSlash(BigDecimal value) {
            this.projectileExplosiveAttackDtSlash = value;
            markPresent("projectile__explosiveAttack__DT_SLASH");
        }

        /** 行为配置列表的投射物的爆炸攻击的病毒伤害；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__DT_VIRAL")
        private BigDecimal projectileExplosiveAttackDtViral;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的病毒伤害是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的病毒伤害，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__DT_VIRAL")
        public void setProjectileExplosiveAttackDtViral(BigDecimal value) {
            this.projectileExplosiveAttackDtViral = value;
            markPresent("projectile__explosiveAttack__DT_VIRAL");
        }

        /** 行为配置列表的投射物的爆炸攻击的异常触发几率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("projectile__explosiveAttack__procChance")
        private BigDecimal projectileExplosiveAttackProcChance;

        /**
         * 记录行为配置列表的投射物的爆炸攻击的异常触发几率是否出现，同时区分未提供与显式空值。
         *
         * @param value 行为配置列表的投射物的爆炸攻击的异常触发几率，保留协议中的显式空值。
         */
        @JsonSetter("projectile__explosiveAttack__procChance")
        public void setProjectileExplosiveAttackProcChance(BigDecimal value) {
            this.projectileExplosiveAttackProcChance = value;
            markPresent("projectile__explosiveAttack__procChance");
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
