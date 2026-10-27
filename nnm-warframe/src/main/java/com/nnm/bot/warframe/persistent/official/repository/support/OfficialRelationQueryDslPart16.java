package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItemsRewardsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsRewardsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStagesItemsGroupsItemsGroupsItemsRewardsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStagesItemsGroupsItemsRewardsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStagesItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStagesItemsRewardsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialDropTableRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 16；由模型生成器维护。 */
final class OfficialRelationQueryDslPart16 {
    private OfficialRelationQueryDslPart16() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_bounty_stages_items_groups_items_groups__b308a061aa" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bounty_stages_items_groups_items_groups__91ce8b9ab5" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItemsRewardsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItemsRewardsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "chanceResolutionState", "chanceResolution", "dropChancePercentState", "dropChancePercent", "itemCountState", "itemCount", "itemIconStatusState", "itemIconStatus", "itemIconUrlState", "itemIconUrl", "itemIdentityResolutionState", "itemIdentityResolution", "itemNameState", "itemName", "itemNameEnState", "itemNameEn", "itemNameZhState", "itemNameZh", "itemRefinementState", "itemRefinement", "itemUniqueNameState", "itemUniqueName", "probabilityState", "probability", "rarityState", "rarity"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.chanceResolutionState, row.chanceResolution, row.dropChancePercentState, row.dropChancePercent, row.itemCountState, row.itemCount, row.itemIconStatusState, row.itemIconStatus, row.itemIconUrlState, row.itemIconUrl, row.itemIdentityResolutionState, row.itemIdentityResolution, row.itemNameState, row.itemName, row.itemNameEnState, row.itemNameEn, row.itemNameZhState, row.itemNameZh, row.itemRefinementState, row.itemRefinement, row.itemUniqueNameState, row.itemUniqueName, row.probabilityState, row.probability, row.rarityState, row.rarity));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bounty_stages_items_groups_items_groups__d46a605e6d" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsRewardsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsRewardsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "chanceResolutionState", "chanceResolution", "dropChancePercentState", "dropChancePercent", "itemContentResolutionState", "itemContentResolution", "itemCountState", "itemCount", "itemCurrencyState", "itemCurrency", "itemDisplayNameState", "itemDisplayName", "itemIconStatusState", "itemIconStatus", "itemIconUrlState", "itemIconUrl", "itemIdentityResolutionState", "itemIdentityResolution", "itemKindState", "itemKind", "itemNameState", "itemName", "itemNameEnState", "itemNameEn", "itemNameZhState", "itemNameZh", "itemQuantityState", "itemQuantity", "itemUniqueNameState", "itemUniqueName", "probabilityState", "probability", "rarityState", "rarity"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.chanceResolutionState, row.chanceResolution, row.dropChancePercentState, row.dropChancePercent, row.itemContentResolutionState, row.itemContentResolution, row.itemCountState, row.itemCount, row.itemCurrencyState, row.itemCurrency, row.itemDisplayNameState, row.itemDisplayName, row.itemIconStatusState, row.itemIconStatus, row.itemIconUrlState, row.itemIconUrl, row.itemIdentityResolutionState, row.itemIdentityResolution, row.itemKindState, row.itemKind, row.itemNameState, row.itemName, row.itemNameEnState, row.itemNameEn, row.itemNameZhState, row.itemNameZh, row.itemQuantityState, row.itemQuantity, row.itemUniqueNameState, row.itemUniqueName, row.probabilityState, row.probability, row.rarityState, row.rarity));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bounty_stages_items_groups_items_groups__b74c8b29bf" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsRewardsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItemsRewardsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "chanceResolutionState", "chanceResolution", "dropChancePercentState", "dropChancePercent", "itemContentResolutionState", "itemContentResolution", "itemCountState", "itemCount", "itemCurrencyState", "itemCurrency", "itemDisplayNameState", "itemDisplayName", "itemIconStatusState", "itemIconStatus", "itemIconUrlState", "itemIconUrl", "itemIdentityResolutionState", "itemIdentityResolution", "itemKindState", "itemKind", "itemNameState", "itemName", "itemNameEnState", "itemNameEn", "itemNameZhState", "itemNameZh", "itemQuantityState", "itemQuantity", "itemResolutionBasisState", "itemResolutionBasis", "itemUniqueNameState", "itemUniqueName", "probabilityState", "probability", "rarityState", "rarity"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.chanceResolutionState, row.chanceResolution, row.dropChancePercentState, row.dropChancePercent, row.itemContentResolutionState, row.itemContentResolution, row.itemCountState, row.itemCount, row.itemCurrencyState, row.itemCurrency, row.itemDisplayNameState, row.itemDisplayName, row.itemIconStatusState, row.itemIconStatus, row.itemIconUrlState, row.itemIconUrl, row.itemIdentityResolutionState, row.itemIdentityResolution, row.itemKindState, row.itemKind, row.itemNameState, row.itemName, row.itemNameEnState, row.itemNameEn, row.itemNameZhState, row.itemNameZh, row.itemQuantityState, row.itemQuantity, row.itemResolutionBasisState, row.itemResolutionBasis, row.itemUniqueNameState, row.itemUniqueName, row.probabilityState, row.probability, row.rarityState, row.rarity));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bounty_stages_items_groups_items_rewards_items" -> {
                QOfficialBountyStagesItemsGroupsItemsRewardsItems row = new QOfficialBountyStagesItemsGroupsItemsRewardsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "chanceResolutionState", "chanceResolution", "dropChancePercentState", "dropChancePercent", "itemCountState", "itemCount", "itemCurrencyState", "itemCurrency", "itemDisplayNameState", "itemDisplayName", "itemIconStatusState", "itemIconStatus", "itemIconUrlState", "itemIconUrl", "itemIdentityResolutionState", "itemIdentityResolution", "itemKindState", "itemKind", "itemNameState", "itemName", "itemNameEnState", "itemNameEn", "itemNameZhState", "itemNameZh", "itemQuantityState", "itemQuantity", "itemRefinementState", "itemRefinement", "itemResolutionBasisState", "itemResolutionBasis", "itemUniqueNameState", "itemUniqueName", "probabilityState", "probability", "rarityState", "rarity"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.chanceResolutionState, row.chanceResolution, row.dropChancePercentState, row.dropChancePercent, row.itemCountState, row.itemCount, row.itemCurrencyState, row.itemCurrency, row.itemDisplayNameState, row.itemDisplayName, row.itemIconStatusState, row.itemIconStatus, row.itemIconUrlState, row.itemIconUrl, row.itemIdentityResolutionState, row.itemIdentityResolution, row.itemKindState, row.itemKind, row.itemNameState, row.itemName, row.itemNameEnState, row.itemNameEn, row.itemNameZhState, row.itemNameZh, row.itemQuantityState, row.itemQuantity, row.itemRefinementState, row.itemRefinement, row.itemResolutionBasisState, row.itemResolutionBasis, row.itemUniqueNameState, row.itemUniqueName, row.probabilityState, row.probability, row.rarityState, row.rarity));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bounty_stages_items_rewards_items" -> {
                QOfficialBountyStagesItemsRewardsItems row = new QOfficialBountyStagesItemsRewardsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bounty_stages_items_items" -> {
                QOfficialBountyStagesItemsItems row = new QOfficialBountyStagesItemsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_drop_table_root" -> {
                QOfficialDropTableRoot row = new QOfficialDropTableRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "activationProbabilityState", "activationProbability", "bountyCatalogState", "bountyCatalog", "categoryState", "category", "drawCountState", "drawCount", "drawModeState", "drawMode", "groupsState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "poolIdState", "poolId", "qualityItemIdentityAmbiguousState", "qualityItemIdentityAmbiguous", "qualityItemIdentityCategoriesState", "qualityItemIdentityCategories", "qualityItemIdentityCurrenciesState", "qualityItemIdentityCurrencies", "qualityItemIdentityRulesState", "qualityItemIdentityRules", "qualityItemIdentityUniqueState", "qualityItemIdentityUnique", "qualityItemIdentityUnresolvedState", "qualityItemIdentityUnresolved", "qualityReverseIndexReconciliationBlueprintsForwardOnlyRowsState", "qualityReverseIndexReconciliationBlueprintsForwardOnlyRows", "qualityReverseIndexReconciliationBlueprintsForwardRowsState", "qualityReverseIndexReconciliationBlueprintsForwardRows", "qualityReverseIndexReconciliationBlueprintsMatchedRowsState", "qualityReverseIndexReconciliationBlueprintsMatchedRows", "qualityReverseIndexReconciliationBlueprintsProbabilityDisagreementsState", "qualityReverseIndexReconciliationBlueprintsProbabilityDisagreements", "qualityReverseIndexReconciliationBlueprintsReverseOnlyRowsState", "qualityReverseIndexReconciliationBlueprintsReverseOnlyRows", "qualityReverseIndexReconciliationBlueprintsReverseRowsState", "qualityReverseIndexReconciliationBlueprintsReverseRows", "qualityReverseIndexReconciliationBlueprintsSourceDropChanceDisagreementsState", "qualityReverseIndexReconciliationBlueprintsSourceDropChanceDisagreements", "qualityReverseIndexReconciliationModsForwardOnlyRowsState", "qualityReverseIndexReconciliationModsForwardOnlyRows", "qualityReverseIndexReconciliationModsForwardRowsState", "qualityReverseIndexReconciliationModsForwardRows", "qualityReverseIndexReconciliationModsMatchedRowsState", "qualityReverseIndexReconciliationModsMatchedRows", "qualityReverseIndexReconciliationModsProbabilityDisagreementsState", "qualityReverseIndexReconciliationModsProbabilityDisagreements", "qualityReverseIndexReconciliationModsReverseOnlyRowsState", "qualityReverseIndexReconciliationModsReverseOnlyRows", "qualityReverseIndexReconciliationModsReverseRowsState", "qualityReverseIndexReconciliationModsReverseRows", "qualityReverseIndexReconciliationModsSourceDropChanceDisagreementsState", "qualityReverseIndexReconciliationModsSourceDropChanceDisagreements", "qualityReverseIndexReconciliationResourcesForwardOnlyRowsState", "qualityReverseIndexReconciliationResourcesForwardOnlyRows", "qualityReverseIndexReconciliationResourcesForwardRowsState", "qualityReverseIndexReconciliationResourcesForwardRows", "qualityReverseIndexReconciliationResourcesMatchedRowsState", "qualityReverseIndexReconciliationResourcesMatchedRows", "qualityReverseIndexReconciliationResourcesProbabilityDisagreementsState", "qualityReverseIndexReconciliationResourcesProbabilityDisagreements", "qualityReverseIndexReconciliationResourcesReverseOnlyRowsState", "qualityReverseIndexReconciliationResourcesReverseOnlyRows", "qualityReverseIndexReconciliationResourcesReverseRowsState", "qualityReverseIndexReconciliationResourcesReverseRows", "qualityReverseIndexReconciliationResourcesSourceDropChanceDisagreementsState", "qualityReverseIndexReconciliationResourcesSourceDropChanceDisagreements", "qualityRewardCountState", "qualityRewardCount", "qualitySourceContextCountState", "qualitySourceContextCount", "resourcesState", "rewardsState", "sourceCandidateUniqueNamesState", "sourceContentResolutionState", "sourceContentResolution", "sourceIconStatusState", "sourceIconStatus", "sourceIconUrlState", "sourceIconUrl", "sourceIdentityResolutionState", "sourceIdentityResolution", "sourceKindState", "sourceKind", "sourceNameState", "sourceName", "sourceNameEnState", "sourceNameEn", "sourceNameZhState", "sourceNameZh", "sourceUniqueNameState", "sourceUniqueName", "sourceDropChancePercentState", "sourceDropChancePercent", "systemIndexState", "systemIndex"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.activationProbabilityState, row.activationProbability, row.bountyCatalogState, row.bountyCatalog, row.categoryState, row.category, row.drawCountState, row.drawCount, row.drawModeState, row.drawMode, row.groupsState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.poolIdState, row.poolId, row.qualityItemIdentityAmbiguousState, row.qualityItemIdentityAmbiguous, row.qualityItemIdentityCategoriesState, row.qualityItemIdentityCategories, row.qualityItemIdentityCurrenciesState, row.qualityItemIdentityCurrencies, row.qualityItemIdentityRulesState, row.qualityItemIdentityRules, row.qualityItemIdentityUniqueState, row.qualityItemIdentityUnique, row.qualityItemIdentityUnresolvedState, row.qualityItemIdentityUnresolved, row.qualityReverseIndexReconciliationBlueprintsForwardOnlyRowsState, row.qualityReverseIndexReconciliationBlueprintsForwardOnlyRows, row.qualityReverseIndexReconciliationBlueprintsForwardRowsState, row.qualityReverseIndexReconciliationBlueprintsForwardRows, row.qualityReverseIndexReconciliationBlueprintsMatchedRowsState, row.qualityReverseIndexReconciliationBlueprintsMatchedRows, row.qualityReverseIndexReconciliationBlueprintsProbabilityDisagreementsState, row.qualityReverseIndexReconciliationBlueprintsProbabilityDisagreements, row.qualityReverseIndexReconciliationBlueprintsReverseOnlyRowsState, row.qualityReverseIndexReconciliationBlueprintsReverseOnlyRows, row.qualityReverseIndexReconciliationBlueprintsReverseRowsState, row.qualityReverseIndexReconciliationBlueprintsReverseRows, row.qualityReverseIndexReconciliationBlueprintsSourceDropChanceDisagreementsState, row.qualityReverseIndexReconciliationBlueprintsSourceDropChanceDisagreements, row.qualityReverseIndexReconciliationModsForwardOnlyRowsState, row.qualityReverseIndexReconciliationModsForwardOnlyRows, row.qualityReverseIndexReconciliationModsForwardRowsState, row.qualityReverseIndexReconciliationModsForwardRows, row.qualityReverseIndexReconciliationModsMatchedRowsState, row.qualityReverseIndexReconciliationModsMatchedRows, row.qualityReverseIndexReconciliationModsProbabilityDisagreementsState, row.qualityReverseIndexReconciliationModsProbabilityDisagreements, row.qualityReverseIndexReconciliationModsReverseOnlyRowsState, row.qualityReverseIndexReconciliationModsReverseOnlyRows, row.qualityReverseIndexReconciliationModsReverseRowsState, row.qualityReverseIndexReconciliationModsReverseRows, row.qualityReverseIndexReconciliationModsSourceDropChanceDisagreementsState, row.qualityReverseIndexReconciliationModsSourceDropChanceDisagreements, row.qualityReverseIndexReconciliationResourcesForwardOnlyRowsState, row.qualityReverseIndexReconciliationResourcesForwardOnlyRows, row.qualityReverseIndexReconciliationResourcesForwardRowsState, row.qualityReverseIndexReconciliationResourcesForwardRows, row.qualityReverseIndexReconciliationResourcesMatchedRowsState, row.qualityReverseIndexReconciliationResourcesMatchedRows, row.qualityReverseIndexReconciliationResourcesProbabilityDisagreementsState, row.qualityReverseIndexReconciliationResourcesProbabilityDisagreements, row.qualityReverseIndexReconciliationResourcesReverseOnlyRowsState, row.qualityReverseIndexReconciliationResourcesReverseOnlyRows, row.qualityReverseIndexReconciliationResourcesReverseRowsState, row.qualityReverseIndexReconciliationResourcesReverseRows, row.qualityReverseIndexReconciliationResourcesSourceDropChanceDisagreementsState, row.qualityReverseIndexReconciliationResourcesSourceDropChanceDisagreements, row.qualityRewardCountState, row.qualityRewardCount, row.qualitySourceContextCountState, row.qualitySourceContextCount, row.resourcesState, row.rewardsState, row.sourceCandidateUniqueNamesState, row.sourceContentResolutionState, row.sourceContentResolution, row.sourceIconStatusState, row.sourceIconStatus, row.sourceIconUrlState, row.sourceIconUrl, row.sourceIdentityResolutionState, row.sourceIdentityResolution, row.sourceKindState, row.sourceKind, row.sourceNameState, row.sourceName, row.sourceNameEnState, row.sourceNameEn, row.sourceNameZhState, row.sourceNameZh, row.sourceUniqueNameState, row.sourceUniqueName, row.sourceDropChancePercentState, row.sourceDropChancePercent, row.systemIndexState, row.systemIndex));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_bounty_stages_items_groups_items_groups__b308a061aa" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bounty_stages_items_groups_items_groups__91ce8b9ab5" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItemsRewardsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItemsRewardsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bounty_stages_items_groups_items_groups__d46a605e6d" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsRewardsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsRewardsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bounty_stages_items_groups_items_groups__b74c8b29bf" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsRewardsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItemsRewardsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bounty_stages_items_groups_items_rewards_items" -> {
                QOfficialBountyStagesItemsGroupsItemsRewardsItems row = new QOfficialBountyStagesItemsGroupsItemsRewardsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bounty_stages_items_rewards_items" -> {
                QOfficialBountyStagesItemsRewardsItems row = new QOfficialBountyStagesItemsRewardsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bounty_stages_items_items" -> {
                QOfficialBountyStagesItemsItems row = new QOfficialBountyStagesItemsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_bounty_stages_items_groups_items_groups__b308a061aa" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stages_items_groups_items_groups__91ce8b9ab5" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItemsRewardsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItemsRewardsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stages_items_groups_items_groups__d46a605e6d" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsRewardsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsRewardsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stages_items_groups_items_groups__b74c8b29bf" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsRewardsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItemsRewardsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stages_items_groups_items_rewards_items" -> {
                QOfficialBountyStagesItemsGroupsItemsRewardsItems row = new QOfficialBountyStagesItemsGroupsItemsRewardsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stages_items_rewards_items" -> {
                QOfficialBountyStagesItemsRewardsItems row = new QOfficialBountyStagesItemsRewardsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stages_items_items" -> {
                QOfficialBountyStagesItemsItems row = new QOfficialBountyStagesItemsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_drop_table_root" -> {
                QOfficialDropTableRoot row = new QOfficialDropTableRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    private static <T> List<OfficialRelationRowDTO> selectRoot(JPAQueryFactory query, EntityPathBase<T> row,
            NumberPath<Long> id, Collection<Long> ids, OfficialRelationRowProjection projection) {
        return query.select(projection).from(row).where(id.in(ids)).orderBy(id.asc()).fetch();
    }

    private static <T> List<OfficialRelationRowDTO> selectChildren(JPAQueryFactory query, EntityPathBase<T> row,
            NumberPath<Long> id, NumberPath<Long> parentId, NumberPath<Integer> sortOrder, Collection<Long> ids,
            OfficialRelationRowProjection projection) {
        return query.select(projection).from(row).where(parentId.in(ids))
                .orderBy(parentId.asc(), sortOrder.asc(), id.asc()).fetch();
    }
}
