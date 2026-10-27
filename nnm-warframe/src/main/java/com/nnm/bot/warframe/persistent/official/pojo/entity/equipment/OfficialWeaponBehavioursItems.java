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

/** 源 WEAPON behaviours/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_weapon_behaviours_items")
public class OfficialWeaponBehavioursItems extends OfficialChildRow {
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

    /** 蓄力投射物的攻击的腐蚀伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_attack_dt_corrosive_state")
    private Short chargedProjectileAttackDtCorrosiveState;

    /** 蓄力投射物的攻击的腐蚀伤害。 */
    @Column(name = "chargedprojectile_attack_dt_corrosive", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileAttackDtCorrosive;

    /** 蓄力投射物的攻击的电击伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_attack_dt_electricity_state")
    private Short chargedProjectileAttackDtElectricityState;

    /** 蓄力投射物的攻击的电击伤害。 */
    @Column(name = "chargedprojectile_attack_dt_electricity", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileAttackDtElectricity;

    /** 蓄力投射物的攻击的爆炸伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_attack_dt_explosion_state")
    private Short chargedProjectileAttackDtExplosionState;

    /** 蓄力投射物的攻击的爆炸伤害。 */
    @Column(name = "chargedprojectile_attack_dt_explosion", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileAttackDtExplosion;

    /** 蓄力投射物的攻击的火焰伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_attack_dt_fire_state")
    private Short chargedProjectileAttackDtFireState;

    /** 蓄力投射物的攻击的火焰伤害。 */
    @Column(name = "chargedprojectile_attack_dt_fire", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileAttackDtFire;

    /** 蓄力投射物的攻击的冰冻伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_attack_dt_freeze_state")
    private Short chargedProjectileAttackDtFreezeState;

    /** 蓄力投射物的攻击的冰冻伤害。 */
    @Column(name = "chargedprojectile_attack_dt_freeze", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileAttackDtFreeze;

    /** 蓄力投射物的攻击的冲击伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_attack_dt_impact_state")
    private Short chargedProjectileAttackDtImpactState;

    /** 蓄力投射物的攻击的冲击伤害。 */
    @Column(name = "chargedprojectile_attack_dt_impact", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileAttackDtImpact;

    /** 蓄力投射物的攻击的磁力伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_attack_dt_magnetic_state")
    private Short chargedProjectileAttackDtMagneticState;

    /** 蓄力投射物的攻击的磁力伤害。 */
    @Column(name = "chargedprojectile_attack_dt_magnetic", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileAttackDtMagnetic;

    /** 蓄力投射物的攻击的穿刺伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_attack_dt_puncture_state")
    private Short chargedProjectileAttackDtPunctureState;

    /** 蓄力投射物的攻击的穿刺伤害。 */
    @Column(name = "chargedprojectile_attack_dt_puncture", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileAttackDtPuncture;

    /** 蓄力投射物的攻击的辐射伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_attack_dt_radiation_state")
    private Short chargedProjectileAttackDtRadiationState;

    /** 蓄力投射物的攻击的辐射伤害。 */
    @Column(name = "chargedprojectile_attack_dt_radiation", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileAttackDtRadiation;

    /** 蓄力投射物的攻击的切割伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_attack_dt_slash_state")
    private Short chargedProjectileAttackDtSlashState;

    /** 蓄力投射物的攻击的切割伤害。 */
    @Column(name = "chargedprojectile_attack_dt_slash", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileAttackDtSlash;

    /** 蓄力投射物的攻击的异常触发几率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_attack_procchance_state")
    private Short chargedProjectileAttackProcChanceState;

    /** 蓄力投射物的攻击的异常触发几率。 */
    @Column(name = "chargedprojectile_attack_procchance", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileAttackProcChance;

    /** 蓄力投射物的嵌入目标后死亡触发攻击的爆炸伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_embeddeathattack_dt_explosion_state")
    private Short chargedProjectileEmbedDeathAttackDtExplosionState;

    /** 蓄力投射物的嵌入目标后死亡触发攻击的爆炸伤害。 */
    @Column(name = "chargedprojectile_embeddeathattack_dt_explosion", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileEmbedDeathAttackDtExplosion;

    /** 蓄力投射物的嵌入目标后死亡触发攻击的辐射伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_embeddeathattack_dt_radiation_state")
    private Short chargedProjectileEmbedDeathAttackDtRadiationState;

    /** 蓄力投射物的嵌入目标后死亡触发攻击的辐射伤害。 */
    @Column(name = "chargedprojectile_embeddeathattack_dt_radiation", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileEmbedDeathAttackDtRadiation;

    /** 蓄力投射物的嵌入目标后死亡触发攻击的病毒伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_embeddeathattack_dt_viral_state")
    private Short chargedProjectileEmbedDeathAttackDtViralState;

    /** 蓄力投射物的嵌入目标后死亡触发攻击的病毒伤害。 */
    @Column(name = "chargedprojectile_embeddeathattack_dt_viral", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileEmbedDeathAttackDtViral;

    /** 蓄力投射物的嵌入目标后死亡触发攻击的异常触发几率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_embeddeathattack_procchance_state")
    private Short chargedProjectileEmbedDeathAttackProcChanceState;

    /** 蓄力投射物的嵌入目标后死亡触发攻击的异常触发几率。 */
    @Column(name = "chargedprojectile_embeddeathattack_procchance", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileEmbedDeathAttackProcChance;

    /** 蓄力投射物的爆炸攻击的腐蚀伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_explosiveattack_dt_corrosive_state")
    private Short chargedProjectileExplosiveAttackDtCorrosiveState;

    /** 蓄力投射物的爆炸攻击的腐蚀伤害。 */
    @Column(name = "chargedprojectile_explosiveattack_dt_corrosive", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileExplosiveAttackDtCorrosive;

    /** 蓄力投射物的爆炸攻击的爆炸伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_explosiveattack_dt_explosion_state")
    private Short chargedProjectileExplosiveAttackDtExplosionState;

    /** 蓄力投射物的爆炸攻击的爆炸伤害。 */
    @Column(name = "chargedprojectile_explosiveattack_dt_explosion", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileExplosiveAttackDtExplosion;

    /** 蓄力投射物的爆炸攻击的火焰伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_explosiveattack_dt_fire_state")
    private Short chargedProjectileExplosiveAttackDtFireState;

    /** 蓄力投射物的爆炸攻击的火焰伤害。 */
    @Column(name = "chargedprojectile_explosiveattack_dt_fire", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileExplosiveAttackDtFire;

    /** 蓄力投射物的爆炸攻击的冰冻伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_explosiveattack_dt_freeze_state")
    private Short chargedProjectileExplosiveAttackDtFreezeState;

    /** 蓄力投射物的爆炸攻击的冰冻伤害。 */
    @Column(name = "chargedprojectile_explosiveattack_dt_freeze", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileExplosiveAttackDtFreeze;

    /** 蓄力投射物的爆炸攻击的穿刺伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_explosiveattack_dt_puncture_state")
    private Short chargedProjectileExplosiveAttackDtPunctureState;

    /** 蓄力投射物的爆炸攻击的穿刺伤害。 */
    @Column(name = "chargedprojectile_explosiveattack_dt_puncture", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileExplosiveAttackDtPuncture;

    /** 蓄力投射物的爆炸攻击的辐射伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_explosiveattack_dt_radiation_state")
    private Short chargedProjectileExplosiveAttackDtRadiationState;

    /** 蓄力投射物的爆炸攻击的辐射伤害。 */
    @Column(name = "chargedprojectile_explosiveattack_dt_radiation", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileExplosiveAttackDtRadiation;

    /** 蓄力投射物的爆炸攻击的切割伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_explosiveattack_dt_slash_state")
    private Short chargedProjectileExplosiveAttackDtSlashState;

    /** 蓄力投射物的爆炸攻击的切割伤害。 */
    @Column(name = "chargedprojectile_explosiveattack_dt_slash", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileExplosiveAttackDtSlash;

    /** 蓄力投射物的爆炸攻击的异常触发几率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chargedprojectile_explosiveattack_procchance_state")
    private Short chargedProjectileExplosiveAttackProcChanceState;

    /** 蓄力投射物的爆炸攻击的异常触发几率。 */
    @Column(name = "chargedprojectile_explosiveattack_procchance", columnDefinition = "NUMERIC")
    private BigDecimal chargedProjectileExplosiveAttackProcChance;

    /** 单次开火迭代次数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "fireiterations_state")
    private Short fireIterationsState;

    /** 单次开火迭代次数。 */
    @Column(name = "fireiterations", columnDefinition = "NUMERIC")
    private BigDecimal fireIterations;

    /** 冲击伤害的腐蚀伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_dt_corrosive_state")
    private Short impactDtCorrosiveState;

    /** 冲击伤害的腐蚀伤害。 */
    @Column(name = "impact_dt_corrosive", columnDefinition = "NUMERIC")
    private BigDecimal impactDtCorrosive;

    /** 冲击伤害的电击伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_dt_electricity_state")
    private Short impactDtElectricityState;

    /** 冲击伤害的电击伤害。 */
    @Column(name = "impact_dt_electricity", columnDefinition = "NUMERIC")
    private BigDecimal impactDtElectricity;

    /** 冲击伤害的爆炸伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_dt_explosion_state")
    private Short impactDtExplosionState;

    /** 冲击伤害的爆炸伤害。 */
    @Column(name = "impact_dt_explosion", columnDefinition = "NUMERIC")
    private BigDecimal impactDtExplosion;

    /** 冲击伤害的火焰伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_dt_fire_state")
    private Short impactDtFireState;

    /** 冲击伤害的火焰伤害。 */
    @Column(name = "impact_dt_fire", columnDefinition = "NUMERIC")
    private BigDecimal impactDtFire;

    /** 冲击伤害的冰冻伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_dt_freeze_state")
    private Short impactDtFreezeState;

    /** 冲击伤害的冰冻伤害。 */
    @Column(name = "impact_dt_freeze", columnDefinition = "NUMERIC")
    private BigDecimal impactDtFreeze;

    /** 冲击伤害的毒气伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_dt_gas_state")
    private Short impactDtGasState;

    /** 冲击伤害的毒气伤害。 */
    @Column(name = "impact_dt_gas", columnDefinition = "NUMERIC")
    private BigDecimal impactDtGas;

    /** 冲击伤害的冲击伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_dt_impact_state")
    private Short impactDtImpactState;

    /** 冲击伤害的冲击伤害。 */
    @Column(name = "impact_dt_impact", columnDefinition = "NUMERIC")
    private BigDecimal impactDtImpact;

    /** 冲击伤害的磁力伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_dt_magnetic_state")
    private Short impactDtMagneticState;

    /** 冲击伤害的磁力伤害。 */
    @Column(name = "impact_dt_magnetic", columnDefinition = "NUMERIC")
    private BigDecimal impactDtMagnetic;

    /** 冲击伤害的毒素伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_dt_poison_state")
    private Short impactDtPoisonState;

    /** 冲击伤害的毒素伤害。 */
    @Column(name = "impact_dt_poison", columnDefinition = "NUMERIC")
    private BigDecimal impactDtPoison;

    /** 冲击伤害的穿刺伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_dt_puncture_state")
    private Short impactDtPunctureState;

    /** 冲击伤害的穿刺伤害。 */
    @Column(name = "impact_dt_puncture", columnDefinition = "NUMERIC")
    private BigDecimal impactDtPuncture;

    /** 冲击伤害的辐射伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_dt_radiation_state")
    private Short impactDtRadiationState;

    /** 冲击伤害的辐射伤害。 */
    @Column(name = "impact_dt_radiation", columnDefinition = "NUMERIC")
    private BigDecimal impactDtRadiation;

    /** 冲击伤害的感知伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_dt_sentient_state")
    private Short impactDtSentientState;

    /** 冲击伤害的感知伤害。 */
    @Column(name = "impact_dt_sentient", columnDefinition = "NUMERIC")
    private BigDecimal impactDtSentient;

    /** 冲击伤害的切割伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_dt_slash_state")
    private Short impactDtSlashState;

    /** 冲击伤害的切割伤害。 */
    @Column(name = "impact_dt_slash", columnDefinition = "NUMERIC")
    private BigDecimal impactDtSlash;

    /** 冲击伤害的病毒伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_dt_viral_state")
    private Short impactDtViralState;

    /** 冲击伤害的病毒伤害。 */
    @Column(name = "impact_dt_viral", columnDefinition = "NUMERIC")
    private BigDecimal impactDtViral;

    /** 冲击伤害的异常触发几率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "impact_procchance_state")
    private Short impactProcChanceState;

    /** 冲击伤害的异常触发几率。 */
    @Column(name = "impact_procchance", columnDefinition = "NUMERIC")
    private BigDecimal impactProcChance;

    /** 投射物的攻击的腐蚀伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_attack_dt_corrosive_state")
    private Short projectileAttackDtCorrosiveState;

    /** 投射物的攻击的腐蚀伤害。 */
    @Column(name = "projectile_attack_dt_corrosive", columnDefinition = "NUMERIC")
    private BigDecimal projectileAttackDtCorrosive;

    /** 投射物的攻击的电击伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_attack_dt_electricity_state")
    private Short projectileAttackDtElectricityState;

    /** 投射物的攻击的电击伤害。 */
    @Column(name = "projectile_attack_dt_electricity", columnDefinition = "NUMERIC")
    private BigDecimal projectileAttackDtElectricity;

    /** 投射物的攻击的爆炸伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_attack_dt_explosion_state")
    private Short projectileAttackDtExplosionState;

    /** 投射物的攻击的爆炸伤害。 */
    @Column(name = "projectile_attack_dt_explosion", columnDefinition = "NUMERIC")
    private BigDecimal projectileAttackDtExplosion;

    /** 投射物的攻击的火焰伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_attack_dt_fire_state")
    private Short projectileAttackDtFireState;

    /** 投射物的攻击的火焰伤害。 */
    @Column(name = "projectile_attack_dt_fire", columnDefinition = "NUMERIC")
    private BigDecimal projectileAttackDtFire;

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

    /** 投射物的攻击的磁力伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_attack_dt_magnetic_state")
    private Short projectileAttackDtMagneticState;

    /** 投射物的攻击的磁力伤害。 */
    @Column(name = "projectile_attack_dt_magnetic", columnDefinition = "NUMERIC")
    private BigDecimal projectileAttackDtMagnetic;

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

    /** 投射物的攻击的辉光伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_attack_dt_radiant_state")
    private Short projectileAttackDtRadiantState;

    /** 投射物的攻击的辉光伤害。 */
    @Column(name = "projectile_attack_dt_radiant", columnDefinition = "NUMERIC")
    private BigDecimal projectileAttackDtRadiant;

    /** 投射物的攻击的辐射伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_attack_dt_radiation_state")
    private Short projectileAttackDtRadiationState;

    /** 投射物的攻击的辐射伤害。 */
    @Column(name = "projectile_attack_dt_radiation", columnDefinition = "NUMERIC")
    private BigDecimal projectileAttackDtRadiation;

    /** 投射物的攻击的切割伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_attack_dt_slash_state")
    private Short projectileAttackDtSlashState;

    /** 投射物的攻击的切割伤害。 */
    @Column(name = "projectile_attack_dt_slash", columnDefinition = "NUMERIC")
    private BigDecimal projectileAttackDtSlash;

    /** 投射物的攻击的病毒伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_attack_dt_viral_state")
    private Short projectileAttackDtViralState;

    /** 投射物的攻击的病毒伤害。 */
    @Column(name = "projectile_attack_dt_viral", columnDefinition = "NUMERIC")
    private BigDecimal projectileAttackDtViral;

    /** 投射物的攻击的异常触发几率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_attack_procchance_state")
    private Short projectileAttackProcChanceState;

    /** 投射物的攻击的异常触发几率。 */
    @Column(name = "projectile_attack_procchance", columnDefinition = "NUMERIC")
    private BigDecimal projectileAttackProcChance;

    /** 投射物的嵌入目标后死亡触发攻击的腐蚀伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_embeddeathattack_dt_corrosive_state")
    private Short projectileEmbedDeathAttackDtCorrosiveState;

    /** 投射物的嵌入目标后死亡触发攻击的腐蚀伤害。 */
    @Column(name = "projectile_embeddeathattack_dt_corrosive", columnDefinition = "NUMERIC")
    private BigDecimal projectileEmbedDeathAttackDtCorrosive;

    /** 投射物的嵌入目标后死亡触发攻击的电击伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_embeddeathattack_dt_electricity_state")
    private Short projectileEmbedDeathAttackDtElectricityState;

    /** 投射物的嵌入目标后死亡触发攻击的电击伤害。 */
    @Column(name = "projectile_embeddeathattack_dt_electricity", columnDefinition = "NUMERIC")
    private BigDecimal projectileEmbedDeathAttackDtElectricity;

    /** 投射物的嵌入目标后死亡触发攻击的爆炸伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_embeddeathattack_dt_explosion_state")
    private Short projectileEmbedDeathAttackDtExplosionState;

    /** 投射物的嵌入目标后死亡触发攻击的爆炸伤害。 */
    @Column(name = "projectile_embeddeathattack_dt_explosion", columnDefinition = "NUMERIC")
    private BigDecimal projectileEmbedDeathAttackDtExplosion;

    /** 投射物的嵌入目标后死亡触发攻击的火焰伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_embeddeathattack_dt_fire_state")
    private Short projectileEmbedDeathAttackDtFireState;

    /** 投射物的嵌入目标后死亡触发攻击的火焰伤害。 */
    @Column(name = "projectile_embeddeathattack_dt_fire", columnDefinition = "NUMERIC")
    private BigDecimal projectileEmbedDeathAttackDtFire;

    /** 投射物的嵌入目标后死亡触发攻击的冰冻伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_embeddeathattack_dt_freeze_state")
    private Short projectileEmbedDeathAttackDtFreezeState;

    /** 投射物的嵌入目标后死亡触发攻击的冰冻伤害。 */
    @Column(name = "projectile_embeddeathattack_dt_freeze", columnDefinition = "NUMERIC")
    private BigDecimal projectileEmbedDeathAttackDtFreeze;

    /** 投射物的嵌入目标后死亡触发攻击的毒气伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_embeddeathattack_dt_gas_state")
    private Short projectileEmbedDeathAttackDtGasState;

    /** 投射物的嵌入目标后死亡触发攻击的毒气伤害。 */
    @Column(name = "projectile_embeddeathattack_dt_gas", columnDefinition = "NUMERIC")
    private BigDecimal projectileEmbedDeathAttackDtGas;

    /** 投射物的嵌入目标后死亡触发攻击的冲击伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_embeddeathattack_dt_impact_state")
    private Short projectileEmbedDeathAttackDtImpactState;

    /** 投射物的嵌入目标后死亡触发攻击的冲击伤害。 */
    @Column(name = "projectile_embeddeathattack_dt_impact", columnDefinition = "NUMERIC")
    private BigDecimal projectileEmbedDeathAttackDtImpact;

    /** 投射物的嵌入目标后死亡触发攻击的毒素伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_embeddeathattack_dt_poison_state")
    private Short projectileEmbedDeathAttackDtPoisonState;

    /** 投射物的嵌入目标后死亡触发攻击的毒素伤害。 */
    @Column(name = "projectile_embeddeathattack_dt_poison", columnDefinition = "NUMERIC")
    private BigDecimal projectileEmbedDeathAttackDtPoison;

    /** 投射物的嵌入目标后死亡触发攻击的穿刺伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_embeddeathattack_dt_puncture_state")
    private Short projectileEmbedDeathAttackDtPunctureState;

    /** 投射物的嵌入目标后死亡触发攻击的穿刺伤害。 */
    @Column(name = "projectile_embeddeathattack_dt_puncture", columnDefinition = "NUMERIC")
    private BigDecimal projectileEmbedDeathAttackDtPuncture;

    /** 投射物的嵌入目标后死亡触发攻击的辐射伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_embeddeathattack_dt_radiation_state")
    private Short projectileEmbedDeathAttackDtRadiationState;

    /** 投射物的嵌入目标后死亡触发攻击的辐射伤害。 */
    @Column(name = "projectile_embeddeathattack_dt_radiation", columnDefinition = "NUMERIC")
    private BigDecimal projectileEmbedDeathAttackDtRadiation;

    /** 投射物的嵌入目标后死亡触发攻击的切割伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_embeddeathattack_dt_slash_state")
    private Short projectileEmbedDeathAttackDtSlashState;

    /** 投射物的嵌入目标后死亡触发攻击的切割伤害。 */
    @Column(name = "projectile_embeddeathattack_dt_slash", columnDefinition = "NUMERIC")
    private BigDecimal projectileEmbedDeathAttackDtSlash;

    /** 投射物的嵌入目标后死亡触发攻击的病毒伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_embeddeathattack_dt_viral_state")
    private Short projectileEmbedDeathAttackDtViralState;

    /** 投射物的嵌入目标后死亡触发攻击的病毒伤害。 */
    @Column(name = "projectile_embeddeathattack_dt_viral", columnDefinition = "NUMERIC")
    private BigDecimal projectileEmbedDeathAttackDtViral;

    /** 投射物的嵌入目标后死亡触发攻击的异常触发几率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_embeddeathattack_procchance_state")
    private Short projectileEmbedDeathAttackProcChanceState;

    /** 投射物的嵌入目标后死亡触发攻击的异常触发几率。 */
    @Column(name = "projectile_embeddeathattack_procchance", columnDefinition = "NUMERIC")
    private BigDecimal projectileEmbedDeathAttackProcChance;

    /** 投射物的爆炸攻击的腐蚀伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_explosiveattack_dt_corrosive_state")
    private Short projectileExplosiveAttackDtCorrosiveState;

    /** 投射物的爆炸攻击的腐蚀伤害。 */
    @Column(name = "projectile_explosiveattack_dt_corrosive", columnDefinition = "NUMERIC")
    private BigDecimal projectileExplosiveAttackDtCorrosive;

    /** 投射物的爆炸攻击的电击伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_explosiveattack_dt_electricity_state")
    private Short projectileExplosiveAttackDtElectricityState;

    /** 投射物的爆炸攻击的电击伤害。 */
    @Column(name = "projectile_explosiveattack_dt_electricity", columnDefinition = "NUMERIC")
    private BigDecimal projectileExplosiveAttackDtElectricity;

    /** 投射物的爆炸攻击的爆炸伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_explosiveattack_dt_explosion_state")
    private Short projectileExplosiveAttackDtExplosionState;

    /** 投射物的爆炸攻击的爆炸伤害。 */
    @Column(name = "projectile_explosiveattack_dt_explosion", columnDefinition = "NUMERIC")
    private BigDecimal projectileExplosiveAttackDtExplosion;

    /** 投射物的爆炸攻击的火焰伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_explosiveattack_dt_fire_state")
    private Short projectileExplosiveAttackDtFireState;

    /** 投射物的爆炸攻击的火焰伤害。 */
    @Column(name = "projectile_explosiveattack_dt_fire", columnDefinition = "NUMERIC")
    private BigDecimal projectileExplosiveAttackDtFire;

    /** 投射物的爆炸攻击的冰冻伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_explosiveattack_dt_freeze_state")
    private Short projectileExplosiveAttackDtFreezeState;

    /** 投射物的爆炸攻击的冰冻伤害。 */
    @Column(name = "projectile_explosiveattack_dt_freeze", columnDefinition = "NUMERIC")
    private BigDecimal projectileExplosiveAttackDtFreeze;

    /** 投射物的爆炸攻击的毒气伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_explosiveattack_dt_gas_state")
    private Short projectileExplosiveAttackDtGasState;

    /** 投射物的爆炸攻击的毒气伤害。 */
    @Column(name = "projectile_explosiveattack_dt_gas", columnDefinition = "NUMERIC")
    private BigDecimal projectileExplosiveAttackDtGas;

    /** 投射物的爆炸攻击的冲击伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_explosiveattack_dt_impact_state")
    private Short projectileExplosiveAttackDtImpactState;

    /** 投射物的爆炸攻击的冲击伤害。 */
    @Column(name = "projectile_explosiveattack_dt_impact", columnDefinition = "NUMERIC")
    private BigDecimal projectileExplosiveAttackDtImpact;

    /** 投射物的爆炸攻击的毒素伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_explosiveattack_dt_poison_state")
    private Short projectileExplosiveAttackDtPoisonState;

    /** 投射物的爆炸攻击的毒素伤害。 */
    @Column(name = "projectile_explosiveattack_dt_poison", columnDefinition = "NUMERIC")
    private BigDecimal projectileExplosiveAttackDtPoison;

    /** 投射物的爆炸攻击的穿刺伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_explosiveattack_dt_puncture_state")
    private Short projectileExplosiveAttackDtPunctureState;

    /** 投射物的爆炸攻击的穿刺伤害。 */
    @Column(name = "projectile_explosiveattack_dt_puncture", columnDefinition = "NUMERIC")
    private BigDecimal projectileExplosiveAttackDtPuncture;

    /** 投射物的爆炸攻击的辐射伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_explosiveattack_dt_radiation_state")
    private Short projectileExplosiveAttackDtRadiationState;

    /** 投射物的爆炸攻击的辐射伤害。 */
    @Column(name = "projectile_explosiveattack_dt_radiation", columnDefinition = "NUMERIC")
    private BigDecimal projectileExplosiveAttackDtRadiation;

    /** 投射物的爆炸攻击的切割伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_explosiveattack_dt_slash_state")
    private Short projectileExplosiveAttackDtSlashState;

    /** 投射物的爆炸攻击的切割伤害。 */
    @Column(name = "projectile_explosiveattack_dt_slash", columnDefinition = "NUMERIC")
    private BigDecimal projectileExplosiveAttackDtSlash;

    /** 投射物的爆炸攻击的病毒伤害是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_explosiveattack_dt_viral_state")
    private Short projectileExplosiveAttackDtViralState;

    /** 投射物的爆炸攻击的病毒伤害。 */
    @Column(name = "projectile_explosiveattack_dt_viral", columnDefinition = "NUMERIC")
    private BigDecimal projectileExplosiveAttackDtViral;

    /** 投射物的爆炸攻击的异常触发几率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "projectile_explosiveattack_procchance_state")
    private Short projectileExplosiveAttackProcChanceState;

    /** 投射物的爆炸攻击的异常触发几率。 */
    @Column(name = "projectile_explosiveattack_procchance", columnDefinition = "NUMERIC")
    private BigDecimal projectileExplosiveAttackProcChance;

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
    private OfficialWeaponRoot parent;
}
