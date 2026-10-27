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
import java.math.BigDecimal;

/** 源 RELIC locations/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_relic_locations_items")
public class OfficialRelicLocationsItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 启动概率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "activationprobability_state")
    private Short activationProbabilityState;

    /** 启动概率。 */
    @Column(name = "activationprobability", columnDefinition = "NUMERIC")
    private BigDecimal activationProbability;

    /** 分类是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "category_state")
    private Short categoryState;

    /** 分类。 */
    @Column(name = "category", columnDefinition = "TEXT")
    private String category;

    /** 几率解析状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chanceresolution_state")
    private Short chanceResolutionState;

    /** 几率解析状态。 */
    @Column(name = "chanceresolution", columnDefinition = "TEXT")
    private String chanceResolution;

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

    /** 掉落几率百分比是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "dropchancepercent_state")
    private Short dropChancePercentState;

    /** 掉落几率百分比。 */
    @Column(name = "dropchancepercent", columnDefinition = "NUMERIC")
    private BigDecimal dropChancePercent;

    /** 分组列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "groups_state")
    private Short groupsState;

    /** 概率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "probability_state")
    private Short probabilityState;

    /** 概率。 */
    @Column(name = "probability", columnDefinition = "NUMERIC")
    private BigDecimal probability;

    /** 稀有度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rarity_state")
    private Short rarityState;

    /** 稀有度。 */
    @Column(name = "rarity", columnDefinition = "TEXT")
    private String rarity;

    /** 来源信息是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "source_state")
    private Short sourceState;

    /** 来源信息。 */
    @Column(name = "source", columnDefinition = "TEXT")
    private String source;

    /** 来源掉落几率百分比是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "sourcedropchancepercent_state")
    private Short sourceDropChancePercentState;

    /** 来源掉落几率百分比。 */
    @Column(name = "sourcedropchancepercent", columnDefinition = "NUMERIC")
    private BigDecimal sourceDropChancePercent;

    /** 来源中文说明是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "sourcezh_state")
    private Short sourceZhState;

    /** 来源中文说明。 */
    @Column(name = "sourcezh", columnDefinition = "TEXT")
    private String sourceZh;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialRelicRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRelicLocationsItemsGroupsItems> officialRelicLocationsItemsGroupsItemsRows = new ArrayList<>();
}
