package com.nnm.bot.warframe.persistent.official.pojo.entity.equipment;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/** 源 WEAPON 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_weapon_root")
public class OfficialWeaponRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 精准度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "accuracy_state")
    private Short accuracyState;

    /** 精准度。 */
    @Column(name = "accuracy", columnDefinition = "NUMERIC")
    private BigDecimal accuracy;

    /** 额外物品列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "additionalitems_state")
    private Short additionalItemsState;

    /** 刺刀关联武器类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "bayonetotherweapontype_state")
    private Short bayonetOtherWeaponTypeState;

    /** 刺刀关联武器类型。 */
    @Column(name = "bayonetotherweapontype", columnDefinition = "TEXT")
    private String bayonetOtherWeaponType;

    /** 行为配置列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "behaviours_state")
    private Short behavioursState;

    /** 格挡角度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "blockingangle_state")
    private Short blockingAngleState;

    /** 格挡角度。 */
    @Column(name = "blockingangle", columnDefinition = "NUMERIC")
    private BigDecimal blockingAngle;

    /** 是否为资料库隐藏条目是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "codexsecret_state")
    private Short codexSecretState;

    /** 是否为资料库隐藏条目。 */
    @Column(name = "codexsecret")
    private Boolean codexSecret;

    /** 连击持续时间是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "comboduration_state")
    private Short comboDurationState;

    /** 连击持续时间。 */
    @Column(name = "comboduration", columnDefinition = "NUMERIC")
    private BigDecimal comboDuration;

    /** 兼容性标签列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "compatibilitytags_state")
    private Short compatibilityTagsState;

    /** 现金消耗是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "creditscost_state")
    private Short creditsCostState;

    /** 现金消耗。 */
    @Column(name = "creditscost", columnDefinition = "NUMERIC")
    private BigDecimal creditsCost;

    /** 暴击几率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "criticalchance_state")
    private Short criticalChanceState;

    /** 暴击几率。 */
    @Column(name = "criticalchance", columnDefinition = "NUMERIC")
    private BigDecimal criticalChance;

    /** 暴击倍率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "criticalmultiplier_state")
    private Short criticalMultiplierState;

    /** 暴击倍率。 */
    @Column(name = "criticalmultiplier", columnDefinition = "NUMERIC")
    private BigDecimal criticalMultiplier;

    /** 单发伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "damagepershot_state")
    private Short damagePerShotState;

    /** 预装强化列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "defaultupgrades_state")
    private Short defaultUpgradesState;

    /** 描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "description_value_state")
    private Short descriptionState;

    /** 描述。 */
    @Column(name = "description_value", columnDefinition = "TEXT")
    private String descriptionValue;

    /** 英文描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "descriptionen_state")
    private Short descriptionEnState;

    /** 英文描述。 */
    @Column(name = "descriptionen", columnDefinition = "TEXT")
    private String descriptionEn;

    /** 中文描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "descriptionzh_state")
    private Short descriptionZhState;

    /** 中文描述。 */
    @Column(name = "descriptionzh", columnDefinition = "TEXT")
    private String descriptionZh;

    /** 捐赠声望加成是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "donationstandingbonus_state")
    private Short donationStandingBonusState;

    /** 捐赠声望加成。 */
    @Column(name = "donationstandingbonus", columnDefinition = "NUMERIC")
    private BigDecimal donationStandingBonus;

    /** 是否排除在资料库之外是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "excludefromcodex_state")
    private Short excludeFromCodexState;

    /** 是否排除在资料库之外。 */
    @Column(name = "excludefromcodex")
    private Boolean excludeFromCodexValue;

    /** 是否排除在商店之外是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "excludefrommarket_state")
    private Short excludeFromMarketState;

    /** 是否排除在商店之外。 */
    @Column(name = "excludefrommarket")
    private Boolean excludeFromMarket;

    /** 射速是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "firerate_state")
    private Short fireRateState;

    /** 射速。 */
    @Column(name = "firerate", columnDefinition = "NUMERIC")
    private BigDecimal fireRate;

    /** 穿透衰减是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "followthrough_state")
    private Short followThroughState;

    /** 穿透衰减。 */
    @Column(name = "followthrough", columnDefinition = "NUMERIC")
    private BigDecimal followThrough;

    /** 枪械类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "guntype_state")
    private Short gunTypeState;

    /** 枪械类型。 */
    @Column(name = "guntype", columnDefinition = "TEXT")
    private String gunType;

    /** 重击伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "heavyattackdamage_state")
    private Short heavyAttackDamageState;

    /** 重击伤害。 */
    @Column(name = "heavyattackdamage", columnDefinition = "NUMERIC")
    private BigDecimal heavyAttackDamage;

    /** 重型震地攻击伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "heavyslamattack_state")
    private Short heavySlamAttackState;

    /** 重型震地攻击伤害。 */
    @Column(name = "heavyslamattack", columnDefinition = "NUMERIC")
    private BigDecimal heavySlamAttack;

    /** 重型震地范围伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "heavyslamradialdamage_state")
    private Short heavySlamRadialDamageState;

    /** 重型震地范围伤害。 */
    @Column(name = "heavyslamradialdamage", columnDefinition = "NUMERIC")
    private BigDecimal heavySlamRadialDamage;

    /** 重型震地半径是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "heavyslamradius_state")
    private Short heavySlamRadiusState;

    /** 重型震地半径。 */
    @Column(name = "heavyslamradius", columnDefinition = "NUMERIC")
    private BigDecimal heavySlamRadius;

    /** 收纳位置分类是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "holstercategory_state")
    private Short holsterCategoryState;

    /** 收纳位置分类。 */
    @Column(name = "holstercategory", columnDefinition = "TEXT")
    private String holsterCategory;

    /** 图标是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "icon_state")
    private Short iconState;

    /** 图标。 */
    @Column(name = "icon", columnDefinition = "TEXT")
    private String icon;

    /** 图标状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "iconstatus_state")
    private Short iconStatusState;

    /** 图标状态。 */
    @Column(name = "iconstatus", columnDefinition = "TEXT")
    private String iconStatus;

    /** 图标地址是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "iconurl_state")
    private Short iconUrlState;

    /** 图标地址。 */
    @Column(name = "iconurl", columnDefinition = "TEXT")
    private String iconUrl;

    /** 首次推出时间是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "introducedat_state")
    private Short introducedAtState;

    /** 首次推出时间。 */
    @Column(name = "introducedat", columnDefinition = "NUMERIC")
    private BigDecimal introducedAt;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    /** 弹匣容量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "magazinesize_state")
    private Short magazineSizeState;

    /** 弹匣容量。 */
    @Column(name = "magazinesize", columnDefinition = "NUMERIC")
    private BigDecimal magazineSize;

    /** 所需段位是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "masteryreq_state")
    private Short masteryReqState;

    /** 所需段位。 */
    @Column(name = "masteryreq", columnDefinition = "NUMERIC")
    private BigDecimal masteryReq;

    /** 等级上限是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "maxlevelcap_state")
    private Short maxLevelCapState;

    /** 等级上限。 */
    @Column(name = "maxlevelcap", columnDefinition = "NUMERIC")
    private BigDecimal maxLevelCap;

    /** 多重射击是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "multishot_state")
    private Short multishotState;

    /** 多重射击。 */
    @Column(name = "multishot", columnDefinition = "NUMERIC")
    private BigDecimal multishot;

    /** 名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "name_state")
    private Short nameState;

    /** 名称。 */
    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    /** 英文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "nameen_state")
    private Short nameEnState;

    /** 英文名称。 */
    @Column(name = "nameen", columnDefinition = "TEXT")
    private String nameEn;

    /** 名称本地化键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "namekey_state")
    private Short nameKeyState;

    /** 名称本地化键。 */
    @Column(name = "namekey", columnDefinition = "TEXT")
    private String nameKey;

    /** 中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "namezh_state")
    private Short nameZhState;

    /** 中文名称。 */
    @Column(name = "namezh", columnDefinition = "TEXT")
    private String nameZh;

    /** 噪音级别是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "noise_state")
    private Short noiseState;

    /** 噪音级别。 */
    @Column(name = "noise", columnDefinition = "TEXT")
    private String noise;

    /** 裂罅倾向值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "omegaattenuation_state")
    private Short omegaAttenuationState;

    /** 裂罅倾向值。 */
    @Column(name = "omegaattenuation", columnDefinition = "NUMERIC")
    private BigDecimal omegaAttenuation;

    /** 是否仅可购买一次是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "onetimepurchasable_state")
    private Short oneTimePurchasableState;

    /** 是否仅可购买一次。 */
    @Column(name = "onetimepurchasable")
    private Boolean oneTimePurchasable;

    /** 父级名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "parentname_state")
    private Short parentNameState;

    /** 父级名称。 */
    @Column(name = "parentname", columnDefinition = "TEXT")
    private String parentNameValue;

    /** 部件类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "parttype_state")
    private Short partTypeState;

    /** 部件类型。 */
    @Column(name = "parttype", columnDefinition = "TEXT")
    private String partType;

    /** 铂金消耗是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "platinumcost_state")
    private Short platinumCostState;

    /** 铂金消耗。 */
    @Column(name = "platinumcost", columnDefinition = "NUMERIC")
    private BigDecimal platinumCost;

    /** 圣装裂罅倾向值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "primeomegaattenuation_state")
    private Short primeOmegaAttenuationState;

    /** 圣装裂罅倾向值。 */
    @Column(name = "primeomegaattenuation", columnDefinition = "NUMERIC")
    private BigDecimal primeOmegaAttenuation;

    /** 异常触发几率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "procchance_state")
    private Short procChanceState;

    /** 异常触发几率。 */
    @Column(name = "procchance", columnDefinition = "NUMERIC")
    private BigDecimal procChance;

    /** 产品分类是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "productcategory_state")
    private Short productCategoryState;

    /** 产品分类。 */
    @Column(name = "productcategory", columnDefinition = "TEXT")
    private String productCategoryValue;

    /** 范围是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "range_state")
    private Short rangeState;

    /** 范围。 */
    @Column(name = "range", columnDefinition = "NUMERIC")
    private BigDecimal range;

    /** 记录键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "recordkey_state")
    private Short recordKeyState;

    /** 记录键。 */
    @Column(name = "recordkey", columnDefinition = "TEXT")
    private String recordKey;

    /** 装填时间是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "reloadtime_state")
    private Short reloadTimeState;

    /** 装填时间。 */
    @Column(name = "reloadtime", columnDefinition = "NUMERIC")
    private BigDecimal reloadTime;

    /** 守护是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "sentinel_state")
    private Short sentinelState;

    /** 守护。 */
    @Column(name = "sentinel")
    private Boolean sentinel;

    /** 震地攻击伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "slamattack_state")
    private Short slamAttackState;

    /** 震地攻击伤害。 */
    @Column(name = "slamattack", columnDefinition = "NUMERIC")
    private BigDecimal slamAttack;

    /** 震地范围伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "slamradialdamage_state")
    private Short slamRadialDamageState;

    /** 震地范围伤害。 */
    @Column(name = "slamradialdamage", columnDefinition = "NUMERIC")
    private BigDecimal slamRadialDamage;

    /** 震地半径是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "slamradius_state")
    private Short slamRadiusState;

    /** 震地半径。 */
    @Column(name = "slamradius", columnDefinition = "NUMERIC")
    private BigDecimal slamRadius;

    /** 滑行攻击伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "slideattack_state")
    private Short slideAttackState;

    /** 滑行攻击伤害。 */
    @Column(name = "slideattack", columnDefinition = "NUMERIC")
    private BigDecimal slideAttack;

    /** 槽位是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "slot_state")
    private Short slotState;

    /** 槽位。 */
    @Column(name = "slot", columnDefinition = "NUMERIC")
    private BigDecimal slot;

    /** 总伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "totaldamage_state")
    private Short totalDamageState;

    /** 总伤害。 */
    @Column(name = "totaldamage", columnDefinition = "NUMERIC")
    private BigDecimal totalDamage;

    /** 可交易是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "tradable_state")
    private Short tradableState;

    /** 可交易。 */
    @Column(name = "tradable")
    private Boolean tradable;

    /** 触发条件是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "trigger_state")
    private Short triggerState;

    /** 触发条件。 */
    @Column(name = "trigger", columnDefinition = "TEXT")
    private String trigger;

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    /** 变体类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "varianttype_state")
    private Short variantTypeState;

    /** 变体类型。 */
    @Column(name = "varianttype", columnDefinition = "TEXT")
    private String variantType;

    /** 蓄力时间是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "windup_state")
    private Short windUpState;

    /** 蓄力时间。 */
    @Column(name = "windup", columnDefinition = "NUMERIC")
    private BigDecimal windUp;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialWeaponAdditionalItemsItems> officialWeaponAdditionalItemsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialWeaponBehavioursItems> officialWeaponBehavioursItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialWeaponCompatibilityTagsItems> officialWeaponCompatibilityTagsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialWeaponDamagePerShotItems> officialWeaponDamagePerShotItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialWeaponDefaultUpgradesItems> officialWeaponDefaultUpgradesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialWeaponItemReferencesItems> officialWeaponItemReferencesItemsRows = new ArrayList<>();
}
