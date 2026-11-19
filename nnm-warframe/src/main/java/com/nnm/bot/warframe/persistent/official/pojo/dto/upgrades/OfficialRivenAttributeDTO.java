package com.nnm.bot.warframe.persistent.official.pojo.dto.upgrades;

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

/** RIVEN_ATTRIBUTE 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialRivenAttributeDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 定义解析状态；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("definitionResolution")
    private String definitionResolution;

    /**
     * 记录定义解析状态是否出现，同时区分未提供与显式空值。
     *
     * @param value 定义解析状态，保留协议中的显式空值。
     */
    @JsonSetter("definitionResolution")
    public void setDefinitionResolution(String value) {
        this.definitionResolution = value;
        markPresent("definitionResolution");
    }

    /** 效果；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("effect")
    private String effect;

    /**
     * 记录效果是否出现，同时区分未提供与显式空值。
     *
     * @param value 效果，保留协议中的显式空值。
     */
    @JsonSetter("effect")
    public void setEffect(String value) {
        this.effect = value;
        markPresent("effect");
    }

    /** 中文效果描述；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("effectZh")
    private String effectZh;

    /**
     * 记录中文效果描述是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文效果描述，保留协议中的显式空值。
     */
    @JsonSetter("effectZh")
    public void setEffectZh(String value) {
        this.effectZh = value;
        markPresent("effectZh");
    }

    /** 专属兼容组列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("exclusiveTo")
    private List<String> exclusiveTo;

    /**
     * 记录专属兼容组列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 专属兼容组列表，保留协议中的显式空值。
     */
    @JsonSetter("exclusiveTo")
    public void setExclusiveTo(List<String> value) {
        this.exclusiveTo = value;
        markPresent("exclusiveTo");
    }

    /** 游戏引用；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("gameRef")
    private String gameRef;

    /**
     * 记录游戏引用是否出现，同时区分未提供与显式空值。
     *
     * @param value 游戏引用，保留协议中的显式空值。
     */
    @JsonSetter("gameRef")
    public void setGameRef(String value) {
        this.gameRef = value;
        markPresent("gameRef");
    }

    /** 组；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("group")
    private String group;

    /**
     * 记录组是否出现，同时区分未提供与显式空值。
     *
     * @param value 组，保留协议中的显式空值。
     */
    @JsonSetter("group")
    public void setGroup(String value) {
        this.group = value;
        markPresent("group");
    }

    /** 图标说明；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("iconReason")
    private String iconReason;

    /**
     * 记录图标说明是否出现，同时区分未提供与显式空值。
     *
     * @param value 图标说明，保留协议中的显式空值。
     */
    @JsonSetter("iconReason")
    public void setIconReason(String value) {
        this.iconReason = value;
        markPresent("iconReason");
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

    /** 标识；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("id")
    private String id;

    /**
     * 记录标识是否出现，同时区分未提供与显式空值。
     *
     * @param value 标识，保留协议中的显式空值。
     */
    @JsonSetter("id")
    public void setId(String value) {
        this.id = value;
        markPresent("id");
    }

    /** 标识列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("ids")
    private BigDecimal ids;

    /**
     * 记录标识列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 标识列表，保留协议中的显式空值。
     */
    @JsonSetter("ids")
    public void setIds(BigDecimal value) {
        this.ids = value;
        markPresent("ids");
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

    /** 是否仅为负面属性；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("negative_only")
    private Boolean negativeOnly;

    /**
     * 记录是否仅为负面属性是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否仅为负面属性，保留协议中的显式空值。
     */
    @JsonSetter("negative_only")
    public void setNegativeOnly(Boolean value) {
        this.negativeOnly = value;
        markPresent("negative_only");
    }

    /** 是否仅为负面属性；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("negativeOnly")
    private Boolean negativeOnly_dc2c5e2a;

    /**
     * 记录是否仅为负面属性是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否仅为负面属性，保留协议中的显式空值。
     */
    @JsonSetter("negativeOnly")
    public void setNegativeOnly_dc2c5e2a(Boolean value) {
        this.negativeOnly_dc2c5e2a = value;
        markPresent("negativeOnly");
    }

    /** 官方强化定义条目列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("officialEntries")
    private List<OfficialEntriesDTO> officialEntries;

    /**
     * 记录官方强化定义条目列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 官方强化定义条目列表，保留协议中的显式空值。
     */
    @JsonSetter("officialEntries")
    public void setOfficialEntries(List<OfficialEntriesDTO> value) {
        this.officialEntries = value;
        markPresent("officialEntries");
    }

    /** 正向数值是否代表负面效果；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("positive_is_negative")
    private Boolean positiveIsNegative;

    /**
     * 记录正向数值是否代表负面效果是否出现，同时区分未提供与显式空值。
     *
     * @param value 正向数值是否代表负面效果，保留协议中的显式空值。
     */
    @JsonSetter("positive_is_negative")
    public void setPositiveIsNegative(Boolean value) {
        this.positiveIsNegative = value;
        markPresent("positive_is_negative");
    }

    /** 正向数值是否代表负面效果；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("positiveIsNegative")
    private Boolean positiveIsNegative_50714505;

    /**
     * 记录正向数值是否代表负面效果是否出现，同时区分未提供与显式空值。
     *
     * @param value 正向数值是否代表负面效果，保留协议中的显式空值。
     */
    @JsonSetter("positiveIsNegative")
    public void setPositiveIsNegative_50714505(Boolean value) {
        this.positiveIsNegative_50714505 = value;
        markPresent("positiveIsNegative");
    }

    /** 是否仅为正面属性；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("positiveOnly")
    private Boolean positiveOnly;

    /**
     * 记录是否仅为正面属性是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否仅为正面属性，保留协议中的显式空值。
     */
    @JsonSetter("positiveOnly")
    public void setPositiveOnly(Boolean value) {
        this.positiveOnly = value;
        markPresent("positiveOnly");
    }

    /** 前缀；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("prefix")
    private String prefix;

    /**
     * 记录前缀是否出现，同时区分未提供与显式空值。
     *
     * @param value 前缀，保留协议中的显式空值。
     */
    @JsonSetter("prefix")
    public void setPrefix(String value) {
        this.prefix = value;
        markPresent("prefix");
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

    /** 是否仅用于搜索；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("search_only")
    private Boolean searchOnly;

    /**
     * 记录是否仅用于搜索是否出现，同时区分未提供与显式空值。
     *
     * @param value 是否仅用于搜索，保留协议中的显式空值。
     */
    @JsonSetter("search_only")
    public void setSearchOnly(Boolean value) {
        this.searchOnly = value;
        markPresent("search_only");
    }

    /** 网址标识；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("slug")
    private String slug;

    /**
     * 记录网址标识是否出现，同时区分未提供与显式空值。
     *
     * @param value 网址标识，保留协议中的显式空值。
     */
    @JsonSetter("slug")
    public void setSlug(String value) {
        this.slug = value;
        markPresent("slug");
    }

    /** 后缀；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("suffix")
    private String suffix;

    /**
     * 记录后缀是否出现，同时区分未提供与显式空值。
     *
     * @param value 后缀，保留协议中的显式空值。
     */
    @JsonSetter("suffix")
    public void setSuffix(String value) {
        this.suffix = value;
        markPresent("suffix");
    }

    /** 中文文本模板；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("templatesZh")
    private List<String> templatesZh;

    /**
     * 记录中文文本模板是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文文本模板，保留协议中的显式空值。
     */
    @JsonSetter("templatesZh")
    public void setTemplatesZh(List<String> value) {
        this.templatesZh = value;
        markPresent("templatesZh");
    }

    /** 单位；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("unit")
    private String unit;

    /**
     * 记录单位是否出现，同时区分未提供与显式空值。
     *
     * @param value 单位，保留协议中的显式空值。
     */
    @JsonSetter("unit")
    public void setUnit(String value) {
        this.unit = value;
        markPresent("unit");
    }

    /** 单位列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("units")
    private String units;

    /**
     * 记录单位列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 单位列表，保留协议中的显式空值。
     */
    @JsonSetter("units")
    public void setUnits(String value) {
        this.units = value;
        markPresent("units");
    }

    /** 网址标识；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("url_name")
    private String urlName;

    /**
     * 记录网址标识是否出现，同时区分未提供与显式空值。
     *
     * @param value 网址标识，保留协议中的显式空值。
     */
    @JsonSetter("url_name")
    public void setUrlName(String value) {
        this.urlName = value;
        markPresent("url_name");
    }

    /** 数值含义；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("valueSemantics")
    private String valueSemantics;

    /**
     * 记录数值含义是否出现，同时区分未提供与显式空值。
     *
     * @param value 数值含义，保留协议中的显式空值。
     */
    @JsonSetter("valueSemantics")
    public void setValueSemantics(String value) {
        this.valueSemantics = value;
        markPresent("valueSemantics");
    }

    /** 中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("zhName")
    private String zhName;

    /**
     * 记录中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文名称，保留协议中的显式空值。
     */
    @JsonSetter("zhName")
    public void setZhName(String value) {
        this.zhName = value;
        markPresent("zhName");
    }

    /**
     * 提取本记录的常用检索字段，不携带 JPA 受管状态。
     *
     * @return 名称、分类和说明等检索摘要；上游缺省字段保持为空。
     */
    @Override
    public OfficialRecordHeaderDTO header() {
        return new OfficialRecordHeaderDTO(OfficialInputText.first(nameEn, name), OfficialInputText.first(zhName), null, null, null);
    }

    /**
     * 读取上游显式业务键，不使用数组位置生成身份。
     *
     * @return 上游提供的首个稳定业务键；未提供时为空。
     */
    @Override
    public String stableKey() {
        return OfficialInputText.first(recordKey, id, slug);
    }

    /** 官方嵌套对象 OfficialEntriesItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class OfficialEntriesItemReferencesDTO extends OfficialObjectDTO {
        /** 官方强化定义条目列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录官方强化定义条目列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 官方强化定义条目列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录官方强化定义条目列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 官方强化定义条目列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录官方强化定义条目列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 官方强化定义条目列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录官方强化定义条目列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 官方强化定义条目列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录官方强化定义条目列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 官方强化定义条目列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录官方强化定义条目列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 官方强化定义条目列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录官方强化定义条目列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 官方强化定义条目列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录官方强化定义条目列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 官方强化定义条目列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录官方强化定义条目列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 OfficialEntriesUpgradeValuesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class OfficialEntriesUpgradeValuesDTO extends OfficialObjectDTO {
        /** 官方强化定义条目列表的强化数值列表的本地化文本标签；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("locTag")
        private String locTag;

        /**
         * 记录官方强化定义条目列表的强化数值列表的本地化文本标签是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的强化数值列表的本地化文本标签，保留协议中的显式空值。
         */
        @JsonSetter("locTag")
        public void setLocTag(String value) {
            this.locTag = value;
            markPresent("locTag");
        }

        /** 官方强化定义条目列表的强化数值列表的中文本地化文本；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("locTagZh")
        private String locTagZh;

        /**
         * 记录官方强化定义条目列表的强化数值列表的中文本地化文本是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的强化数值列表的中文本地化文本，保留协议中的显式空值。
         */
        @JsonSetter("locTagZh")
        public void setLocTagZh(String value) {
            this.locTagZh = value;
            markPresent("locTagZh");
        }

        /** 官方强化定义条目列表的强化数值列表的是否反转数值符号；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("reverseValueSymbol")
        private Boolean reverseValueSymbol;

        /**
         * 记录官方强化定义条目列表的强化数值列表的是否反转数值符号是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的强化数值列表的是否反转数值符号，保留协议中的显式空值。
         */
        @JsonSetter("reverseValueSymbol")
        public void setReverseValueSymbol(Boolean value) {
            this.reverseValueSymbol = value;
            markPresent("reverseValueSymbol");
        }

        /** 官方强化定义条目列表的强化数值列表的数值；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("value")
        private BigDecimal value;

        /**
         * 记录官方强化定义条目列表的强化数值列表的数值是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的强化数值列表的数值，保留协议中的显式空值。
         */
        @JsonSetter("value")
        public void setValue(BigDecimal value) {
            this.value = value;
            markPresent("value");
        }

    }

    /** 官方嵌套对象 OfficialEntriesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class OfficialEntriesDTO extends OfficialObjectDTO {
        /** 官方强化定义条目列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<OfficialEntriesItemReferencesDTO> itemReferences;

        /**
         * 记录官方强化定义条目列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<OfficialEntriesItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 官方强化定义条目列表的参数含义；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("parameterSemantics")
        private String parameterSemantics;

        /**
         * 记录官方强化定义条目列表的参数含义是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的参数含义，保留协议中的显式空值。
         */
        @JsonSetter("parameterSemantics")
        public void setParameterSemantics(String value) {
            this.parameterSemantics = value;
            markPresent("parameterSemantics");
        }

        /** 官方强化定义条目列表的前缀标签；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("prefixTag")
        private String prefixTag;

        /**
         * 记录官方强化定义条目列表的前缀标签是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的前缀标签，保留协议中的显式空值。
         */
        @JsonSetter("prefixTag")
        public void setPrefixTag(String value) {
            this.prefixTag = value;
            markPresent("prefixTag");
        }

        /** 官方强化定义条目列表的裂罅名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rivenName")
        private String rivenName;

        /**
         * 记录官方强化定义条目列表的裂罅名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的裂罅名称，保留协议中的显式空值。
         */
        @JsonSetter("rivenName")
        public void setRivenName(String value) {
            this.rivenName = value;
            markPresent("rivenName");
        }

        /** 官方强化定义条目列表的裂罅中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rivenNameZh")
        private String rivenNameZh;

        /**
         * 记录官方强化定义条目列表的裂罅中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的裂罅中文名称，保留协议中的显式空值。
         */
        @JsonSetter("rivenNameZh")
        public void setRivenNameZh(String value) {
            this.rivenNameZh = value;
            markPresent("rivenNameZh");
        }

        /** 官方强化定义条目列表的裂罅唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rivenUniqueName")
        private String rivenUniqueName;

        /**
         * 记录官方强化定义条目列表的裂罅唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的裂罅唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("rivenUniqueName")
        public void setRivenUniqueName(String value) {
            this.rivenUniqueName = value;
            markPresent("rivenUniqueName");
        }

        /** 官方强化定义条目列表的后缀标签；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("suffixTag")
        private String suffixTag;

        /**
         * 记录官方强化定义条目列表的后缀标签是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的后缀标签，保留协议中的显式空值。
         */
        @JsonSetter("suffixTag")
        public void setSuffixTag(String value) {
            this.suffixTag = value;
            markPresent("suffixTag");
        }

        /** 官方强化定义条目列表的标签；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("tag")
        private String tag;

        /**
         * 记录官方强化定义条目列表的标签是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的标签，保留协议中的显式空值。
         */
        @JsonSetter("tag")
        public void setTag(String value) {
            this.tag = value;
            markPresent("tag");
        }

        /** 官方强化定义条目列表的强化数值列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("upgradeValues")
        private List<OfficialEntriesUpgradeValuesDTO> upgradeValues;

        /**
         * 记录官方强化定义条目列表的强化数值列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 官方强化定义条目列表的强化数值列表，保留协议中的显式空值。
         */
        @JsonSetter("upgradeValues")
        public void setUpgradeValues(List<OfficialEntriesUpgradeValuesDTO> value) {
            this.upgradeValues = value;
            markPresent("upgradeValues");
        }

    }

}
