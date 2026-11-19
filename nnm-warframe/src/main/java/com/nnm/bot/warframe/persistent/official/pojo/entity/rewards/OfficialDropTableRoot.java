package com.nnm.bot.warframe.persistent.official.pojo.entity.rewards;

import com.nnm.bot.warframe.persistent.official.pojo.entity.support.OfficialRecord;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/** 源 DROP_TABLE 独立主表 的固定业务列；由生成器和契约测试共同维护。 */
@Getter
@Setter
@Entity
@Table(name = "wf_official_drop_table_root")
public class OfficialDropTableRoot extends OfficialRecord {
    /** 本表记录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "root_state")
    private Short rootState;

    /** 启动概率是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "activationprobability_state")
    private Short activationProbabilityState;

    /** 启动概率。 */
    @Column(name = "activationprobability", columnDefinition = "NUMERIC")
    private BigDecimal activationProbability;

    /** 赏金目录是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "bountycatalog_state")
    private Short bountyCatalogState;

    /** 赏金目录。 */
    @Column(name = "bountycatalog", columnDefinition = "TEXT")
    private String bountyCatalog;

    /** 分类是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "category_state")
    private Short categoryState;

    /** 分类。 */
    @Column(name = "category", columnDefinition = "TEXT")
    private String category;

    /** 抽取次数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "drawcount_state")
    private Short drawCountState;

    /** 抽取次数。 */
    @Column(name = "drawcount", columnDefinition = "NUMERIC")
    private BigDecimal drawCount;

    /** 抽取方式是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "drawmode_state")
    private Short drawModeState;

    /** 抽取方式。 */
    @Column(name = "drawmode", columnDefinition = "TEXT")
    private String drawMode;

    /** 分组列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "groups_state")
    private Short groupsState;

    /** 名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "name_state")
    private Short nameState;

    /** 名称。 */
    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    /** 英文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "nameen_state")
    private Short nameEnState;

    /** 英文名称。 */
    @Column(name = "nameen", columnDefinition = "TEXT")
    private String nameEn;

    /** 中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "namezh_state")
    private Short nameZhState;

    /** 中文名称。 */
    @Column(name = "namezh", columnDefinition = "TEXT")
    private String nameZh;

    /** 奖池标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "poolid_state")
    private Short poolIdState;

    /** 奖池标识。 */
    @Column(name = "poolid", columnDefinition = "TEXT")
    private String poolId;

    /** 数据质量统计的物品身份统计的存在身份歧义的记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_itemidentity_ambiguous_state")
    private Short qualityItemIdentityAmbiguousState;

    /** 数据质量统计的物品身份统计的存在身份歧义的记录数。 */
    @Column(name = "quality_itemidentity_ambiguous", columnDefinition = "NUMERIC")
    private BigDecimal qualityItemIdentityAmbiguous;

    /** 数据质量统计的物品身份统计的分类统计是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_itemidentity_categories_state")
    private Short qualityItemIdentityCategoriesState;

    /** 数据质量统计的物品身份统计的分类统计。 */
    @Column(name = "quality_itemidentity_categories", columnDefinition = "NUMERIC")
    private BigDecimal qualityItemIdentityCategories;

    /** 数据质量统计的物品身份统计的货币统计是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_itemidentity_currencies_state")
    private Short qualityItemIdentityCurrenciesState;

    /** 数据质量统计的物品身份统计的货币统计。 */
    @Column(name = "quality_itemidentity_currencies", columnDefinition = "NUMERIC")
    private BigDecimal qualityItemIdentityCurrencies;

    /** 数据质量统计的物品身份统计的规则统计是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_itemidentity_rules_state")
    private Short qualityItemIdentityRulesState;

    /** 数据质量统计的物品身份统计的规则统计。 */
    @Column(name = "quality_itemidentity_rules", columnDefinition = "NUMERIC")
    private BigDecimal qualityItemIdentityRules;

    /** 数据质量统计的物品身份统计的唯一身份记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_itemidentity_unique_state")
    private Short qualityItemIdentityUniqueState;

    /** 数据质量统计的物品身份统计的唯一身份记录数。 */
    @Column(name = "quality_itemidentity_unique", columnDefinition = "NUMERIC")
    private BigDecimal qualityItemIdentityUnique;

    /** 数据质量统计的物品身份统计的未解析是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_itemidentity_unresolved_state")
    private Short qualityItemIdentityUnresolvedState;

    /** 数据质量统计的物品身份统计的未解析。 */
    @Column(name = "quality_itemidentity_unresolved", columnDefinition = "NUMERIC")
    private BigDecimal qualityItemIdentityUnresolved;

