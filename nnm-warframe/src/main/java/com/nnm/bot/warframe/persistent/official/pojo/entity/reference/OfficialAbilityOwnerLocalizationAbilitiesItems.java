package com.nnm.bot.warframe.persistent.official.pojo.entity.reference;

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

/** 源 ABILITY_OWNER_LOCALIZATION abilities/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_ability_owner_localization_abilities_items")
public class OfficialAbilityOwnerLocalizationAbilitiesItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 技能名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "abilityname_state")
    private Short abilityNameState;

    /** 技能名称。 */
    @Column(name = "abilityname", columnDefinition = "TEXT")
    private String abilityName;

    /** 技能唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "abilityuniquename_state")
    private Short abilityUniqueNameState;

    /** 技能唯一标识。 */
    @Column(name = "abilityuniquename", columnDefinition = "TEXT")
    private String abilityUniqueName;

    /** 描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "description_value_state")
    private Short descriptionState;

    /** 描述。 */
    @Column(name = "description_value", columnDefinition = "TEXT")
    private String description;

    /** 技能中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "zhabilityname_state")
    private Short zhAbilityNameState;

    /** 技能中文名称。 */
    @Column(name = "zhabilityname", columnDefinition = "TEXT")
    private String zhAbilityName;

    /** 中文描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "zhdescription_state")
    private Short zhDescriptionState;

    /** 中文描述。 */
    @Column(name = "zhdescription", columnDefinition = "TEXT")
    private String zhDescription;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialAbilityOwnerLocalizationRoot parent;
}
