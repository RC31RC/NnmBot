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

/** 源 VENDOR items/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_vendor_items_items")
public class OfficialVendorItemsItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 是否始终出售是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "alwaysoffered_state")
    private Short alwaysOfferedState;

    /** 是否始终出售。 */
    @Column(name = "alwaysoffered")
    private Boolean alwaysOffered;

    /** 槽位是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "bin_state")
    private Short binState;

    /** 槽位。 */
    @Column(name = "bin", columnDefinition = "NUMERIC")
    private BigDecimal bin;

    /** 现金是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "credits_state")
    private Short creditsState;

    /** 现金。 */
    @Column(name = "credits", columnDefinition = "NUMERIC")
    private BigDecimal credits;

    /** 现金的最大值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "credits_maxvalue_state")
    private Short creditsMaxValueState;

    /** 现金的最大值。 */
    @Column(name = "credits_maxvalue", columnDefinition = "NUMERIC")
    private BigDecimal creditsMaxValue;

    /** 现金的最小值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "credits_minvalue_state")
    private Short creditsMinValueState;

    /** 现金的最小值。 */
    @Column(name = "credits_minvalue", columnDefinition = "NUMERIC")
    private BigDecimal creditsMinValue;

    /** 现金的步长是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "credits_step_state")
    private Short creditsStepState;

    /** 现金的步长。 */
    @Column(name = "credits_step", columnDefinition = "NUMERIC")
    private BigDecimal creditsStep;

    /** 重复商品数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "duplicates_state")
    private Short duplicatesState;

    /** 重复商品数量。 */
    @Column(name = "duplicates", columnDefinition = "NUMERIC")
    private BigDecimal duplicates;

    /** 持续小时数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "durationhours_state")
    private Short durationHoursState;

    /** 持续小时数。 */
    @Column(name = "durationhours", columnDefinition = "NUMERIC")
    private BigDecimal durationHours;

    /** 持续小时数的最大值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "durationhours_maxvalue_state")
    private Short durationHoursMaxValueState;

    /** 持续小时数的最大值。 */
    @Column(name = "durationhours_maxvalue", columnDefinition = "NUMERIC")
    private BigDecimal durationHoursMaxValue;

    /** 持续小时数的最小值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "durationhours_minvalue_state")
    private Short durationHoursMinValueState;

    /** 持续小时数的最小值。 */
    @Column(name = "durationhours_minvalue", columnDefinition = "NUMERIC")
    private BigDecimal durationHoursMinValue;

    /** 专精经验消耗的消耗是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "focusxpcost_cost_state")
    private Short focusXpCostCostState;

    /** 专精经验消耗的消耗。 */
    @Column(name = "focusxpcost_cost", columnDefinition = "NUMERIC")
    private BigDecimal focusXpCostCost;

    /** 专精经验消耗的极性是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "focusxpcost_polarity_state")
    private Short focusXpCostPolarityState;

    /** 专精经验消耗的极性。 */
    @Column(name = "focusxpcost_polarity", columnDefinition = "TEXT")
    private String focusXpCostPolarity;

    /** 物品价格列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemprices_state")
    private Short itemPricesState;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    /** 随机定价项数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "numrandomitemprices_state")
    private Short numRandomItemPricesState;

    /** 随机定价项数量。 */
    @Column(name = "numrandomitemprices", columnDefinition = "NUMERIC")
    private BigDecimal numRandomItemPrices;

    /** 铂金数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "platinum_state")
    private Short platinumState;

    /** 铂金数量。 */
    @Column(name = "platinum", columnDefinition = "NUMERIC")
    private BigDecimal platinum;

    /** 铂金数量的最大值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "platinum_maxvalue_state")
    private Short platinumMaxValueState;

    /** 铂金数量的最大值。 */
    @Column(name = "platinum_maxvalue", columnDefinition = "NUMERIC")
    private BigDecimal platinumMaxValue;

    /** 铂金数量的最小值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "platinum_minvalue_state")
    private Short platinumMinValueState;

    /** 铂金数量的最小值。 */
    @Column(name = "platinum_minvalue", columnDefinition = "NUMERIC")
    private BigDecimal platinumMinValue;

    /** 概率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "probability_state")
    private Short probabilityState;

    /** 概率。 */
    @Column(name = "probability", columnDefinition = "NUMERIC")
    private BigDecimal probability;

    /** 购买数量上限是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "purchaselimit_state")
    private Short purchaseLimitState;

    /** 购买数量上限。 */
    @Column(name = "purchaselimit", columnDefinition = "NUMERIC")
    private BigDecimal purchaseLimit;

    /** 数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quantity_state")
    private Short quantityState;

    /** 数量。 */
    @Column(name = "quantity", columnDefinition = "NUMERIC")
    private BigDecimal quantity;

    /** 是否每周轮换是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rotatedweekly_state")
    private Short rotatedWeeklyState;

    /** 是否每周轮换。 */
    @Column(name = "rotatedweekly")
    private Boolean rotatedWeekly;

    /** 商店物品是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "storeitem_state")
    private Short storeItemState;

    /** 商店物品。 */
    @Column(name = "storeitem", columnDefinition = "TEXT")
    private String storeItem;

    /** 集团的每级负声望等级的增加量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "syndicate_increasepernegativerank_state")
    private Short syndicateIncreasePerNegativeRankState;

    /** 集团的每级负声望等级的增加量。 */
    @Column(name = "syndicate_increasepernegativerank", columnDefinition = "NUMERIC")
    private BigDecimal syndicateIncreasePerNegativeRank;

    /** 集团的最低等级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "syndicate_minrank_state")
    private Short syndicateMinRankState;

    /** 集团的最低等级。 */
    @Column(name = "syndicate_minrank", columnDefinition = "NUMERIC")
    private BigDecimal syndicateMinRank;

    /** 集团的每级正声望等级的减少量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "syndicate_reductionperpositiverank_state")
    private Short syndicateReductionPerPositiveRankState;

    /** 集团的每级正声望等级的减少量。 */
    @Column(name = "syndicate_reductionperpositiverank", columnDefinition = "NUMERIC")
    private BigDecimal syndicateReductionPerPositiveRank;

    /** 集团的声望消耗是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "syndicate_standingcost_state")
    private Short syndicateStandingCostState;

    /** 集团的声望消耗。 */
    @Column(name = "syndicate_standingcost", columnDefinition = "NUMERIC")
    private BigDecimal syndicateStandingCost;

    /** 集团的标签是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "syndicate_tag_state")
    private Short syndicateTagState;

    /** 集团的标签。 */
    @Column(name = "syndicate_tag", columnDefinition = "TEXT")
    private String syndicateTag;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialVendorRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialVendorItemsItemsItemPricesItems> officialVendorItemsItemsItemPricesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialVendorItemsItemsItemReferencesItems> officialVendorItemsItemsItemReferencesItemsRows = new ArrayList<>();
}