    /** 数据质量统计的正反索引核对统计的蓝图的仅正向索引存在的记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_bluepr_3591e72403_state")
    private Short qualityReverseIndexReconciliationBlueprintsForwardOnlyRowsState;

    /** 数据质量统计的正反索引核对统计的蓝图的仅正向索引存在的记录数。 */
    @Column(name = "quality_reverseindexreconciliation_bluepr_3591e72403", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationBlueprintsForwardOnlyRows;

    /** 数据质量统计的正反索引核对统计的蓝图的正向索引记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_bluepr_d1c5ff4ab7_state")
    private Short qualityReverseIndexReconciliationBlueprintsForwardRowsState;

    /** 数据质量统计的正反索引核对统计的蓝图的正向索引记录数。 */
    @Column(name = "quality_reverseindexreconciliation_bluepr_d1c5ff4ab7", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationBlueprintsForwardRows;

    /** 数据质量统计的正反索引核对统计的蓝图的已匹配记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_bluepr_c14863f614_state")
    private Short qualityReverseIndexReconciliationBlueprintsMatchedRowsState;

    /** 数据质量统计的正反索引核对统计的蓝图的已匹配记录数。 */
    @Column(name = "quality_reverseindexreconciliation_bluepr_c14863f614", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationBlueprintsMatchedRows;

    /** 数据质量统计的正反索引核对统计的蓝图的概率不一致的记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_bluepr_20132ddedf_state")
    private Short qualityReverseIndexReconciliationBlueprintsProbabilityDisagreementsState;

    /** 数据质量统计的正反索引核对统计的蓝图的概率不一致的记录数。 */
    @Column(name = "quality_reverseindexreconciliation_bluepr_20132ddedf", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationBlueprintsProbabilityDisagreements;

    /** 数据质量统计的正反索引核对统计的蓝图的仅反向索引存在的记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_bluepr_de0e048825_state")
    private Short qualityReverseIndexReconciliationBlueprintsReverseOnlyRowsState;

    /** 数据质量统计的正反索引核对统计的蓝图的仅反向索引存在的记录数。 */
    @Column(name = "quality_reverseindexreconciliation_bluepr_de0e048825", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationBlueprintsReverseOnlyRows;

    /** 数据质量统计的正反索引核对统计的蓝图的反向索引记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_bluepr_758f9d0ee9_state")
    private Short qualityReverseIndexReconciliationBlueprintsReverseRowsState;

    /** 数据质量统计的正反索引核对统计的蓝图的反向索引记录数。 */
    @Column(name = "quality_reverseindexreconciliation_bluepr_758f9d0ee9", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationBlueprintsReverseRows;

    /** 数据质量统计的正反索引核对统计的蓝图的来源掉落几率不一致的记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_bluepr_1bac733a44_state")
    private Short qualityReverseIndexReconciliationBlueprintsSourceDropChanceDisagreementsState;

    /** 数据质量统计的正反索引核对统计的蓝图的来源掉落几率不一致的记录数。 */
    @Column(name = "quality_reverseindexreconciliation_bluepr_1bac733a44", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationBlueprintsSourceDropChanceDisagreements;

    /** 数据质量统计的正反索引核对统计的振幅晶体的仅正向索引存在的记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_mods_f_05d9680bbc_state")
    private Short qualityReverseIndexReconciliationModsForwardOnlyRowsState;

    /** 数据质量统计的正反索引核对统计的振幅晶体的仅正向索引存在的记录数。 */
    @Column(name = "quality_reverseindexreconciliation_mods_f_05d9680bbc", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationModsForwardOnlyRows;

    /** 数据质量统计的正反索引核对统计的振幅晶体的正向索引记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_mods_forwardrows_state")
    private Short qualityReverseIndexReconciliationModsForwardRowsState;

    /** 数据质量统计的正反索引核对统计的振幅晶体的正向索引记录数。 */
    @Column(name = "quality_reverseindexreconciliation_mods_forwardrows", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationModsForwardRows;

    /** 数据质量统计的正反索引核对统计的振幅晶体的已匹配记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_mods_matchedrows_state")
    private Short qualityReverseIndexReconciliationModsMatchedRowsState;

    /** 数据质量统计的正反索引核对统计的振幅晶体的已匹配记录数。 */
    @Column(name = "quality_reverseindexreconciliation_mods_matchedrows", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationModsMatchedRows;

    /** 数据质量统计的正反索引核对统计的振幅晶体的概率不一致的记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_mods_p_70155d49e9_state")
    private Short qualityReverseIndexReconciliationModsProbabilityDisagreementsState;

    /** 数据质量统计的正反索引核对统计的振幅晶体的概率不一致的记录数。 */
    @Column(name = "quality_reverseindexreconciliation_mods_p_70155d49e9", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationModsProbabilityDisagreements;

    /** 数据质量统计的正反索引核对统计的振幅晶体的仅反向索引存在的记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_mods_r_bf1e48b205_state")
    private Short qualityReverseIndexReconciliationModsReverseOnlyRowsState;

    /** 数据质量统计的正反索引核对统计的振幅晶体的仅反向索引存在的记录数。 */
    @Column(name = "quality_reverseindexreconciliation_mods_r_bf1e48b205", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationModsReverseOnlyRows;

    /** 数据质量统计的正反索引核对统计的振幅晶体的反向索引记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_mods_reverserows_state")
    private Short qualityReverseIndexReconciliationModsReverseRowsState;

    /** 数据质量统计的正反索引核对统计的振幅晶体的反向索引记录数。 */
    @Column(name = "quality_reverseindexreconciliation_mods_reverserows", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationModsReverseRows;

    /** 数据质量统计的正反索引核对统计的振幅晶体的来源掉落几率不一致的记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_mods_s_768a096e59_state")
    private Short qualityReverseIndexReconciliationModsSourceDropChanceDisagreementsState;

    /** 数据质量统计的正反索引核对统计的振幅晶体的来源掉落几率不一致的记录数。 */
    @Column(name = "quality_reverseindexreconciliation_mods_s_768a096e59", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationModsSourceDropChanceDisagreements;

    /** 数据质量统计的正反索引核对统计的资源列表的仅正向索引存在的记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_resour_8fd308a6fd_state")
    private Short qualityReverseIndexReconciliationResourcesForwardOnlyRowsState;

    /** 数据质量统计的正反索引核对统计的资源列表的仅正向索引存在的记录数。 */
    @Column(name = "quality_reverseindexreconciliation_resour_8fd308a6fd", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationResourcesForwardOnlyRows;

    /** 数据质量统计的正反索引核对统计的资源列表的正向索引记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_resour_5c5219f1bd_state")
    private Short qualityReverseIndexReconciliationResourcesForwardRowsState;

    /** 数据质量统计的正反索引核对统计的资源列表的正向索引记录数。 */
    @Column(name = "quality_reverseindexreconciliation_resour_5c5219f1bd", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationResourcesForwardRows;

    /** 数据质量统计的正反索引核对统计的资源列表的已匹配记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_resour_901b957dc3_state")
    private Short qualityReverseIndexReconciliationResourcesMatchedRowsState;

    /** 数据质量统计的正反索引核对统计的资源列表的已匹配记录数。 */
    @Column(name = "quality_reverseindexreconciliation_resour_901b957dc3", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationResourcesMatchedRows;

    /** 数据质量统计的正反索引核对统计的资源列表的概率不一致的记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_resour_772c1d12b5_state")
    private Short qualityReverseIndexReconciliationResourcesProbabilityDisagreementsState;

    /** 数据质量统计的正反索引核对统计的资源列表的概率不一致的记录数。 */
    @Column(name = "quality_reverseindexreconciliation_resour_772c1d12b5", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationResourcesProbabilityDisagreements;

    /** 数据质量统计的正反索引核对统计的资源列表的仅反向索引存在的记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_resour_7f59b213ac_state")
    private Short qualityReverseIndexReconciliationResourcesReverseOnlyRowsState;

    /** 数据质量统计的正反索引核对统计的资源列表的仅反向索引存在的记录数。 */
    @Column(name = "quality_reverseindexreconciliation_resour_7f59b213ac", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationResourcesReverseOnlyRows;

    /** 数据质量统计的正反索引核对统计的资源列表的反向索引记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_resour_b863b360ca_state")
    private Short qualityReverseIndexReconciliationResourcesReverseRowsState;

    /** 数据质量统计的正反索引核对统计的资源列表的反向索引记录数。 */
    @Column(name = "quality_reverseindexreconciliation_resour_b863b360ca", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationResourcesReverseRows;

    /** 数据质量统计的正反索引核对统计的资源列表的来源掉落几率不一致的记录数是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_reverseindexreconciliation_resour_26e6755a65_state")
    private Short qualityReverseIndexReconciliationResourcesSourceDropChanceDisagreementsState;

    /** 数据质量统计的正反索引核对统计的资源列表的来源掉落几率不一致的记录数。 */
    @Column(name = "quality_reverseindexreconciliation_resour_26e6755a65", columnDefinition = "NUMERIC")
    private BigDecimal qualityReverseIndexReconciliationResourcesSourceDropChanceDisagreements;

    /** 数据质量统计的奖励数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_rewardcount_state")
    private Short qualityRewardCountState;

    /** 数据质量统计的奖励数量。 */
    @Column(name = "quality_rewardcount", columnDefinition = "NUMERIC")
    private BigDecimal qualityRewardCount;

    /** 数据质量统计的来源场景数量是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "quality_sourcecontextcount_state")
    private Short qualitySourceContextCountState;

    /** 数据质量统计的来源场景数量。 */
    @Column(name = "quality_sourcecontextcount", columnDefinition = "NUMERIC")
    private BigDecimal qualitySourceContextCount;

    /** 资源列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "resources_state")
    private Short resourcesState;

    /** 奖励是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "rewards_state")
    private Short rewardsState;

    /** 来源信息的候选物品唯一标识列表是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "source_candidateuniquenames_state")
    private Short sourceCandidateUniqueNamesState;

    /** 来源信息的内容解析状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "source_contentresolution_state")
    private Short sourceContentResolutionState;

    /** 来源信息的内容解析状态。 */
    @Column(name = "source_contentresolution", columnDefinition = "TEXT")
    private String sourceContentResolution;

    /** 来源信息的图标状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "source_iconstatus_state")
    private Short sourceIconStatusState;

    /** 来源信息的图标状态。 */
    @Column(name = "source_iconstatus", columnDefinition = "TEXT")
    private String sourceIconStatus;

    /** 来源信息的图标地址是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "source_iconurl_state")
    private Short sourceIconUrlState;

    /** 来源信息的图标地址。 */
    @Column(name = "source_iconurl", columnDefinition = "TEXT")
    private String sourceIconUrl;

    /** 来源信息的身份解析状态是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "source_identityresolution_state")
    private Short sourceIdentityResolutionState;

    /** 来源信息的身份解析状态。 */
    @Column(name = "source_identityresolution", columnDefinition = "TEXT")
    private String sourceIdentityResolution;

    /** 来源信息的种类是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "source_kind_state")
    private Short sourceKindState;

    /** 来源信息的种类。 */
    @Column(name = "source_kind", columnDefinition = "TEXT")
    private String sourceKind;

    /** 来源信息的名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "source_name_state")
    private Short sourceNameState;

    /** 来源信息的名称。 */
    @Column(name = "source_name", columnDefinition = "TEXT")
    private String sourceName;

    /** 来源信息的英文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "source_nameen_state")
    private Short sourceNameEnState;

    /** 来源信息的英文名称。 */
    @Column(name = "source_nameen", columnDefinition = "TEXT")
    private String sourceNameEn;

    /** 来源信息的中文名称是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "source_namezh_state")
    private Short sourceNameZhState;

    /** 来源信息的中文名称。 */
    @Column(name = "source_namezh", columnDefinition = "TEXT")
    private String sourceNameZh;

    /** 来源信息的唯一标识是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "source_uniquename_state")
    private Short sourceUniqueNameState;

    /** 来源信息的唯一标识。 */
    @Column(name = "source_uniquename", columnDefinition = "TEXT")
    private String sourceUniqueName;

    /** 来源掉落几率百分比是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "sourcedropchancepercent_state")
    private Short sourceDropChancePercentState;

    /** 来源掉落几率百分比。 */
    @Column(name = "sourcedropchancepercent", columnDefinition = "NUMERIC")
    private BigDecimal sourceDropChancePercent;

    /** 星系序号是否缺失、为空或属于特定 JSON 类型。 */
    @Column(name = "systemindex_state")
    private Short systemIndexState;

    /** 星系序号。 */
    @Column(name = "systemindex", columnDefinition = "NUMERIC")
    private BigDecimal systemIndex;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialDropTableGroupsItems> officialDropTableGroupsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialDropTableResourcesItems> officialDropTableResourcesItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialDropTableRewardsItems> officialDropTableRewardsItemsRows = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfficialDropTableSourceCandidateUniqueNamesItems> officialDropTableSourceCandidateUniqueNamesItemsRows = new ArrayList<>();
}
