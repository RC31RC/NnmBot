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

/** 源 BUNDLE components/items 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_bundle_components_items")
public class OfficialBundleComponentsItems extends OfficialChildRow {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 耐久天数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "durabilitydays_state")
    private Short durabilityDaysState;

    /** 耐久天数。 */
    @Column(name = "durabilitydays", columnDefinition = "NUMERIC")
    private BigDecimal durabilityDays;

    /** 是否给予满级物品是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "givemaxrank_state")
    private Short giveMaxRankState;

    /** 是否给予满级物品。 */
    @Column(name = "givemaxrank")
    private Boolean giveMaxRank;

    /** 物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "itemreferences_state")
    private Short itemReferencesState;

    /** 每次购买数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "purchasequantity_state")
    private Short purchaseQuantityState;

    /** 每次购买数量。 */
    @Column(name = "purchasequantity", columnDefinition = "NUMERIC")
    private BigDecimal purchaseQuantity;

    /** 类型名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "typename_state")
    private Short typeNameState;

    /** 类型名称。 */
    @Column(name = "typename", columnDefinition = "TEXT")
    private String typeName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bkey", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OfficialBundleRoot parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialBundleComponentsItemsItemReferencesItems> officialBundleComponentsItemsItemReferencesItemsRows = new ArrayList<>();
}
