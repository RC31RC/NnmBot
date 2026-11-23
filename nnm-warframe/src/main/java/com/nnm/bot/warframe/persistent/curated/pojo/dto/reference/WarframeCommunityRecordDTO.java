package com.nnm.bot.warframe.persistent.curated.pojo.dto.reference;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeArbitrationEfficiencyGrade;

/** 社区人工资料的非受管记录；四种业务载荷各有固定字段。 */
public record WarframeCommunityRecordDTO<T>(
    /** 数据库记录主键。 */
    Long databaseId,
    /** 人工资料的稳定业务键。 */
    String recordKey,
    /** 当前资料类型的具体业务内容。 */
    T details) {

    /** 物品别名与其标准名称。 */
    @JsonIgnoreProperties("key")
    public record AliasDTO(
        /** 玩家使用的物品别名。 */
        @JsonProperty("aliasName") String aliasName,
        /** 别名对应的标准物品名称。 */
        @JsonProperty("targetName") String targetName) implements WarframeCommunityInputDTO {
    }

    /** 术语分组、英文与中文解释。 */
    @JsonIgnoreProperties("key")
    public record TermDTO(
        /** 术语所属分组；未分组时为空字符串。 */
        @JsonProperty("termGroup") String termGroup,
        /** 术语的英文表达。 */
        @JsonProperty("englishText") String englishText,
        /** 术语的中文解释。 */
        @JsonProperty("chineseText") String chineseText) implements WarframeCommunityInputDTO {
    }

    /** 仲裁节点、精华估值与人工确认的效率评级。 */
    @JsonIgnoreProperties("key")
    public record EssenceDTO(
        /** 官方星图节点稳定键。 */
        @JsonProperty("nodeKey") String nodeKey,
        /** 官方星图节点显示名。 */
        @JsonProperty("nodeName") String nodeName,
        /** 官方星图节点所属星球或星系名称。 */
        @JsonProperty("systemName") String systemName,
        /** 该节点社区估计的每小时仲裁精华收益；没有可信估值时为空。 */
        @JsonProperty("essenceAmount") Integer essenceAmount,
        /** 由社区维护者确认的效率评级。 */
        @JsonProperty("efficiencyGrade") WarframeArbitrationEfficiencyGrade efficiencyGrade)
        implements
            WarframeCommunityInputDTO {
    }

    /** 紫卡属性组合代码与自定义别名。 */
    @JsonIgnoreProperties("key")
    public record RivenAliasDTO(
        /** 一至三个紫卡属性代码，以加号连接。 */
        @JsonProperty("attributeCode") String attributeCode,
        /** 紫卡属性或组合的自定义别名。 */
        @JsonProperty("aliasName") String aliasName) implements WarframeCommunityInputDTO {
    }

    /** 社区维护的解析作用域、原始字段和目标。 */
    @JsonIgnoreProperties("key")
    public record ReferenceMappingDTO(String mappingScope, String sourceKey, String targetKey)
        implements WarframeCommunityInputDTO {
    }

    /** 沉沦之地楼层与已采纳奖池之间的维护记录。 */
    @JsonIgnoreProperties("key")
    public record DescendiaRewardDTO(Integer floorNumber, String difficulty, String label, String officialPoolKey,
        String expectedPoolName, Integer itemCountOverride, String note, Boolean guaranteed,
        String sourceUrl) implements WarframeCommunityInputDTO {
    }
}
