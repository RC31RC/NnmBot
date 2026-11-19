package com.nnm.bot.warframe.persistent.official.pojo.entity.rewards;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialChildRow;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

/** 源 RELIC states/entries/itemReferences/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_relic_states_entries_itemreferences_items")
public class OfficialRelicStatesEntriesItemReferencesItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 字段是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "field_state")
    private Short fieldState;

    /** 字段。 */
    @Column(name = "field", columnDefinition = "TEXT")
    private String field;

    /** 图标状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "iconstatus_state")
    private Short iconStatusState;

    /** 图标状态。 */
    @Column(name = "iconstatus", columnDefinition = "TEXT")
    private String iconStatus;

    /** 概率单位是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "probabilityunit_state")
    private Short probabilityUnitState;

    /** 概率单位。 */
    @Column(name = "probabilityunit", columnDefinition = "TEXT")
    private String probabilityUnit;

    /** 引用类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "referencekind_state")
    private Short referenceKindState;

    /** 引用类型。 */
    @Column(name = "referencekind", columnDefinition = "TEXT")
    private String referenceKind;

    /** 解析状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "resolution_state")
    private Short resolutionState;

    /** 解析状态。 */
    @Column(name = "resolution", columnDefinition = "TEXT")
    private String resolution;

    /** 轮次是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rotations_state")
    private Short rotationsState;

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialRelicStatesEntries parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRelicStatesEntriesItemReferencesItemsRotationsItems> officialRelicStatesEntriesItemReferencesItemsRotationsItemsRows = new ArrayList<>();
}
