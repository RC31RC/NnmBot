package com.nnm.bot.warframe.persistent.official.pojo.entity.items;

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

/** 源 REWARD_PACK components/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_reward_pack_components_items")
public class OfficialRewardPackComponentsItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "amount_state")
    private Short amountState;

    /** 数量。 */
    @Column(name = "amount", columnDefinition = "NUMERIC")
    private BigDecimal amount;

    /** 物品是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "item_state")
    private Short itemState;

    /** 物品。 */
    @Column(name = "item", columnDefinition = "TEXT")
    private String item;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    /** 保底概率增长率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "pityincreaserate_state")
    private Short pityIncreaseRateState;

    /** 保底概率增长率。 */
    @Column(name = "pityincreaserate", columnDefinition = "NUMERIC")
    private BigDecimal pityIncreaseRate;

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
    private OfficialRewardPackRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRewardPackComponentsItemsItemReferencesItems> officialRewardPackComponentsItemsItemReferencesItemsRows = new ArrayList<>();
}
