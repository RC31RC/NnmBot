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

/** 源 RESOURCES 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_resources_root")
public class OfficialResourcesRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 是否为资料库隐藏条目是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "codexsecret_state")
    private Short codexSecretState;

    /** 是否为资料库隐藏条目。 */
    @Column(name = "codexsecret")
    private Boolean codexSecret;

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

    /** 蠕虫喂食配置的数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "helminthsnack_count_state")
    private Short helminthSnackCountState;

    /** 蠕虫喂食配置的数量。 */
    @Column(name = "helminthsnack_count", columnDefinition = "NUMERIC")
    private BigDecimal helminthSnackCount;

    /** 蠕虫喂食配置的获得量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "helminthsnack_gain_state")
    private Short helminthSnackGainState;

    /** 蠕虫喂食配置的获得量。 */
    @Column(name = "helminthsnack_gain", columnDefinition = "NUMERIC")
    private BigDecimal helminthSnackGain;

    /** 蠕虫喂食配置的物品引用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "helminthsnack_itemreferences_state")
    private Short helminthSnackItemReferencesState;

    /** 蠕虫喂食配置的类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "helminthsnack_type_state")
    private Short helminthSnackTypeState;

    /** 蠕虫喂食配置的类型。 */
    @Column(name = "helminthsnack_type", columnDefinition = "TEXT")
    private String helminthSnackType;

    /** 图标是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "icon_state")
    private Short iconState;

    /** 图标。 */
    @Column(name = "icon", columnDefinition = "TEXT")
    private String icon;

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

    /** 详细描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "longdescription_state")
    private Short longDescriptionState;

    /** 详细描述。 */
    @Column(name = "longdescription", columnDefinition = "TEXT")
    private String longDescriptionValue;

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

    /** 父级名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "parentname_state")
    private Short parentNameState;

    /** 父级名称。 */
    @Column(name = "parentname", columnDefinition = "TEXT")
    private String parentNameValue;

    /** 拾取数量的最大值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "pickupquantity_maxvalue_state")
    private Short pickupQuantityMaxValueState;

    /** 拾取数量的最大值。 */
    @Column(name = "pickupquantity_maxvalue", columnDefinition = "NUMERIC")
    private BigDecimal pickupQuantityMaxValue;

    /** 拾取数量的最小值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "pickupquantity_minvalue_state")
    private Short pickupQuantityMinValueState;

    /** 拾取数量的最小值。 */
    @Column(name = "pickupquantity_minvalue", columnDefinition = "NUMERIC")
    private BigDecimal pickupQuantityMinValue;

    /** 铂金消耗是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "platinumcost_state")
    private Short platinumCostState;

    /** 铂金消耗。 */
    @Column(name = "platinumcost", columnDefinition = "NUMERIC")
    private BigDecimal platinumCost;

    /** 产品分类是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "productcategory_state")
    private Short productCategoryState;

    /** 产品分类。 */
    @Column(name = "productcategory", columnDefinition = "TEXT")
    private String productCategoryValue;

    /** 每次购买数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "purchasequantity_state")
    private Short purchaseQuantityState;

    /** 每次购买数量。 */
    @Column(name = "purchasequantity", columnDefinition = "NUMERIC")
    private BigDecimal purchaseQuantity;

    /** 稀有度是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rarity_state")
    private Short rarityState;

    /** 稀有度。 */
    @Column(name = "rarity", columnDefinition = "TEXT")
    private String rarity;

    /** 记录键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "recordkey_state")
    private Short recordKeyState;

    /** 记录键。 */
    @Column(name = "recordkey", columnDefinition = "TEXT")
    private String recordKey;

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialResourcesHelminthSnackItemReferencesItems> officialResourcesHelminthSnackItemReferencesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialResourcesItemReferencesItems> officialResourcesItemReferencesItemsRows = new ArrayList<>();
}
