package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicLocationsItemsGroupsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicStatesEntriesItemReferencesItemsRotationsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialDropTableSourceCandidateUniqueNamesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicLocationsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicMarketListingsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicStatesEntries;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicStatesEntriesItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 18；由模型生成器维护。 */
final class OfficialRelationQueryDslPart18 {
    private OfficialRelationQueryDslPart18() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_drop_table_source_candidateuniquenames_items" -> {
                QOfficialDropTableSourceCandidateUniqueNamesItems row = new QOfficialDropTableSourceCandidateUniqueNamesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_relic_root" -> {
                QOfficialRelicRoot row = new QOfficialRelicRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "locationsState", "marketListingsState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "qualityDeduplicatedRowsState", "qualityDeduplicatedRows", "qualityDropTableRowsState", "qualityDropTableRows", "qualityInferredSourceGroupsState", "qualityInferredSourceGroups", "qualityMatchedDropRowsState", "qualityMatchedDropRows", "qualityMissionLocationRowsState", "qualityMissionLocationRows", "qualityOfficialStateCountState", "qualityOfficialStateCount", "qualityRelicCountState", "qualityRelicCount", "qualityRelicSourceRowsState", "qualityRelicSourceRows", "qualityUnresolvedDropRowsState", "qualityUnresolvedDropRows", "qualityUnresolvedLocationRowsState", "qualityUnresolvedLocationRows", "recordKeyState", "recordKey", "relicCodeState", "relicCode", "statesState", "tierState", "tier", "uniqueNameState", "uniqueName", "unresolvedDropRowsState", "unresolvedLocationRowsState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.locationsState, row.marketListingsState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.qualityDeduplicatedRowsState, row.qualityDeduplicatedRows, row.qualityDropTableRowsState, row.qualityDropTableRows, row.qualityInferredSourceGroupsState, row.qualityInferredSourceGroups, row.qualityMatchedDropRowsState, row.qualityMatchedDropRows, row.qualityMissionLocationRowsState, row.qualityMissionLocationRows, row.qualityOfficialStateCountState, row.qualityOfficialStateCount, row.qualityRelicCountState, row.qualityRelicCount, row.qualityRelicSourceRowsState, row.qualityRelicSourceRows, row.qualityUnresolvedDropRowsState, row.qualityUnresolvedDropRows, row.qualityUnresolvedLocationRowsState, row.qualityUnresolvedLocationRows, row.recordKeyState, row.recordKey, row.relicCodeState, row.relicCode, row.statesState, row.tierState, row.tier, row.uniqueNameState, row.uniqueName, row.unresolvedDropRowsState, row.unresolvedLocationRowsState));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_relic_locations_items" -> {
                QOfficialRelicLocationsItems row = new QOfficialRelicLocationsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "activationProbabilityState", "activationProbability", "categoryState", "category", "chanceResolutionState", "chanceResolution", "drawCountState", "drawCount", "drawModeState", "drawMode", "dropChancePercentState", "dropChancePercent", "groupsState", "probabilityState", "probability", "rarityState", "rarity", "sourceState", "source", "sourceDropChancePercentState", "sourceDropChancePercent", "sourceZhState", "sourceZh"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.activationProbabilityState, row.activationProbability, row.categoryState, row.category, row.chanceResolutionState, row.chanceResolution, row.drawCountState, row.drawCount, row.drawModeState, row.drawMode, row.dropChancePercentState, row.dropChancePercent, row.groupsState, row.probabilityState, row.probability, row.rarityState, row.rarity, row.sourceState, row.source, row.sourceDropChancePercentState, row.sourceDropChancePercent, row.sourceZhState, row.sourceZh));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_relic_locations_items_groups_items" -> {
                QOfficialRelicLocationsItemsGroupsItems row = new QOfficialRelicLocationsItemsGroupsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_relic_marketlistings_items" -> {
                QOfficialRelicMarketListingsItems row = new QOfficialRelicMarketListingsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "gameRefState", "gameRef", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "slugState", "slug", "urlState", "url"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.gameRefState, row.gameRef, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.slugState, row.slug, row.urlState, row.url));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_relic_states_entries" -> {
                QOfficialRelicStatesEntries row = new QOfficialRelicStatesEntries("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "categoryState", "category", "codexSecretState", "codexSecret", "descriptionState", "description", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "eraState", "era", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "introducedAtState", "introducedAt", "itemReferencesState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "qualityState", "quality", "refinementState", "refinement", "rewardManifestState", "rewardManifest", "rewardsState", "uniqueNameState", "uniqueName", "unresolvedDropRewardsState", "vaultedAtState", "vaultedAt"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.categoryState, row.category, row.codexSecretState, row.codexSecret, row.descriptionState, row.description, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.eraState, row.era, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.introducedAtState, row.introducedAt, row.itemReferencesState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.qualityState, row.quality, row.refinementState, row.refinement, row.rewardManifestState, row.rewardManifest, row.rewardsState, row.uniqueNameState, row.uniqueName, row.unresolvedDropRewardsState, row.vaultedAtState, row.vaultedAt));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_relic_states_entries_itemreferences_items" -> {
                QOfficialRelicStatesEntriesItemReferencesItems row = new QOfficialRelicStatesEntriesItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "fieldState", "field", "iconStatusState", "iconStatus", "probabilityUnitState", "probabilityUnit", "referenceKindState", "referenceKind", "resolutionState", "resolution", "rotationsState", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.probabilityUnitState, row.probabilityUnit, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.rotationsState, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_relic_states_entries_itemreferences_item_71d5962619" -> {
                QOfficialRelicStatesEntriesItemReferencesItemsRotationsItems row = new QOfficialRelicStatesEntriesItemReferencesItemsRotationsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_drop_table_source_candidateuniquenames_items" -> {
                QOfficialDropTableSourceCandidateUniqueNamesItems row = new QOfficialDropTableSourceCandidateUniqueNamesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_relic_locations_items" -> {
                QOfficialRelicLocationsItems row = new QOfficialRelicLocationsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_relic_locations_items_groups_items" -> {
                QOfficialRelicLocationsItemsGroupsItems row = new QOfficialRelicLocationsItemsGroupsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_relic_marketlistings_items" -> {
                QOfficialRelicMarketListingsItems row = new QOfficialRelicMarketListingsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_relic_states_entries" -> {
                QOfficialRelicStatesEntries row = new QOfficialRelicStatesEntries("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_relic_states_entries_itemreferences_items" -> {
                QOfficialRelicStatesEntriesItemReferencesItems row = new QOfficialRelicStatesEntriesItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_relic_states_entries_itemreferences_item_71d5962619" -> {
                QOfficialRelicStatesEntriesItemReferencesItemsRotationsItems row = new QOfficialRelicStatesEntriesItemReferencesItemsRotationsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_drop_table_source_candidateuniquenames_items" -> {
                QOfficialDropTableSourceCandidateUniqueNamesItems row = new QOfficialDropTableSourceCandidateUniqueNamesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_root" -> {
                QOfficialRelicRoot row = new QOfficialRelicRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_locations_items" -> {
                QOfficialRelicLocationsItems row = new QOfficialRelicLocationsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_locations_items_groups_items" -> {
                QOfficialRelicLocationsItemsGroupsItems row = new QOfficialRelicLocationsItemsGroupsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_marketlistings_items" -> {
                QOfficialRelicMarketListingsItems row = new QOfficialRelicMarketListingsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_states_entries" -> {
                QOfficialRelicStatesEntries row = new QOfficialRelicStatesEntries("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_states_entries_itemreferences_items" -> {
                QOfficialRelicStatesEntriesItemReferencesItems row = new QOfficialRelicStatesEntriesItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_states_entries_itemreferences_item_71d5962619" -> {
                QOfficialRelicStatesEntriesItemReferencesItemsRotationsItems row = new QOfficialRelicStatesEntriesItemReferencesItemsRotationsItems("row");
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
