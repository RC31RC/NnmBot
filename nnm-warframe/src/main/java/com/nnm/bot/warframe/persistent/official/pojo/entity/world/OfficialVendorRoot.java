package com.nnm.bot.warframe.persistent.official.pojo.entity.world;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/** 源 VENDOR 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_vendor_root")
public class OfficialVendorRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 是否为动态商人是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "isdynamic_state")
    private Short isDynamicState;

    /** 是否为动态商人。 */
    @Column(name = "isdynamic")
    private Boolean isDynamic;

    /** 每个周期是否仅使用一个货架是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "isonebinpercycle_state")
    private Short isOneBinPerCycleState;

    /** 每个周期是否仅使用一个货架。 */
    @Column(name = "isonebinpercycle")
    private Boolean isOneBinPerCycle;

    /** 物品列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "items_state")
    private Short itemsState;

    /** 商品数量的最大值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "numitems_maxvalue_state")
    private Short numItemsMaxValueState;

    /** 商品数量的最大值。 */
    @Column(name = "numitems_maxvalue", columnDefinition = "NUMERIC")
    private BigDecimal numItemsMaxValue;

    /** 商品数量的最小值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "numitems_minvalue_state")
    private Short numItemsMinValueState;

    /** 商品数量的最小值。 */
    @Column(name = "numitems_minvalue", columnDefinition = "NUMERIC")
    private BigDecimal numItemsMinValue;

    /** 每个货架的商品数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "numitemsperbin_state")
    private Short numItemsPerBinState;

    /** 各货架随机价格列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "randomitempricesperbin_state")
    private Short randomItemPricesPerBinState;

    /** 随机种子类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "randomseedtype_state")
    private Short randomSeedTypeState;

    /** 随机种子类型。 */
    @Column(name = "randomseedtype", columnDefinition = "TEXT")
    private String randomSeedType;

    /** 所需目标标签是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "requiredgoaltag_state")
    private Short requiredGoalTagState;

    /** 所需目标标签。 */
    @Column(name = "requiredgoaltag", columnDefinition = "TEXT")
    private String requiredGoalTag;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialVendorItemsItems> officialVendorItemsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialVendorNumItemsPerBinItems> officialVendorNumItemsPerBinItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialVendorRandomItemPricesPerBinItems> officialVendorRandomItemPricesPerBinItemsRows = new ArrayList<>();
}
