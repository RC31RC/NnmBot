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

/** 源 ACHIEVEMENT 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_achievement_root")
public class OfficialAchievementRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 子项是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "children_state")
    private Short childrenState;

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

    /** 隐藏是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "hidden_state")
    private Short hiddenState;

    /** 隐藏。 */
    @Column(name = "hidden")
    private Boolean hidden;

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

    /** 进度提示频率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "progressindicatorfreq_state")
    private Short progressIndicatorFreqState;

    /** 进度提示频率。 */
    @Column(name = "progressindicatorfreq", columnDefinition = "NUMERIC")
    private BigDecimal progressIndicatorFreq;

    /** 需求数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "requiredcount_state")
    private Short requiredCountState;

    /** 需求数量。 */
    @Column(name = "requiredcount", columnDefinition = "NUMERIC")
    private BigDecimal requiredCount;

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialAchievementChildrenItems> officialAchievementChildrenItemsRows = new ArrayList<>();
}
