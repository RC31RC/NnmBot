package com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate;

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

/** 源 SYNDICATE favours/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_syndicate_favours_items")
public class OfficialSyndicateFavoursItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 现金消耗是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "creditscost_state")
    private Short creditsCostState;

    /** 现金消耗。 */
    @Column(name = "creditscost", columnDefinition = "NUMERIC")
    private BigDecimal creditsCost;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    /** 晋级奖励是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rankupreward_state")
    private Short rankUpRewardState;

    /** 晋级奖励。 */
    @Column(name = "rankupreward")
    private Boolean rankUpReward;

    /** 所需等级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "requiredlevel_state")
    private Short requiredLevelState;

    /** 所需等级。 */
    @Column(name = "requiredlevel", columnDefinition = "NUMERIC")
    private BigDecimal requiredLevel;

    /** 声望消耗是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "standingcost_state")
    private Short standingCostState;

    /** 声望消耗。 */
    @Column(name = "standingcost", columnDefinition = "NUMERIC")
    private BigDecimal standingCost;

    /** 商店物品是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "storeitem_state")
    private Short storeItemState;

    /** 商店物品。 */
    @Column(name = "storeitem", columnDefinition = "TEXT")
    private String storeItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialSyndicateRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialSyndicateFavoursItemsItemReferencesItems> officialSyndicateFavoursItemsItemReferencesItemsRows = new ArrayList<>();
}
