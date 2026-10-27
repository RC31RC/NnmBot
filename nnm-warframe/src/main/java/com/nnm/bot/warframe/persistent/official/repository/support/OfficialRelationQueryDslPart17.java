package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialDropTableGroupsItemsRewardsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialDropTableResourcesItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialDropTableRewardsItemsItemCandidatesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialDropTableGroupsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialDropTableGroupsItemsGroupsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialDropTableGroupsItemsRewardsItemsItemCandidatesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialDropTableResourcesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialDropTableRewardsItems;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 17；由模型生成器维护。 */
final class OfficialRelationQueryDslPart17 {
    private OfficialRelationQueryDslPart17() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_drop_table_groups_items" -> {
                QOfficialDropTableGroupsItems row = new QOfficialDropTableGroupsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "groupsState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "rewardsState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.groupsState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.rewardsState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_drop_table_groups_items_groups_items" -> {
                QOfficialDropTableGroupsItemsGroupsItems row = new QOfficialDropTableGroupsItemsGroupsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_drop_table_groups_items_rewards_items" -> {
                QOfficialDropTableGroupsItemsRewardsItems row = new QOfficialDropTableGroupsItemsRewardsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "chanceResolutionState", "chanceResolution", "dropChancePercentState", "dropChancePercent", "itemCandidatesState", "itemCountState", "itemCount", "itemCurrencyState", "itemCurrency", "itemDisplayNameState", "itemDisplayName", "itemIconStatusState", "itemIconStatus", "itemIconUrlState", "itemIconUrl", "itemIdentityResolutionState", "itemIdentityResolution", "itemKindState", "itemKind", "itemNameState", "itemName", "itemNameEnState", "itemNameEn", "itemNameZhState", "itemNameZh", "itemProbabilityScopeState", "itemProbabilityScope", "itemQuantityState", "itemQuantity", "itemRefinementState", "itemRefinement", "itemResolutionBasisState", "itemResolutionBasis", "itemSelectionState", "itemSelection", "itemUniqueNameState", "itemUniqueName", "probabilityState", "probability", "rarityState", "rarity"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.chanceResolutionState, row.chanceResolution, row.dropChancePercentState, row.dropChancePercent, row.itemCandidatesState, row.itemCountState, row.itemCount, row.itemCurrencyState, row.itemCurrency, row.itemDisplayNameState, row.itemDisplayName, row.itemIconStatusState, row.itemIconStatus, row.itemIconUrlState, row.itemIconUrl, row.itemIdentityResolutionState, row.itemIdentityResolution, row.itemKindState, row.itemKind, row.itemNameState, row.itemName, row.itemNameEnState, row.itemNameEn, row.itemNameZhState, row.itemNameZh, row.itemProbabilityScopeState, row.itemProbabilityScope, row.itemQuantityState, row.itemQuantity, row.itemRefinementState, row.itemRefinement, row.itemResolutionBasisState, row.itemResolutionBasis, row.itemSelectionState, row.itemSelection, row.itemUniqueNameState, row.itemUniqueName, row.probabilityState, row.probability, row.rarityState, row.rarity));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_drop_table_groups_items_rewards_items_it_bafb9689bd" -> {
                QOfficialDropTableGroupsItemsRewardsItemsItemCandidatesItems row = new QOfficialDropTableGroupsItemsRewardsItemsItemCandidatesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_drop_table_resources_items" -> {
                QOfficialDropTableResourcesItems row = new QOfficialDropTableResourcesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemIconStatusState", "itemIconStatus", "itemIconUrlState", "itemIconUrl", "itemNameState", "itemName", "itemNameEnState", "itemNameEn", "itemNameZhState", "itemNameZh", "itemResolutionState", "itemResolution", "itemUniqueNameState", "itemUniqueName", "itemReferencesState", "itemTypeState", "itemType", "rarityState", "rarity", "storeItemState", "storeItem"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemIconStatusState, row.itemIconStatus, row.itemIconUrlState, row.itemIconUrl, row.itemNameState, row.itemName, row.itemNameEnState, row.itemNameEn, row.itemNameZhState, row.itemNameZh, row.itemResolutionState, row.itemResolution, row.itemUniqueNameState, row.itemUniqueName, row.itemReferencesState, row.itemTypeState, row.itemType, row.rarityState, row.rarity, row.storeItemState, row.storeItem));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_drop_table_resources_items_itemreferences_items" -> {
                QOfficialDropTableResourcesItemsItemReferencesItems row = new QOfficialDropTableResourcesItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_drop_table_rewards_items" -> {
                QOfficialDropTableRewardsItems row = new QOfficialDropTableRewardsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "chanceResolutionState", "chanceResolution", "dropChancePercentState", "dropChancePercent", "itemCandidatesState", "itemCountState", "itemCount", "itemCurrencyState", "itemCurrency", "itemDisplayNameState", "itemDisplayName", "itemIconStatusState", "itemIconStatus", "itemIconUrlState", "itemIconUrl", "itemIdentityResolutionState", "itemIdentityResolution", "itemKindState", "itemKind", "itemNameState", "itemName", "itemNameEnState", "itemNameEn", "itemNameZhState", "itemNameZh", "itemProbabilityScopeState", "itemProbabilityScope", "itemQuantityState", "itemQuantity", "itemRefinementState", "itemRefinement", "itemRegionSelectionState", "itemRegionSelection", "itemResolutionBasisState", "itemResolutionBasis", "itemResourceCandidatesRefState", "itemResourceCandidatesRef", "itemRuleKeyState", "itemRuleKey", "itemSelectionState", "itemSelection", "itemUniqueNameState", "itemUniqueName", "probabilityState", "probability", "rarityState", "rarity", "reverseIndexSourceDropChancePercentState", "reverseIndexSourceDropChancePercent", "reverseIndexStatusState", "reverseIndexStatus", "sourceDropChancePercentState", "sourceDropChancePercent"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.chanceResolutionState, row.chanceResolution, row.dropChancePercentState, row.dropChancePercent, row.itemCandidatesState, row.itemCountState, row.itemCount, row.itemCurrencyState, row.itemCurrency, row.itemDisplayNameState, row.itemDisplayName, row.itemIconStatusState, row.itemIconStatus, row.itemIconUrlState, row.itemIconUrl, row.itemIdentityResolutionState, row.itemIdentityResolution, row.itemKindState, row.itemKind, row.itemNameState, row.itemName, row.itemNameEnState, row.itemNameEn, row.itemNameZhState, row.itemNameZh, row.itemProbabilityScopeState, row.itemProbabilityScope, row.itemQuantityState, row.itemQuantity, row.itemRefinementState, row.itemRefinement, row.itemRegionSelectionState, row.itemRegionSelection, row.itemResolutionBasisState, row.itemResolutionBasis, row.itemResourceCandidatesRefState, row.itemResourceCandidatesRef, row.itemRuleKeyState, row.itemRuleKey, row.itemSelectionState, row.itemSelection, row.itemUniqueNameState, row.itemUniqueName, row.probabilityState, row.probability, row.rarityState, row.rarity, row.reverseIndexSourceDropChancePercentState, row.reverseIndexSourceDropChancePercent, row.reverseIndexStatusState, row.reverseIndexStatus, row.sourceDropChancePercentState, row.sourceDropChancePercent));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_drop_table_rewards_items_item_candidates_items" -> {
                QOfficialDropTableRewardsItemsItemCandidatesItems row = new QOfficialDropTableRewardsItemsItemCandidatesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_drop_table_groups_items" -> {
                QOfficialDropTableGroupsItems row = new QOfficialDropTableGroupsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_drop_table_groups_items_groups_items" -> {
                QOfficialDropTableGroupsItemsGroupsItems row = new QOfficialDropTableGroupsItemsGroupsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_drop_table_groups_items_rewards_items" -> {
                QOfficialDropTableGroupsItemsRewardsItems row = new QOfficialDropTableGroupsItemsRewardsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_drop_table_groups_items_rewards_items_it_bafb9689bd" -> {
                QOfficialDropTableGroupsItemsRewardsItemsItemCandidatesItems row = new QOfficialDropTableGroupsItemsRewardsItemsItemCandidatesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_drop_table_resources_items" -> {
                QOfficialDropTableResourcesItems row = new QOfficialDropTableResourcesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_drop_table_resources_items_itemreferences_items" -> {
                QOfficialDropTableResourcesItemsItemReferencesItems row = new QOfficialDropTableResourcesItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_drop_table_rewards_items" -> {
                QOfficialDropTableRewardsItems row = new QOfficialDropTableRewardsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_drop_table_rewards_items_item_candidates_items" -> {
                QOfficialDropTableRewardsItemsItemCandidatesItems row = new QOfficialDropTableRewardsItemsItemCandidatesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_drop_table_groups_items" -> {
                QOfficialDropTableGroupsItems row = new QOfficialDropTableGroupsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_drop_table_groups_items_groups_items" -> {
                QOfficialDropTableGroupsItemsGroupsItems row = new QOfficialDropTableGroupsItemsGroupsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_drop_table_groups_items_rewards_items" -> {
                QOfficialDropTableGroupsItemsRewardsItems row = new QOfficialDropTableGroupsItemsRewardsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_drop_table_groups_items_rewards_items_it_bafb9689bd" -> {
                QOfficialDropTableGroupsItemsRewardsItemsItemCandidatesItems row = new QOfficialDropTableGroupsItemsRewardsItemsItemCandidatesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_drop_table_resources_items" -> {
                QOfficialDropTableResourcesItems row = new QOfficialDropTableResourcesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_drop_table_resources_items_itemreferences_items" -> {
                QOfficialDropTableResourcesItemsItemReferencesItems row = new QOfficialDropTableResourcesItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_drop_table_rewards_items" -> {
                QOfficialDropTableRewardsItems row = new QOfficialDropTableRewardsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_drop_table_rewards_items_item_candidates_items" -> {
                QOfficialDropTableRewardsItemsItemCandidatesItems row = new QOfficialDropTableRewardsItemsItemCandidatesItems("row");
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
