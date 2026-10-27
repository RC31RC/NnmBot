package com.nnm.bot.warframe.persistent.official.pojo.entity.progression;

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

/** 源 CHALLENGE countedRewards/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_challenge_countedrewards_items")
public class OfficialChallengeCountedRewardsItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 物品数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemcount_state")
    private Short itemCountState;

    /** 物品数量。 */
    @Column(name = "itemcount", columnDefinition = "NUMERIC")
    private BigDecimal itemCount;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    /** 商店物品路径是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "storeitem_state")
    private Short storeItemState;

    /** 商店物品路径。 */
    @Column(name = "storeitem", columnDefinition = "TEXT")
    private String storeItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialChallengeRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialChallengeCountedRewardsItemsItemReferencesItems> officialChallengeCountedRewardsItemsItemReferencesItemsRows = new ArrayList<>();
}
