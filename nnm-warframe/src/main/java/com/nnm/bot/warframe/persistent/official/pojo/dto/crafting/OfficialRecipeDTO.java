package com.nnm.bot.warframe.persistent.official.pojo.dto.crafting;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialInputText;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

/** RECIPE 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialRecipeDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 动作键；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("actionKey")
    private String actionKey;

    /**
     * 记录动作键是否出现，同时区分未提供与显式空值。
     *
     * @param value 动作键，保留协议中的显式空值。
     */
    @JsonSetter("actionKey")
    public void setActionKey(String value) {
        this.actionKey = value;
        markPresent("actionKey");
    }

    /** 是否始终可用；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("alwaysAvailable")
    private Boolean alwaysAvailable;

    /**
     * 记录是否始终可用是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否始终可用，保留协议中的显式空值。
     */
    @JsonSetter("alwaysAvailable")
    public void setAlwaysAvailable(Boolean value) {
        this.alwaysAvailable = value;
        markPresent("alwaysAvailable");
    }

    /** 制造现金消耗；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("buildPrice")
    private BigDecimal buildPrice;

    /**
     * 记录制造现金消耗是否出现，同时区分未提供与显式空值。
     *
     * @param value 制造现金消耗，保留协议中的显式空值。
     */
    @JsonSetter("buildPrice")
    public void setBuildPrice(BigDecimal value) {
        this.buildPrice = value;
        markPresent("buildPrice");
    }

    /** 制造耗时；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("buildTime")
    private BigDecimal buildTime;

    /**
     * 记录制造耗时是否出现，同时区分未提供与显式空值。
     *
     * @param value 制造耗时，保留协议中的显式空值。
     */
    @JsonSetter("buildTime")
    public void setBuildTime(BigDecimal value) {
        this.buildTime = value;
        markPresent("buildTime");
    }

    /** 是否为资料库隐藏条目；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("codexSecret")
    private Boolean codexSecret;

    /**
     * 记录是否为资料库隐藏条目是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否为资料库隐藏条目，保留协议中的显式空值。
     */
    @JsonSetter("codexSecret")
    public void setCodexSecret(Boolean value) {
        this.codexSecret = value;
        markPresent("codexSecret");
    }

    /** 使用后是否消耗；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("consumeOnUse")
    private Boolean consumeOnUse;

    /**
     * 记录使用后是否消耗是否出现，同时区分未提供与显式空值。
     *
     * @param value 使用后是否消耗，保留协议中的显式空值。
     */
    @JsonSetter("consumeOnUse")
    public void setConsumeOnUse(Boolean value) {
        this.consumeOnUse = value;
        markPresent("consumeOnUse");
    }

    /** 现金消耗；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("creditsCost")
    private BigDecimal creditsCost;

    /**
     * 记录现金消耗是否出现，同时区分未提供与显式空值。
     *
     * @param value 现金消耗，保留协议中的显式空值。
     */
    @JsonSetter("creditsCost")
    public void setCreditsCost(BigDecimal value) {
        this.creditsCost = value;
        markPresent("creditsCost");
    }

    /** 是否排除在资料库之外；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("excludeFromCodex")
    private Boolean excludeFromCodex;

    /**
     * 记录是否排除在资料库之外是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否排除在资料库之外，保留协议中的显式空值。
     */
    @JsonSetter("excludeFromCodex")
    public void setExcludeFromCodex(Boolean value) {
        this.excludeFromCodex = value;
        markPresent("excludeFromCodex");
    }

    /** 是否排除在商店之外；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("excludeFromMarket")
    private Boolean excludeFromMarket;

    /**
     * 记录是否排除在商店之外是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否排除在商店之外，保留协议中的显式空值。
     */
    @JsonSetter("excludeFromMarket")
    public void setExcludeFromMarket(Boolean value) {
        this.excludeFromMarket = value;
        markPresent("excludeFromMarket");
    }

    /** 隐藏；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("hidden")
    private Boolean hidden;

    /**
     * 记录隐藏是否出现，同时区分未提供与显式空值。
     *
     * @param value 隐藏，保留协议中的显式空值。
     */
    @JsonSetter("hidden")
    public void setHidden(Boolean value) {
        this.hidden = value;
        markPresent("hidden");
    }

    /** 图标状态；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("iconStatus")
    private String iconStatus;

    /**
     * 记录图标状态是否出现，同时区分未提供与显式空值。
     *
     * @param value 图标状态，保留协议中的显式空值。
     */
    @JsonSetter("iconStatus")
    public void setIconStatus(String value) {
        this.iconStatus = value;
        markPresent("iconStatus");
    }

    /** 图标地址；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("iconUrl")
    private String iconUrl;

    /**
     * 记录图标地址是否出现，同时区分未提供与显式空值。
     *
     * @param value 图标地址，保留协议中的显式空值。
     */
    @JsonSetter("iconUrl")
    public void setIconUrl(String value) {
        this.iconUrl = value;
        markPresent("iconUrl");
    }

    /** 身份别名列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("identityAliases")
    private List<String> identityAliases;

    /**
     * 记录身份别名列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 身份别名列表，保留协议中的显式空值。
     */
    @JsonSetter("identityAliases")
    public void setIdentityAliases(List<String> value) {
        this.identityAliases = value;
        markPresent("identityAliases");
    }

    /** 制造材料列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("ingredients")
    private List<IngredientsDTO> ingredients;

    /**
     * 记录制造材料列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 制造材料列表，保留协议中的显式空值。
     */
    @JsonSetter("ingredients")
    public void setIngredients(List<IngredientsDTO> value) {
        this.ingredients = value;
        markPresent("ingredients");
    }

    /** 物品引用；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("itemReferences")
    private List<ItemReferencesDTO> itemReferences;

    /**
     * 记录物品引用是否出现，同时区分未提供与显式空值。
     *
     * @param value 物品引用，保留协议中的显式空值。
     */
    @JsonSetter("itemReferences")
    public void setItemReferences(List<ItemReferencesDTO> value) {
        this.itemReferences = value;
        markPresent("itemReferences");
    }

    /** 名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("name")
    private String name;

    /**
     * 记录名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 名称，保留协议中的显式空值。
     */
    @JsonSetter("name")
    public void setName(String value) {
        this.name = value;
        markPresent("name");
    }

    /** 英文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("nameEn")
    private String nameEn;

    /**
     * 记录英文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 英文名称，保留协议中的显式空值。
     */
    @JsonSetter("nameEn")
    public void setNameEn(String value) {
        this.nameEn = value;
        markPresent("nameEn");
    }

    /** 中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("nameZh")
    private String nameZh;

    /**
     * 记录中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文名称，保留协议中的显式空值。
     */
    @JsonSetter("nameZh")
    public void setNameZh(String value) {
        this.nameZh = value;
        markPresent("nameZh");
    }

    /** 产出数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("num")
    private BigDecimal num;

    /**
     * 记录产出数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 产出数量，保留协议中的显式空值。
     */
    @JsonSetter("num")
    public void setNum(BigDecimal value) {
        this.num = value;
        markPresent("num");
    }

    /** 是否仅可购买一次；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("oneTimePurchasable")
    private Boolean oneTimePurchasable;

    /**
     * 记录是否仅可购买一次是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否仅可购买一次，保留协议中的显式空值。
     */
    @JsonSetter("oneTimePurchasable")
    public void setOneTimePurchasable(Boolean value) {
        this.oneTimePurchasable = value;
        markPresent("oneTimePurchasable");
    }

    /** 铂金消耗；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("platinumCost")
    private BigDecimal platinumCost;

    /**
     * 记录铂金消耗是否出现，同时区分未提供与显式空值。
     *
     * @param value 铂金消耗，保留协议中的显式空值。
     */
    @JsonSetter("platinumCost")
    public void setPlatinumCost(BigDecimal value) {
        this.platinumCost = value;
        markPresent("platinumCost");
    }

    /** 圣装出售价格；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("primeSellingPrice")
    private BigDecimal primeSellingPrice;

    /**
     * 记录圣装出售价格是否出现，同时区分未提供与显式空值。
     *
     * @param value 圣装出售价格，保留协议中的显式空值。
     */
    @JsonSetter("primeSellingPrice")
    public void setPrimeSellingPrice(BigDecimal value) {
        this.primeSellingPrice = value;
        markPresent("primeSellingPrice");
    }

    /** 数量计算依据；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quantityBasis")
    private String quantityBasis;

    /**
     * 记录数量计算依据是否出现，同时区分未提供与显式空值。
     *
     * @param value 数量计算依据，保留协议中的显式空值。
     */
    @JsonSetter("quantityBasis")
    public void setQuantityBasis(String value) {
        this.quantityBasis = value;
        markPresent("quantityBasis");
    }

    /** 配方类型；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("recipeKind")
    private String recipeKind;

    /**
     * 记录配方类型是否出现，同时区分未提供与显式空值。
     *
     * @param value 配方类型，保留协议中的显式空值。
     */
    @JsonSetter("recipeKind")
    public void setRecipeKind(String value) {
        this.recipeKind = value;
        markPresent("recipeKind");
    }

    /** 记录键；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("recordKey")
    private String recordKey;

    /**
     * 记录记录键是否出现，同时区分未提供与显式空值。
     *
     * @param value 记录键，保留协议中的显式空值。
     */
    @JsonSetter("recordKey")
    public void setRecordKey(String value) {
        this.recordKey = value;
        markPresent("recordKey");
    }

    /** 结果物品的图标状态；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("resultItem__iconStatus")
    private String resultItemIconStatus;

    /**
     * 记录结果物品的图标状态是否出现，同时区分未提供与显式空值。
     *
     * @param value 结果物品的图标状态，保留协议中的显式空值。
     */
    @JsonSetter("resultItem__iconStatus")
    public void setResultItemIconStatus(String value) {
        this.resultItemIconStatus = value;
        markPresent("resultItem__iconStatus");
    }

    /** 结果物品的图标地址；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("resultItem__iconUrl")
    private String resultItemIconUrl;

    /**
     * 记录结果物品的图标地址是否出现，同时区分未提供与显式空值。
     *
     * @param value 结果物品的图标地址，保留协议中的显式空值。
     */
    @JsonSetter("resultItem__iconUrl")
    public void setResultItemIconUrl(String value) {
        this.resultItemIconUrl = value;
        markPresent("resultItem__iconUrl");
    }

    /** 结果物品的名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("resultItem__name")
    private String resultItemName;

    /**
     * 记录结果物品的名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 结果物品的名称，保留协议中的显式空值。
     */
    @JsonSetter("resultItem__name")
    public void setResultItemName(String value) {
        this.resultItemName = value;
        markPresent("resultItem__name");
    }

    /** 结果物品的英文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("resultItem__nameEn")
    private String resultItemNameEn;

    /**
     * 记录结果物品的英文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 结果物品的英文名称，保留协议中的显式空值。
     */
    @JsonSetter("resultItem__nameEn")
    public void setResultItemNameEn(String value) {
        this.resultItemNameEn = value;
        markPresent("resultItem__nameEn");
    }

    /** 结果物品的中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("resultItem__nameZh")
    private String resultItemNameZh;

    /**
     * 记录结果物品的中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 结果物品的中文名称，保留协议中的显式空值。
     */
    @JsonSetter("resultItem__nameZh")
    public void setResultItemNameZh(String value) {
        this.resultItemNameZh = value;
        markPresent("resultItem__nameZh");
    }

    /** 结果物品的唯一标识；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("resultItem__uniqueName")
    private String resultItemUniqueName;

    /**
     * 记录结果物品的唯一标识是否出现，同时区分未提供与显式空值。
     *
     * @param value 结果物品的唯一标识，保留协议中的显式空值。
     */
    @JsonSetter("resultItem__uniqueName")
    public void setResultItemUniqueName(String value) {
        this.resultItemUniqueName = value;
        markPresent("resultItem__uniqueName");
    }

    /** 结果类型；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("resultType")
    private String resultType;

    /**
     * 记录结果类型是否出现，同时区分未提供与显式空值。
     *
     * @param value 结果类型，保留协议中的显式空值。
     */
    @JsonSetter("resultType")
    public void setResultType(String value) {
        this.resultType = value;
        markPresent("resultType");
    }

    /** 产出物品类型中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("resultTypeNameZh")
    private String resultTypeNameZh;

    /**
     * 记录产出物品类型中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 产出物品类型中文名称，保留协议中的显式空值。
     */
    @JsonSetter("resultTypeNameZh")
    public void setResultTypeNameZh(String value) {
        this.resultTypeNameZh = value;
        markPresent("resultTypeNameZh");
    }

    /** 隐藏材料操作要求；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("secretIngredientAction")
    private String secretIngredientAction;

    /**
     * 记录隐藏材料操作要求是否出现，同时区分未提供与显式空值。
     *
     * @param value 隐藏材料操作要求，保留协议中的显式空值。
     */
    @JsonSetter("secretIngredientAction")
    public void setSecretIngredientAction(String value) {
        this.secretIngredientAction = value;
        markPresent("secretIngredientAction");
    }

    /** 隐藏材料列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("secretIngredients")
    private List<SecretIngredientsDTO> secretIngredients;

    /**
     * 记录隐藏材料列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 隐藏材料列表，保留协议中的显式空值。
     */
    @JsonSetter("secretIngredients")
    public void setSecretIngredients(List<SecretIngredientsDTO> value) {
        this.secretIngredients = value;
        markPresent("secretIngredients");
    }

    /** 加速制造价格；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("skipBuildTimePrice")
    private BigDecimal skipBuildTimePrice;

    /**
     * 记录加速制造价格是否出现，同时区分未提供与显式空值。
     *
     * @param value 加速制造价格，保留协议中的显式空值。
     */
    @JsonSetter("skipBuildTimePrice")
    public void setSkipBuildTimePrice(BigDecimal value) {
        this.skipBuildTimePrice = value;
        markPresent("skipBuildTimePrice");
    }

    /** 集团声望变化的标签；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("syndicateStandingChange__tag")
    private String syndicateStandingChangeTag;

    /**
     * 记录集团声望变化的标签是否出现，同时区分未提供与显式空值。
     *
     * @param value 集团声望变化的标签，保留协议中的显式空值。
     */
    @JsonSetter("syndicateStandingChange__tag")
    public void setSyndicateStandingChangeTag(String value) {
        this.syndicateStandingChangeTag = value;
        markPresent("syndicateStandingChange__tag");
    }

    /** 集团声望变化的数值；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("syndicateStandingChange__value")
    private BigDecimal syndicateStandingChangeValue;

    /**
     * 记录集团声望变化的数值是否出现，同时区分未提供与显式空值。
     *
     * @param value 集团声望变化的数值，保留协议中的显式空值。
     */
    @JsonSetter("syndicateStandingChange__value")
    public void setSyndicateStandingChangeValue(BigDecimal value) {
        this.syndicateStandingChangeValue = value;
        markPresent("syndicateStandingChange__value");
    }

    /** 可交易；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("tradable")
    private Boolean tradable;

    /**
     * 记录可交易是否出现，同时区分未提供与显式空值。
     *
     * @param value 可交易，保留协议中的显式空值。
     */
    @JsonSetter("tradable")
    public void setTradable(Boolean value) {
        this.tradable = value;
        markPresent("tradable");
    }

    /** 唯一标识；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("uniqueName")
    private String uniqueName;

    /**
     * 记录唯一标识是否出现，同时区分未提供与显式空值。
     *
     * @param value 唯一标识，保留协议中的显式空值。
     */
    @JsonSetter("uniqueName")
    public void setUniqueName(String value) {
        this.uniqueName = value;
        markPresent("uniqueName");
    }

    /**
     * 提取本记录的常用检索字段，不携带 JPA 受管状态。
     *
     * @return 名称、分类和说明等检索摘要；上游缺省字段保持为空。
     */
    @Override
    public OfficialRecordHeaderDTO header() {
        return new OfficialRecordHeaderDTO(OfficialInputText.first(nameEn, name), OfficialInputText.first(nameZh), null, null, null);
    }

    /**
     * 读取上游显式业务键，不使用数组位置生成身份。
     *
     * @return 上游提供的首个稳定业务键；未提供时为空。
     */
    @Override
    public String stableKey() {
        return OfficialInputText.first(recordKey, uniqueName);
    }

    /** 官方嵌套对象 IngredientsItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class IngredientsItemReferencesDTO extends OfficialObjectDTO {
        /** 制造材料列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录制造材料列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 制造材料列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录制造材料列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 制造材料列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录制造材料列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 制造材料列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录制造材料列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 制造材料列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录制造材料列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 制造材料列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录制造材料列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 制造材料列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录制造材料列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 制造材料列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录制造材料列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 制造材料列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录制造材料列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 IngredientsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class IngredientsDTO extends OfficialObjectDTO {
        /** 制造材料列表的材料顺序；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ingredientOrder")
        private BigDecimal ingredientOrder;

        /**
         * 记录制造材料列表的材料顺序是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的材料顺序，保留协议中的显式空值。
         */
        @JsonSetter("ingredientOrder")
        public void setIngredientOrder(BigDecimal value) {
            this.ingredientOrder = value;
            markPresent("ingredientOrder");
        }

        /** 制造材料列表的物品的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconStatus")
        private String itemIconStatus;

        /**
         * 记录制造材料列表的物品的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的物品的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("item__iconStatus")
        public void setItemIconStatus(String value) {
            this.itemIconStatus = value;
            markPresent("item__iconStatus");
        }

        /** 制造材料列表的物品的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconUrl")
        private String itemIconUrl;

        /**
         * 记录制造材料列表的物品的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的物品的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("item__iconUrl")
        public void setItemIconUrl(String value) {
            this.itemIconUrl = value;
            markPresent("item__iconUrl");
        }

        /** 制造材料列表的物品的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__name")
        private String itemName;

        /**
         * 记录制造材料列表的物品的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的物品的名称，保留协议中的显式空值。
         */
        @JsonSetter("item__name")
        public void setItemName(String value) {
            this.itemName = value;
            markPresent("item__name");
        }

        /** 制造材料列表的物品的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameEn")
        private String itemNameEn;

        /**
         * 记录制造材料列表的物品的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的物品的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameEn")
        public void setItemNameEn(String value) {
            this.itemNameEn = value;
            markPresent("item__nameEn");
        }

        /** 制造材料列表的物品的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameZh")
        private String itemNameZh;

        /**
         * 记录制造材料列表的物品的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的物品的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameZh")
        public void setItemNameZh(String value) {
            this.itemNameZh = value;
            markPresent("item__nameZh");
        }

        /** 制造材料列表的物品的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__uniqueName")
        private String itemUniqueName;

        /**
         * 记录制造材料列表的物品的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的物品的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("item__uniqueName")
        public void setItemUniqueName(String value) {
            this.itemUniqueName = value;
            markPresent("item__uniqueName");
        }

        /** 制造材料列表的物品数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ItemCount")
        private BigDecimal itemCount;

        /**
         * 记录制造材料列表的物品数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的物品数量，保留协议中的显式空值。
         */
        @JsonSetter("ItemCount")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("ItemCount");
        }

        /** 制造材料列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<IngredientsItemReferencesDTO> itemReferences;

        /**
         * 记录制造材料列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<IngredientsItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 制造材料列表的物品类型路径；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ItemType")
        private String itemType;

        /**
         * 记录制造材料列表的物品类型路径是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的物品类型路径，保留协议中的显式空值。
         */
        @JsonSetter("ItemType")
        public void setItemType(String value) {
            this.itemType = value;
            markPresent("ItemType");
        }

        /** 制造材料列表的物品类型中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemTypeNameZh")
        private String itemTypeNameZh;

        /**
         * 记录制造材料列表的物品类型中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的物品类型中文名称，保留协议中的显式空值。
         */
        @JsonSetter("itemTypeNameZh")
        public void setItemTypeNameZh(String value) {
            this.itemTypeNameZh = value;
            markPresent("itemTypeNameZh");
        }

        /** 制造材料列表的产品分类；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ProductCategory")
        private String productCategory;

        /**
         * 记录制造材料列表的产品分类是否出现，同时区分未提供与显式空值。
         *
         * @param value 制造材料列表的产品分类，保留协议中的显式空值。
         */
        @JsonSetter("ProductCategory")
        public void setProductCategory(String value) {
            this.productCategory = value;
            markPresent("ProductCategory");
        }

    }

    /** 官方嵌套对象 ItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ItemReferencesDTO extends OfficialObjectDTO {
        /** 物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 SecretIngredientsItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class SecretIngredientsItemReferencesDTO extends OfficialObjectDTO {
        /** 隐藏材料列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录隐藏材料列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏材料列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 隐藏材料列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录隐藏材料列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏材料列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 隐藏材料列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录隐藏材料列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏材料列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 隐藏材料列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录隐藏材料列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏材料列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 隐藏材料列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录隐藏材料列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏材料列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 隐藏材料列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录隐藏材料列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏材料列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 隐藏材料列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录隐藏材料列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏材料列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 隐藏材料列表的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录隐藏材料列表的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏材料列表的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 隐藏材料列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录隐藏材料列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏材料列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 隐藏材料列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录隐藏材料列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏材料列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 SecretIngredientsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class SecretIngredientsDTO extends OfficialObjectDTO {
        /** 隐藏材料列表的物品数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ItemCount")
        private BigDecimal itemCount;

        /**
         * 记录隐藏材料列表的物品数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏材料列表的物品数量，保留协议中的显式空值。
         */
        @JsonSetter("ItemCount")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("ItemCount");
        }

        /** 隐藏材料列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<SecretIngredientsItemReferencesDTO> itemReferences;

        /**
         * 记录隐藏材料列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏材料列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<SecretIngredientsItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 隐藏材料列表的物品类型路径；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ItemType")
        private String itemType;

        /**
         * 记录隐藏材料列表的物品类型路径是否出现，同时区分未提供与显式空值。
         *
         * @param value 隐藏材料列表的物品类型路径，保留协议中的显式空值。
         */
        @JsonSetter("ItemType")
        public void setItemType(String value) {
            this.itemType = value;
            markPresent("ItemType");
        }

    }

}
