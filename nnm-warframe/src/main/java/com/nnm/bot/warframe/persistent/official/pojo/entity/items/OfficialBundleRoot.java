package com.nnm.bot.warframe.persistent.official.pojo.entity.items;

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

/** 源 BUNDLE 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_bundle_root")
public class OfficialBundleRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 附带铂金数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "bundledplatinum_state")
    private Short bundledPlatinumState;

    /** 附带铂金数量。 */
    @Column(name = "bundledplatinum", columnDefinition = "NUMERIC")
    private BigDecimal bundledPlatinum;

    /** 组成部件列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "components_state")
    private Short componentsState;

    /** 现金消耗是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "creditscost_state")
    private Short creditsCostState;

    /** 现金消耗。 */
    @Column(name = "creditscost", columnDefinition = "NUMERIC")
    private BigDecimal creditsCost;

    /** 描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "description_value_state")
    private Short descriptionState;

    /** 描述。 */
    @Column(name = "description_value", columnDefinition = "TEXT")
    private String descriptionValue;

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

    /** 不支持的平台列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "excludedplatforms_state")
    private Short excludedPlatformsState;

    /** 是否排除在资料库之外是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "excludefromcodex_state")
    private Short excludeFromCodexState;

    /** 是否排除在资料库之外。 */
    @Column(name = "excludefromcodex")
    private Boolean excludeFromCodexValue;

    /** 是否排除在商店之外是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "excludefrommarket_state")
    private Short excludeFromMarketState;

    /** 是否排除在商店之外。 */
    @Column(name = "excludefrommarket")
    private Boolean excludeFromMarket;

    /** 赠礼加成是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "giftingbonus_state")
    private Short giftingBonusState;

    /** 赠礼加成。 */
    @Column(name = "giftingbonus", columnDefinition = "TEXT")
    private String giftingBonus;

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

    /** 是否仅可购买一次是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "onetimepurchasable_state")
    private Short oneTimePurchasableState;

    /** 是否仅可购买一次。 */
    @Column(name = "onetimepurchasable")
    private Boolean oneTimePurchasable;

    /** 礼包折扣是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "packagediscount_state")
    private Short packageDiscountState;

    /** 礼包折扣。 */
    @Column(name = "packagediscount", columnDefinition = "NUMERIC")
    private BigDecimal packageDiscount;

    /** 配对铂金礼包是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "platinumbundlepair_state")
    private Short platinumBundlePairState;

    /** 配对铂金礼包。 */
    @Column(name = "platinumbundlepair", columnDefinition = "TEXT")
    private String platinumBundlePair;

    /** 铂金消耗是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "platinumcost_state")
    private Short platinumCostState;

    /** 铂金消耗。 */
    @Column(name = "platinumcost", columnDefinition = "NUMERIC")
    private BigDecimal platinumCost;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialBundleComponentsItems> officialBundleComponentsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialBundleExcludedPlatformsItems> officialBundleExcludedPlatformsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialBundleItemReferencesItems> officialBundleItemReferencesItemsRows = new ArrayList<>();
}
