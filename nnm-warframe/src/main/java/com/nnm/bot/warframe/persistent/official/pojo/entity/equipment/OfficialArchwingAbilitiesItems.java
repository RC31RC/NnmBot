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

/** 源 ARCHWING abilities/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_archwing_abilities_items")
public class OfficialArchwingAbilitiesItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 技能图标地址是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "abilityiconurl_state")
    private Short abilityIconUrlState;

    /** 技能图标地址。 */
    @Column(name = "abilityiconurl", columnDefinition = "TEXT")
    private String abilityIconUrl;

    /** 技能名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "abilityname_state")
    private Short abilityNameState;

    /** 技能名称。 */
    @Column(name = "abilityname", columnDefinition = "TEXT")
    private String abilityName;

    /** 技能中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "abilitynamezh_state")
    private Short abilityNameZhState;

    /** 技能中文名称。 */
    @Column(name = "abilitynamezh", columnDefinition = "TEXT")
    private String abilityNameZh;

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

    /** 持续能量消耗是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "energyconsumptionovertime_state")
    private Short energyConsumptionOverTimeState;

    /** 持续能量消耗。 */
    @Column(name = "energyconsumptionovertime", columnDefinition = "NUMERIC")
    private BigDecimal energyConsumptionOverTime;

    /** 启动所需能量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "energyrequiredtoactivate_state")
    private Short energyRequiredToActivateState;

    /** 启动所需能量。 */
    @Column(name = "energyrequiredtoactivate", columnDefinition = "NUMERIC")
    private BigDecimal energyRequiredToActivate;

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

    /** 记录键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "recordkey_state")
    private Short recordKeyState;

    /** 记录键。 */
    @Column(name = "recordkey", columnDefinition = "TEXT")
    private String recordKey;

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialArchwingRoot parent;
}
