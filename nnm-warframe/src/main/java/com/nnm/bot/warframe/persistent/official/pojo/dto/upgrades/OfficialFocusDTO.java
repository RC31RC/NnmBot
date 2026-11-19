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

/** FOCUS 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialFocusDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 基础容量消耗；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("baseDrain")
    private BigDecimal baseDrain;

    /**
     * 记录基础容量消耗是否出现，同时区分未提供与显式空值。
     *
     * @param value 基础容量消耗，保留协议中的显式空值。
     */
    @JsonSetter("baseDrain")
    public void setBaseDrain(BigDecimal value) {
        this.baseDrain = value;
        markPresent("baseDrain");
    }

    /** 基础专精点消耗；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("baseFocusPointCost")
    private BigDecimal baseFocusPointCost;

    /**
     * 记录基础专精点消耗是否出现，同时区分未提供与显式空值。
     *
     * @param value 基础专精点消耗，保留协议中的显式空值。
     */
    @JsonSetter("baseFocusPointCost")
    public void setBaseFocusPointCost(BigDecimal value) {
        this.baseFocusPointCost = value;
        markPresent("baseFocusPointCost");
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

    /** 英文描述；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("descriptionEn")
    private String descriptionEn;

    /**
     * 记录英文描述是否出现，同时区分未提供与显式空值。
     *
     * @param value 英文描述，保留协议中的显式空值。
     */
    @JsonSetter("descriptionEn")
    public void setDescriptionEn(String value) {
        this.descriptionEn = value;
        markPresent("descriptionEn");
    }

    /** 中文描述；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("descriptionZh")
    private String descriptionZh;

    /**
     * 记录中文描述是否出现，同时区分未提供与显式空值。
     *
     * @param value 中文描述，保留协议中的显式空值。
     */
    @JsonSetter("descriptionZh")
    public void setDescriptionZh(String value) {
        this.descriptionZh = value;
        markPresent("descriptionZh");
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

    /** 融合等级上限；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("fusionLimit")
    private BigDecimal fusionLimit;

    /**
     * 记录融合等级上限是否出现，同时区分未提供与显式空值。
     *
     * @param value 融合等级上限，保留协议中的显式空值。
     */
    @JsonSetter("fusionLimit")
    public void setFusionLimit(BigDecimal value) {
        this.fusionLimit = value;
        markPresent("fusionLimit");
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

    /** 名称本地化键；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("nameKey")
    private String nameKey;

    /**
     * 记录名称本地化键是否出现，同时区分未提供与显式空值。
     *
     * @param value 名称本地化键，保留协议中的显式空值。
     */
    @JsonSetter("nameKey")
    public void setNameKey(String value) {
        this.nameKey = value;
        markPresent("nameKey");
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

    /** 极性；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("polarity")
    private String polarity;

    /**
     * 记录极性是否出现，同时区分未提供与显式空值。
     *
     * @param value 极性，保留协议中的显式空值。
     */
    @JsonSetter("polarity")
    public void setPolarity(String value) {
        this.polarity = value;
        markPresent("polarity");
    }

    /** 等级配置列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("ranks")
    private List<RanksDTO> ranks;

    /**
     * 记录等级配置列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 等级配置列表，保留协议中的显式空值。
     */
    @JsonSetter("ranks")
    public void setRanks(List<RanksDTO> value) {
        this.ranks = value;
        markPresent("ranks");
    }

    /** 稀有度；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("rarity")
    private String rarity;

    /**
     * 记录稀有度是否出现，同时区分未提供与显式空值。
     *
     * @param value 稀有度，保留协议中的显式空值。
     */
    @JsonSetter("rarity")
    public void setRarity(String value) {
        this.rarity = value;
        markPresent("rarity");
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

    /** 强化类型；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("upgradeType")
    private String upgradeType;

    /**
     * 记录强化类型是否出现，同时区分未提供与显式空值。
     *
     * @param value 强化类型，保留协议中的显式空值。
     */
    @JsonSetter("upgradeType")
    public void setUpgradeType(String value) {
        this.upgradeType = value;
        markPresent("upgradeType");
    }

    /**
     * 提取本记录的常用检索字段，不携带 JPA 受管状态。
     *
     * @return 名称、分类和说明等检索摘要；上游缺省字段保持为空。
     */
    @Override
    public OfficialRecordHeaderDTO header() {
        return new OfficialRecordHeaderDTO(OfficialInputText.first(nameEn, name), OfficialInputText.first(nameZh), null, null, OfficialInputText.first(descriptionZh, descriptionEn, description));
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

    /** 官方嵌套对象 RanksStatsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class RanksStatsDTO extends OfficialObjectDTO {
        /** 等级配置列表的属性统计的文本行序号；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("lineIndex")
        private BigDecimal lineIndex;

        /**
         * 记录等级配置列表的属性统计的文本行序号是否出现，同时区分未提供与显式空值。
         *
         * @param value 等级配置列表的属性统计的文本行序号，保留协议中的显式空值。
         */
        @JsonSetter("lineIndex")
        public void setLineIndex(BigDecimal value) {
            this.lineIndex = value;
            markPresent("lineIndex");
        }

        /** 等级配置列表的属性统计的文本；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("text")
        private String text;

        /**
         * 记录等级配置列表的属性统计的文本是否出现，同时区分未提供与显式空值。
         *
         * @param value 等级配置列表的属性统计的文本，保留协议中的显式空值。
         */
        @JsonSetter("text")
        public void setText(String value) {
            this.text = value;
            markPresent("text");
        }

        /** 等级配置列表的属性统计的中文文本；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("zhText")
        private String zhText;

        /**
         * 记录等级配置列表的属性统计的中文文本是否出现，同时区分未提供与显式空值。
         *
         * @param value 等级配置列表的属性统计的中文文本，保留协议中的显式空值。
         */
        @JsonSetter("zhText")
        public void setZhText(String value) {
            this.zhText = value;
            markPresent("zhText");
        }

    }

    /** 官方嵌套对象 RanksDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class RanksDTO extends OfficialObjectDTO {
        /** 等级配置列表的等级；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rank")
        private BigDecimal rank;

        /**
         * 记录等级配置列表的等级是否出现，同时区分未提供与显式空值。
         *
         * @param value 等级配置列表的等级，保留协议中的显式空值。
         */
        @JsonSetter("rank")
        public void setRank(BigDecimal value) {
            this.rank = value;
            markPresent("rank");
        }

        /** 等级配置列表的属性统计；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("stats")
        private List<RanksStatsDTO> stats;

        /**
         * 记录等级配置列表的属性统计是否出现，同时区分未提供与显式空值。
         *
         * @param value 等级配置列表的属性统计，保留协议中的显式空值。
         */
        @JsonSetter("stats")
        public void setStats(List<RanksStatsDTO> value) {
            this.stats = value;
            markPresent("stats");
        }

    }

}
