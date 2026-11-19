package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionNextNodesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionQuestReqsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionRewardManifestsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionSystemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionSystemsItemsResourcesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionSystemsItemsResourcesItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialTilesetRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialTilesetMissionDefinitionsItems;

/** 官方关系表 QueryDSL 标量投影分片 33；由模型生成器维护。 */
final class OfficialRelationQueryDslPart33 {
    private OfficialRelationQueryDslPart33() { }

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
            case "wf_official_region_nextnodes_items" -> {
                QOfficialRegionNextNodesItems row = new QOfficialRegionNextNodesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_questreqs_items" -> {
                QOfficialRegionQuestReqsItems row = new QOfficialRegionQuestReqsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_rewardmanifests_items" -> {
                QOfficialRegionRewardManifestsItems row = new QOfficialRegionRewardManifestsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_systems_items" -> {
                QOfficialRegionSystemsItems row = new QOfficialRegionSystemsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "damageChanceState", "damageChance", "droneDamageMaxValueState", "droneDamageMaxValue", "droneDamageMinValueState", "droneDamageMinValue", "indexState", "index", "nameState", "name", "nameEnState", "nameEn", "resourcesState", "systemIndexState", "systemIndex", "zhNameState", "zhName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.damageChanceState, row.damageChance, row.droneDamageMaxValueState, row.droneDamageMaxValue, row.droneDamageMinValueState, row.droneDamageMinValue, row.indexState, row.index, row.nameState, row.name, row.nameEnState, row.nameEn, row.resourcesState, row.systemIndexState, row.systemIndex, row.zhNameState, row.zhName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_systems_items_resources_items" -> {
                QOfficialRegionSystemsItemsResourcesItems row = new QOfficialRegionSystemsItemsResourcesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemIconStatusState", "itemIconStatus", "itemIconUrlState", "itemIconUrl", "itemNameState", "itemName", "itemNameEnState", "itemNameEn", "itemNameZhState", "itemNameZh", "itemResolutionState", "itemResolution", "itemUniqueNameState", "itemUniqueName", "itemReferencesState", "itemTypeState", "itemType", "rarityState", "rarity", "storeItemState", "storeItem"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemIconStatusState, row.itemIconStatus, row.itemIconUrlState, row.itemIconUrl, row.itemNameState, row.itemName, row.itemNameEnState, row.itemNameEn, row.itemNameZhState, row.itemNameZh, row.itemResolutionState, row.itemResolution, row.itemUniqueNameState, row.itemUniqueName, row.itemReferencesState, row.itemTypeState, row.itemType, row.rarityState, row.rarity, row.storeItemState, row.storeItem));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_systems_items_resources_items_ite_b571aa65e8" -> {
                QOfficialRegionSystemsItemsResourcesItemsItemReferencesItems row = new QOfficialRegionSystemsItemsResourcesItemsItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_tileset_root" -> {
                QOfficialTilesetRoot row = new QOfficialTilesetRoot(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "factionState", "faction", "factionNameEnState", "factionNameEn", "factionNameZhState", "factionNameZh", "iconStatusState", "iconStatus", "missionDefinitionsState", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.factionState, row.faction, row.factionNameEnState, row.factionNameEn, row.factionNameZhState, row.factionNameZh, row.iconStatusState, row.iconStatus, row.missionDefinitionsState, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_tileset_missiondefinitions_items" -> {
                QOfficialTilesetMissionDefinitionsItems row = new QOfficialTilesetMissionDefinitionsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "advancedSpawnersState", "enemySpecsState", "extraEnemySpecsState", "itemReferencesState", "missionTypeState", "missionType", "nameEnState", "nameEn", "nameZhState", "nameZh", "procLevelState", "procLevel", "vipAgentState", "vipAgent"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.advancedSpawnersState, row.enemySpecsState, row.extraEnemySpecsState, row.itemReferencesState, row.missionTypeState, row.missionType, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.procLevelState, row.procLevel, row.vipAgentState, row.vipAgent));
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
            case "wf_official_region_nextnodes_items" -> {
                QOfficialRegionNextNodesItems row = new QOfficialRegionNextNodesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_questreqs_items" -> {
                QOfficialRegionQuestReqsItems row = new QOfficialRegionQuestReqsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_rewardmanifests_items" -> {
                QOfficialRegionRewardManifestsItems row = new QOfficialRegionRewardManifestsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_systems_items" -> {
                QOfficialRegionSystemsItems row = new QOfficialRegionSystemsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_systems_items_resources_items" -> {
                QOfficialRegionSystemsItemsResourcesItems row = new QOfficialRegionSystemsItemsResourcesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_systems_items_resources_items_ite_b571aa65e8" -> {
                QOfficialRegionSystemsItemsResourcesItemsItemReferencesItems row = new QOfficialRegionSystemsItemsResourcesItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_tileset_missiondefinitions_items" -> {
                QOfficialTilesetMissionDefinitionsItems row = new QOfficialTilesetMissionDefinitionsItems(ROW);
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
            case "wf_official_region_nextnodes_items" -> {
                QOfficialRegionNextNodesItems row = new QOfficialRegionNextNodesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_questreqs_items" -> {
                QOfficialRegionQuestReqsItems row = new QOfficialRegionQuestReqsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_rewardmanifests_items" -> {
                QOfficialRegionRewardManifestsItems row = new QOfficialRegionRewardManifestsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_systems_items" -> {
                QOfficialRegionSystemsItems row = new QOfficialRegionSystemsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_systems_items_resources_items" -> {
                QOfficialRegionSystemsItemsResourcesItems row = new QOfficialRegionSystemsItemsResourcesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_systems_items_resources_items_ite_b571aa65e8" -> {
                QOfficialRegionSystemsItemsResourcesItemsItemReferencesItems row = new QOfficialRegionSystemsItemsResourcesItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_tileset_root" -> {
                QOfficialTilesetRoot row = new QOfficialTilesetRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_tileset_missiondefinitions_items" -> {
                QOfficialTilesetMissionDefinitionsItems row = new QOfficialTilesetMissionDefinitionsItems(ROW);
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
