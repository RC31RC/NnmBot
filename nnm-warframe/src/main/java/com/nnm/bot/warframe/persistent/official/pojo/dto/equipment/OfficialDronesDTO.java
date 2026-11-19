package com.nnm.bot.warframe.persistent.official.pojo.dto.equipment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialInputText;
import com.nnm.bot.warframe.persistent.official.parser.input.OfficialNullDeserializer;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRecordHeaderDTO;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

/** DRONES 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialDronesDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 货架容量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("binCapacity")
    private BigDecimal binCapacity;

    /**
     * 记录货架容量是否出现，同时区分未提供与显式空值。
     *
     * @param value 货架容量，保留协议中的显式空值。
     */
    @JsonSetter("binCapacity")
    public void setBinCapacity(BigDecimal value) {
        this.binCapacity = value;
        markPresent("binCapacity");
    }

    /** 货架数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("binCount")
    private BigDecimal binCount;

    /**
     * 记录货架数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 货架数量，保留协议中的显式空值。
     */
    @JsonSetter("binCount")
    public void setBinCount(BigDecimal value) {
        this.binCount = value;
        markPresent("binCount");
    }

    /** 容量倍率；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("capacityMultiplier")
    private List<BigDecimal> capacityMultiplier;

    /**
     * 记录容量倍率是否出现，同时区分未提供与显式空值。
     *
     * @param value 容量倍率，保留协议中的显式空值。
     */
    @JsonSetter("capacityMultiplier")
    public void setCapacityMultiplier(List<BigDecimal> value) {
        this.capacityMultiplier = value;
        markPresent("capacityMultiplier");
    }

    /** 容量倍率列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("capacityMultipliers")
    private CapacityMultipliersDTO capacityMultipliers;

    /**
     * 记录容量倍率列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 容量倍率列表，保留协议中的显式空值。
     */
    @JsonSetter("capacityMultipliers")
    public void setCapacityMultipliers(CapacityMultipliersDTO value) {
        this.capacityMultipliers = value;
        markPresent("capacityMultipliers");
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

    /** 耐久度；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("durability")
    private BigDecimal durability;

    /**
     * 记录耐久度是否出现，同时区分未提供与显式空值。
     *
     * @param value 耐久度，保留协议中的显式空值。
     */
    @JsonSetter("durability")
    public void setDurability(BigDecimal value) {
        this.durability = value;
        markPresent("durability");
    }

    /** 补充速率；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("fillRate")
    private BigDecimal fillRate;

    /**
     * 记录补充速率是否出现，同时区分未提供与显式空值。
     *
     * @param value 补充速率，保留协议中的显式空值。
     */
    @JsonSetter("fillRate")
    public void setFillRate(BigDecimal value) {
        this.fillRate = value;
        markPresent("fillRate");
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

    /** 概率列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("probabilities")
    private ProbabilitiesDTO probabilities;

    /**
     * 记录概率列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 概率列表，保留协议中的显式空值。
     */
    @JsonSetter("probabilities")
    public void setProbabilities(ProbabilitiesDTO value) {
        this.probabilities = value;
        markPresent("probabilities");
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

    /** 修复速率；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("repairRate")
    private BigDecimal repairRate;

    /**
     * 记录修复速率是否出现，同时区分未提供与显式空值。
     *
     * @param value 修复速率，保留协议中的显式空值。
     */
    @JsonSetter("repairRate")
    public void setRepairRate(BigDecimal value) {
        this.repairRate = value;
        markPresent("repairRate");
    }

    /** 专长列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("specialities")
    @JsonDeserialize(contentUsing = OfficialNullDeserializer.class)
    private List<Void> specialities;

    /**
     * 记录专长列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 专长列表，保留协议中的显式空值。
     */
    @JsonSetter("specialities")
    public void setSpecialities(List<Void> value) {
        this.specialities = value;
        markPresent("specialities");
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

    /** 官方嵌套对象 CapacityMultipliersDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class CapacityMultipliersDTO extends OfficialObjectDTO {
        /** 容量倍率列表的普通稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("COMMON")
        private BigDecimal common;

        /**
         * 记录容量倍率列表的普通稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 容量倍率列表的普通稀有度，保留协议中的显式空值。
         */
        @JsonSetter("COMMON")
        public void setCommon(BigDecimal value) {
            this.common = value;
            markPresent("COMMON");
        }

        /** 容量倍率列表的传奇稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("LEGENDARY")
        private BigDecimal legendary;

        /**
         * 记录容量倍率列表的传奇稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 容量倍率列表的传奇稀有度，保留协议中的显式空值。
         */
        @JsonSetter("LEGENDARY")
        public void setLegendary(BigDecimal value) {
            this.legendary = value;
            markPresent("LEGENDARY");
        }

        /** 容量倍率列表的稀有稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("RARE")
        private BigDecimal rare;

        /**
         * 记录容量倍率列表的稀有稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 容量倍率列表的稀有稀有度，保留协议中的显式空值。
         */
        @JsonSetter("RARE")
        public void setRare(BigDecimal value) {
            this.rare = value;
            markPresent("RARE");
        }

        /** 容量倍率列表的罕见稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("UNCOMMON")
        private BigDecimal uncommon;

        /**
         * 记录容量倍率列表的罕见稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 容量倍率列表的罕见稀有度，保留协议中的显式空值。
         */
        @JsonSetter("UNCOMMON")
        public void setUncommon(BigDecimal value) {
            this.uncommon = value;
            markPresent("UNCOMMON");
        }

    }

    /** 官方嵌套对象 ProbabilitiesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ProbabilitiesDTO extends OfficialObjectDTO {
        /** 概率列表的普通稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("COMMON")
        private BigDecimal common;

        /**
         * 记录概率列表的普通稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 概率列表的普通稀有度，保留协议中的显式空值。
         */
        @JsonSetter("COMMON")
        public void setCommon(BigDecimal value) {
            this.common = value;
            markPresent("COMMON");
        }

        /** 概率列表的传奇稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("LEGENDARY")
        private BigDecimal legendary;

        /**
         * 记录概率列表的传奇稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 概率列表的传奇稀有度，保留协议中的显式空值。
         */
        @JsonSetter("LEGENDARY")
        public void setLegendary(BigDecimal value) {
            this.legendary = value;
            markPresent("LEGENDARY");
        }

        /** 概率列表的稀有稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("RARE")
        private BigDecimal rare;

        /**
         * 记录概率列表的稀有稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 概率列表的稀有稀有度，保留协议中的显式空值。
         */
        @JsonSetter("RARE")
        public void setRare(BigDecimal value) {
            this.rare = value;
            markPresent("RARE");
        }

        /** 概率列表的罕见稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("UNCOMMON")
        private BigDecimal uncommon;

        /**
         * 记录概率列表的罕见稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 概率列表的罕见稀有度，保留协议中的显式空值。
         */
        @JsonSetter("UNCOMMON")
        public void setUncommon(BigDecimal value) {
            this.uncommon = value;
            markPresent("UNCOMMON");
        }

    }

}
