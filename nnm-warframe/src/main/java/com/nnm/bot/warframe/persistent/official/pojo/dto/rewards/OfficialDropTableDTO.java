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
import lombok.Getter;

/** DROP_TABLE 的 JSON 输入合同；由生成器和契约测试共同维护，不携带 JPA 状态。 */
@Getter
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class OfficialDropTableDTO extends OfficialObjectDTO implements OfficialRecordDTO {
    /** 启动概率；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("activationProbability")
    private BigDecimal activationProbability;

    /**
     * 记录启动概率是否出现，同时区分未提供与显式空值。
     *
     * @param value 启动概率，保留协议中的显式空值。
     */
    @JsonSetter("activationProbability")
    public void setActivationProbability(BigDecimal value) {
        this.activationProbability = value;
        markPresent("activationProbability");
    }

    /** 赏金目录；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("bountyCatalog")
    private String bountyCatalog;

    /**
     * 记录赏金目录是否出现，同时区分未提供与显式空值。
     *
     * @param value 赏金目录，保留协议中的显式空值。
     */
    @JsonSetter("bountyCatalog")
    public void setBountyCatalog(String value) {
        this.bountyCatalog = value;
        markPresent("bountyCatalog");
    }

    /** 分类；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("category")
    private String category;

    /**
     * 记录分类是否出现，同时区分未提供与显式空值。
     *
     * @param value 分类，保留协议中的显式空值。
     */
    @JsonSetter("category")
    public void setCategory(String value) {
        this.category = value;
        markPresent("category");
    }

    /** 抽取次数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("drawCount")
    private BigDecimal drawCount;

    /**
     * 记录抽取次数是否出现，同时区分未提供与显式空值。
     *
     * @param value 抽取次数，保留协议中的显式空值。
     */
    @JsonSetter("drawCount")
    public void setDrawCount(BigDecimal value) {
        this.drawCount = value;
        markPresent("drawCount");
    }

    /** 抽取方式；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("drawMode")
    private String drawMode;

    /**
     * 记录抽取方式是否出现，同时区分未提供与显式空值。
     *
     * @param value 抽取方式，保留协议中的显式空值。
     */
    @JsonSetter("drawMode")
    public void setDrawMode(String value) {
        this.drawMode = value;
        markPresent("drawMode");
    }

    /** 分组列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("groups")
    private List<GroupsDTO> groups;

    /**
     * 记录分组列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 分组列表，保留协议中的显式空值。
     */
    @JsonSetter("groups")
    public void setGroups(List<GroupsDTO> value) {
        this.groups = value;
        markPresent("groups");
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

    /** 奖池标识；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("poolId")
    private String poolId;

    /**
     * 记录奖池标识是否出现，同时区分未提供与显式空值。
     *
     * @param value 奖池标识，保留协议中的显式空值。
     */
    @JsonSetter("poolId")
    public void setPoolId(String value) {
        this.poolId = value;
        markPresent("poolId");
    }

    /** 数据质量统计的物品身份统计的存在身份歧义的记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__itemIdentity__ambiguous")
    private BigDecimal qualityItemIdentityAmbiguous;

    /**
     * 记录数据质量统计的物品身份统计的存在身份歧义的记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的物品身份统计的存在身份歧义的记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__itemIdentity__ambiguous")
    public void setQualityItemIdentityAmbiguous(BigDecimal value) {
        this.qualityItemIdentityAmbiguous = value;
        markPresent("quality__itemIdentity__ambiguous");
    }

    /** 数据质量统计的物品身份统计的分类统计；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__itemIdentity__categories")
    private BigDecimal qualityItemIdentityCategories;

    /**
     * 记录数据质量统计的物品身份统计的分类统计是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的物品身份统计的分类统计，保留协议中的显式空值。
     */
    @JsonSetter("quality__itemIdentity__categories")
    public void setQualityItemIdentityCategories(BigDecimal value) {
        this.qualityItemIdentityCategories = value;
        markPresent("quality__itemIdentity__categories");
    }

    /** 数据质量统计的物品身份统计的货币统计；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__itemIdentity__currencies")
    private BigDecimal qualityItemIdentityCurrencies;

    /**
     * 记录数据质量统计的物品身份统计的货币统计是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的物品身份统计的货币统计，保留协议中的显式空值。
     */
    @JsonSetter("quality__itemIdentity__currencies")
    public void setQualityItemIdentityCurrencies(BigDecimal value) {
        this.qualityItemIdentityCurrencies = value;
        markPresent("quality__itemIdentity__currencies");
    }

    /** 数据质量统计的物品身份统计的规则统计；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__itemIdentity__rules")
    private BigDecimal qualityItemIdentityRules;

    /**
     * 记录数据质量统计的物品身份统计的规则统计是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的物品身份统计的规则统计，保留协议中的显式空值。
     */
    @JsonSetter("quality__itemIdentity__rules")
    public void setQualityItemIdentityRules(BigDecimal value) {
        this.qualityItemIdentityRules = value;
        markPresent("quality__itemIdentity__rules");
    }

    /** 数据质量统计的物品身份统计的唯一身份记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__itemIdentity__unique")
    private BigDecimal qualityItemIdentityUnique;

    /**
     * 记录数据质量统计的物品身份统计的唯一身份记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的物品身份统计的唯一身份记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__itemIdentity__unique")
    public void setQualityItemIdentityUnique(BigDecimal value) {
        this.qualityItemIdentityUnique = value;
        markPresent("quality__itemIdentity__unique");
    }

    /** 数据质量统计的物品身份统计的未解析；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__itemIdentity__unresolved")
    private BigDecimal qualityItemIdentityUnresolved;

    /**
     * 记录数据质量统计的物品身份统计的未解析是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的物品身份统计的未解析，保留协议中的显式空值。
     */
    @JsonSetter("quality__itemIdentity__unresolved")
    public void setQualityItemIdentityUnresolved(BigDecimal value) {
        this.qualityItemIdentityUnresolved = value;
        markPresent("quality__itemIdentity__unresolved");
    }

    /** 数据质量统计的正反索引核对统计的蓝图的仅正向索引存在的记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__blueprints__forwardOnlyRows")
    private BigDecimal qualityReverseIndexReconciliationBlueprintsForwardOnlyRows;

    /**
     * 记录数据质量统计的正反索引核对统计的蓝图的仅正向索引存在的记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的蓝图的仅正向索引存在的记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__blueprints__forwardOnlyRows")
    public void setQualityReverseIndexReconciliationBlueprintsForwardOnlyRows(BigDecimal value) {
        this.qualityReverseIndexReconciliationBlueprintsForwardOnlyRows = value;
        markPresent("quality__reverseIndexReconciliation__blueprints__forwardOnlyRows");
    }

    /** 数据质量统计的正反索引核对统计的蓝图的正向索引记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__blueprints__forwardRows")
    private BigDecimal qualityReverseIndexReconciliationBlueprintsForwardRows;

    /**
     * 记录数据质量统计的正反索引核对统计的蓝图的正向索引记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的蓝图的正向索引记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__blueprints__forwardRows")
    public void setQualityReverseIndexReconciliationBlueprintsForwardRows(BigDecimal value) {
        this.qualityReverseIndexReconciliationBlueprintsForwardRows = value;
        markPresent("quality__reverseIndexReconciliation__blueprints__forwardRows");
    }

    /** 数据质量统计的正反索引核对统计的蓝图的已匹配记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__blueprints__matchedRows")
    private BigDecimal qualityReverseIndexReconciliationBlueprintsMatchedRows;

    /**
     * 记录数据质量统计的正反索引核对统计的蓝图的已匹配记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的蓝图的已匹配记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__blueprints__matchedRows")
    public void setQualityReverseIndexReconciliationBlueprintsMatchedRows(BigDecimal value) {
        this.qualityReverseIndexReconciliationBlueprintsMatchedRows = value;
        markPresent("quality__reverseIndexReconciliation__blueprints__matchedRows");
    }

    /** 数据质量统计的正反索引核对统计的蓝图的概率不一致的记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__blueprints__probabilityDisagreements")
    private BigDecimal qualityReverseIndexReconciliationBlueprintsProbabilityDisagreements;

    /**
     * 记录数据质量统计的正反索引核对统计的蓝图的概率不一致的记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的蓝图的概率不一致的记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__blueprints__probabilityDisagreements")
    public void setQualityReverseIndexReconciliationBlueprintsProbabilityDisagreements(BigDecimal value) {
        this.qualityReverseIndexReconciliationBlueprintsProbabilityDisagreements = value;
        markPresent("quality__reverseIndexReconciliation__blueprints__probabilityDisagreements");
    }

    /** 数据质量统计的正反索引核对统计的蓝图的仅反向索引存在的记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__blueprints__reverseOnlyRows")
    private BigDecimal qualityReverseIndexReconciliationBlueprintsReverseOnlyRows;

    /**
     * 记录数据质量统计的正反索引核对统计的蓝图的仅反向索引存在的记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的蓝图的仅反向索引存在的记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__blueprints__reverseOnlyRows")
    public void setQualityReverseIndexReconciliationBlueprintsReverseOnlyRows(BigDecimal value) {
        this.qualityReverseIndexReconciliationBlueprintsReverseOnlyRows = value;
        markPresent("quality__reverseIndexReconciliation__blueprints__reverseOnlyRows");
    }

    /** 数据质量统计的正反索引核对统计的蓝图的反向索引记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__blueprints__reverseRows")
    private BigDecimal qualityReverseIndexReconciliationBlueprintsReverseRows;

    /**
     * 记录数据质量统计的正反索引核对统计的蓝图的反向索引记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的蓝图的反向索引记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__blueprints__reverseRows")
    public void setQualityReverseIndexReconciliationBlueprintsReverseRows(BigDecimal value) {
        this.qualityReverseIndexReconciliationBlueprintsReverseRows = value;
        markPresent("quality__reverseIndexReconciliation__blueprints__reverseRows");
    }

    /** 数据质量统计的正反索引核对统计的蓝图的来源掉落几率不一致的记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__blueprints__sourceDropChanceDisagreements")
    private BigDecimal qualityReverseIndexReconciliationBlueprintsSourceDropChanceDisagreements;

    /**
     * 记录数据质量统计的正反索引核对统计的蓝图的来源掉落几率不一致的记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的蓝图的来源掉落几率不一致的记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__blueprints__sourceDropChanceDisagreements")
    public void setQualityReverseIndexReconciliationBlueprintsSourceDropChanceDisagreements(BigDecimal value) {
        this.qualityReverseIndexReconciliationBlueprintsSourceDropChanceDisagreements = value;
        markPresent("quality__reverseIndexReconciliation__blueprints__sourceDropChanceDisagreements");
    }

    /** 数据质量统计的正反索引核对统计的振幅晶体的仅正向索引存在的记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__mods__forwardOnlyRows")
    private BigDecimal qualityReverseIndexReconciliationModsForwardOnlyRows;

    /**
     * 记录数据质量统计的正反索引核对统计的振幅晶体的仅正向索引存在的记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的振幅晶体的仅正向索引存在的记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__mods__forwardOnlyRows")
    public void setQualityReverseIndexReconciliationModsForwardOnlyRows(BigDecimal value) {
        this.qualityReverseIndexReconciliationModsForwardOnlyRows = value;
        markPresent("quality__reverseIndexReconciliation__mods__forwardOnlyRows");
    }

    /** 数据质量统计的正反索引核对统计的振幅晶体的正向索引记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__mods__forwardRows")
    private BigDecimal qualityReverseIndexReconciliationModsForwardRows;

    /**
     * 记录数据质量统计的正反索引核对统计的振幅晶体的正向索引记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的振幅晶体的正向索引记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__mods__forwardRows")
    public void setQualityReverseIndexReconciliationModsForwardRows(BigDecimal value) {
        this.qualityReverseIndexReconciliationModsForwardRows = value;
        markPresent("quality__reverseIndexReconciliation__mods__forwardRows");
    }

    /** 数据质量统计的正反索引核对统计的振幅晶体的已匹配记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__mods__matchedRows")
    private BigDecimal qualityReverseIndexReconciliationModsMatchedRows;

    /**
     * 记录数据质量统计的正反索引核对统计的振幅晶体的已匹配记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的振幅晶体的已匹配记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__mods__matchedRows")
    public void setQualityReverseIndexReconciliationModsMatchedRows(BigDecimal value) {
        this.qualityReverseIndexReconciliationModsMatchedRows = value;
        markPresent("quality__reverseIndexReconciliation__mods__matchedRows");
    }

    /** 数据质量统计的正反索引核对统计的振幅晶体的概率不一致的记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__mods__probabilityDisagreements")
    private BigDecimal qualityReverseIndexReconciliationModsProbabilityDisagreements;

    /**
     * 记录数据质量统计的正反索引核对统计的振幅晶体的概率不一致的记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的振幅晶体的概率不一致的记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__mods__probabilityDisagreements")
    public void setQualityReverseIndexReconciliationModsProbabilityDisagreements(BigDecimal value) {
        this.qualityReverseIndexReconciliationModsProbabilityDisagreements = value;
        markPresent("quality__reverseIndexReconciliation__mods__probabilityDisagreements");
    }

    /** 数据质量统计的正反索引核对统计的振幅晶体的仅反向索引存在的记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__mods__reverseOnlyRows")
    private BigDecimal qualityReverseIndexReconciliationModsReverseOnlyRows;

    /**
     * 记录数据质量统计的正反索引核对统计的振幅晶体的仅反向索引存在的记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的振幅晶体的仅反向索引存在的记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__mods__reverseOnlyRows")
    public void setQualityReverseIndexReconciliationModsReverseOnlyRows(BigDecimal value) {
        this.qualityReverseIndexReconciliationModsReverseOnlyRows = value;
        markPresent("quality__reverseIndexReconciliation__mods__reverseOnlyRows");
    }

    /** 数据质量统计的正反索引核对统计的振幅晶体的反向索引记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__mods__reverseRows")
    private BigDecimal qualityReverseIndexReconciliationModsReverseRows;

    /**
     * 记录数据质量统计的正反索引核对统计的振幅晶体的反向索引记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的振幅晶体的反向索引记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__mods__reverseRows")
    public void setQualityReverseIndexReconciliationModsReverseRows(BigDecimal value) {
        this.qualityReverseIndexReconciliationModsReverseRows = value;
        markPresent("quality__reverseIndexReconciliation__mods__reverseRows");
    }

    /** 数据质量统计的正反索引核对统计的振幅晶体的来源掉落几率不一致的记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__mods__sourceDropChanceDisagreements")
    private BigDecimal qualityReverseIndexReconciliationModsSourceDropChanceDisagreements;

    /**
     * 记录数据质量统计的正反索引核对统计的振幅晶体的来源掉落几率不一致的记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的振幅晶体的来源掉落几率不一致的记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__mods__sourceDropChanceDisagreements")
    public void setQualityReverseIndexReconciliationModsSourceDropChanceDisagreements(BigDecimal value) {
        this.qualityReverseIndexReconciliationModsSourceDropChanceDisagreements = value;
        markPresent("quality__reverseIndexReconciliation__mods__sourceDropChanceDisagreements");
    }

    /** 数据质量统计的正反索引核对统计的资源列表的仅正向索引存在的记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__resources__forwardOnlyRows")
    private BigDecimal qualityReverseIndexReconciliationResourcesForwardOnlyRows;

    /**
     * 记录数据质量统计的正反索引核对统计的资源列表的仅正向索引存在的记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的资源列表的仅正向索引存在的记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__resources__forwardOnlyRows")
    public void setQualityReverseIndexReconciliationResourcesForwardOnlyRows(BigDecimal value) {
        this.qualityReverseIndexReconciliationResourcesForwardOnlyRows = value;
        markPresent("quality__reverseIndexReconciliation__resources__forwardOnlyRows");
    }

    /** 数据质量统计的正反索引核对统计的资源列表的正向索引记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__resources__forwardRows")
    private BigDecimal qualityReverseIndexReconciliationResourcesForwardRows;

    /**
     * 记录数据质量统计的正反索引核对统计的资源列表的正向索引记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的资源列表的正向索引记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__resources__forwardRows")
    public void setQualityReverseIndexReconciliationResourcesForwardRows(BigDecimal value) {
        this.qualityReverseIndexReconciliationResourcesForwardRows = value;
        markPresent("quality__reverseIndexReconciliation__resources__forwardRows");
    }

    /** 数据质量统计的正反索引核对统计的资源列表的已匹配记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__resources__matchedRows")
    private BigDecimal qualityReverseIndexReconciliationResourcesMatchedRows;

    /**
     * 记录数据质量统计的正反索引核对统计的资源列表的已匹配记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的资源列表的已匹配记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__resources__matchedRows")
    public void setQualityReverseIndexReconciliationResourcesMatchedRows(BigDecimal value) {
        this.qualityReverseIndexReconciliationResourcesMatchedRows = value;
        markPresent("quality__reverseIndexReconciliation__resources__matchedRows");
    }

    /** 数据质量统计的正反索引核对统计的资源列表的概率不一致的记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__resources__probabilityDisagreements")
    private BigDecimal qualityReverseIndexReconciliationResourcesProbabilityDisagreements;

    /**
     * 记录数据质量统计的正反索引核对统计的资源列表的概率不一致的记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的资源列表的概率不一致的记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__resources__probabilityDisagreements")
    public void setQualityReverseIndexReconciliationResourcesProbabilityDisagreements(BigDecimal value) {
        this.qualityReverseIndexReconciliationResourcesProbabilityDisagreements = value;
        markPresent("quality__reverseIndexReconciliation__resources__probabilityDisagreements");
    }

    /** 数据质量统计的正反索引核对统计的资源列表的仅反向索引存在的记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__resources__reverseOnlyRows")
    private BigDecimal qualityReverseIndexReconciliationResourcesReverseOnlyRows;

    /**
     * 记录数据质量统计的正反索引核对统计的资源列表的仅反向索引存在的记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的资源列表的仅反向索引存在的记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__resources__reverseOnlyRows")
    public void setQualityReverseIndexReconciliationResourcesReverseOnlyRows(BigDecimal value) {
        this.qualityReverseIndexReconciliationResourcesReverseOnlyRows = value;
        markPresent("quality__reverseIndexReconciliation__resources__reverseOnlyRows");
    }

    /** 数据质量统计的正反索引核对统计的资源列表的反向索引记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__resources__reverseRows")
    private BigDecimal qualityReverseIndexReconciliationResourcesReverseRows;

    /**
     * 记录数据质量统计的正反索引核对统计的资源列表的反向索引记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的资源列表的反向索引记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__resources__reverseRows")
    public void setQualityReverseIndexReconciliationResourcesReverseRows(BigDecimal value) {
        this.qualityReverseIndexReconciliationResourcesReverseRows = value;
        markPresent("quality__reverseIndexReconciliation__resources__reverseRows");
    }

    /** 数据质量统计的正反索引核对统计的资源列表的来源掉落几率不一致的记录数；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__reverseIndexReconciliation__resources__sourceDropChanceDisagreements")
    private BigDecimal qualityReverseIndexReconciliationResourcesSourceDropChanceDisagreements;

    /**
     * 记录数据质量统计的正反索引核对统计的资源列表的来源掉落几率不一致的记录数是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的正反索引核对统计的资源列表的来源掉落几率不一致的记录数，保留协议中的显式空值。
     */
    @JsonSetter("quality__reverseIndexReconciliation__resources__sourceDropChanceDisagreements")
    public void setQualityReverseIndexReconciliationResourcesSourceDropChanceDisagreements(BigDecimal value) {
        this.qualityReverseIndexReconciliationResourcesSourceDropChanceDisagreements = value;
        markPresent("quality__reverseIndexReconciliation__resources__sourceDropChanceDisagreements");
    }

    /** 数据质量统计的奖励数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__rewardCount")
    private BigDecimal qualityRewardCount;

    /**
     * 记录数据质量统计的奖励数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的奖励数量，保留协议中的显式空值。
     */
    @JsonSetter("quality__rewardCount")
    public void setQualityRewardCount(BigDecimal value) {
        this.qualityRewardCount = value;
        markPresent("quality__rewardCount");
    }

    /** 数据质量统计的来源场景数量；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("quality__sourceContextCount")
    private BigDecimal qualitySourceContextCount;

    /**
     * 记录数据质量统计的来源场景数量是否出现，同时区分未提供与显式空值。
     *
     * @param value 数据质量统计的来源场景数量，保留协议中的显式空值。
     */
    @JsonSetter("quality__sourceContextCount")
    public void setQualitySourceContextCount(BigDecimal value) {
        this.qualitySourceContextCount = value;
        markPresent("quality__sourceContextCount");
    }

    /** 资源列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("resources")
    private List<ResourcesDTO> resources;

    /**
     * 记录资源列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 资源列表，保留协议中的显式空值。
     */
    @JsonSetter("resources")
    public void setResources(List<ResourcesDTO> value) {
        this.resources = value;
        markPresent("resources");
    }

    /** 奖励；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("rewards")
    private List<RewardsDTO> rewards;

    /**
     * 记录奖励是否出现，同时区分未提供与显式空值。
     *
     * @param value 奖励，保留协议中的显式空值。
     */
    @JsonSetter("rewards")
    public void setRewards(List<RewardsDTO> value) {
        this.rewards = value;
        markPresent("rewards");
    }

    /** 来源信息的候选物品唯一标识列表；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("source__candidateUniqueNames")
    private List<String> sourceCandidateUniqueNames;

    /**
     * 记录来源信息的候选物品唯一标识列表是否出现，同时区分未提供与显式空值。
     *
     * @param value 来源信息的候选物品唯一标识列表，保留协议中的显式空值。
     */
    @JsonSetter("source__candidateUniqueNames")
    public void setSourceCandidateUniqueNames(List<String> value) {
        this.sourceCandidateUniqueNames = value;
        markPresent("source__candidateUniqueNames");
    }

    /** 来源信息的内容解析状态；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("source__contentResolution")
    private String sourceContentResolution;

    /**
     * 记录来源信息的内容解析状态是否出现，同时区分未提供与显式空值。
     *
     * @param value 来源信息的内容解析状态，保留协议中的显式空值。
     */
    @JsonSetter("source__contentResolution")
    public void setSourceContentResolution(String value) {
        this.sourceContentResolution = value;
        markPresent("source__contentResolution");
    }

    /** 来源信息的图标状态；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("source__iconStatus")
    private String sourceIconStatus;

    /**
     * 记录来源信息的图标状态是否出现，同时区分未提供与显式空值。
     *
     * @param value 来源信息的图标状态，保留协议中的显式空值。
     */
    @JsonSetter("source__iconStatus")
    public void setSourceIconStatus(String value) {
        this.sourceIconStatus = value;
        markPresent("source__iconStatus");
    }

    /** 来源信息的图标地址；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("source__iconUrl")
    private String sourceIconUrl;

    /**
     * 记录来源信息的图标地址是否出现，同时区分未提供与显式空值。
     *
     * @param value 来源信息的图标地址，保留协议中的显式空值。
     */
    @JsonSetter("source__iconUrl")
    public void setSourceIconUrl(String value) {
        this.sourceIconUrl = value;
        markPresent("source__iconUrl");
    }

    /** 来源信息的身份解析状态；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("source__identityResolution")
    private String sourceIdentityResolution;

    /**
     * 记录来源信息的身份解析状态是否出现，同时区分未提供与显式空值。
     *
     * @param value 来源信息的身份解析状态，保留协议中的显式空值。
     */
    @JsonSetter("source__identityResolution")
    public void setSourceIdentityResolution(String value) {
        this.sourceIdentityResolution = value;
        markPresent("source__identityResolution");
    }

    /** 来源信息的种类；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("source__kind")
    private String sourceKind;

    /**
     * 记录来源信息的种类是否出现，同时区分未提供与显式空值。
     *
     * @param value 来源信息的种类，保留协议中的显式空值。
     */
    @JsonSetter("source__kind")
    public void setSourceKind(String value) {
        this.sourceKind = value;
        markPresent("source__kind");
    }

    /** 来源信息的名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("source__name")
    private String sourceName;

    /**
     * 记录来源信息的名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 来源信息的名称，保留协议中的显式空值。
     */
    @JsonSetter("source__name")
    public void setSourceName(String value) {
        this.sourceName = value;
        markPresent("source__name");
    }

    /** 来源信息的英文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("source__nameEn")
    private String sourceNameEn;

    /**
     * 记录来源信息的英文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 来源信息的英文名称，保留协议中的显式空值。
     */
    @JsonSetter("source__nameEn")
    public void setSourceNameEn(String value) {
        this.sourceNameEn = value;
        markPresent("source__nameEn");
    }

    /** 来源信息的中文名称；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("source__nameZh")
    private String sourceNameZh;

    /**
     * 记录来源信息的中文名称是否出现，同时区分未提供与显式空值。
     *
     * @param value 来源信息的中文名称，保留协议中的显式空值。
     */
    @JsonSetter("source__nameZh")
    public void setSourceNameZh(String value) {
        this.sourceNameZh = value;
        markPresent("source__nameZh");
    }

    /** 来源信息的唯一标识；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("source__uniqueName")
    private String sourceUniqueName;

    /**
     * 记录来源信息的唯一标识是否出现，同时区分未提供与显式空值。
     *
     * @param value 来源信息的唯一标识，保留协议中的显式空值。
     */
    @JsonSetter("source__uniqueName")
    public void setSourceUniqueName(String value) {
        this.sourceUniqueName = value;
        markPresent("source__uniqueName");
    }

    /** 来源掉落几率百分比；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("sourceDropChancePercent")
    private BigDecimal sourceDropChancePercent;

    /**
     * 记录来源掉落几率百分比是否出现，同时区分未提供与显式空值。
     *
     * @param value 来源掉落几率百分比，保留协议中的显式空值。
     */
    @JsonSetter("sourceDropChancePercent")
    public void setSourceDropChancePercent(BigDecimal value) {
        this.sourceDropChancePercent = value;
        markPresent("sourceDropChancePercent");
    }

    /** 星系序号；保留该字段的原始类型与缺省状态。 */
    @JsonProperty("systemIndex")
    private BigDecimal systemIndex;

    /**
     * 记录星系序号是否出现，同时区分未提供与显式空值。
     *
     * @param value 星系序号，保留协议中的显式空值。
     */
    @JsonSetter("systemIndex")
    public void setSystemIndex(BigDecimal value) {
        this.systemIndex = value;
        markPresent("systemIndex");
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
        return OfficialInputText.first(poolId);
    }

    /** 官方嵌套对象 GroupsRewardsItemCandidatesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class GroupsRewardsItemCandidatesDTO extends OfficialObjectDTO {
        /** 分组列表的奖励的物品的候选项列表的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录分组列表的奖励的物品的候选项列表的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的候选项列表的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 分组列表的奖励的物品的候选项列表的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录分组列表的奖励的物品的候选项列表的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的候选项列表的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 分组列表的奖励的物品的候选项列表的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录分组列表的奖励的物品的候选项列表的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的候选项列表的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 分组列表的奖励的物品的候选项列表的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录分组列表的奖励的物品的候选项列表的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的候选项列表的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 分组列表的奖励的物品的候选项列表的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录分组列表的奖励的物品的候选项列表的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的候选项列表的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 分组列表的奖励的物品的候选项列表的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录分组列表的奖励的物品的候选项列表的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的候选项列表的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 GroupsRewardsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class GroupsRewardsDTO extends OfficialObjectDTO {
        /** 分组列表的奖励的几率解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chanceResolution")
        private String chanceResolution;

        /**
         * 记录分组列表的奖励的几率解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的几率解析状态，保留协议中的显式空值。
         */
        @JsonSetter("chanceResolution")
        public void setChanceResolution(String value) {
            this.chanceResolution = value;
            markPresent("chanceResolution");
        }

        /** 分组列表的奖励的掉落几率百分比；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("dropChancePercent")
        private BigDecimal dropChancePercent;

        /**
         * 记录分组列表的奖励的掉落几率百分比是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的掉落几率百分比，保留协议中的显式空值。
         */
        @JsonSetter("dropChancePercent")
        public void setDropChancePercent(BigDecimal value) {
            this.dropChancePercent = value;
            markPresent("dropChancePercent");
        }

        /** 分组列表的奖励的物品的候选项列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__candidates")
        private List<GroupsRewardsItemCandidatesDTO> itemCandidates;

        /**
         * 记录分组列表的奖励的物品的候选项列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的候选项列表，保留协议中的显式空值。
         */
        @JsonSetter("item__candidates")
        public void setItemCandidates(List<GroupsRewardsItemCandidatesDTO> value) {
            this.itemCandidates = value;
            markPresent("item__candidates");
        }

        /** 分组列表的奖励的物品的数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__count")
        private BigDecimal itemCount;

        /**
         * 记录分组列表的奖励的物品的数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的数量，保留协议中的显式空值。
         */
        @JsonSetter("item__count")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("item__count");
        }

        /** 分组列表的奖励的物品的货币；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__currency")
        private String itemCurrency;

        /**
         * 记录分组列表的奖励的物品的货币是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的货币，保留协议中的显式空值。
         */
        @JsonSetter("item__currency")
        public void setItemCurrency(String value) {
            this.itemCurrency = value;
            markPresent("item__currency");
        }

        /** 分组列表的奖励的物品的展示名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__displayName")
        private String itemDisplayName;

        /**
         * 记录分组列表的奖励的物品的展示名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的展示名称，保留协议中的显式空值。
         */
        @JsonSetter("item__displayName")
        public void setItemDisplayName(String value) {
            this.itemDisplayName = value;
            markPresent("item__displayName");
        }

        /** 分组列表的奖励的物品的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconStatus")
        private String itemIconStatus;

        /**
         * 记录分组列表的奖励的物品的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("item__iconStatus")
        public void setItemIconStatus(String value) {
            this.itemIconStatus = value;
            markPresent("item__iconStatus");
        }

        /** 分组列表的奖励的物品的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconUrl")
        private String itemIconUrl;

        /**
         * 记录分组列表的奖励的物品的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("item__iconUrl")
        public void setItemIconUrl(String value) {
            this.itemIconUrl = value;
            markPresent("item__iconUrl");
        }

        /** 分组列表的奖励的物品的身份解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__identityResolution")
        private String itemIdentityResolution;

        /**
         * 记录分组列表的奖励的物品的身份解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的身份解析状态，保留协议中的显式空值。
         */
        @JsonSetter("item__identityResolution")
        public void setItemIdentityResolution(String value) {
            this.itemIdentityResolution = value;
            markPresent("item__identityResolution");
        }

        /** 分组列表的奖励的物品的种类；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__kind")
        private String itemKind;

        /**
         * 记录分组列表的奖励的物品的种类是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的种类，保留协议中的显式空值。
         */
        @JsonSetter("item__kind")
        public void setItemKind(String value) {
            this.itemKind = value;
            markPresent("item__kind");
        }

        /** 分组列表的奖励的物品的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__name")
        private String itemName;

        /**
         * 记录分组列表的奖励的物品的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的名称，保留协议中的显式空值。
         */
        @JsonSetter("item__name")
        public void setItemName(String value) {
            this.itemName = value;
            markPresent("item__name");
        }

        /** 分组列表的奖励的物品的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameEn")
        private String itemNameEn;

        /**
         * 记录分组列表的奖励的物品的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameEn")
        public void setItemNameEn(String value) {
            this.itemNameEn = value;
            markPresent("item__nameEn");
        }

        /** 分组列表的奖励的物品的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameZh")
        private String itemNameZh;

        /**
         * 记录分组列表的奖励的物品的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameZh")
        public void setItemNameZh(String value) {
            this.itemNameZh = value;
            markPresent("item__nameZh");
        }

        /** 分组列表的奖励的物品的概率适用范围；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__probabilityScope")
        private String itemProbabilityScope;

        /**
         * 记录分组列表的奖励的物品的概率适用范围是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的概率适用范围，保留协议中的显式空值。
         */
        @JsonSetter("item__probabilityScope")
        public void setItemProbabilityScope(String value) {
            this.itemProbabilityScope = value;
            markPresent("item__probabilityScope");
        }

        /** 分组列表的奖励的物品的数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__quantity")
        private BigDecimal itemQuantity;

        /**
         * 记录分组列表的奖励的物品的数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的数量，保留协议中的显式空值。
         */
        @JsonSetter("item__quantity")
        public void setItemQuantity(BigDecimal value) {
            this.itemQuantity = value;
            markPresent("item__quantity");
        }

        /** 分组列表的奖励的物品的精炼等级；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__refinement")
        private String itemRefinement;

        /**
         * 记录分组列表的奖励的物品的精炼等级是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的精炼等级，保留协议中的显式空值。
         */
        @JsonSetter("item__refinement")
        public void setItemRefinement(String value) {
            this.itemRefinement = value;
            markPresent("item__refinement");
        }

        /** 分组列表的奖励的物品的解析依据；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__resolutionBasis")
        private String itemResolutionBasis;

        /**
         * 记录分组列表的奖励的物品的解析依据是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的解析依据，保留协议中的显式空值。
         */
        @JsonSetter("item__resolutionBasis")
        public void setItemResolutionBasis(String value) {
            this.itemResolutionBasis = value;
            markPresent("item__resolutionBasis");
        }

        /** 分组列表的奖励的物品的选择项；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__selection")
        private String itemSelection;

        /**
         * 记录分组列表的奖励的物品的选择项是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的选择项，保留协议中的显式空值。
         */
        @JsonSetter("item__selection")
        public void setItemSelection(String value) {
            this.itemSelection = value;
            markPresent("item__selection");
        }

        /** 分组列表的奖励的物品的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__uniqueName")
        private String itemUniqueName;

        /**
         * 记录分组列表的奖励的物品的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的物品的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("item__uniqueName")
        public void setItemUniqueName(String value) {
            this.itemUniqueName = value;
            markPresent("item__uniqueName");
        }

        /** 分组列表的奖励的概率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("probability")
        private BigDecimal probability;

        /**
         * 记录分组列表的奖励的概率是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的概率，保留协议中的显式空值。
         */
        @JsonSetter("probability")
        public void setProbability(BigDecimal value) {
            this.probability = value;
            markPresent("probability");
        }

        /** 分组列表的奖励的稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rarity")
        private String rarity;

        /**
         * 记录分组列表的奖励的稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励的稀有度，保留协议中的显式空值。
         */
        @JsonSetter("rarity")
        public void setRarity(String value) {
            this.rarity = value;
            markPresent("rarity");
        }

    }

    /** 官方嵌套对象 GroupsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class GroupsDTO extends OfficialObjectDTO {
        /** 分组列表的分组列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("groups")
        @JsonDeserialize(contentUsing = OfficialNullDeserializer.class)
        private List<Void> groups;

        /**
         * 记录分组列表的分组列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的分组列表，保留协议中的显式空值。
         */
        @JsonSetter("groups")
        public void setGroups(List<Void> value) {
            this.groups = value;
            markPresent("groups");
        }

        /** 分组列表的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录分组列表的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 分组列表的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录分组列表的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 分组列表的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录分组列表的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 分组列表的奖励；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rewards")
        private List<GroupsRewardsDTO> rewards;

        /**
         * 记录分组列表的奖励是否出现，同时区分未提供与显式空值。
         *
         * @param value 分组列表的奖励，保留协议中的显式空值。
         */
        @JsonSetter("rewards")
        public void setRewards(List<GroupsRewardsDTO> value) {
            this.rewards = value;
            markPresent("rewards");
        }

    }

    /** 官方嵌套对象 ResourcesItemReferencesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ResourcesItemReferencesDTO extends OfficialObjectDTO {
        /** 资源列表的物品引用的领域；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("domain")
        private String domain;

        /**
         * 记录资源列表的物品引用的领域是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的物品引用的领域，保留协议中的显式空值。
         */
        @JsonSetter("domain")
        public void setDomain(String value) {
            this.domain = value;
            markPresent("domain");
        }

        /** 资源列表的物品引用的字段；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("field")
        private String field;

        /**
         * 记录资源列表的物品引用的字段是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的物品引用的字段，保留协议中的显式空值。
         */
        @JsonSetter("field")
        public void setField(String value) {
            this.field = value;
            markPresent("field");
        }

        /** 资源列表的物品引用的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录资源列表的物品引用的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的物品引用的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 资源列表的物品引用的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录资源列表的物品引用的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的物品引用的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 资源列表的物品引用的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录资源列表的物品引用的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的物品引用的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 资源列表的物品引用的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录资源列表的物品引用的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的物品引用的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 资源列表的物品引用的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录资源列表的物品引用的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的物品引用的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 资源列表的物品引用的引用类型；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("referenceKind")
        private String referenceKind;

        /**
         * 记录资源列表的物品引用的引用类型是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的物品引用的引用类型，保留协议中的显式空值。
         */
        @JsonSetter("referenceKind")
        public void setReferenceKind(String value) {
            this.referenceKind = value;
            markPresent("referenceKind");
        }

        /** 资源列表的物品引用的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("resolution")
        private String resolution;

        /**
         * 记录资源列表的物品引用的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的物品引用的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("resolution")
        public void setResolution(String value) {
            this.resolution = value;
            markPresent("resolution");
        }

        /** 资源列表的物品引用的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录资源列表的物品引用的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的物品引用的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 ResourcesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class ResourcesDTO extends OfficialObjectDTO {
        /** 资源列表的物品的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconStatus")
        private String itemIconStatus;

        /**
         * 记录资源列表的物品的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的物品的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("item__iconStatus")
        public void setItemIconStatus(String value) {
            this.itemIconStatus = value;
            markPresent("item__iconStatus");
        }

        /** 资源列表的物品的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconUrl")
        private String itemIconUrl;

        /**
         * 记录资源列表的物品的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的物品的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("item__iconUrl")
        public void setItemIconUrl(String value) {
            this.itemIconUrl = value;
            markPresent("item__iconUrl");
        }

        /** 资源列表的物品的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__name")
        private String itemName;

        /**
         * 记录资源列表的物品的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的物品的名称，保留协议中的显式空值。
         */
        @JsonSetter("item__name")
        public void setItemName(String value) {
            this.itemName = value;
            markPresent("item__name");
        }

        /** 资源列表的物品的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameEn")
        private String itemNameEn;

        /**
         * 记录资源列表的物品的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的物品的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameEn")
        public void setItemNameEn(String value) {
            this.itemNameEn = value;
            markPresent("item__nameEn");
        }

        /** 资源列表的物品的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameZh")
        private String itemNameZh;

        /**
         * 记录资源列表的物品的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的物品的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameZh")
        public void setItemNameZh(String value) {
            this.itemNameZh = value;
            markPresent("item__nameZh");
        }

        /** 资源列表的物品的解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__resolution")
        private String itemResolution;

        /**
         * 记录资源列表的物品的解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的物品的解析状态，保留协议中的显式空值。
         */
        @JsonSetter("item__resolution")
        public void setItemResolution(String value) {
            this.itemResolution = value;
            markPresent("item__resolution");
        }

        /** 资源列表的物品的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__uniqueName")
        private String itemUniqueName;

        /**
         * 记录资源列表的物品的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的物品的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("item__uniqueName")
        public void setItemUniqueName(String value) {
            this.itemUniqueName = value;
            markPresent("item__uniqueName");
        }

        /** 资源列表的物品引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("itemReferences")
        private List<ResourcesItemReferencesDTO> itemReferences;

        /**
         * 记录资源列表的物品引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的物品引用，保留协议中的显式空值。
         */
        @JsonSetter("itemReferences")
        public void setItemReferences(List<ResourcesItemReferencesDTO> value) {
            this.itemReferences = value;
            markPresent("itemReferences");
        }

        /** 资源列表的物品类型路径；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("ItemType")
        private String itemType;

        /**
         * 记录资源列表的物品类型路径是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的物品类型路径，保留协议中的显式空值。
         */
        @JsonSetter("ItemType")
        public void setItemType(String value) {
            this.itemType = value;
            markPresent("ItemType");
        }

        /** 资源列表的稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("Rarity")
        private String rarity;

        /**
         * 记录资源列表的稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的稀有度，保留协议中的显式空值。
         */
        @JsonSetter("Rarity")
        public void setRarity(String value) {
            this.rarity = value;
            markPresent("Rarity");
        }

        /** 资源列表的商店物品路径；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("StoreItem")
        private String storeItem;

        /**
         * 记录资源列表的商店物品路径是否出现，同时区分未提供与显式空值。
         *
         * @param value 资源列表的商店物品路径，保留协议中的显式空值。
         */
        @JsonSetter("StoreItem")
        public void setStoreItem(String value) {
            this.storeItem = value;
            markPresent("StoreItem");
        }

    }

    /** 官方嵌套对象 RewardsItemCandidatesDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class RewardsItemCandidatesDTO extends OfficialObjectDTO {
        /** 奖励的物品的候选项列表的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconStatus")
        private String iconStatus;

        /**
         * 记录奖励的物品的候选项列表的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的候选项列表的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("iconStatus")
        public void setIconStatus(String value) {
            this.iconStatus = value;
            markPresent("iconStatus");
        }

        /** 奖励的物品的候选项列表的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("iconUrl")
        private String iconUrl;

        /**
         * 记录奖励的物品的候选项列表的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的候选项列表的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("iconUrl")
        public void setIconUrl(String value) {
            this.iconUrl = value;
            markPresent("iconUrl");
        }

        /** 奖励的物品的候选项列表的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("name")
        private String name;

        /**
         * 记录奖励的物品的候选项列表的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的候选项列表的名称，保留协议中的显式空值。
         */
        @JsonSetter("name")
        public void setName(String value) {
            this.name = value;
            markPresent("name");
        }

        /** 奖励的物品的候选项列表的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameEn")
        private String nameEn;

        /**
         * 记录奖励的物品的候选项列表的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的候选项列表的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameEn")
        public void setNameEn(String value) {
            this.nameEn = value;
            markPresent("nameEn");
        }

        /** 奖励的物品的候选项列表的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("nameZh")
        private String nameZh;

        /**
         * 记录奖励的物品的候选项列表的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的候选项列表的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("nameZh")
        public void setNameZh(String value) {
            this.nameZh = value;
            markPresent("nameZh");
        }

        /** 奖励的物品的候选项列表的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("uniqueName")
        private String uniqueName;

        /**
         * 记录奖励的物品的候选项列表的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的候选项列表的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("uniqueName")
        public void setUniqueName(String value) {
            this.uniqueName = value;
            markPresent("uniqueName");
        }

    }

    /** 官方嵌套对象 RewardsDTO 的固定字段。 */
    @Getter
    @JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
            isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class RewardsDTO extends OfficialObjectDTO {
        /** 奖励的几率解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("chanceResolution")
        private String chanceResolution;

        /**
         * 记录奖励的几率解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的几率解析状态，保留协议中的显式空值。
         */
        @JsonSetter("chanceResolution")
        public void setChanceResolution(String value) {
            this.chanceResolution = value;
            markPresent("chanceResolution");
        }

        /** 奖励的掉落几率百分比；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("dropChancePercent")
        private BigDecimal dropChancePercent;

        /**
         * 记录奖励的掉落几率百分比是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的掉落几率百分比，保留协议中的显式空值。
         */
        @JsonSetter("dropChancePercent")
        public void setDropChancePercent(BigDecimal value) {
            this.dropChancePercent = value;
            markPresent("dropChancePercent");
        }

        /** 奖励的物品的候选项列表；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__candidates")
        private List<RewardsItemCandidatesDTO> itemCandidates;

        /**
         * 记录奖励的物品的候选项列表是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的候选项列表，保留协议中的显式空值。
         */
        @JsonSetter("item__candidates")
        public void setItemCandidates(List<RewardsItemCandidatesDTO> value) {
            this.itemCandidates = value;
            markPresent("item__candidates");
        }

        /** 奖励的物品的数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__count")
        private BigDecimal itemCount;

        /**
         * 记录奖励的物品的数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的数量，保留协议中的显式空值。
         */
        @JsonSetter("item__count")
        public void setItemCount(BigDecimal value) {
            this.itemCount = value;
            markPresent("item__count");
        }

        /** 奖励的物品的货币；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__currency")
        private String itemCurrency;

        /**
         * 记录奖励的物品的货币是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的货币，保留协议中的显式空值。
         */
        @JsonSetter("item__currency")
        public void setItemCurrency(String value) {
            this.itemCurrency = value;
            markPresent("item__currency");
        }

        /** 奖励的物品的展示名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__displayName")
        private String itemDisplayName;

        /**
         * 记录奖励的物品的展示名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的展示名称，保留协议中的显式空值。
         */
        @JsonSetter("item__displayName")
        public void setItemDisplayName(String value) {
            this.itemDisplayName = value;
            markPresent("item__displayName");
        }

        /** 奖励的物品的图标状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconStatus")
        private String itemIconStatus;

        /**
         * 记录奖励的物品的图标状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的图标状态，保留协议中的显式空值。
         */
        @JsonSetter("item__iconStatus")
        public void setItemIconStatus(String value) {
            this.itemIconStatus = value;
            markPresent("item__iconStatus");
        }

        /** 奖励的物品的图标地址；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__iconUrl")
        private String itemIconUrl;

        /**
         * 记录奖励的物品的图标地址是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的图标地址，保留协议中的显式空值。
         */
        @JsonSetter("item__iconUrl")
        public void setItemIconUrl(String value) {
            this.itemIconUrl = value;
            markPresent("item__iconUrl");
        }

        /** 奖励的物品的身份解析状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__identityResolution")
        private String itemIdentityResolution;

        /**
         * 记录奖励的物品的身份解析状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的身份解析状态，保留协议中的显式空值。
         */
        @JsonSetter("item__identityResolution")
        public void setItemIdentityResolution(String value) {
            this.itemIdentityResolution = value;
            markPresent("item__identityResolution");
        }

        /** 奖励的物品的种类；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__kind")
        private String itemKind;

        /**
         * 记录奖励的物品的种类是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的种类，保留协议中的显式空值。
         */
        @JsonSetter("item__kind")
        public void setItemKind(String value) {
            this.itemKind = value;
            markPresent("item__kind");
        }

        /** 奖励的物品的名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__name")
        private String itemName;

        /**
         * 记录奖励的物品的名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的名称，保留协议中的显式空值。
         */
        @JsonSetter("item__name")
        public void setItemName(String value) {
            this.itemName = value;
            markPresent("item__name");
        }

        /** 奖励的物品的英文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameEn")
        private String itemNameEn;

        /**
         * 记录奖励的物品的英文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的英文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameEn")
        public void setItemNameEn(String value) {
            this.itemNameEn = value;
            markPresent("item__nameEn");
        }

        /** 奖励的物品的中文名称；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__nameZh")
        private String itemNameZh;

        /**
         * 记录奖励的物品的中文名称是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的中文名称，保留协议中的显式空值。
         */
        @JsonSetter("item__nameZh")
        public void setItemNameZh(String value) {
            this.itemNameZh = value;
            markPresent("item__nameZh");
        }

        /** 奖励的物品的概率适用范围；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__probabilityScope")
        private String itemProbabilityScope;

        /**
         * 记录奖励的物品的概率适用范围是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的概率适用范围，保留协议中的显式空值。
         */
        @JsonSetter("item__probabilityScope")
        public void setItemProbabilityScope(String value) {
            this.itemProbabilityScope = value;
            markPresent("item__probabilityScope");
        }

        /** 奖励的物品的数量；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__quantity")
        private BigDecimal itemQuantity;

        /**
         * 记录奖励的物品的数量是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的数量，保留协议中的显式空值。
         */
        @JsonSetter("item__quantity")
        public void setItemQuantity(BigDecimal value) {
            this.itemQuantity = value;
            markPresent("item__quantity");
        }

        /** 奖励的物品的精炼等级；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__refinement")
        private String itemRefinement;

        /**
         * 记录奖励的物品的精炼等级是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的精炼等级，保留协议中的显式空值。
         */
        @JsonSetter("item__refinement")
        public void setItemRefinement(String value) {
            this.itemRefinement = value;
            markPresent("item__refinement");
        }

        /** 奖励的物品的区域选择；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__regionSelection")
        private String itemRegionSelection;

        /**
         * 记录奖励的物品的区域选择是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的区域选择，保留协议中的显式空值。
         */
        @JsonSetter("item__regionSelection")
        public void setItemRegionSelection(String value) {
            this.itemRegionSelection = value;
            markPresent("item__regionSelection");
        }

        /** 奖励的物品的解析依据；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__resolutionBasis")
        private String itemResolutionBasis;

        /**
         * 记录奖励的物品的解析依据是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的解析依据，保留协议中的显式空值。
         */
        @JsonSetter("item__resolutionBasis")
        public void setItemResolutionBasis(String value) {
            this.itemResolutionBasis = value;
            markPresent("item__resolutionBasis");
        }

        /** 奖励的物品的资源候选项引用；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__resourceCandidatesRef")
        private String itemResourceCandidatesRef;

        /**
         * 记录奖励的物品的资源候选项引用是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的资源候选项引用，保留协议中的显式空值。
         */
        @JsonSetter("item__resourceCandidatesRef")
        public void setItemResourceCandidatesRef(String value) {
            this.itemResourceCandidatesRef = value;
            markPresent("item__resourceCandidatesRef");
        }

        /** 奖励的物品的规则键；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__ruleKey")
        private String itemRuleKey;

        /**
         * 记录奖励的物品的规则键是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的规则键，保留协议中的显式空值。
         */
        @JsonSetter("item__ruleKey")
        public void setItemRuleKey(String value) {
            this.itemRuleKey = value;
            markPresent("item__ruleKey");
        }

        /** 奖励的物品的选择项；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__selection")
        private String itemSelection;

        /**
         * 记录奖励的物品的选择项是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的选择项，保留协议中的显式空值。
         */
        @JsonSetter("item__selection")
        public void setItemSelection(String value) {
            this.itemSelection = value;
            markPresent("item__selection");
        }

        /** 奖励的物品的唯一标识；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("item__uniqueName")
        private String itemUniqueName;

        /**
         * 记录奖励的物品的唯一标识是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的物品的唯一标识，保留协议中的显式空值。
         */
        @JsonSetter("item__uniqueName")
        public void setItemUniqueName(String value) {
            this.itemUniqueName = value;
            markPresent("item__uniqueName");
        }

        /** 奖励的概率；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("probability")
        private BigDecimal probability;

        /**
         * 记录奖励的概率是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的概率，保留协议中的显式空值。
         */
        @JsonSetter("probability")
        public void setProbability(BigDecimal value) {
            this.probability = value;
            markPresent("probability");
        }

        /** 奖励的稀有度；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("rarity")
        private String rarity;

        /**
         * 记录奖励的稀有度是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的稀有度，保留协议中的显式空值。
         */
        @JsonSetter("rarity")
        public void setRarity(String value) {
            this.rarity = value;
            markPresent("rarity");
        }

        /** 奖励的反向索引的来源掉落几率百分比；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("reverseIndex__sourceDropChancePercent")
        private BigDecimal reverseIndexSourceDropChancePercent;

        /**
         * 记录奖励的反向索引的来源掉落几率百分比是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的反向索引的来源掉落几率百分比，保留协议中的显式空值。
         */
        @JsonSetter("reverseIndex__sourceDropChancePercent")
        public void setReverseIndexSourceDropChancePercent(BigDecimal value) {
            this.reverseIndexSourceDropChancePercent = value;
            markPresent("reverseIndex__sourceDropChancePercent");
        }

        /** 奖励的反向索引的状态；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("reverseIndex__status")
        private String reverseIndexStatus;

        /**
         * 记录奖励的反向索引的状态是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的反向索引的状态，保留协议中的显式空值。
         */
        @JsonSetter("reverseIndex__status")
        public void setReverseIndexStatus(String value) {
            this.reverseIndexStatus = value;
            markPresent("reverseIndex__status");
        }

        /** 奖励的来源掉落几率百分比；保留该字段的原始类型与缺省状态。 */
        @JsonProperty("sourceDropChancePercent")
        private BigDecimal sourceDropChancePercent;

        /**
         * 记录奖励的来源掉落几率百分比是否出现，同时区分未提供与显式空值。
         *
         * @param value 奖励的来源掉落几率百分比，保留协议中的显式空值。
         */
        @JsonSetter("sourceDropChancePercent")
        public void setSourceDropChancePercent(BigDecimal value) {
            this.sourceDropChancePercent = value;
            markPresent("sourceDropChancePercent");
        }

    }

}
