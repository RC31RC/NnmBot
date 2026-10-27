package com.nnm.bot.warframe.persistent.official.pojo.entity.world;

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
import java.math.BigDecimal;

/** 源 ENEMY stages/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_enemy_stages_items")
public class OfficialEnemyStagesItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 启动概率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "activationprobability_state")
    private Short activationProbabilityState;

    /** 启动概率。 */
    @Column(name = "activationprobability", columnDefinition = "NUMERIC")
    private BigDecimal activationProbability;

    /** 抽取次数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "drawcount_state")
    private Short drawCountState;

    /** 抽取次数。 */
    @Column(name = "drawcount", columnDefinition = "NUMERIC")
    private BigDecimal drawCount;

    /** 抽取方式是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "drawmode_state")
    private Short drawModeState;

    /** 抽取方式。 */
    @Column(name = "drawmode", columnDefinition = "TEXT")
    private String drawMode;

    /** 物品列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "items_state")
    private Short itemsState;

    /** 概率单位是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "probabilityunit_state")
    private Short probabilityUnitState;

    /** 概率单位。 */
    @Column(name = "probabilityunit", columnDefinition = "TEXT")
    private String probabilityUnit;

    /** 类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "type_state")
    private Short typeState;

    /** 类型。 */
    @Column(name = "type", columnDefinition = "TEXT")
    private String type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialEnemyRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialEnemyStagesItemsItemsItems> officialEnemyStagesItemsItemsItemsRows = new ArrayList<>();
}
