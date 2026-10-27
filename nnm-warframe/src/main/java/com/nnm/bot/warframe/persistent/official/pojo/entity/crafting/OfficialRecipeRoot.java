package com.nnm.bot.warframe.persistent.official.pojo.entity.crafting;

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

/** 源 RECIPE 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_recipe_root")
public class OfficialRecipeRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 动作键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "actionkey_state")
    private Short actionKeyState;

    /** 动作键。 */
    @Column(name = "actionkey", columnDefinition = "TEXT")
    private String actionKey;

    /** 是否始终可用是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "alwaysavailable_state")
    private Short alwaysAvailableState;

    /** 是否始终可用。 */
    @Column(name = "alwaysavailable")
    private Boolean alwaysAvailable;

    /** 制造现金消耗是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "buildprice_state")
    private Short buildPriceState;

    /** 制造现金消耗。 */
    @Column(name = "buildprice", columnDefinition = "NUMERIC")
    private BigDecimal buildPrice;

    /** 制造耗时是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "buildtime_state")
    private Short buildTimeState;

    /** 制造耗时。 */
    @Column(name = "buildtime", columnDefinition = "NUMERIC")
    private BigDecimal buildTime;

    /** 是否为资料库隐藏条目是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "codexsecret_state")
    private Short codexSecretState;

    /** 是否为资料库隐藏条目。 */
    @Column(name = "codexsecret")
    private Boolean codexSecret;

    /** 使用后是否消耗是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "consumeonuse_state")
    private Short consumeOnUseState;

    /** 使用后是否消耗。 */
    @Column(name = "consumeonuse")
    private Boolean consumeOnUse;

    /** 现金消耗是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "creditscost_state")
    private Short creditsCostState;

    /** 现金消耗。 */
    @Column(name = "creditscost", columnDefinition = "NUMERIC")
    private BigDecimal creditsCost;

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

    /** 隐藏是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "hidden_state")
    private Short hiddenState;

    /** 隐藏。 */
    @Column(name = "hidden")
    private Boolean hidden;

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

    /** 身份别名列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "identityaliases_state")
    private Short identityAliasesState;

    /** 制造材料列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "ingredients_state")
    private Short ingredientsState;

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

    /** 产出数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "num_state")
    private Short numState;

    /** 产出数量。 */
    @Column(name = "num", columnDefinition = "NUMERIC")
    private BigDecimal num;

    /** 是否仅可购买一次是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "onetimepurchasable_state")
    private Short oneTimePurchasableState;

    /** 是否仅可购买一次。 */
    @Column(name = "onetimepurchasable")
    private Boolean oneTimePurchasable;

    /** 铂金消耗是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "platinumcost_state")
    private Short platinumCostState;

    /** 铂金消耗。 */
    @Column(name = "platinumcost", columnDefinition = "NUMERIC")
    private BigDecimal platinumCost;

    /** 圣装出售价格是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "primesellingprice_state")
    private Short primeSellingPriceState;

    /** 圣装出售价格。 */
    @Column(name = "primesellingprice", columnDefinition = "NUMERIC")
    private BigDecimal primeSellingPrice;

    /** 数量计算依据是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quantitybasis_state")
    private Short quantityBasisState;

    /** 数量计算依据。 */
    @Column(name = "quantitybasis", columnDefinition = "TEXT")
    private String quantityBasis;

    /** 配方类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "recipekind_state")
    private Short recipeKindState;

    /** 配方类型。 */
    @Column(name = "recipekind", columnDefinition = "TEXT")
    private String recipeKind;

    /** 记录键是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "recordkey_state")
    private Short recordKeyState;

    /** 记录键。 */
    @Column(name = "recordkey", columnDefinition = "TEXT")
    private String recordKey;

    /** 结果物品的图标状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "resultitem_iconstatus_state")
    private Short resultItemIconStatusState;

    /** 结果物品的图标状态。 */
    @Column(name = "resultitem_iconstatus", columnDefinition = "TEXT")
    private String resultItemIconStatus;

    /** 结果物品的图标地址是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "resultitem_iconurl_state")
    private Short resultItemIconUrlState;

    /** 结果物品的图标地址。 */
    @Column(name = "resultitem_iconurl", columnDefinition = "TEXT")
    private String resultItemIconUrl;

    /** 结果物品的名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "resultitem_name_state")
    private Short resultItemNameState;

    /** 结果物品的名称。 */
    @Column(name = "resultitem_name", columnDefinition = "TEXT")
    private String resultItemName;

    /** 结果物品的英文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "resultitem_nameen_state")
    private Short resultItemNameEnState;

    /** 结果物品的英文名称。 */
    @Column(name = "resultitem_nameen", columnDefinition = "TEXT")
    private String resultItemNameEn;

    /** 结果物品的中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "resultitem_namezh_state")
    private Short resultItemNameZhState;

    /** 结果物品的中文名称。 */
    @Column(name = "resultitem_namezh", columnDefinition = "TEXT")
    private String resultItemNameZh;

    /** 结果物品的唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "resultitem_uniquename_state")
    private Short resultItemUniqueNameState;

    /** 结果物品的唯一标识。 */
    @Column(name = "resultitem_uniquename", columnDefinition = "TEXT")
    private String resultItemUniqueName;

    /** 结果类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "resulttype_state")
    private Short resultTypeState;

    /** 结果类型。 */
    @Column(name = "resulttype", columnDefinition = "TEXT")
    private String resultType;

    /** 产出物品类型中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "resulttypenamezh_state")
    private Short resultTypeNameZhState;

    /** 产出物品类型中文名称。 */
    @Column(name = "resulttypenamezh", columnDefinition = "TEXT")
    private String resultTypeNameZh;

    /** 隐藏材料操作要求是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "secretingredientaction_state")
    private Short secretIngredientActionState;

    /** 隐藏材料操作要求。 */
    @Column(name = "secretingredientaction", columnDefinition = "TEXT")
    private String secretIngredientAction;

    /** 隐藏材料列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "secretingredients_state")
    private Short secretIngredientsState;

    /** 加速制造价格是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "skipbuildtimeprice_state")
    private Short skipBuildTimePriceState;

    /** 加速制造价格。 */
    @Column(name = "skipbuildtimeprice", columnDefinition = "NUMERIC")
    private BigDecimal skipBuildTimePrice;

    /** 集团声望变化的标签是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "syndicatestandingchange_tag_state")
    private Short syndicateStandingChangeTagState;

    /** 集团声望变化的标签。 */
    @Column(name = "syndicatestandingchange_tag", columnDefinition = "TEXT")
    private String syndicateStandingChangeTag;

    /** 集团声望变化的数值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "syndicatestandingchange_value_state")
    private Short syndicateStandingChangeValueState;

    /** 集团声望变化的数值。 */
    @Column(name = "syndicatestandingchange_value", columnDefinition = "NUMERIC")
    private BigDecimal syndicateStandingChangeValue;

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
    private List<OfficialRecipeIdentityAliasesItems> officialRecipeIdentityAliasesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRecipeIngredientsItems> officialRecipeIngredientsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRecipeItemReferencesItems> officialRecipeItemReferencesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialRecipeSecretIngredientsItems> officialRecipeSecretIngredientsItemsRows = new ArrayList<>();
}
