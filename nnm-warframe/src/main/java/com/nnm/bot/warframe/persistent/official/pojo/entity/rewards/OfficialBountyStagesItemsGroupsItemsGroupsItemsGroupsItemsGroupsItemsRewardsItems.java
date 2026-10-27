package com.nnm.bot.warframe.persistent.official.pojo.entity.rewards;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialChildRow;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/** 源 BOUNTY stages/items/groups/items/groups/items/groups/items/groups/items/rewards/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_bounty_stages_items_groups_items_groups__91ce8b9ab5")
public class OfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItemsRewardsItems extends OfficialChildRow {
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

    /** 物品的数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_count_state")
    private Short itemCountState;

    /** 物品的数量。 */
    @Column(name = "item_count", columnDefinition = "NUMERIC")
    private BigDecimal itemCount;

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

    /** 物品的精炼等级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_refinement_state")
    private Short itemRefinementState;

    /** 物品的精炼等级。 */
    @Column(name = "item_refinement", columnDefinition = "TEXT")
    private String itemRefinement;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems parent;
}
