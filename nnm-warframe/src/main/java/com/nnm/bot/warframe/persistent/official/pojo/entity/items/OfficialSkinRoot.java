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

/** 源 SKIN 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_skin_root")
public class OfficialSkinRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 额外物品列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "additionalitems_state")
    private Short additionalItemsState;

    /** 是否始终可用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "alwaysavailable_state")
    private Short alwaysAvailableState;

    /** 是否始终可用。 */
    @Column(name = "alwaysavailable")
    private Boolean alwaysAvailable;

    /** 基础是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "base_state")
    private Short baseState;

    /** 基础。 */
    @Column(name = "base", columnDefinition = "TEXT")
    private String base;

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

    /** 随机强化列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "randomisedupgrades_state")
    private Short randomisedUpgradesState;

    /** 记录键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "recordkey_state")
    private Short recordKeyState;

    /** 记录键。 */
    @Column(name = "recordkey", columnDefinition = "TEXT")
    private String recordKey;

    /** 需求条件是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "requirement_state")
    private Short requirementState;

    /** 需求条件。 */
    @Column(name = "requirement", columnDefinition = "TEXT")
    private String requirement;

    /** 子程序列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "subroutines_state")
    private Short subroutinesState;

    /** 可交易是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "tradable_state")
    private Short tradableState;

    /** 可交易。 */
    @Column(name = "tradable")
    private Boolean tradable;

    /** 唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "uniquename_state")
    private Short uniqueNameState;

    /** 唯一标识。 */
    @Column(name = "uniquename", columnDefinition = "TEXT")
    private String uniqueName;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialSkinAdditionalItemsItems> officialSkinAdditionalItemsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialSkinExcludedPlatformsItems> officialSkinExcludedPlatformsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialSkinItemReferencesItems> officialSkinItemReferencesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialSkinRandomisedUpgradesItems> officialSkinRandomisedUpgradesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialSkinSubroutinesItems> officialSkinSubroutinesItemsRows = new ArrayList<>();
}
