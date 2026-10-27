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

/** 源 SYNDICATE titles/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_syndicate_titles_items")
public class OfficialSyndicateTitlesItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "description_value_state")
    private Short descriptionState;

    /** 描述。 */
    @Column(name = "description_value", columnDefinition = "TEXT")
    private String description;

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

    /** 图标地址是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "iconurl_state")
    private Short iconUrlState;

    /** 图标地址。 */
    @Column(name = "iconurl", columnDefinition = "TEXT")
    private String iconUrl;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    /** 等级是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "level_state")
    private Short levelState;

    /** 等级。 */
    @Column(name = "level", columnDefinition = "NUMERIC")
    private BigDecimal level;

    /** 声望上限是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "maxstanding_state")
    private Short maxStandingState;

    /** 声望上限。 */
    @Column(name = "maxstanding", columnDefinition = "NUMERIC")
    private BigDecimal maxStanding;

    /** 最低声望是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "minstanding_state")
    private Short minStandingState;

    /** 最低声望。 */
    @Column(name = "minstanding", columnDefinition = "NUMERIC")
    private BigDecimal minStanding;

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

    /** 奖励的物品数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "reward_itemcount_state")
    private Short rewardItemCountState;

    /** 奖励的物品数量。 */
    @Column(name = "reward_itemcount", columnDefinition = "NUMERIC")
    private BigDecimal rewardItemCount;

    /** 奖励的物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "reward_itemreferences_state")
    private Short rewardItemReferencesState;

    /** 奖励的物品类型路径是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "reward_itemtype_state")
    private Short rewardItemTypeState;

    /** 奖励的物品类型路径。 */
    @Column(name = "reward_itemtype", columnDefinition = "TEXT")
    private String rewardItemType;

    /** 献祭的现金是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "sacrifice_credits_state")
    private Short sacrificeCreditsState;

    /** 献祭的现金。 */
    @Column(name = "sacrifice_credits", columnDefinition = "NUMERIC")
    private BigDecimal sacrificeCredits;

    /** 献祭的物品列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "sacrifice_items_state")
    private Short sacrificeItemsState;

    /** 商店物品奖励是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "storeitemreward_state")
    private Short storeItemRewardState;

    /** 商店物品奖励。 */
    @Column(name = "storeitemreward", columnDefinition = "TEXT")
    private String storeItemReward;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialSyndicateRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialSyndicateTitlesItemsItemReferencesItems> officialSyndicateTitlesItemsItemReferencesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialSyndicateTitlesItemsRewardItemReferencesItems> officialSyndicateTitlesItemsRewardItemReferencesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialSyndicateTitlesItemsSacrificeItemsItems> officialSyndicateTitlesItemsSacrificeItemsItemsRows = new ArrayList<>();
}
