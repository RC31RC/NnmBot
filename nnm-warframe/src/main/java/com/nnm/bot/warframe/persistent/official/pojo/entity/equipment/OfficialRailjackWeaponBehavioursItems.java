package com.nnm.bot.warframe.persistent.official.pojo.entity.equipment;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialChildRow;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/** 源 RAILJACK_WEAPON behaviours/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_railjack_weapon_behaviours_items")
public class OfficialRailjackWeaponBehavioursItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 连发配置的数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "burst_count_state")
    private Short burstCountState;

    /** 连发配置的数量。 */
    @Column(name = "burst_count", columnDefinition = "NUMERIC")
    private BigDecimal burstCount;

    /** 连发配置的延迟时间是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "burst_delay_state")
    private Short burstDelayState;

    /** 连发配置的延迟时间。 */
    @Column(name = "burst_delay", columnDefinition = "NUMERIC")
    private BigDecimal burstDelay;

    /** 单次开火迭代次数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "fireiterations_state")
    private Short fireIterationsState;

    /** 单次开火迭代次数。 */
    @Column(name = "fireiterations", columnDefinition = "NUMERIC")
    private BigDecimal fireIterations;

    /** 冲击伤害的电击伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_dt_electricity_state")
    private Short impactDtElectricityState;

    /** 冲击伤害的电击伤害。 */
    @Column(name = "impact_dt_electricity", columnDefinition = "NUMERIC")
    private BigDecimal impactDtElectricity;

    /** 冲击伤害的火焰伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_dt_fire_state")
    private Short impactDtFireState;

    /** 冲击伤害的火焰伤害。 */
    @Column(name = "impact_dt_fire", columnDefinition = "NUMERIC")
    private BigDecimal impactDtFire;

    /** 冲击伤害的冲击伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_dt_impact_state")
    private Short impactDtImpactState;

    /** 冲击伤害的冲击伤害。 */
    @Column(name = "impact_dt_impact", columnDefinition = "NUMERIC")
    private BigDecimal impactDtImpact;

    /** 冲击伤害的穿刺伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_dt_puncture_state")
    private Short impactDtPunctureState;

    /** 冲击伤害的穿刺伤害。 */
    @Column(name = "impact_dt_puncture", columnDefinition = "NUMERIC")
    private BigDecimal impactDtPuncture;

    /** 冲击伤害的切割伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_dt_slash_state")
    private Short impactDtSlashState;

    /** 冲击伤害的切割伤害。 */
    @Column(name = "impact_dt_slash", columnDefinition = "NUMERIC")
    private BigDecimal impactDtSlash;

    /** 冲击伤害的异常触发几率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_procchance_state")
    private Short impactProcChanceState;

    /** 冲击伤害的异常触发几率。 */
    @Column(name = "impact_procchance", columnDefinition = "NUMERIC")
    private BigDecimal impactProcChance;

    /** 投射物的攻击的电击伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_attack_dt_electricity_state")
    private Short projectileAttackDtElectricityState;

    /** 投射物的攻击的电击伤害。 */
    @Column(name = "projectile_attack_dt_electricity", columnDefinition = "NUMERIC")
    private BigDecimal projectileAttackDtElectricity;

    /** 投射物的攻击的冰冻伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_attack_dt_freeze_state")
    private Short projectileAttackDtFreezeState;

    /** 投射物的攻击的冰冻伤害。 */
    @Column(name = "projectile_attack_dt_freeze", columnDefinition = "NUMERIC")
    private BigDecimal projectileAttackDtFreeze;

    /** 投射物的攻击的冲击伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_attack_dt_impact_state")
    private Short projectileAttackDtImpactState;

    /** 投射物的攻击的冲击伤害。 */
    @Column(name = "projectile_attack_dt_impact", columnDefinition = "NUMERIC")
    private BigDecimal projectileAttackDtImpact;

    /** 投射物的攻击的毒素伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_attack_dt_poison_state")
    private Short projectileAttackDtPoisonState;

    /** 投射物的攻击的毒素伤害。 */
    @Column(name = "projectile_attack_dt_poison", columnDefinition = "NUMERIC")
    private BigDecimal projectileAttackDtPoison;

    /** 投射物的攻击的穿刺伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_attack_dt_puncture_state")
    private Short projectileAttackDtPunctureState;

    /** 投射物的攻击的穿刺伤害。 */
    @Column(name = "projectile_attack_dt_puncture", columnDefinition = "NUMERIC")
    private BigDecimal projectileAttackDtPuncture;

    /** 投射物的攻击的切割伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_attack_dt_slash_state")
    private Short projectileAttackDtSlashState;

    /** 投射物的攻击的切割伤害。 */
    @Column(name = "projectile_attack_dt_slash", columnDefinition = "NUMERIC")
    private BigDecimal projectileAttackDtSlash;

    /** 投射物的攻击的异常触发几率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_attack_procchance_state")
    private Short projectileAttackProcChanceState;

    /** 投射物的攻击的异常触发几率。 */
    @Column(name = "projectile_attack_procchance", columnDefinition = "NUMERIC")
    private BigDecimal projectileAttackProcChance;

    /** 状态名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "statename_state")
    private Short stateNameState;

    /** 状态名称。 */
    @Column(name = "statename", columnDefinition = "TEXT")
    private String stateName;

    /** 状态英文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "statenameen_state")
    private Short stateNameEnState;

    /** 状态英文名称。 */
    @Column(name = "statenameen", columnDefinition = "TEXT")
    private String stateNameEn;

    /** 状态中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "statenamezh_state")
    private Short stateNameZhState;

    /** 状态中文名称。 */
    @Column(name = "statenamezh", columnDefinition = "TEXT")
    private String stateNameZh;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialRailjackWeaponRoot parent;
}
