package com.nnm.bot.warframe.persistent.official.pojo.dto.world;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

/** VENDOR 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialVendorDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 是否为动态商人；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("isDynamic")
    private Boolean isDynamic;

    /**
     * 记录是否为动态商人是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否为动态商人，保留协议中的显式空值。
     */
    @JsonSetter("isDynamic")
    public void setIsDynamic(Boolean value) {
        this.isDynamic = value;
        markPresent("isDynamic");
    }

    /** 每个周期是否仅使用一个货架；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("isOneBinPerCycle")
    private Boolean isOneBinPerCycle;

    /**
     * 记录每个周期是否仅使用一个货架是否出现，同时区分未提供与显式空值。
     *
     * @param value 每个周期是否仅使用一个货架，保留协议中的显式空值。
     */
    @JsonSetter("isOneBinPerCycle")
    public void setIsOneBinPerCycle(Boolean value) {
        this.isOneBinPerCycle = value;
        markPresent("isOneBinPerCycle");
    }

    /** 物品列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("items")
    private List<ItemsDTO> items;

    /**
     * 记录物品列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 物品列表，保留协议中的显式空值。
     */
    @JsonSetter("items")
    public void setItems(List<ItemsDTO> value) {
        this.items = value;
        markPresent("items");
    }

    /** 商品数量的最大值；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("numItems__maxValue")
    private BigDecimal numItemsMaxValue;

    /**
     * 记录商品数量的最大值是否出现，同时区分未提供与显式空值。
     *
     * @param value 商品数量的最大值，保留协议中的显式空值。
     */
    @JsonSetter("numItems__maxValue")
    public void setNumItemsMaxValue(BigDecimal value) {
        this.numItemsMaxValue = value;
        markPresent("numItems__maxValue");
    }

    /** 商品数量的最小值；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("numItems__minValue")
    private BigDecimal numItemsMinValue;

    /**
     * 记录商品数量的最小值是否出现，同时区分未提供与显式空值。
     *
     * @param value 商品数量的最小值，保留协议中的显式空值。
     */
    @JsonSetter("numItems__minValue")
    public void setNumItemsMinValue(BigDecimal value) {
        this.numItemsMinValue = value;
        markPresent("numItems__minValue");
    }

    /** 每个货架的商品数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("numItemsPerBin")
    private List<BigDecimal> numItemsPerBin;

    /**
     * 记录每个货架的商品数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 每个货架的商品数量，保留协议中的显式空值。
     */
    @JsonSetter("numItemsPerBin")
    public void setNumItemsPerBin(List<BigDecimal> value) {
        this.numItemsPerBin = value;
        markPresent("numItemsPerBin");
    }

    /** 各货架随机价格列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("randomItemPricesPerBin")
    private List<List<RandomItemPricesPerBinDTO>> randomItemPricesPerBin;

    /**
     * 记录各货架随机价格列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 各货架随机价格列表，保留协议中的显式空值。
     */
    @JsonSetter("randomItemPricesPerBin")
    public void setRandomItemPricesPerBin(List<List<RandomItemPricesPerBinDTO>> value) {
        this.randomItemPricesPerBin = value;
        markPresent("randomItemPricesPerBin");
    }

    /** 随机种子类型；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("randomSeedType")
    private String randomSeedType;

    /**
     * 记录随机种子类型是否出现，同时区分未提供与显式空值。
     *
     * @param value 随机种子类型，保留协议中的显式空值。
     */
    @JsonSetter("randomSeedType")
    public void setRandomSeedType(String value) {
        this.randomSeedType = value;
        markPresent("randomSeedType");
    }

    /** 所需目标标签；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("requiredGoalTag")
    private String requiredGoalTag;

    /**
     * 记录所需目标标签是否出现，同时区分未提供与显式空值。
     *
     * @param value 所需目标标签，保留协议中的显式空值。
     */
    @JsonSetter("requiredGoalTag")
    public void setRequiredGoalTag(String value) {
        this.requiredGoalTag = value;
        markPresent("requiredGoalTag");
    }

    /**
     * 提取本记录的常用检索字段，不携带 JPA 受管状态。
     *
     * @return 名称、分类和说明等检索摘要；上游缺省字段保持为空。
     */
    @Override
    public OfficialRecordHeaderDTO header() {
        return new OfficialRecordHeaderDTO(null, null, null, null, null);
    }

    /**
     * 读取上游显式业务键，不使用数组位置生成身份。
     *
     * @return 上游提供的首个稳定业务键；未提供时为空。
     */
    @Override
    public String stableKey() {
        return null;
    }

    /** 官方嵌套对象 ItemsItemPricesItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ItemsItemPricesItemReferencesDTO extends OfficialObjectDTO {
        /** 物品列表的物品价格列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录物品列表的物品价格列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品价格列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 物品列表的物品价格列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录物品列表的物品价格列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品价格列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 物品列表的物品价格列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录物品列表的物品价格列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品价格列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 物品列表的物品价格列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录物品列表的物品价格列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品价格列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 物品列表的物品价格列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录物品列表的物品价格列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品价格列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 物品列表的物品价格列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录物品列表的物品价格列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品价格列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 物品列表的物品价格列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录物品列表的物品价格列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品价格列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 物品列表的物品价格列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录物品列表的物品价格列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品价格列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 物品列表的物品价格列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录物品列表的物品价格列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品价格列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 ItemsItemPricesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ItemsItemPricesDTO extends OfficialObjectDTO {
        /** 物品列表的物品价格列表的物品数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ItemCount")
        private BigDecimal itemCount;

        /**
         * 记录物品列表的物品价格列表的物品数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品价格列表的物品数量，保留协议中的显式空值。
         */
        @JsonSetter("ItemCount")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("ItemCount");
        }

        /** 物品列表的物品价格列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<ItemsItemPricesItemReferencesDTO> itemReferences;

        /**
         * 记录物品列表的物品价格列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品价格列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<ItemsItemPricesItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 物品列表的物品价格列表的物品类型路径；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ItemType")
        private String itemType;

        /**
         * 记录物品列表的物品价格列表的物品类型路径是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品价格列表的物品类型路径，保留协议中的显式空值。
         */
        @JsonSetter("ItemType")
        public void setItemType(String value) {
            this.itemType = value;
            markPresent("ItemType");
        }

    }

    /** 官方嵌套对象 ItemsItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ItemsItemReferencesDTO extends OfficialObjectDTO {
        /** 物品列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录物品列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 物品列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录物品列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 物品列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录物品列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 物品列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录物品列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 物品列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录物品列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 物品列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录物品列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 物品列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录物品列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 物品列表的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录物品列表的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 物品列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录物品列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 物品列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录物品列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 ItemsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ItemsDTO extends OfficialObjectDTO {
        /** 物品列表的是否始终出售；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("alwaysOffered")
        private Boolean alwaysOffered;

        /**
         * 记录物品列表的是否始终出售是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的是否始终出售，保留协议中的显式空值。
         */
        @JsonSetter("alwaysOffered")
        public void setAlwaysOffered(Boolean value) {
            this.alwaysOffered = value;
            markPresent("alwaysOffered");
        }

        /** 物品列表的槽位；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("bin")
        private BigDecimal bin;

        /**
         * 记录物品列表的槽位是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的槽位，保留协议中的显式空值。
         */
        @JsonSetter("bin")
        public void setBin(BigDecimal value) {
            this.bin = value;
            markPresent("bin");
        }

        /** 物品列表的现金；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("credits")
        private BigDecimal credits;

        /**
         * 记录物品列表的现金是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的现金，保留协议中的显式空值。
         */
        @JsonSetter("credits")
        public void setCredits(BigDecimal value) {
            this.credits = value;
            markPresent("credits");
        }

        /** 物品列表的现金的最大值；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("credits__maxValue")
        private BigDecimal creditsMaxValue;

        /**
         * 记录物品列表的现金的最大值是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的现金的最大值，保留协议中的显式空值。
         */
        @JsonSetter("credits__maxValue")
        public void setCreditsMaxValue(BigDecimal value) {
            this.creditsMaxValue = value;
            markPresent("credits__maxValue");
        }

        /** 物品列表的现金的最小值；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("credits__minValue")
        private BigDecimal creditsMinValue;

        /**
         * 记录物品列表的现金的最小值是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的现金的最小值，保留协议中的显式空值。
         */
        @JsonSetter("credits__minValue")
        public void setCreditsMinValue(BigDecimal value) {
            this.creditsMinValue = value;
            markPresent("credits__minValue");
        }

        /** 物品列表的现金的步长；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("credits__step")
        private BigDecimal creditsStep;

        /**
         * 记录物品列表的现金的步长是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的现金的步长，保留协议中的显式空值。
         */
        @JsonSetter("credits__step")
        public void setCreditsStep(BigDecimal value) {
            this.creditsStep = value;
            markPresent("credits__step");
        }

        /** 物品列表的重复商品数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("duplicates")
        private BigDecimal duplicates;

        /**
         * 记录物品列表的重复商品数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的重复商品数量，保留协议中的显式空值。
         */
        @JsonSetter("duplicates")
        public void setDuplicates(BigDecimal value) {
            this.duplicates = value;
            markPresent("duplicates");
        }

        /** 物品列表的持续小时数；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("durationHours")
        private BigDecimal durationHours;

        /**
         * 记录物品列表的持续小时数是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的持续小时数，保留协议中的显式空值。
         */
        @JsonSetter("durationHours")
        public void setDurationHours(BigDecimal value) {
            this.durationHours = value;
            markPresent("durationHours");
        }

        /** 物品列表的持续小时数的最大值；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("durationHours__maxValue")
        private BigDecimal durationHoursMaxValue;

        /**
         * 记录物品列表的持续小时数的最大值是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的持续小时数的最大值，保留协议中的显式空值。
         */
        @JsonSetter("durationHours__maxValue")
        public void setDurationHoursMaxValue(BigDecimal value) {
            this.durationHoursMaxValue = value;
            markPresent("durationHours__maxValue");
        }

        /** 物品列表的持续小时数的最小值；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("durationHours__minValue")
        private BigDecimal durationHoursMinValue;

        /**
         * 记录物品列表的持续小时数的最小值是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的持续小时数的最小值，保留协议中的显式空值。
         */
        @JsonSetter("durationHours__minValue")
        public void setDurationHoursMinValue(BigDecimal value) {
            this.durationHoursMinValue = value;
            markPresent("durationHours__minValue");
        }

        /** 物品列表的专精经验消耗的消耗；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("focusXpCost__cost")
        private BigDecimal focusXpCostCost;

        /**
         * 记录物品列表的专精经验消耗的消耗是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的专精经验消耗的消耗，保留协议中的显式空值。
         */
        @JsonSetter("focusXpCost__cost")
        public void setFocusXpCostCost(BigDecimal value) {
            this.focusXpCostCost = value;
            markPresent("focusXpCost__cost");
        }

        /** 物品列表的专精经验消耗的极性；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("focusXpCost__polarity")
        private String focusXpCostPolarity;

        /**
         * 记录物品列表的专精经验消耗的极性是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的专精经验消耗的极性，保留协议中的显式空值。
         */
        @JsonSetter("focusXpCost__polarity")
        public void setFocusXpCostPolarity(String value) {
            this.focusXpCostPolarity = value;
            markPresent("focusXpCost__polarity");
        }

        /** 物品列表的物品价格列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemPrices")
        private List<ItemsItemPricesDTO> itemPrices;

        /**
         * 记录物品列表的物品价格列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品价格列表，保留协议中的显式空值。
         */
        @JsonSetter("itemPrices")
        public void setItemPrices(List<ItemsItemPricesDTO> value) {
            this.itemPrices = value;
            markPresent("itemPrices");
        }

        /** 物品列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<ItemsItemReferencesDTO> itemReferences;

        /**
         * 记录物品列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<ItemsItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 物品列表的随机定价项数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("numRandomItemPrices")
        private BigDecimal numRandomItemPrices;

        /**
         * 记录物品列表的随机定价项数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的随机定价项数量，保留协议中的显式空值。
         */
        @JsonSetter("numRandomItemPrices")
        public void setNumRandomItemPrices(BigDecimal value) {
            this.numRandomItemPrices = value;
            markPresent("numRandomItemPrices");
        }

        /** 物品列表的铂金数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("platinum")
        private BigDecimal platinum;

        /**
         * 记录物品列表的铂金数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的铂金数量，保留协议中的显式空值。
         */
        @JsonSetter("platinum")
        public void setPlatinum(BigDecimal value) {
            this.platinum = value;
            markPresent("platinum");
        }

        /** 物品列表的铂金数量的最大值；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("platinum__maxValue")
        private BigDecimal platinumMaxValue;

        /**
         * 记录物品列表的铂金数量的最大值是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的铂金数量的最大值，保留协议中的显式空值。
         */
        @JsonSetter("platinum__maxValue")
        public void setPlatinumMaxValue(BigDecimal value) {
            this.platinumMaxValue = value;
            markPresent("platinum__maxValue");
        }

        /** 物品列表的铂金数量的最小值；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("platinum__minValue")
        private BigDecimal platinumMinValue;

        /**
         * 记录物品列表的铂金数量的最小值是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的铂金数量的最小值，保留协议中的显式空值。
         */
        @JsonSetter("platinum__minValue")
        public void setPlatinumMinValue(BigDecimal value) {
            this.platinumMinValue = value;
            markPresent("platinum__minValue");
        }

        /** 物品列表的概率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("probability")
        private BigDecimal probability;

        /**
         * 记录物品列表的概率是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的概率，保留协议中的显式空值。
         */
        @JsonSetter("probability")
        public void setProbability(BigDecimal value) {
            this.probability = value;
            markPresent("probability");
        }

        /** 物品列表的购买数量上限；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("purchaseLimit")
        private BigDecimal purchaseLimit;

        /**
         * 记录物品列表的购买数量上限是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的购买数量上限，保留协议中的显式空值。
         */
        @JsonSetter("purchaseLimit")
        public void setPurchaseLimit(BigDecimal value) {
            this.purchaseLimit = value;
            markPresent("purchaseLimit");
        }

        /** 物品列表的数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("quantity")
        private BigDecimal quantity;

        /**
         * 记录物品列表的数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的数量，保留协议中的显式空值。
         */
        @JsonSetter("quantity")
        public void setQuantity(BigDecimal value) {
            this.quantity = value;
            markPresent("quantity");
        }

        /** 物品列表的是否每周轮换；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rotatedWeekly")
        private Boolean rotatedWeekly;

        /**
         * 记录物品列表的是否每周轮换是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的是否每周轮换，保留协议中的显式空值。
         */
        @JsonSetter("rotatedWeekly")
        public void setRotatedWeekly(Boolean value) {
            this.rotatedWeekly = value;
            markPresent("rotatedWeekly");
        }

        /** 物品列表的商店物品；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("storeItem")
        private String storeItem;

        /**
         * 记录物品列表的商店物品是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的商店物品，保留协议中的显式空值。
         */
        @JsonSetter("storeItem")
        public void setStoreItem(String value) {
            this.storeItem = value;
            markPresent("storeItem");
        }

        /** 物品列表的集团的每级负声望等级的增加量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("syndicate__increasePerNegativeRank")
        private BigDecimal syndicateIncreasePerNegativeRank;

        /**
         * 记录物品列表的集团的每级负声望等级的增加量是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的集团的每级负声望等级的增加量，保留协议中的显式空值。
         */
        @JsonSetter("syndicate__increasePerNegativeRank")
        public void setSyndicateIncreasePerNegativeRank(BigDecimal value) {
            this.syndicateIncreasePerNegativeRank = value;
            markPresent("syndicate__increasePerNegativeRank");
        }

        /** 物品列表的集团的最低等级；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("syndicate__minRank")
        private BigDecimal syndicateMinRank;

        /**
         * 记录物品列表的集团的最低等级是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的集团的最低等级，保留协议中的显式空值。
         */
        @JsonSetter("syndicate__minRank")
        public void setSyndicateMinRank(BigDecimal value) {
            this.syndicateMinRank = value;
            markPresent("syndicate__minRank");
        }

        /** 物品列表的集团的每级正声望等级的减少量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("syndicate__reductionPerPositiveRank")
        private BigDecimal syndicateReductionPerPositiveRank;

        /**
         * 记录物品列表的集团的每级正声望等级的减少量是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的集团的每级正声望等级的减少量，保留协议中的显式空值。
         */
        @JsonSetter("syndicate__reductionPerPositiveRank")
        public void setSyndicateReductionPerPositiveRank(BigDecimal value) {
            this.syndicateReductionPerPositiveRank = value;
            markPresent("syndicate__reductionPerPositiveRank");
        }

        /** 物品列表的集团的声望消耗；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("syndicate__standingCost")
        private BigDecimal syndicateStandingCost;

        /**
         * 记录物品列表的集团的声望消耗是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的集团的声望消耗，保留协议中的显式空值。
         */
        @JsonSetter("syndicate__standingCost")
        public void setSyndicateStandingCost(BigDecimal value) {
            this.syndicateStandingCost = value;
            markPresent("syndicate__standingCost");
        }

        /** 物品列表的集团的标签；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("syndicate__tag")
        private String syndicateTag;

        /**
         * 记录物品列表的集团的标签是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品列表的集团的标签，保留协议中的显式空值。
         */
        @JsonSetter("syndicate__tag")
        public void setSyndicateTag(String value) {
            this.syndicateTag = value;
            markPresent("syndicate__tag");
        }

    }

    /** 官方嵌套对象 RandomItemPricesPerBinItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class RandomItemPricesPerBinItemReferencesDTO extends OfficialObjectDTO {
        /** 各货架随机价格列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录各货架随机价格列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 各货架随机价格列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 各货架随机价格列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录各货架随机价格列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 各货架随机价格列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 各货架随机价格列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录各货架随机价格列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 各货架随机价格列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 各货架随机价格列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录各货架随机价格列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 各货架随机价格列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 各货架随机价格列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录各货架随机价格列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 各货架随机价格列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 各货架随机价格列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录各货架随机价格列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 各货架随机价格列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 各货架随机价格列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录各货架随机价格列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 各货架随机价格列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 各货架随机价格列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录各货架随机价格列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 各货架随机价格列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 各货架随机价格列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录各货架随机价格列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 各货架随机价格列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 RandomItemPricesPerBinDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class RandomItemPricesPerBinDTO extends OfficialObjectDTO {
        /** 各货架随机价格列表的数量的最大值；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("count__maxValue")
        private BigDecimal countMaxValue;

        /**
         * 记录各货架随机价格列表的数量的最大值是否出现，同时区分未提供与显式空值。
         *
         * @param value 各货架随机价格列表的数量的最大值，保留协议中的显式空值。
         */
        @JsonSetter("count__maxValue")
        public void setCountMaxValue(BigDecimal value) {
            this.countMaxValue = value;
            markPresent("count__maxValue");
        }

        /** 各货架随机价格列表的数量的最小值；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("count__minValue")
        private BigDecimal countMinValue;

        /**
         * 记录各货架随机价格列表的数量的最小值是否出现，同时区分未提供与显式空值。
         *
         * @param value 各货架随机价格列表的数量的最小值，保留协议中的显式空值。
         */
        @JsonSetter("count__minValue")
        public void setCountMinValue(BigDecimal value) {
            this.countMinValue = value;
            markPresent("count__minValue");
        }

        /** 各货架随机价格列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<RandomItemPricesPerBinItemReferencesDTO> itemReferences;

        /**
         * 记录各货架随机价格列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 各货架随机价格列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<RandomItemPricesPerBinItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 各货架随机价格列表的类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("type")
        private String type;

        /**
         * 记录各货架随机价格列表的类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 各货架随机价格列表的类型，保留协议中的显式空值。
         */
        @JsonSetter("type")
        public void setType(String value) {
            this.type = value;
            markPresent("type");
        }

    }

}
