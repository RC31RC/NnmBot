package com.nnm.bot.warframe.persistent.official.pojo.dto.rewards;

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
import java.util.Map;
import lombok.Getter;

/** RELIC 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialRelicDTO extends OfficialObjectDTO implements OfficialRecordDTO {
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

    /** 获取地点列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("locations")
    private List<LocationsDTO> locations;

    /**
     * 记录获取地点列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 获取地点列表，保留协议中的显式空值。
     */
    @JsonSetter("locations")
    public void setLocations(List<LocationsDTO> value) {
        this.locations = value;
        markPresent("locations");
    }

    /** 关联市场商品列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("marketListings")
    private List<MarketListingsDTO> marketListings;

    /**
     * 记录关联市场商品列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 关联市场商品列表，保留协议中的显式空值。
     */
    @JsonSetter("marketListings")
    public void setMarketListings(List<MarketListingsDTO> value) {
        this.marketListings = value;
        markPresent("marketListings");
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

    /** 数据质量统计的去重后的记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__deduplicatedRows")
    private BigDecimal qualityDeduplicatedRows;

    /**
     * 记录数据质量统计的去重后的记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的去重后的记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__deduplicatedRows")
    public void setQualityDeduplicatedRows(BigDecimal value) {
        this.qualityDeduplicatedRows = value;
        markPresent("quality__deduplicatedRows");
    }

    /** 数据质量统计的掉落表记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__dropTableRows")
    private BigDecimal qualityDropTableRows;

    /**
     * 记录数据质量统计的掉落表记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的掉落表记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__dropTableRows")
    public void setQualityDropTableRows(BigDecimal value) {
        this.qualityDropTableRows = value;
        markPresent("quality__dropTableRows");
    }

    /** 数据质量统计的推断出的来源分组数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__inferredSourceGroups")
    private BigDecimal qualityInferredSourceGroups;

    /**
     * 记录数据质量统计的推断出的来源分组数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的推断出的来源分组数，保留协议中的显式空值。
     */
    @JsonSetter("quality__inferredSourceGroups")
    public void setQualityInferredSourceGroups(BigDecimal value) {
        this.qualityInferredSourceGroups = value;
        markPresent("quality__inferredSourceGroups");
    }

    /** 数据质量统计的已匹配的掉落记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__matchedDropRows")
    private BigDecimal qualityMatchedDropRows;

    /**
     * 记录数据质量统计的已匹配的掉落记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的已匹配的掉落记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__matchedDropRows")
    public void setQualityMatchedDropRows(BigDecimal value) {
        this.qualityMatchedDropRows = value;
        markPresent("quality__matchedDropRows");
    }

    /** 数据质量统计的任务地点记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__missionLocationRows")
    private BigDecimal qualityMissionLocationRows;

    /**
     * 记录数据质量统计的任务地点记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的任务地点记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__missionLocationRows")
    public void setQualityMissionLocationRows(BigDecimal value) {
        this.qualityMissionLocationRows = value;
        markPresent("quality__missionLocationRows");
    }

    /** 数据质量统计的官方精炼状态数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__officialStateCount")
    private BigDecimal qualityOfficialStateCount;

    /**
     * 记录数据质量统计的官方精炼状态数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的官方精炼状态数量，保留协议中的显式空值。
     */
    @JsonSetter("quality__officialStateCount")
    public void setQualityOfficialStateCount(BigDecimal value) {
        this.qualityOfficialStateCount = value;
        markPresent("quality__officialStateCount");
    }

    /** 数据质量统计的遗物数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__relicCount")
    private BigDecimal qualityRelicCount;

    /**
     * 记录数据质量统计的遗物数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的遗物数量，保留协议中的显式空值。
     */
    @JsonSetter("quality__relicCount")
    public void setQualityRelicCount(BigDecimal value) {
        this.qualityRelicCount = value;
        markPresent("quality__relicCount");
    }

    /** 数据质量统计的遗物来源记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__relicSourceRows")
    private BigDecimal qualityRelicSourceRows;

    /**
     * 记录数据质量统计的遗物来源记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的遗物来源记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__relicSourceRows")
    public void setQualityRelicSourceRows(BigDecimal value) {
        this.qualityRelicSourceRows = value;
        markPresent("quality__relicSourceRows");
    }

    /** 数据质量统计的未解析的掉落记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__unresolvedDropRows")
    private BigDecimal qualityUnresolvedDropRows;

    /**
     * 记录数据质量统计的未解析的掉落记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的未解析的掉落记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__unresolvedDropRows")
    public void setQualityUnresolvedDropRows(BigDecimal value) {
        this.qualityUnresolvedDropRows = value;
        markPresent("quality__unresolvedDropRows");
    }

    /** 数据质量统计的未解析的地点记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__unresolvedLocationRows")
    private BigDecimal qualityUnresolvedLocationRows;

    /**
     * 记录数据质量统计的未解析的地点记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的未解析的地点记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__unresolvedLocationRows")
    public void setQualityUnresolvedLocationRows(BigDecimal value) {
        this.qualityUnresolvedLocationRows = value;
        markPresent("quality__unresolvedLocationRows");
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

    /** 遗物代码；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("relicCode")
    private String relicCode;

    /**
     * 记录遗物代码是否出现，同时区分未提供与显式空值。
     *
     * @param value 遗物代码，保留协议中的显式空值。
     */
    @JsonSetter("relicCode")
    public void setRelicCode(String value) {
        this.relicCode = value;
        markPresent("relicCode");
    }

    /** 状态集合；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("states")
    private Map<String, StatesKeyDTO> states;

    /**
     * 记录状态集合是否出现，同时区分未提供与显式空值。
     *
     * @param value 状态集合，保留协议中的显式空值。
     */
    @JsonSetter("states")
    public void setStates(Map<String, StatesKeyDTO> value) {
        this.states = value;
        markPresent("states");
    }

    /** 阶级；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("tier")
    private BigDecimal tier;

    /**
     * 记录阶级是否出现，同时区分未提供与显式空值。
     *
     * @param value 阶级，保留协议中的显式空值。
     */
    @JsonSetter("tier")
    public void setTier(BigDecimal value) {
        this.tier = value;
        markPresent("tier");
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

    /** 未解析的掉落记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("unresolvedDropRows")
    @JsonDeserialize(contentUsing = OfficialNullDeserializer.class)
    private List<Void> unresolvedDropRows;

    /**
     * 记录未解析的掉落记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 未解析的掉落记录数，保留协议中的显式空值。
     */
    @JsonSetter("unresolvedDropRows")
    public void setUnresolvedDropRows(List<Void> value) {
        this.unresolvedDropRows = value;
        markPresent("unresolvedDropRows");
    }

    /** 未解析的地点记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("unresolvedLocationRows")
    @JsonDeserialize(contentUsing = OfficialNullDeserializer.class)
    private List<Void> unresolvedLocationRows;

    /**
     * 记录未解析的地点记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 未解析的地点记录数，保留协议中的显式空值。
     */
    @JsonSetter("unresolvedLocationRows")
    public void setUnresolvedLocationRows(List<Void> value) {
        this.unresolvedLocationRows = value;
        markPresent("unresolvedLocationRows");
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

    /** 官方嵌套对象 LocationsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class LocationsDTO extends OfficialObjectDTO {
        /** 获取地点列表的启动概率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("activationProbability")
        private BigDecimal activationProbability;

        /**
         * 记录获取地点列表的启动概率是否出现，同时区分未提供与显式空值。
         *
         * @param value 获取地点列表的启动概率，保留协议中的显式空值。
         */
        @JsonSetter("activationProbability")
        public void setActivationProbability(BigDecimal value) {
            this.activationProbability = value;
            markPresent("activationProbability");
        }

        /** 获取地点列表的分类；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("category")
        private String category;

        /**
         * 记录获取地点列表的分类是否出现，同时区分未提供与显式空值。
         *
         * @param value 获取地点列表的分类，保留协议中的显式空值。
         */
        @JsonSetter("category")
        public void setCategory(String value) {
            this.category = value;
            markPresent("category");
        }

        /** 获取地点列表的几率解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chanceResolution")
        private String chanceResolution;

        /**
         * 记录获取地点列表的几率解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 获取地点列表的几率解析状态，保留协议中的显式空值。
         */
        @JsonSetter("chanceResolution")
        public void setChanceResolution(String value) {
            this.chanceResolution = value;
            markPresent("chanceResolution");
        }

        /** 获取地点列表的抽取次数；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("drawCount")
        private BigDecimal drawCount;

        /**
         * 记录获取地点列表的抽取次数是否出现，同时区分未提供与显式空值。
         *
         * @param value 获取地点列表的抽取次数，保留协议中的显式空值。
         */
        @JsonSetter("drawCount")
        public void setDrawCount(BigDecimal value) {
            this.drawCount = value;
            markPresent("drawCount");
        }

        /** 获取地点列表的抽取方式；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("drawMode")
        private String drawMode;

        /**
         * 记录获取地点列表的抽取方式是否出现，同时区分未提供与显式空值。
         *
         * @param value 获取地点列表的抽取方式，保留协议中的显式空值。
         */
        @JsonSetter("drawMode")
        public void setDrawMode(String value) {
            this.drawMode = value;
            markPresent("drawMode");
        }

        /** 获取地点列表的掉落几率百分比；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("dropChancePercent")
        private BigDecimal dropChancePercent;

        /**
         * 记录获取地点列表的掉落几率百分比是否出现，同时区分未提供与显式空值。
         *
         * @param value 获取地点列表的掉落几率百分比，保留协议中的显式空值。
         */
        @JsonSetter("dropChancePercent")
        public void setDropChancePercent(BigDecimal value) {
            this.dropChancePercent = value;
            markPresent("dropChancePercent");
        }

        /** 获取地点列表的分组列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("groups")
        private List<String> groups;

        /**
         * 记录获取地点列表的分组列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 获取地点列表的分组列表，保留协议中的显式空值。
         */
        @JsonSetter("groups")
        public void setGroups(List<String> value) {
            this.groups = value;
            markPresent("groups");
        }

        /** 获取地点列表的概率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("probability")
        private BigDecimal probability;

        /**
         * 记录获取地点列表的概率是否出现，同时区分未提供与显式空值。
         *
         * @param value 获取地点列表的概率，保留协议中的显式空值。
         */
        @JsonSetter("probability")
        public void setProbability(BigDecimal value) {
            this.probability = value;
            markPresent("probability");
        }

        /** 获取地点列表的稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rarity")
        private String rarity;

        /**
         * 记录获取地点列表的稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 获取地点列表的稀有度，保留协议中的显式空值。
         */
        @JsonSetter("rarity")
        public void setRarity(String value) {
            this.rarity = value;
            markPresent("rarity");
        }

        /** 获取地点列表的来源信息；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("source")
        private String source;

        /**
         * 记录获取地点列表的来源信息是否出现，同时区分未提供与显式空值。
         *
         * @param value 获取地点列表的来源信息，保留协议中的显式空值。
         */
        @JsonSetter("source")
        public void setSource(String value) {
            this.source = value;
            markPresent("source");
        }

        /** 获取地点列表的来源掉落几率百分比；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("sourceDropChancePercent")
        private BigDecimal sourceDropChancePercent;

        /**
         * 记录获取地点列表的来源掉落几率百分比是否出现，同时区分未提供与显式空值。
         *
         * @param value 获取地点列表的来源掉落几率百分比，保留协议中的显式空值。
         */
        @JsonSetter("sourceDropChancePercent")
        public void setSourceDropChancePercent(BigDecimal value) {
            this.sourceDropChancePercent = value;
            markPresent("sourceDropChancePercent");
        }

        /** 获取地点列表的来源中文说明；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("sourceZh")
        private String sourceZh;

        /**
         * 记录获取地点列表的来源中文说明是否出现，同时区分未提供与显式空值。
         *
         * @param value 获取地点列表的来源中文说明，保留协议中的显式空值。
         */
        @JsonSetter("sourceZh")
        public void setSourceZh(String value) {
            this.sourceZh = value;
            markPresent("sourceZh");
        }

    }

    /** 官方嵌套对象 MarketListingsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class MarketListingsDTO extends OfficialObjectDTO {
        /** 关联市场商品列表的游戏引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("gameRef")
        private String gameRef;

        /**
         * 记录关联市场商品列表的游戏引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的游戏引用，保留协议中的显式空值。
         */
        @JsonSetter("gameRef")
        public void setGameRef(String value) {
            this.gameRef = value;
            markPresent("gameRef");
        }

        /** 关联市场商品列表的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录关联市场商品列表的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 关联市场商品列表的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录关联市场商品列表的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 关联市场商品列表的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录关联市场商品列表的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 关联市场商品列表的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录关联市场商品列表的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 关联市场商品列表的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录关联市场商品列表的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 关联市场商品列表的网址标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("slug")
        private String slug;

        /**
         * 记录关联市场商品列表的网址标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的网址标识，保留协议中的显式空值。
         */
        @JsonSetter("slug")
        public void setSlug(String value) {
            this.slug = value;
            markPresent("slug");
        }

        /** 关联市场商品列表的访问地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("url")
        private String url;

        /**
         * 记录关联市场商品列表的访问地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 关联市场商品列表的访问地址，保留协议中的显式空值。
         */
        @JsonSetter("url")
        public void setUrl(String value) {
            this.url = value;
            markPresent("url");
        }

    }

    /** 官方嵌套对象 StatesKeyItemReferencesRotationsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StatesKeyItemReferencesRotationsDTO extends OfficialObjectDTO {
        /** 状态集合的物品引用的轮次的物品的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__domain")
        private String itemDomain;

        /**
         * 记录状态集合的物品引用的轮次的物品的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的物品引用的轮次的物品的领域，保留协议中的显式空值。
         */
        @JsonSetter("item__domain")
        public void setItemDomain(String value) {
            this.itemDomain = value;
            markPresent("item__domain");
        }

        /** 状态集合的物品引用的轮次的物品的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconStatus")
        private String itemIconStatus;

        /**
         * 记录状态集合的物品引用的轮次的物品的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的物品引用的轮次的物品的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("item__iconStatus")
        public void setItemIconStatus(String value) {
            this.itemIconStatus = value;
            markPresent("item__iconStatus");
        }

        /** 状态集合的物品引用的轮次的物品的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconUrl")
        private String itemIconUrl;

        /**
         * 记录状态集合的物品引用的轮次的物品的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的物品引用的轮次的物品的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("item__iconUrl")
        public void setItemIconUrl(String value) {
            this.itemIconUrl = value;
            markPresent("item__iconUrl");
        }

        /** 状态集合的物品引用的轮次的物品的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__name")
        private String itemName;

        /**
         * 记录状态集合的物品引用的轮次的物品的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的物品引用的轮次的物品的名称，保留协议中的显式空值。
         */
        @JsonSetter("item__name")
        public void setItemName(String value) {
            this.itemName = value;
            markPresent("item__name");
        }

        /** 状态集合的物品引用的轮次的物品的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameEn")
        private String itemNameEn;

        /**
         * 记录状态集合的物品引用的轮次的物品的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的物品引用的轮次的物品的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameEn")
        public void setItemNameEn(String value) {
            this.itemNameEn = value;
            markPresent("item__nameEn");
        }

        /** 状态集合的物品引用的轮次的物品的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameZh")
        private String itemNameZh;

        /**
         * 记录状态集合的物品引用的轮次的物品的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的物品引用的轮次的物品的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameZh")
        public void setItemNameZh(String value) {
            this.itemNameZh = value;
            markPresent("item__nameZh");
        }

        /** 状态集合的物品引用的轮次的物品的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__resolution")
        private String itemResolution;

        /**
         * 记录状态集合的物品引用的轮次的物品的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的物品引用的轮次的物品的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("item__resolution")
        public void setItemResolution(String value) {
            this.itemResolution = value;
            markPresent("item__resolution");
        }

        /** 状态集合的物品引用的轮次的物品的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__uniqueName")
        private String itemUniqueName;

        /**
         * 记录状态集合的物品引用的轮次的物品的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的物品引用的轮次的物品的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("item__uniqueName")
        public void setItemUniqueName(String value) {
            this.itemUniqueName = value;
            markPresent("item__uniqueName");
        }

        /** 状态集合的物品引用的轮次的物品数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemCount")
        private BigDecimal itemCount;

        /**
         * 记录状态集合的物品引用的轮次的物品数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的物品引用的轮次的物品数量，保留协议中的显式空值。
         */
        @JsonSetter("itemCount")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("itemCount");
        }

        /** 状态集合的物品引用的轮次的稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rarity")
        private String rarity;

        /**
         * 记录状态集合的物品引用的轮次的稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的物品引用的轮次的稀有度，保留协议中的显式空值。
         */
        @JsonSetter("rarity")
        public void setRarity(String value) {
            this.rarity = value;
            markPresent("rarity");
        }

        /** 状态集合的物品引用的轮次的类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("type")
        private String type;

        /**
         * 记录状态集合的物品引用的轮次的类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的物品引用的轮次的类型，保留协议中的显式空值。
         */
        @JsonSetter("type")
        public void setType(String value) {
            this.type = value;
            markPresent("type");
        }

    }

    /** 官方嵌套对象 StatesKeyItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StatesKeyItemReferencesDTO extends OfficialObjectDTO {
        /** 状态集合的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录状态集合的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 状态集合的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录状态集合的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 状态集合的物品引用的概率单位；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("probabilityUnit")
        private String probabilityUnit;

        /**
         * 记录状态集合的物品引用的概率单位是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的物品引用的概率单位，保留协议中的显式空值。
         */
        @JsonSetter("probabilityUnit")
        public void setProbabilityUnit(String value) {
            this.probabilityUnit = value;
            markPresent("probabilityUnit");
        }

        /** 状态集合的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录状态集合的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 状态集合的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录状态集合的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 状态集合的物品引用的轮次；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rotations")
        private List<List<StatesKeyItemReferencesRotationsDTO>> rotations;

        /**
         * 记录状态集合的物品引用的轮次是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的物品引用的轮次，保留协议中的显式空值。
         */
        @JsonSetter("rotations")
        public void setRotations(List<List<StatesKeyItemReferencesRotationsDTO>> value) {
            this.rotations = value;
            markPresent("rotations");
        }

        /** 状态集合的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录状态集合的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 StatesKeyRewardsDropTableEntriesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StatesKeyRewardsDropTableEntriesDTO extends OfficialObjectDTO {
        /** 状态集合的奖励的掉落表条目的分类；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("category")
        private String category;

        /**
         * 记录状态集合的奖励的掉落表条目的分类是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的掉落表条目的分类，保留协议中的显式空值。
         */
        @JsonSetter("category")
        public void setCategory(String value) {
            this.category = value;
            markPresent("category");
        }

        /** 状态集合的奖励的掉落表条目的几率解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chanceResolution")
        private String chanceResolution;

        /**
         * 记录状态集合的奖励的掉落表条目的几率解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的掉落表条目的几率解析状态，保留协议中的显式空值。
         */
        @JsonSetter("chanceResolution")
        public void setChanceResolution(String value) {
            this.chanceResolution = value;
            markPresent("chanceResolution");
        }

        /** 状态集合的奖励的掉落表条目的掉落几率百分比；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("dropChancePercent")
        private BigDecimal dropChancePercent;

        /**
         * 记录状态集合的奖励的掉落表条目的掉落几率百分比是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的掉落表条目的掉落几率百分比，保留协议中的显式空值。
         */
        @JsonSetter("dropChancePercent")
        public void setDropChancePercent(BigDecimal value) {
            this.dropChancePercent = value;
            markPresent("dropChancePercent");
        }

        /** 状态集合的奖励的掉落表条目的分组列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("groups")
        @JsonDeserialize(contentUsing = OfficialNullDeserializer.class)
        private List<Void> groups;

        /**
         * 记录状态集合的奖励的掉落表条目的分组列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的掉落表条目的分组列表，保留协议中的显式空值。
         */
        @JsonSetter("groups")
        public void setGroups(List<Void> value) {
            this.groups = value;
            markPresent("groups");
        }

        /** 状态集合的奖励的掉落表条目的物品的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconStatus")
        private String itemIconStatus;

        /**
         * 记录状态集合的奖励的掉落表条目的物品的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的掉落表条目的物品的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("item__iconStatus")
        public void setItemIconStatus(String value) {
            this.itemIconStatus = value;
            markPresent("item__iconStatus");
        }

        /** 状态集合的奖励的掉落表条目的物品的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconUrl")
        private String itemIconUrl;

        /**
         * 记录状态集合的奖励的掉落表条目的物品的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的掉落表条目的物品的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("item__iconUrl")
        public void setItemIconUrl(String value) {
            this.itemIconUrl = value;
            markPresent("item__iconUrl");
        }

        /** 状态集合的奖励的掉落表条目的物品的物品数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__itemCount")
        private BigDecimal itemItemCount;

        /**
         * 记录状态集合的奖励的掉落表条目的物品的物品数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的掉落表条目的物品的物品数量，保留协议中的显式空值。
         */
        @JsonSetter("item__itemCount")
        public void setItemItemCount(BigDecimal value) {
            this.itemItemCount = value;
            markPresent("item__itemCount");
        }

        /** 状态集合的奖励的掉落表条目的物品的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__name")
        private String itemName;

        /**
         * 记录状态集合的奖励的掉落表条目的物品的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的掉落表条目的物品的名称，保留协议中的显式空值。
         */
        @JsonSetter("item__name")
        public void setItemName(String value) {
            this.itemName = value;
            markPresent("item__name");
        }

        /** 状态集合的奖励的掉落表条目的物品的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameEn")
        private String itemNameEn;

        /**
         * 记录状态集合的奖励的掉落表条目的物品的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的掉落表条目的物品的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameEn")
        public void setItemNameEn(String value) {
            this.itemNameEn = value;
            markPresent("item__nameEn");
        }

        /** 状态集合的奖励的掉落表条目的物品的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameZh")
        private String itemNameZh;

        /**
         * 记录状态集合的奖励的掉落表条目的物品的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的掉落表条目的物品的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameZh")
        public void setItemNameZh(String value) {
            this.itemNameZh = value;
            markPresent("item__nameZh");
        }

        /** 状态集合的奖励的掉落表条目的物品的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__resolution")
        private String itemResolution;

        /**
         * 记录状态集合的奖励的掉落表条目的物品的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的掉落表条目的物品的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("item__resolution")
        public void setItemResolution(String value) {
            this.itemResolution = value;
            markPresent("item__resolution");
        }

        /** 状态集合的奖励的掉落表条目的物品的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__uniqueName")
        private String itemUniqueName;

        /**
         * 记录状态集合的奖励的掉落表条目的物品的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的掉落表条目的物品的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("item__uniqueName")
        public void setItemUniqueName(String value) {
            this.itemUniqueName = value;
            markPresent("item__uniqueName");
        }

        /** 状态集合的奖励的掉落表条目的概率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("probability")
        private BigDecimal probability;

        /**
         * 记录状态集合的奖励的掉落表条目的概率是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的掉落表条目的概率，保留协议中的显式空值。
         */
        @JsonSetter("probability")
        public void setProbability(BigDecimal value) {
            this.probability = value;
            markPresent("probability");
        }

        /** 状态集合的奖励的掉落表条目的稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rarity")
        private String rarity;

        /**
         * 记录状态集合的奖励的掉落表条目的稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的掉落表条目的稀有度，保留协议中的显式空值。
         */
        @JsonSetter("rarity")
        public void setRarity(String value) {
            this.rarity = value;
            markPresent("rarity");
        }

        /** 状态集合的奖励的掉落表条目的来源信息；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("source")
        private String source;

        /**
         * 记录状态集合的奖励的掉落表条目的来源信息是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的掉落表条目的来源信息，保留协议中的显式空值。
         */
        @JsonSetter("source")
        public void setSource(String value) {
            this.source = value;
            markPresent("source");
        }

    }

    /** 官方嵌套对象 StatesKeyRewardsItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StatesKeyRewardsItemReferencesDTO extends OfficialObjectDTO {
        /** 状态集合的奖励的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录状态集合的奖励的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 状态集合的奖励的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录状态集合的奖励的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 状态集合的奖励的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录状态集合的奖励的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 状态集合的奖励的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录状态集合的奖励的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 状态集合的奖励的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录状态集合的奖励的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 状态集合的奖励的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录状态集合的奖励的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 状态集合的奖励的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录状态集合的奖励的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 状态集合的奖励的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录状态集合的奖励的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 状态集合的奖励的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录状态集合的奖励的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 StatesKeyRewardsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StatesKeyRewardsDTO extends OfficialObjectDTO {
        /** 状态集合的奖励的掉落几率百分比；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("dropChancePercent")
        private BigDecimal dropChancePercent;

        /**
         * 记录状态集合的奖励的掉落几率百分比是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的掉落几率百分比，保留协议中的显式空值。
         */
        @JsonSetter("dropChancePercent")
        public void setDropChancePercent(BigDecimal value) {
            this.dropChancePercent = value;
            markPresent("dropChancePercent");
        }

        /** 状态集合的奖励的掉落稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("dropRarity")
        private String dropRarity;

        /**
         * 记录状态集合的奖励的掉落稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的掉落稀有度，保留协议中的显式空值。
         */
        @JsonSetter("dropRarity")
        public void setDropRarity(String value) {
            this.dropRarity = value;
            markPresent("dropRarity");
        }

        /** 状态集合的奖励的掉落表条目；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("dropTableEntries")
        private List<StatesKeyRewardsDropTableEntriesDTO> dropTableEntries;

        /**
         * 记录状态集合的奖励的掉落表条目是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的掉落表条目，保留协议中的显式空值。
         */
        @JsonSetter("dropTableEntries")
        public void setDropTableEntries(List<StatesKeyRewardsDropTableEntriesDTO> value) {
            this.dropTableEntries = value;
            markPresent("dropTableEntries");
        }

        /** 状态集合的奖励的物品的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconStatus")
        private String itemIconStatus;

        /**
         * 记录状态集合的奖励的物品的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的物品的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("item__iconStatus")
        public void setItemIconStatus(String value) {
            this.itemIconStatus = value;
            markPresent("item__iconStatus");
        }

        /** 状态集合的奖励的物品的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconUrl")
        private String itemIconUrl;

        /**
         * 记录状态集合的奖励的物品的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的物品的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("item__iconUrl")
        public void setItemIconUrl(String value) {
            this.itemIconUrl = value;
            markPresent("item__iconUrl");
        }

        /** 状态集合的奖励的物品的市场网址标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__marketSlug")
        private String itemMarketSlug;

        /**
         * 记录状态集合的奖励的物品的市场网址标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的物品的市场网址标识，保留协议中的显式空值。
         */
        @JsonSetter("item__marketSlug")
        public void setItemMarketSlug(String value) {
            this.itemMarketSlug = value;
            markPresent("item__marketSlug");
        }

        /** 状态集合的奖励的物品的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__name")
        private String itemName;

        /**
         * 记录状态集合的奖励的物品的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的物品的名称，保留协议中的显式空值。
         */
        @JsonSetter("item__name")
        public void setItemName(String value) {
            this.itemName = value;
            markPresent("item__name");
        }

        /** 状态集合的奖励的物品的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameEn")
        private String itemNameEn;

        /**
         * 记录状态集合的奖励的物品的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的物品的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameEn")
        public void setItemNameEn(String value) {
            this.itemNameEn = value;
            markPresent("item__nameEn");
        }

        /** 状态集合的奖励的物品的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameZh")
        private String itemNameZh;

        /**
         * 记录状态集合的奖励的物品的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的物品的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameZh")
        public void setItemNameZh(String value) {
            this.itemNameZh = value;
            markPresent("item__nameZh");
        }

        /** 状态集合的奖励的物品的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__resolution")
        private String itemResolution;

        /**
         * 记录状态集合的奖励的物品的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的物品的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("item__resolution")
        public void setItemResolution(String value) {
            this.itemResolution = value;
            markPresent("item__resolution");
        }

        /** 状态集合的奖励的物品的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__uniqueName")
        private String itemUniqueName;

        /**
         * 记录状态集合的奖励的物品的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的物品的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("item__uniqueName")
        public void setItemUniqueName(String value) {
            this.itemUniqueName = value;
            markPresent("item__uniqueName");
        }

        /** 状态集合的奖励的物品数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemCount")
        private BigDecimal itemCount;

        /**
         * 记录状态集合的奖励的物品数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的物品数量，保留协议中的显式空值。
         */
        @JsonSetter("itemCount")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("itemCount");
        }

        /** 状态集合的奖励的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<StatesKeyRewardsItemReferencesDTO> itemReferences;

        /**
         * 记录状态集合的奖励的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<StatesKeyRewardsItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 状态集合的奖励的概率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("probability")
        private BigDecimal probability;

        /**
         * 记录状态集合的奖励的概率是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的概率，保留协议中的显式空值。
         */
        @JsonSetter("probability")
        public void setProbability(BigDecimal value) {
            this.probability = value;
            markPresent("probability");
        }

        /** 状态集合的奖励的稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rarity")
        private String rarity;

        /**
         * 记录状态集合的奖励的稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的稀有度，保留协议中的显式空值。
         */
        @JsonSetter("rarity")
        public void setRarity(String value) {
            this.rarity = value;
            markPresent("rarity");
        }

        /** 状态集合的奖励的奖励名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rewardName")
        private String rewardName;

        /**
         * 记录状态集合的奖励的奖励名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的奖励名称，保留协议中的显式空值。
         */
        @JsonSetter("rewardName")
        public void setRewardName(String value) {
            this.rewardName = value;
            markPresent("rewardName");
        }

        /** 状态集合的奖励的阶级；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("tier")
        private BigDecimal tier;

        /**
         * 记录状态集合的奖励的阶级是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励的阶级，保留协议中的显式空值。
         */
        @JsonSetter("tier")
        public void setTier(BigDecimal value) {
            this.tier = value;
            markPresent("tier");
        }

    }

    /** 官方嵌套对象 StatesKeyDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class StatesKeyDTO extends OfficialObjectDTO {
        /** 状态集合的分类；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("category")
        private String category;

        /**
         * 记录状态集合的分类是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的分类，保留协议中的显式空值。
         */
        @JsonSetter("category")
        public void setCategory(String value) {
            this.category = value;
            markPresent("category");
        }

        /** 状态集合的是否为资料库隐藏条目；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("codexSecret")
        private Boolean codexSecret;

        /**
         * 记录状态集合的是否为资料库隐藏条目是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的是否为资料库隐藏条目，保留协议中的显式空值。
         */
        @JsonSetter("codexSecret")
        public void setCodexSecret(Boolean value) {
            this.codexSecret = value;
            markPresent("codexSecret");
        }

        /** 状态集合的描述；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("description")
        private String description;

        /**
         * 记录状态集合的描述是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的描述，保留协议中的显式空值。
         */
        @JsonSetter("description")
        public void setDescription(String value) {
            this.description = value;
            markPresent("description");
        }

        /** 状态集合的英文描述；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("descriptionEn")
        private String descriptionEn;

        /**
         * 记录状态集合的英文描述是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的英文描述，保留协议中的显式空值。
         */
        @JsonSetter("descriptionEn")
        public void setDescriptionEn(String value) {
            this.descriptionEn = value;
            markPresent("descriptionEn");
        }

        /** 状态集合的中文描述；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("descriptionZh")
        private String descriptionZh;

        /**
         * 记录状态集合的中文描述是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的中文描述，保留协议中的显式空值。
         */
        @JsonSetter("descriptionZh")
        public void setDescriptionZh(String value) {
            this.descriptionZh = value;
            markPresent("descriptionZh");
        }

        /** 状态集合的纪元；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("era")
        private String era;

        /**
         * 记录状态集合的纪元是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的纪元，保留协议中的显式空值。
         */
        @JsonSetter("era")
        public void setEra(String value) {
            this.era = value;
            markPresent("era");
        }

        /** 状态集合的图标；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("icon")
        private String icon;

        /**
         * 记录状态集合的图标是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的图标，保留协议中的显式空值。
         */
        @JsonSetter("icon")
        public void setIcon(String value) {
            this.icon = value;
            markPresent("icon");
        }

        /** 状态集合的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录状态集合的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 状态集合的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录状态集合的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 状态集合的首次推出时间；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("introducedAt")
        private BigDecimal introducedAt;

        /**
         * 记录状态集合的首次推出时间是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的首次推出时间，保留协议中的显式空值。
         */
        @JsonSetter("introducedAt")
        public void setIntroducedAt(BigDecimal value) {
            this.introducedAt = value;
            markPresent("introducedAt");
        }

        /** 状态集合的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<StatesKeyItemReferencesDTO> itemReferences;

        /**
         * 记录状态集合的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<StatesKeyItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 状态集合的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录状态集合的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 状态集合的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录状态集合的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 状态集合的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录状态集合的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 状态集合的品质；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("quality")
        private String quality;

        /**
         * 记录状态集合的品质是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的品质，保留协议中的显式空值。
         */
        @JsonSetter("quality")
        public void setQuality(String value) {
            this.quality = value;
            markPresent("quality");
        }

        /** 状态集合的精炼等级；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("refinement")
        private String refinement;

        /**
         * 记录状态集合的精炼等级是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的精炼等级，保留协议中的显式空值。
         */
        @JsonSetter("refinement")
        public void setRefinement(String value) {
            this.refinement = value;
            markPresent("refinement");
        }

        /** 状态集合的奖励清单；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rewardManifest")
        private String rewardManifest;

        /**
         * 记录状态集合的奖励清单是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励清单，保留协议中的显式空值。
         */
        @JsonSetter("rewardManifest")
        public void setRewardManifest(String value) {
            this.rewardManifest = value;
            markPresent("rewardManifest");
        }

        /** 状态集合的奖励；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rewards")
        private List<StatesKeyRewardsDTO> rewards;

        /**
         * 记录状态集合的奖励是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的奖励，保留协议中的显式空值。
         */
        @JsonSetter("rewards")
        public void setRewards(List<StatesKeyRewardsDTO> value) {
            this.rewards = value;
            markPresent("rewards");
        }

        /** 状态集合的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录状态集合的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

        /** 状态集合的未解析的掉落奖励列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("unresolvedDropRewards")
        @JsonDeserialize(contentUsing = OfficialNullDeserializer.class)
        private List<Void> unresolvedDropRewards;

        /**
         * 记录状态集合的未解析的掉落奖励列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的未解析的掉落奖励列表，保留协议中的显式空值。
         */
        @JsonSetter("unresolvedDropRewards")
        public void setUnresolvedDropRewards(List<Void> value) {
            this.unresolvedDropRewards = value;
            markPresent("unresolvedDropRewards");
        }

        /** 状态集合的入库时间；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("vaultedAt")
        private BigDecimal vaultedAt;

        /**
         * 记录状态集合的入库时间是否出现，同时区分未提供与显式空值。
         *
         * @param value 状态集合的入库时间，保留协议中的显式空值。
         */
        @JsonSetter("vaultedAt")
        public void setVaultedAt(BigDecimal value) {
            this.vaultedAt = value;
            markPresent("vaultedAt");
        }

    }

}
