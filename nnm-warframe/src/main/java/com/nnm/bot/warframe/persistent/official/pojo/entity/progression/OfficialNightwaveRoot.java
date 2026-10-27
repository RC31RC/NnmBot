package com.nnm.bot.warframe.persistent.official.pojo.entity.progression;

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

/** 源 NIGHTWAVE 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_nightwave_root")
public class OfficialNightwaveRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 所属势力标签是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "affiliationtag_state")
    private Short affiliationTagState;

    /** 所属势力标签。 */
    @Column(name = "affiliationtag", columnDefinition = "TEXT")
    private String affiliationTag;

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

    /** 物品数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemcount_state")
    private Short itemCountState;

    /** 物品数量。 */
    @Column(name = "itemcount", columnDefinition = "NUMERIC")
    private BigDecimal itemCount;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

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

    /** 需求是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "required_state")
    private Short requiredState;

    /** 需求。 */
    @Column(name = "required", columnDefinition = "NUMERIC")
    private BigDecimal required;

    /** 声望是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "standing_state")
    private Short standingState;

    /** 声望。 */
    @Column(name = "standing", columnDefinition = "NUMERIC")
    private BigDecimal standing;

    /** 提示说明是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "tip_state")
    private Short tipState;

    /** 提示说明。 */
    @Column(name = "tip", columnDefinition = "TEXT")
    private String tip;

    /** 英文提示说明是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "tipen_state")
    private Short tipEnState;

    /** 英文提示说明。 */
    @Column(name = "tipen", columnDefinition = "TEXT")
    private String tipEn;

    /** 提示图标是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "tipicon_state")
    private Short tipIconState;

    /** 提示图标。 */
    @Column(name = "tipicon", columnDefinition = "TEXT")
    private String tipIcon;

    /** 中文提示说明是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "tipzh_state")
    private Short tipZhState;

    /** 中文提示说明。 */
    @Column(name = "tipzh", columnDefinition = "TEXT")
    private String tipZh;

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialNightwaveItemReferencesItems> officialNightwaveItemReferencesItemsRows = new ArrayList<>();
}
