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

/** 源 KEYS rewards/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_keys_rewards_items")
public class OfficialKeysRewardsItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "amount_state")
    private Short amountState;

    /** 数量。 */
    @Column(name = "amount", columnDefinition = "NUMERIC")
    private BigDecimal amount;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    /** 物品类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemtype_state")
    private Short itemTypeState;

    /** 物品类型。 */
    @Column(name = "itemtype", columnDefinition = "TEXT")
    private String itemType;

    /** 奖励类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rewardtype_state")
    private Short rewardTypeState;

    /** 奖励类型。 */
    @Column(name = "rewardtype", columnDefinition = "TEXT")
    private String rewardType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialKeysRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialKeysRewardsItemsItemReferencesItems> officialKeysRewardsItemsItemReferencesItemsRows = new ArrayList<>();
}
