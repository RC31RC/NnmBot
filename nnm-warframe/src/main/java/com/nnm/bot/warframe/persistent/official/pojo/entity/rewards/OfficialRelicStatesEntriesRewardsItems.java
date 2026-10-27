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

/** 源 RELIC states/entries/rewards/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_relic_states_entries_rewards_items")
public class OfficialRelicStatesEntriesRewardsItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 掉落几率百分比是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "dropchancepercent_state")
    private Short dropChancePercentState;

    /** 掉落几率百分比。 */
    @Column(name = "dropchancepercent", columnDefinition = "NUMERIC")
    private BigDecimal dropChancePercent;

    /** 掉落稀有度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "droprarity_state")
    private Short dropRarityState;

    /** 掉落稀有度。 */
    @Column(name = "droprarity", columnDefinition = "TEXT")
    private String dropRarity;

    /** 掉落表条目是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "droptableentries_state")
    private Short dropTableEntriesState;

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

    /** 物品的市场网址标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_marketslug_state")
    private Short itemMarketSlugState;

    /** 物品的市场网址标识。 */
    @Column(name = "item_marketslug", columnDefinition = "TEXT")
    private String itemMarketSlug;

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

    /** 物品的解析状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_resolution_state")
    private Short itemResolutionState;

    /** 物品的解析状态。 */
    @Column(name = "item_resolution", columnDefinition = "TEXT")
    private String itemResolution;

    /** 物品的唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_uniquename_state")
    private Short itemUniqueNameState;

    /** 物品的唯一标识。 */
    @Column(name = "item_uniquename", columnDefinition = "TEXT")
    private String itemUniqueName;

    /** 物品数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemcount_state")
    private Short itemCountState;

    /** 物品数量。 */
    @Column(name = "itemcount", columnDefinition = "NUMERIC")
    private BigDecimal itemCount;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

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

    /** 奖励名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rewardname_state")
    private Short rewardNameState;

    /** 奖励名称。 */
    @Column(name = "rewardname", columnDefinition = "TEXT")
    private String rewardName;

    /** 阶级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "tier_state")
    private Short tierState;

    /** 阶级。 */
    @Column(name = "tier", columnDefinition = "NUMERIC")
    private BigDecimal tier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialRelicStatesEntries parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRelicStatesEntriesRewardsItemsDropTableEntriesItems> officialRelicStatesEntriesRewardsItemsDropTableEntriesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRelicStatesEntriesRewardsItemsItemReferencesItems> officialRelicStatesEntriesRewardsItemsItemReferencesItemsRows = new ArrayList<>();
}
