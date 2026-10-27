package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionSystemsItemsResourcesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionSystemsItemsResourcesItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionNextNodesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionQuestReqsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionRewardManifestsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionSystemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialTilesetMissionDefinitionsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialTilesetRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 33；由模型生成器维护。 */
final class OfficialRelationQueryDslPart33 {
    private OfficialRelationQueryDslPart33() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_region_nextnodes_items" -> {
                QOfficialRegionNextNodesItems row = new QOfficialRegionNextNodesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_questreqs_items" -> {
                QOfficialRegionQuestReqsItems row = new QOfficialRegionQuestReqsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_rewardmanifests_items" -> {
                QOfficialRegionRewardManifestsItems row = new QOfficialRegionRewardManifestsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_systems_items" -> {
                QOfficialRegionSystemsItems row = new QOfficialRegionSystemsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "damageChanceState", "damageChance", "droneDamageMaxValueState", "droneDamageMaxValue", "droneDamageMinValueState", "droneDamageMinValue", "indexState", "index", "nameState", "name", "nameEnState", "nameEn", "resourcesState", "systemIndexState", "systemIndex", "zhNameState", "zhName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.damageChanceState, row.damageChance, row.droneDamageMaxValueState, row.droneDamageMaxValue, row.droneDamageMinValueState, row.droneDamageMinValue, row.indexState, row.index, row.nameState, row.name, row.nameEnState, row.nameEn, row.resourcesState, row.systemIndexState, row.systemIndex, row.zhNameState, row.zhName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_systems_items_resources_items" -> {
                QOfficialRegionSystemsItemsResourcesItems row = new QOfficialRegionSystemsItemsResourcesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemIconStatusState", "itemIconStatus", "itemIconUrlState", "itemIconUrl", "itemNameState", "itemName", "itemNameEnState", "itemNameEn", "itemNameZhState", "itemNameZh", "itemResolutionState", "itemResolution", "itemUniqueNameState", "itemUniqueName", "itemReferencesState", "itemTypeState", "itemType", "rarityState", "rarity", "storeItemState", "storeItem"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemIconStatusState, row.itemIconStatus, row.itemIconUrlState, row.itemIconUrl, row.itemNameState, row.itemName, row.itemNameEnState, row.itemNameEn, row.itemNameZhState, row.itemNameZh, row.itemResolutionState, row.itemResolution, row.itemUniqueNameState, row.itemUniqueName, row.itemReferencesState, row.itemTypeState, row.itemType, row.rarityState, row.rarity, row.storeItemState, row.storeItem));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_systems_items_resources_items_ite_b571aa65e8" -> {
                QOfficialRegionSystemsItemsResourcesItemsItemReferencesItems row = new QOfficialRegionSystemsItemsResourcesItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_tileset_root" -> {
                QOfficialTilesetRoot row = new QOfficialTilesetRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "factionState", "faction", "factionNameEnState", "factionNameEn", "factionNameZhState", "factionNameZh", "iconStatusState", "iconStatus", "missionDefinitionsState", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.factionState, row.faction, row.factionNameEnState, row.factionNameEn, row.factionNameZhState, row.factionNameZh, row.iconStatusState, row.iconStatus, row.missionDefinitionsState, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_tileset_missiondefinitions_items" -> {
                QOfficialTilesetMissionDefinitionsItems row = new QOfficialTilesetMissionDefinitionsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "advancedSpawnersState", "enemySpecsState", "extraEnemySpecsState", "itemReferencesState", "missionTypeState", "missionType", "nameEnState", "nameEn", "nameZhState", "nameZh", "procLevelState", "procLevel", "vipAgentState", "vipAgent"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.advancedSpawnersState, row.enemySpecsState, row.extraEnemySpecsState, row.itemReferencesState, row.missionTypeState, row.missionType, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.procLevelState, row.procLevel, row.vipAgentState, row.vipAgent));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_region_nextnodes_items" -> {
                QOfficialRegionNextNodesItems row = new QOfficialRegionNextNodesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_questreqs_items" -> {
                QOfficialRegionQuestReqsItems row = new QOfficialRegionQuestReqsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_rewardmanifests_items" -> {
                QOfficialRegionRewardManifestsItems row = new QOfficialRegionRewardManifestsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_systems_items" -> {
                QOfficialRegionSystemsItems row = new QOfficialRegionSystemsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_systems_items_resources_items" -> {
                QOfficialRegionSystemsItemsResourcesItems row = new QOfficialRegionSystemsItemsResourcesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_systems_items_resources_items_ite_b571aa65e8" -> {
                QOfficialRegionSystemsItemsResourcesItemsItemReferencesItems row = new QOfficialRegionSystemsItemsResourcesItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_tileset_missiondefinitions_items" -> {
                QOfficialTilesetMissionDefinitionsItems row = new QOfficialTilesetMissionDefinitionsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_region_nextnodes_items" -> {
                QOfficialRegionNextNodesItems row = new QOfficialRegionNextNodesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_questreqs_items" -> {
                QOfficialRegionQuestReqsItems row = new QOfficialRegionQuestReqsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_rewardmanifests_items" -> {
                QOfficialRegionRewardManifestsItems row = new QOfficialRegionRewardManifestsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_systems_items" -> {
                QOfficialRegionSystemsItems row = new QOfficialRegionSystemsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_systems_items_resources_items" -> {
                QOfficialRegionSystemsItemsResourcesItems row = new QOfficialRegionSystemsItemsResourcesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_systems_items_resources_items_ite_b571aa65e8" -> {
                QOfficialRegionSystemsItemsResourcesItemsItemReferencesItems row = new QOfficialRegionSystemsItemsResourcesItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_tileset_root" -> {
                QOfficialTilesetRoot row = new QOfficialTilesetRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_tileset_missiondefinitions_items" -> {
                QOfficialTilesetMissionDefinitionsItems row = new QOfficialTilesetMissionDefinitionsItems("row");
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
