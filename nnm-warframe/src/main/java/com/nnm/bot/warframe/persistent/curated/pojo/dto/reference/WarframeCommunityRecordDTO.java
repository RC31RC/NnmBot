package com.nnm.bot.warframe.persistent.curated.pojo.dto.reference;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
            @JsonProperty("aliasName")
            String aliasName,
            /** 别名对应的标准物品名称。 */
            @JsonProperty("targetName")
            String targetName) implements WarframeCommunityInputDTO {
    }

    /** 术语分组、英文与中文解释。 */
    @JsonIgnoreProperties("key")
    public record TermDTO(
            /** 术语所属分组；未分组时为空字符串。 */
            @JsonProperty("termGroup")
            String termGroup,
            /** 术语的英文表达。 */
            @JsonProperty("englishText")
            String englishText,
            /** 术语的中文解释。 */
            @JsonProperty("chineseText")
            String chineseText) implements WarframeCommunityInputDTO {
    }

    /** 仲裁节点与精华数量。 */
    @JsonIgnoreProperties("key")
    public record EssenceDTO(
            /** 仲裁任务节点名称。 */
            @JsonProperty("nodeName")
            String nodeName,
            /** 该节点维护的仲裁精华数量。 */
            @JsonProperty("essenceAmount")
            Integer essenceAmount) implements WarframeCommunityInputDTO {
    }

    /** 紫卡属性组合代码与自定义别名。 */
    @JsonIgnoreProperties("key")
    public record RivenAliasDTO(
            /** 一至三个紫卡属性代码，以加号连接。 */
            @JsonProperty("attributeCode")
            String attributeCode,
            /** 紫卡属性或组合的自定义别名。 */
            @JsonProperty("aliasName")
            String aliasName) implements WarframeCommunityInputDTO {
    }
}
