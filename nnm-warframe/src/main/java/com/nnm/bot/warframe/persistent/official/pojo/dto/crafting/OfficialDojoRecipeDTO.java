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

/** DOJO_RECIPE 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialDojoRecipeDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 容量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("capacity")
    private BigDecimal capacity;

    /**
     * 记录容量是否出现，同时区分未提供与显式空值。
     *
     * @param value 容量，保留协议中的显式空值。
     */
    @JsonSetter("capacity")
    public void setCapacity(BigDecimal value) {
        this.capacity = value;
        markPresent("capacity");
    }

    /** 容量消耗；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("capacityCost")
    private BigDecimal capacityCost;

    /**
     * 记录容量消耗是否出现，同时区分未提供与显式空值。
     *
     * @param value 容量消耗，保留协议中的显式空值。
     */
    @JsonSetter("capacityCost")
    public void setCapacityCost(BigDecimal value) {
        this.capacityCost = value;
        markPresent("capacityCost");
    }

    /** 颜色的数值；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("colour__value")
    private String colourValue;

    /**
     * 记录颜色的数值是否出现，同时区分未提供与显式空值。
     *
     * @param value 颜色的数值，保留协议中的显式空值。
     */
    @JsonSetter("colour__value")
    public void setColourValue(String value) {
        this.colourValue = value;
        markPresent("colour__value");
    }

    /** 装饰容量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("decoCapacity")
    private BigDecimal decoCapacity;

    /**
     * 记录装饰容量是否出现，同时区分未提供与显式空值。
     *
     * @param value 装饰容量，保留协议中的显式空值。
     */
    @JsonSetter("decoCapacity")
    public void setDecoCapacity(BigDecimal value) {
        this.decoCapacity = value;
        markPresent("decoCapacity");
    }

    /** 描述；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("description")
    private String description;

    /**
     * 记录描述是否出现，同时区分未提供与显式空值。
     *
     * @param value 描述，保留协议中的显式空值。
     */
    @JsonSetter("description")
    public void setDescription(String value) {
        this.description = value;
        markPresent("description");
    }

    /** 销毁所需时间；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("destructionTime")
    private BigDecimal destructionTime;

    /**
     * 记录销毁所需时间是否出现，同时区分未提供与显式空值。
     *
     * @param value 销毁所需时间，保留协议中的显式空值。
     */
    @JsonSetter("destructionTime")
    public void setDestructionTime(BigDecimal value) {
        this.destructionTime = value;
        markPresent("destructionTime");
    }

    /** 能量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("energy")
    private BigDecimal energy;

    /**
     * 记录能量是否出现，同时区分未提供与显式空值。
     *
     * @param value 能量，保留协议中的显式空值。
     */
    @JsonSetter("energy")
    public void setEnergy(BigDecimal value) {
        this.energy = value;
        markPresent("energy");
    }

    /** 氏族经验值；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("guildXpValue")
    private BigDecimal guildXpValue;

    /**
     * 记录氏族经验值是否出现，同时区分未提供与显式空值。
     *
     * @param value 氏族经验值，保留协议中的显式空值。
     */
    @JsonSetter("guildXpValue")
    public void setGuildXpValue(BigDecimal value) {
        this.guildXpValue = value;
        markPresent("guildXpValue");
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

    /** 图标；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("icon")
    private String icon;

    /**
     * 记录图标是否出现，同时区分未提供与显式空值。
     *
     * @param value 图标，保留协议中的显式空值。
     */
    @JsonSetter("icon")
    public void setIcon(String value) {
        this.icon = value;
        markPresent("icon");
    }

    /** 图标资源；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("iconAsset")
    private String iconAsset;

    /**
     * 记录图标资源是否出现，同时区分未提供与显式空值。
     *
     * @param value 图标资源，保留协议中的显式空值。
     */
    @JsonSetter("iconAsset")
    public void setIconAsset(String value) {
        this.iconAsset = value;
        markPresent("iconAsset");
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

    /** 价格；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("price")
    private BigDecimal price;

    /**
     * 记录价格是否出现，同时区分未提供与显式空值。
     *
     * @param value 价格，保留协议中的显式空值。
     */
    @JsonSetter("price")
    public void setPrice(BigDecimal value) {
        this.price = value;
        markPresent("price");
    }

    /** 复制蓝图价格；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("replicatePrice")
    private BigDecimal replicatePrice;

    /**
     * 记录复制蓝图价格是否出现，同时区分未提供与显式空值。
     *
     * @param value 复制蓝图价格，保留协议中的显式空值。
     */
    @JsonSetter("replicatePrice")
    public void setReplicatePrice(BigDecimal value) {
        this.replicatePrice = value;
        markPresent("replicatePrice");
    }

    /** 是否要求处于宝库中；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("requiredInVault")
    private Boolean requiredInVault;

    /**
     * 记录是否要求处于宝库中是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否要求处于宝库中，保留协议中的显式空值。
     */
    @JsonSetter("requiredInVault")
    public void setRequiredInVault(Boolean value) {
        this.requiredInVault = value;
        markPresent("requiredInVault");
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

    /** 加速价格；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("skipTimePrice")
    private BigDecimal skipTimePrice;

    /**
     * 记录加速价格是否出现，同时区分未提供与显式空值。
     *
     * @param value 加速价格，保留协议中的显式空值。
     */
    @JsonSetter("skipTimePrice")
    public void setSkipTimePrice(BigDecimal value) {
        this.skipTimePrice = value;
        markPresent("skipTimePrice");
    }

    /** 前置科技要求；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("techPrereq")
    private String techPrereq;

    /**
     * 记录前置科技要求是否出现，同时区分未提供与显式空值。
     *
     * @param value 前置科技要求，保留协议中的显式空值。
     */
    @JsonSetter("techPrereq")
    public void setTechPrereq(String value) {
        this.techPrereq = value;
        markPresent("techPrereq");
    }

    /** 时间；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("time")
    private BigDecimal time;

    /**
     * 记录时间是否出现，同时区分未提供与显式空值。
     *
     * @param value 时间，保留协议中的显式空值。
     */
    @JsonSetter("time")
    public void setTime(BigDecimal value) {
        this.time = value;
        markPresent("time");
    }

    /**
     * 提取本记录的常用检索字段，不携带 JPA 受管状态。
     *
     * @return 名称、分类和说明等检索摘要；上游缺省字段保持为空。
     */
    @Override
    public OfficialRecordHeaderDTO header() {
        return new OfficialRecordHeaderDTO(OfficialInputText.first(name), null, null, null, OfficialInputText.first(description));
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

        /** 物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
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

}
