package com.nnm.bot.warframe.persistent.official.pojo.entity.world;

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

/** 源 ENEMY 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_enemy_root")
public class OfficialEnemyRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 护甲是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "armor_state")
    private Short armorState;

    /** 护甲。 */
    @Column(name = "armor", columnDefinition = "NUMERIC")
    private BigDecimal armor;

    /** 角色类型列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "avatartypes_state")
    private Short avatarTypesState;

    /** 基础等级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "baselevel_state")
    private Short baseLevelState;

    /** 基础等级。 */
    @Column(name = "baselevel", columnDefinition = "NUMERIC")
    private BigDecimal baseLevel;

    /** 行为配置列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "behaviours_state")
    private Short behavioursState;

    /** 资料库解锁所需扫描次数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "codexscansrequired_state")
    private Short codexScansRequiredState;

    /** 资料库解锁所需扫描次数。 */
    @Column(name = "codexscansrequired", columnDefinition = "NUMERIC")
    private BigDecimal codexScansRequired;

    /** 是否为资料库隐藏条目是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "codexsecret_state")
    private Short codexSecretState;

    /** 是否为资料库隐藏条目。 */
    @Column(name = "codexsecret")
    private Boolean codexSecret;

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

    /** 伤害衰减是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "damageatten_state")
    private Short damageAttenState;

    /** 伤害衰减。 */
    @Column(name = "damageatten", columnDefinition = "NUMERIC")
    private BigDecimal damageAtten;

    /** 伤害控制配置是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "damagecontroller_state")
    private Short damageControllerState;

    /** 伤害控制配置。 */
    @Column(name = "damagecontroller", columnDefinition = "TEXT")
    private String damageController;

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

    /** 掉落表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "droptable_state")
    private Short droptableState;

    /** 掉落表。 */
    @Column(name = "droptable", columnDefinition = "TEXT")
    private String droptable;

    /** 是否排除在幻影装置之外是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "excludefromsimulacrum_state")
    private Short excludeFromSimulacrumState;

    /** 是否排除在幻影装置之外。 */
    @Column(name = "excludefromsimulacrum")
    private Boolean excludeFromSimulacrum;

    /** 阵营是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "faction_state")
    private Short factionState;

    /** 阵营。 */
    @Column(name = "faction", columnDefinition = "TEXT")
    private String faction;

    /** 阵营抗性关键词是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "factionresistancekeyword_state")
    private Short factionResistanceKeywordState;

    /** 阵营抗性关键词。 */
    @Column(name = "factionresistancekeyword", columnDefinition = "TEXT")
    private String factionResistanceKeyword;

    /** 生命值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "health_state")
    private Short healthState;

    /** 生命值。 */
    @Column(name = "health", columnDefinition = "NUMERIC")
    private BigDecimal health;

    /** 命中判定部位列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "hitproxies_state")
    private Short hitProxiesState;

    /** 图标是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "icon_state")
    private Short iconState;

    /** 图标。 */
    @Column(name = "icon", columnDefinition = "TEXT")
    private String icon;

    /** 图标资源是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "iconasset_state")
    private Short iconAssetState;

    /** 图标资源。 */
    @Column(name = "iconasset", columnDefinition = "TEXT")
    private String iconAsset;

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

    /** 是否为非必要物品是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "isfrivolous_state")
    private Short isFrivolousState;

    /** 是否为非必要物品。 */
    @Column(name = "isfrivolous")
    private Boolean isFrivolous;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    /** 物品列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "items_state")
    private Short itemsState;

    /** 击杀经验奖励是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "killxpreward_state")
    private Short killXPRewardState;

    /** 击杀经验奖励。 */
    @Column(name = "killxpreward", columnDefinition = "NUMERIC")
    private BigDecimal killXPReward;

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

    /** 中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "namezh_state")
    private Short nameZhState;

    /** 中文名称。 */
    @Column(name = "namezh", columnDefinition = "TEXT")
    private String nameZh;

    /** 奖池键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "poolkey_state")
    private Short poolKeyState;

    /** 奖池键。 */
    @Column(name = "poolkey", columnDefinition = "TEXT")
    private String poolKey;

    /** 护盾是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "shield_state")
    private Short shieldState;

    /** 护盾。 */
    @Column(name = "shield", columnDefinition = "NUMERIC")
    private BigDecimal shield;

    /** 阶段列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "stages_state")
    private Short stagesState;

    /** 未处理的异常状态类型列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "unhandledproctypes_state")
    private Short unhandledProcTypesState;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialEnemyAvatarTypesItems> officialEnemyAvatarTypesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialEnemyBehavioursItems> officialEnemyBehavioursItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialEnemyHitProxiesItems> officialEnemyHitProxiesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialEnemyItemReferencesItems> officialEnemyItemReferencesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialEnemyItemsItems> officialEnemyItemsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialEnemyStagesItems> officialEnemyStagesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialEnemyUnhandledProcTypesItems> officialEnemyUnhandledProcTypesItemsRows = new ArrayList<>();
}
