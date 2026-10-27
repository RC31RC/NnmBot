package com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades;

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

/** 源 RIVEN_ATTRIBUTE 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_riven_attribute_root")
public class OfficialRivenAttributeRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 定义解析状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "definitionresolution_state")
    private Short definitionResolutionState;

    /** 定义解析状态。 */
    @Column(name = "definitionresolution", columnDefinition = "TEXT")
    private String definitionResolution;

    /** 效果是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "effect_state")
    private Short effectState;

    /** 效果。 */
    @Column(name = "effect", columnDefinition = "TEXT")
    private String effect;

    /** 中文效果描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "effectzh_state")
    private Short effectZhState;

    /** 中文效果描述。 */
    @Column(name = "effectzh", columnDefinition = "TEXT")
    private String effectZh;

    /** 专属兼容组列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "exclusiveto_state")
    private Short exclusiveToState;

    /** 游戏引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "gameref_state")
    private Short gameRefState;

    /** 游戏引用。 */
    @Column(name = "gameref", columnDefinition = "TEXT")
    private String gameRef;

    /** 组是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "group_value_state")
    private Short groupState;

    /** 组。 */
    @Column(name = "group_value", columnDefinition = "TEXT")
    private String group;

    /** 图标说明是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "iconreason_state")
    private Short iconReasonState;

    /** 图标说明。 */
    @Column(name = "iconreason", columnDefinition = "TEXT")
    private String iconReason;

    /** 图标状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "iconstatus_state")
    private Short iconStatusState;

    /** 图标状态。 */
    @Column(name = "iconstatus", columnDefinition = "TEXT")
    private String iconStatus;

    /** 标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "id_value_state")
    private Short idState;

    /** 标识。 */
    @Column(name = "id_value", columnDefinition = "TEXT")
    private String idValue;

    /** 标识列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "ids_state")
    private Short idsState;

    /** 标识列表。 */
    @Column(name = "ids", columnDefinition = "NUMERIC")
    private BigDecimal ids;

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

    /** 是否仅为负面属性是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "negative_only_state")
    private Short negativeOnlyState;

    /** 是否仅为负面属性。 */
    @Column(name = "negative_only")
    private Boolean negativeOnly;

    /** 是否仅为负面属性是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "negativeonly_state")
    private Short negativeOnlyState1f16bbf25f;

    /** 是否仅为负面属性。 */
    @Column(name = "negativeonly")
    private Boolean negativeOnlyc24601d326;

    /** 官方强化定义条目列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "officialentries_state")
    private Short officialEntriesState;

    /** 正向数值是否代表负面效果是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "positive_is_negative_state")
    private Short positiveIsNegativeState;

    /** 正向数值是否代表负面效果。 */
    @Column(name = "positive_is_negative")
    private Boolean positiveIsNegative;

    /** 正向数值是否代表负面效果是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "positiveisnegative_state")
    private Short positiveIsNegativeState971bce76c4;

    /** 正向数值是否代表负面效果。 */
    @Column(name = "positiveisnegative")
    private Boolean positiveIsNegative15b9913200;

    /** 是否仅为正面属性是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "positiveonly_state")
    private Short positiveOnlyState;

    /** 是否仅为正面属性。 */
    @Column(name = "positiveonly")
    private Boolean positiveOnly;

    /** 前缀是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "prefix_state")
    private Short prefixState;

    /** 前缀。 */
    @Column(name = "prefix", columnDefinition = "TEXT")
    private String prefix;

    /** 记录键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "recordkey_state")
    private Short recordKeyState;

    /** 记录键。 */
    @Column(name = "recordkey", columnDefinition = "TEXT")
    private String recordKey;

    /** 是否仅用于搜索是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "search_only_state")
    private Short searchOnlyState;

    /** 是否仅用于搜索。 */
    @Column(name = "search_only")
    private Boolean searchOnly;

    /** 网址标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "slug_state")
    private Short slugState;

    /** 网址标识。 */
    @Column(name = "slug", columnDefinition = "TEXT")
    private String slug;

    /** 后缀是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "suffix_state")
    private Short suffixState;

    /** 后缀。 */
    @Column(name = "suffix", columnDefinition = "TEXT")
    private String suffix;

    /** 中文文本模板是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "templateszh_state")
    private Short templatesZhState;

    /** 单位是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "unit_state")
    private Short unitState;

    /** 单位。 */
    @Column(name = "unit", columnDefinition = "TEXT")
    private String unit;

    /** 单位列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "units_state")
    private Short unitsState;

    /** 单位列表。 */
    @Column(name = "units", columnDefinition = "TEXT")
    private String units;

    /** 网址标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "url_name_state")
    private Short urlNameState;

    /** 网址标识。 */
    @Column(name = "url_name", columnDefinition = "TEXT")
    private String urlName;

    /** 数值含义是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "valuesemantics_state")
    private Short valueSemanticsState;

    /** 数值含义。 */
    @Column(name = "valuesemantics", columnDefinition = "TEXT")
    private String valueSemantics;

    /** 中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "zhname_state")
    private Short zhNameState;

    /** 中文名称。 */
    @Column(name = "zhname", columnDefinition = "TEXT")
    private String zhName;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRivenAttributeExclusiveToItems> officialRivenAttributeExclusiveToItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRivenAttributeOfficialEntriesItems> officialRivenAttributeOfficialEntriesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRivenAttributeTemplatesZhItems> officialRivenAttributeTemplatesZhItemsRows = new ArrayList<>();
}
