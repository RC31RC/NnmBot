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

/** 源 DOJO_RECIPE 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_dojo_recipe_root")
public class OfficialDojoRecipeRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 容量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "capacity_state")
    private Short capacityState;

    /** 容量。 */
    @Column(name = "capacity", columnDefinition = "NUMERIC")
    private BigDecimal capacity;

    /** 容量消耗是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "capacitycost_state")
    private Short capacityCostState;

    /** 容量消耗。 */
    @Column(name = "capacitycost", columnDefinition = "NUMERIC")
    private BigDecimal capacityCost;

    /** 颜色的数值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "colour_value_state")
    private Short colourValueState;

    /** 颜色的数值。 */
    @Column(name = "colour_value", columnDefinition = "TEXT")
    private String colourValue;

    /** 装饰容量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "decocapacity_state")
    private Short decoCapacityState;

    /** 装饰容量。 */
    @Column(name = "decocapacity", columnDefinition = "NUMERIC")
    private BigDecimal decoCapacity;

    /** 描述是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "description_value_state")
    private Short descriptionState;

    /** 描述。 */
    @Column(name = "description_value", columnDefinition = "TEXT")
    private String descriptionValue;

    /** 销毁所需时间是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "destructiontime_state")
    private Short destructionTimeState;

    /** 销毁所需时间。 */
    @Column(name = "destructiontime", columnDefinition = "NUMERIC")
    private BigDecimal destructionTime;

    /** 能量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "energy_state")
    private Short energyState;

    /** 能量。 */
    @Column(name = "energy", columnDefinition = "NUMERIC")
    private BigDecimal energy;

    /** 氏族经验值是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "guildxpvalue_state")
    private Short guildXpValueState;

    /** 氏族经验值。 */
    @Column(name = "guildxpvalue", columnDefinition = "NUMERIC")
    private BigDecimal guildXpValue;

    /** 隐藏是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "hidden_state")
    private Short hiddenState;

    /** 隐藏。 */
    @Column(name = "hidden")
    private Boolean hidden;

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

    /** 价格是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "price_state")
    private Short priceState;

    /** 价格。 */
    @Column(name = "price", columnDefinition = "NUMERIC")
    private BigDecimal price;

    /** 复制蓝图价格是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "replicateprice_state")
    private Short replicatePriceState;

    /** 复制蓝图价格。 */
    @Column(name = "replicateprice", columnDefinition = "NUMERIC")
    private BigDecimal replicatePrice;

    /** 是否要求处于宝库中是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "requiredinvault_state")
    private Short requiredInVaultState;

    /** 是否要求处于宝库中。 */
    @Column(name = "requiredinvault")
    private Boolean requiredInVault;

    /** 结果类型是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "resulttype_state")
    private Short resultTypeState;

    /** 结果类型。 */
    @Column(name = "resulttype", columnDefinition = "TEXT")
    private String resultType;

    /** 加速价格是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "skiptimeprice_state")
    private Short skipTimePriceState;

    /** 加速价格。 */
    @Column(name = "skiptimeprice", columnDefinition = "NUMERIC")
    private BigDecimal skipTimePrice;

    /** 前置科技要求是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "techprereq_state")
    private Short techPrereqState;

    /** 前置科技要求。 */
    @Column(name = "techprereq", columnDefinition = "TEXT")
    private String techPrereq;

    /** 时间是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "time_state")
    private Short timeState;

    /** 时间。 */
    @Column(name = "time", columnDefinition = "NUMERIC")
    private BigDecimal time;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialDojoRecipeIngredientsItems> officialDojoRecipeIngredientsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialDojoRecipeItemReferencesItems> officialDojoRecipeItemReferencesItemsRows = new ArrayList<>();
}
