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

/** 源 DRONES 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_drones_root")
public class OfficialDronesRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 货架容量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "bincapacity_state")
    private Short binCapacityState;

    /** 货架容量。 */
    @Column(name = "bincapacity", columnDefinition = "NUMERIC")
    private BigDecimal binCapacity;

    /** 货架数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "bincount_state")
    private Short binCountState;

    /** 货架数量。 */
    @Column(name = "bincount", columnDefinition = "NUMERIC")
    private BigDecimal binCount;

    /** 容量倍率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "capacitymultiplier_state")
    private Short capacityMultiplierState;

    /** 容量倍率列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "capacitymultipliers_state")
    private Short capacityMultipliersState;

    /** 容量倍率列表的普通稀有度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "capacitymultipliers_common_state")
    private Short capacityMultipliersCommonState;

    /** 容量倍率列表的普通稀有度。 */
    @Column(name = "capacitymultipliers_common", columnDefinition = "NUMERIC")
    private BigDecimal capacityMultipliersCommon;

    /** 容量倍率列表的传奇稀有度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "capacitymultipliers_legendary_state")
    private Short capacityMultipliersLegendaryState;

    /** 容量倍率列表的传奇稀有度。 */
    @Column(name = "capacitymultipliers_legendary", columnDefinition = "NUMERIC")
    private BigDecimal capacityMultipliersLegendary;

    /** 容量倍率列表的稀有稀有度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "capacitymultipliers_rare_state")
    private Short capacityMultipliersRareState;

    /** 容量倍率列表的稀有稀有度。 */
    @Column(name = "capacitymultipliers_rare", columnDefinition = "NUMERIC")
    private BigDecimal capacityMultipliersRare;

    /** 容量倍率列表的罕见稀有度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "capacitymultipliers_uncommon_state")
    private Short capacityMultipliersUncommonState;

    /** 容量倍率列表的罕见稀有度。 */
    @Column(name = "capacitymultipliers_uncommon", columnDefinition = "NUMERIC")
    private BigDecimal capacityMultipliersUncommon;

    /** 是否为资料库隐藏条目是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "codexsecret_state")
    private Short codexSecretState;

    /** 是否为资料库隐藏条目。 */
    @Column(name = "codexsecret")
    private Boolean codexSecret;

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

    /** 耐久度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "durability_state")
    private Short durabilityState;

    /** 耐久度。 */
    @Column(name = "durability", columnDefinition = "NUMERIC")
    private BigDecimal durability;

    /** 补充速率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "fillrate_state")
    private Short fillRateState;

    /** 补充速率。 */
    @Column(name = "fillrate", columnDefinition = "NUMERIC")
    private BigDecimal fillRate;

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

    /** 概率列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "probabilities_state")
    private Short probabilitiesState;

    /** 概率列表的普通稀有度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "probabilities_common_state")
    private Short probabilitiesCommonState;

    /** 概率列表的普通稀有度。 */
    @Column(name = "probabilities_common", columnDefinition = "NUMERIC")
    private BigDecimal probabilitiesCommon;

    /** 概率列表的传奇稀有度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "probabilities_legendary_state")
    private Short probabilitiesLegendaryState;

    /** 概率列表的传奇稀有度。 */
    @Column(name = "probabilities_legendary", columnDefinition = "NUMERIC")
    private BigDecimal probabilitiesLegendary;

    /** 概率列表的稀有稀有度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "probabilities_rare_state")
    private Short probabilitiesRareState;

    /** 概率列表的稀有稀有度。 */
    @Column(name = "probabilities_rare", columnDefinition = "NUMERIC")
    private BigDecimal probabilitiesRare;

    /** 概率列表的罕见稀有度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "probabilities_uncommon_state")
    private Short probabilitiesUncommonState;

    /** 概率列表的罕见稀有度。 */
    @Column(name = "probabilities_uncommon", columnDefinition = "NUMERIC")
    private BigDecimal probabilitiesUncommon;

    /** 记录键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "recordkey_state")
    private Short recordKeyState;

    /** 记录键。 */
    @Column(name = "recordkey", columnDefinition = "TEXT")
    private String recordKey;

    /** 修复速率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "repairrate_state")
    private Short repairRateState;

    /** 修复速率。 */
    @Column(name = "repairrate", columnDefinition = "NUMERIC")
    private BigDecimal repairRate;

    /** 专长列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "specialities_state")
    private Short specialitiesState;

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialDronesCapacityMultiplierItems> officialDronesCapacityMultiplierItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialDronesSpecialitiesItems> officialDronesSpecialitiesItemsRows = new ArrayList<>();
}
