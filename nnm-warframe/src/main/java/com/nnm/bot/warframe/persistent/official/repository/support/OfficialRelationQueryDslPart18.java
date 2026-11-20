package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialDropTableSourceCandidateUniqueNamesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicLocationsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicLocationsItemsGroupsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicMarketListingsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicStatesEntries;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicStatesEntriesItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicStatesEntriesItemReferencesItemsRotationsItems;

/** 官方关系表 QueryDSL 标量投影分片 18；由模型生成器维护。 */
final class OfficialRelationQueryDslPart18 {
    private OfficialRelationQueryDslPart18() {}

    /**
     * 查询官方关系表指定主记录的类型化标量投影。
     *
     * @param query 执行查询的 QueryDSL 工厂。
     * @param table 官方关系表名。
     * @param ids 待查询的主记录标识。
     * @return 按记录标识排序的关系行投影。
     */
    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_drop_table_source_candidateuniquenames_items" -> {
                QOfficialDropTableSourceCandidateUniqueNamesItems row =
                    new QOfficialDropTableSourceCandidateUniqueNamesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_relic_root" -> {
                QOfficialRelicRoot row = new QOfficialRelicRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl",
                        "locationsState", "marketListingsState", "nameState", "name", "nameEnState", "nameEn",
                        "nameZhState", "nameZh", "qualityDeduplicatedRowsState", "qualityDeduplicatedRows",
                        "qualityDropTableRowsState", "qualityDropTableRows", "qualityInferredSourceGroupsState",
                        "qualityInferredSourceGroups", "qualityMatchedDropRowsState", "qualityMatchedDropRows",
                        "qualityMissionLocationRowsState", "qualityMissionLocationRows",
                        "qualityOfficialStateCountState", "qualityOfficialStateCount", "qualityRelicCountState",
                        "qualityRelicCount", "qualityRelicSourceRowsState", "qualityRelicSourceRows",
                        "qualityUnresolvedDropRowsState", "qualityUnresolvedDropRows",
                        "qualityUnresolvedLocationRowsState", "qualityUnresolvedLocationRows", "recordKeyState",
                        "recordKey", "relicCodeState", "relicCode", "statesState", "tierState", "tier",
                        "uniqueNameState", "uniqueName", "unresolvedDropRowsState", "unresolvedLocationRowsState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.iconStatusState, row.iconStatus,
                        row.iconUrlState, row.iconUrl, row.locationsState, row.marketListingsState, row.nameState,
                        row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh,
                        row.qualityDeduplicatedRowsState, row.qualityDeduplicatedRows, row.qualityDropTableRowsState,
                        row.qualityDropTableRows, row.qualityInferredSourceGroupsState, row.qualityInferredSourceGroups,
                        row.qualityMatchedDropRowsState, row.qualityMatchedDropRows,
                        row.qualityMissionLocationRowsState, row.qualityMissionLocationRows,
                        row.qualityOfficialStateCountState, row.qualityOfficialStateCount, row.qualityRelicCountState,
                        row.qualityRelicCount, row.qualityRelicSourceRowsState, row.qualityRelicSourceRows,
                        row.qualityUnresolvedDropRowsState, row.qualityUnresolvedDropRows,
                        row.qualityUnresolvedLocationRowsState, row.qualityUnresolvedLocationRows, row.recordKeyState,
                        row.recordKey, row.relicCodeState, row.relicCode, row.statesState, row.tierState, row.tier,
                        row.uniqueNameState, row.uniqueName, row.unresolvedDropRowsState,
                        row.unresolvedLocationRowsState));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_relic_locations_items" -> {
                QOfficialRelicLocationsItems row = new QOfficialRelicLocationsItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "activationProbabilityState", "activationProbability",
                        "categoryState", "category", "chanceResolutionState", "chanceResolution", "drawCountState",
                        "drawCount", "drawModeState", "drawMode", "dropChancePercentState", "dropChancePercent",
                        "groupsState", "probabilityState", "probability", "rarityState", "rarity", "sourceState",
                        "source", "sourceDropChancePercentState", "sourceDropChancePercent", "sourceZhState",
                        "sourceZh"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.activationProbabilityState,
                        row.activationProbability, row.categoryState, row.category, row.chanceResolutionState,
                        row.chanceResolution, row.drawCountState, row.drawCount, row.drawModeState, row.drawMode,
                        row.dropChancePercentState, row.dropChancePercent, row.groupsState, row.probabilityState,
                        row.probability, row.rarityState, row.rarity, row.sourceState, row.source,
                        row.sourceDropChancePercentState, row.sourceDropChancePercent, row.sourceZhState,
                        row.sourceZh));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_relic_locations_items_groups_items" -> {
                QOfficialRelicLocationsItemsGroupsItems row = new QOfficialRelicLocationsItemsGroupsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_relic_marketlistings_items" -> {
                QOfficialRelicMarketListingsItems row = new QOfficialRelicMarketListingsItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "gameRefState", "gameRef", "iconStatusState", "iconStatus",
                        "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState",
                        "nameZh", "slugState", "slug", "urlState", "url"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.gameRefState, row.gameRef,
                        row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name,
                        row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.slugState, row.slug, row.urlState,
                        row.url));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_relic_states_entries" -> {
                QOfficialRelicStatesEntries row = new QOfficialRelicStatesEntries(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "categoryState", "category", "codexSecretState", "codexSecret",
                        "descriptionState", "description", "descriptionEnState", "descriptionEn", "descriptionZhState",
                        "descriptionZh", "eraState", "era", "iconState", "icon", "iconStatusState", "iconStatus",
                        "iconUrlState", "iconUrl", "introducedAtState", "introducedAt", "itemReferencesState",
                        "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "qualityState",
                        "quality", "refinementState", "refinement", "rewardManifestState", "rewardManifest",
                        "rewardsState", "uniqueNameState", "uniqueName", "unresolvedDropRewardsState", "vaultedAtState",
                        "vaultedAt"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.categoryState, row.category,
                        row.codexSecretState, row.codexSecret, row.descriptionState, row.description,
                        row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh,
                        row.eraState, row.era, row.iconState, row.icon, row.iconStatusState, row.iconStatus,
                        row.iconUrlState, row.iconUrl, row.introducedAtState, row.introducedAt, row.itemReferencesState,
                        row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh,
                        row.qualityState, row.quality, row.refinementState, row.refinement, row.rewardManifestState,
                        row.rewardManifest, row.rewardsState, row.uniqueNameState, row.uniqueName,
                        row.unresolvedDropRewardsState, row.vaultedAtState, row.vaultedAt));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_relic_states_entries_itemreferences_items" -> {
                QOfficialRelicStatesEntriesItemReferencesItems row =
                    new QOfficialRelicStatesEntriesItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "fieldState", "field", "iconStatusState", "iconStatus",
                        "probabilityUnitState", "probabilityUnit", "referenceKindState", "referenceKind",
                        "resolutionState", "resolution", "rotationsState", "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.fieldState, row.field,
                        row.iconStatusState, row.iconStatus, row.probabilityUnitState, row.probabilityUnit,
                        row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution,
                        row.rotationsState, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_relic_states_entries_itemreferences_item_71d5962619" -> {
                QOfficialRelicStatesEntriesItemReferencesItemsRotationsItems row =
                    new QOfficialRelicStatesEntriesItemReferencesItemsRotationsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    /**
     * 查询指定官方子表中关联到父记录的子行标识。
     *
     * @param query 执行查询的 QueryDSL 工厂。
     * @param table 官方子表名。
     * @param parentIds 父记录主键集合。
     * @return 匹配的子行主键。
     */
    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_drop_table_source_candidateuniquenames_items" -> {
                QOfficialDropTableSourceCandidateUniqueNamesItems row =
                    new QOfficialDropTableSourceCandidateUniqueNamesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_relic_locations_items" -> {
                QOfficialRelicLocationsItems row = new QOfficialRelicLocationsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_relic_locations_items_groups_items" -> {
                QOfficialRelicLocationsItemsGroupsItems row = new QOfficialRelicLocationsItemsGroupsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_relic_marketlistings_items" -> {
                QOfficialRelicMarketListingsItems row = new QOfficialRelicMarketListingsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_relic_states_entries" -> {
                QOfficialRelicStatesEntries row = new QOfficialRelicStatesEntries(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_relic_states_entries_itemreferences_items" -> {
                QOfficialRelicStatesEntriesItemReferencesItems row =
                    new QOfficialRelicStatesEntriesItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_relic_states_entries_itemreferences_item_71d5962619" -> {
                QOfficialRelicStatesEntriesItemReferencesItemsRotationsItems row =
                    new QOfficialRelicStatesEntriesItemReferencesItemsRotationsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    /**
     * 按标识批量删除官方关系表记录。
     *
     * @param query 执行删除的 QueryDSL 工厂。
     * @param table 官方关系表名。
     * @param ids 待删除的关系记录主键。
     * @return 实际删除的记录数。
     */
    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_drop_table_source_candidateuniquenames_items" -> {
                QOfficialDropTableSourceCandidateUniqueNamesItems row =
                    new QOfficialDropTableSourceCandidateUniqueNamesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_root" -> {
                QOfficialRelicRoot row = new QOfficialRelicRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_locations_items" -> {
                QOfficialRelicLocationsItems row = new QOfficialRelicLocationsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_locations_items_groups_items" -> {
                QOfficialRelicLocationsItemsGroupsItems row = new QOfficialRelicLocationsItemsGroupsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_marketlistings_items" -> {
                QOfficialRelicMarketListingsItems row = new QOfficialRelicMarketListingsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_states_entries" -> {
                QOfficialRelicStatesEntries row = new QOfficialRelicStatesEntries(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_states_entries_itemreferences_items" -> {
                QOfficialRelicStatesEntriesItemReferencesItems row =
                    new QOfficialRelicStatesEntriesItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_states_entries_itemreferences_item_71d5962619" -> {
                QOfficialRelicStatesEntriesItemReferencesItemsRotationsItems row =
                    new QOfficialRelicStatesEntriesItemReferencesItemsRotationsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    /**
     * 查询官方独立主表的关系字段投影。
     *
     * @param query 执行查询的 QueryDSL 工厂。
     * @param row 当前官方主表的实体路径。
     * @param id 主表主键路径。
     * @param ids 待查询的主记录标识。
     * @param projection 关系字段到非受管 DTO 的投影。
     * @return 按主键排序的关系行投影。
     */
    private static <T> List<OfficialRelationRowDTO> selectRoot(JPAQueryFactory query, EntityPathBase<T> row,
        NumberPath<Long> id, Collection<Long> ids, OfficialRelationRowProjection projection) {
        return query.select(projection).from(row).where(id.in(ids)).orderBy(id.asc()).fetch();
    }

    /**
     * 按父记录及源顺序查询官方子表字段投影。
     *
     * @param query 执行查询的 QueryDSL 工厂。
     * @param row 当前官方子表的实体路径。
     * @param id 子表主键路径。
     * @param parentId 直接父记录主键路径。
     * @param sortOrder 源文件中的子行顺序路径。
     * @param ids 父记录主键集合。
     * @param projection 关系字段到非受管 DTO 的投影。
     * @return 按父记录和源顺序排列的关系行投影。
     */
    private static <T> List<OfficialRelationRowDTO> selectChildren(JPAQueryFactory query, EntityPathBase<T> row,
        NumberPath<Long> id, NumberPath<Long> parentId, NumberPath<Integer> sortOrder, Collection<Long> ids,
        OfficialRelationRowProjection projection) {
        return query.select(projection).from(row).where(parentId.in(ids))
            .orderBy(parentId.asc(), sortOrder.asc(), id.asc()).fetch();
    }
}
