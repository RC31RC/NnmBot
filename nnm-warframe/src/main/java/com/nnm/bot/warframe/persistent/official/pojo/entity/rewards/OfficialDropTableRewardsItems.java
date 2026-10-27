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

/** 源 DROP_TABLE rewards/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_drop_table_rewards_items")
public class OfficialDropTableRewardsItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 几率解析状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "chanceresolution_state")
    private Short chanceResolutionState;

    /** 几率解析状态。 */
    @Column(name = "chanceresolution", columnDefinition = "TEXT")
    private String chanceResolution;

    /** 掉落几率百分比是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "dropchancepercent_state")
    private Short dropChancePercentState;

    /** 掉落几率百分比。 */
    @Column(name = "dropchancepercent", columnDefinition = "NUMERIC")
    private BigDecimal dropChancePercent;

    /** 物品的候选项列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_candidates_state")
    private Short itemCandidatesState;

    /** 物品的数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_count_state")
    private Short itemCountState;

    /** 物品的数量。 */
    @Column(name = "item_count", columnDefinition = "NUMERIC")
    private BigDecimal itemCount;

    /** 物品的货币是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_currency_state")
    private Short itemCurrencyState;

    /** 物品的货币。 */
    @Column(name = "item_currency", columnDefinition = "TEXT")
    private String itemCurrency;

    /** 物品的展示名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_displayname_state")
    private Short itemDisplayNameState;

    /** 物品的展示名称。 */
    @Column(name = "item_displayname", columnDefinition = "TEXT")
    private String itemDisplayName;

    /** 物品的图标状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_iconstatus_state")
    private Short itemIconStatusState;

    /** 物品的图标状态。 */
    @Column(name = "item_iconstatus", columnDefinition = "TEXT")
    private String itemIconStatus;

    /** 物品的图标地址是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_iconurl_state")
    private Short itemIconUrlState;

    /** 物品的图标地址。 */
    @Column(name = "item_iconurl", columnDefinition = "TEXT")
    private String itemIconUrl;

    /** 物品的身份解析状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_identityresolution_state")
    private Short itemIdentityResolutionState;

    /** 物品的身份解析状态。 */
    @Column(name = "item_identityresolution", columnDefinition = "TEXT")
    private String itemIdentityResolution;

    /** 物品的种类是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_kind_state")
    private Short itemKindState;

    /** 物品的种类。 */
    @Column(name = "item_kind", columnDefinition = "TEXT")
    private String itemKind;

    /** 物品的名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_name_state")
    private Short itemNameState;

    /** 物品的名称。 */
    @Column(name = "item_name", columnDefinition = "TEXT")
    private String itemName;

    /** 物品的英文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_nameen_state")
    private Short itemNameEnState;

    /** 物品的英文名称。 */
    @Column(name = "item_nameen", columnDefinition = "TEXT")
    private String itemNameEn;

    /** 物品的中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_namezh_state")
    private Short itemNameZhState;

    /** 物品的中文名称。 */
    @Column(name = "item_namezh", columnDefinition = "TEXT")
    private String itemNameZh;

    /** 物品的概率适用范围是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_probabilityscope_state")
    private Short itemProbabilityScopeState;

    /** 物品的概率适用范围。 */
    @Column(name = "item_probabilityscope", columnDefinition = "TEXT")
    private String itemProbabilityScope;

    /** 物品的数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_quantity_state")
    private Short itemQuantityState;

    /** 物品的数量。 */
    @Column(name = "item_quantity", columnDefinition = "NUMERIC")
    private BigDecimal itemQuantity;

    /** 物品的精炼等级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_refinement_state")
    private Short itemRefinementState;

    /** 物品的精炼等级。 */
    @Column(name = "item_refinement", columnDefinition = "TEXT")
    private String itemRefinement;

    /** 物品的区域选择是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_regionselection_state")
    private Short itemRegionSelectionState;

    /** 物品的区域选择。 */
    @Column(name = "item_regionselection", columnDefinition = "TEXT")
    private String itemRegionSelection;

    /** 物品的解析依据是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_resolutionbasis_state")
    private Short itemResolutionBasisState;

    /** 物品的解析依据。 */
    @Column(name = "item_resolutionbasis", columnDefinition = "TEXT")
    private String itemResolutionBasis;

    /** 物品的资源候选项引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_resourcecandidatesref_state")
    private Short itemResourceCandidatesRefState;

    /** 物品的资源候选项引用。 */
    @Column(name = "item_resourcecandidatesref", columnDefinition = "TEXT")
    private String itemResourceCandidatesRef;

    /** 物品的规则键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_rulekey_state")
    private Short itemRuleKeyState;

    /** 物品的规则键。 */
    @Column(name = "item_rulekey", columnDefinition = "TEXT")
    private String itemRuleKey;

    /** 物品的选择项是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_selection_state")
    private Short itemSelectionState;

    /** 物品的选择项。 */
    @Column(name = "item_selection", columnDefinition = "TEXT")
    private String itemSelection;

    /** 物品的唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_uniquename_state")
    private Short itemUniqueNameState;

    /** 物品的唯一标识。 */
    @Column(name = "item_uniquename", columnDefinition = "TEXT")
    private String itemUniqueName;

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

    /** 反向索引的来源掉落几率百分比是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "reverseindex_sourcedropchancepercent_state")
    private Short reverseIndexSourceDropChancePercentState;

    /** 反向索引的来源掉落几率百分比。 */
    @Column(name = "reverseindex_sourcedropchancepercent", columnDefinition = "NUMERIC")
    private BigDecimal reverseIndexSourceDropChancePercent;

    /** 反向索引的状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "reverseindex_status_state")
    private Short reverseIndexStatusState;

    /** 反向索引的状态。 */
    @Column(name = "reverseindex_status", columnDefinition = "TEXT")
    private String reverseIndexStatus;

    /** 来源掉落几率百分比是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "sourcedropchancepercent_state")
    private Short sourceDropChancePercentState;

    /** 来源掉落几率百分比。 */
    @Column(name = "sourcedropchancepercent", columnDefinition = "NUMERIC")
    private BigDecimal sourceDropChancePercent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialDropTableRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialDropTableRewardsItemsItemCandidatesItems> officialDropTableRewardsItemsItemCandidatesItemsRows = new ArrayList<>();
}
