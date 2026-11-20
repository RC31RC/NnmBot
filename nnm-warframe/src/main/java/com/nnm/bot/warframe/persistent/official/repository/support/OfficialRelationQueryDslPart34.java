package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialTilesetMissionDefinitionsItemsAdvancedSpawnersItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialTilesetMissionDefinitionsItemsEnemySpecsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialTilesetMissionDefinitionsItemsExtraEnemySpecsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialTilesetMissionDefinitionsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialVendorRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialVendorItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialVendorItemsItemsItemPricesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialVendorItemsItemsItemPricesItemsItemReferencesItems;

/** 官方关系表 QueryDSL 标量投影分片 34；由模型生成器维护。 */
final class OfficialRelationQueryDslPart34 {
    private OfficialRelationQueryDslPart34() {}

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
            case "wf_official_tileset_missiondefinitions_items_advance_5b88cc826b" -> {
                QOfficialTilesetMissionDefinitionsItemsAdvancedSpawnersItems row =
                    new QOfficialTilesetMissionDefinitionsItemsAdvancedSpawnersItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_tileset_missiondefinitions_items_enemyspecs_items" -> {
                QOfficialTilesetMissionDefinitionsItemsEnemySpecsItems row =
                    new QOfficialTilesetMissionDefinitionsItemsEnemySpecsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_tileset_missiondefinitions_items_extraen_5acf4c8d13" -> {
                QOfficialTilesetMissionDefinitionsItemsExtraEnemySpecsItems row =
                    new QOfficialTilesetMissionDefinitionsItemsExtraEnemySpecsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_tileset_missiondefinitions_items_itemref_fd49f61252" -> {
                QOfficialTilesetMissionDefinitionsItemsItemReferencesItems row =
                    new QOfficialTilesetMissionDefinitionsItemsItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field",
                        "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState",
                        "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution",
                        "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState,
                        row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState,
                        row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind,
                        row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_vendor_root" -> {
                QOfficialVendorRoot row = new QOfficialVendorRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "isDynamicState", "isDynamic", "isOneBinPerCycleState",
                        "isOneBinPerCycle", "itemsState", "numItemsMaxValueState", "numItemsMaxValue",
                        "numItemsMinValueState", "numItemsMinValue", "numItemsPerBinState",
                        "randomItemPricesPerBinState", "randomSeedTypeState", "randomSeedType", "requiredGoalTagState",
                        "requiredGoalTag"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.isDynamicState, row.isDynamic,
                        row.isOneBinPerCycleState, row.isOneBinPerCycle, row.itemsState, row.numItemsMaxValueState,
                        row.numItemsMaxValue, row.numItemsMinValueState, row.numItemsMinValue, row.numItemsPerBinState,
                        row.randomItemPricesPerBinState, row.randomSeedTypeState, row.randomSeedType,
                        row.requiredGoalTagState, row.requiredGoalTag));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_vendor_items_items" -> {
                QOfficialVendorItemsItems row = new QOfficialVendorItemsItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "alwaysOfferedState", "alwaysOffered", "binState", "bin",
                        "creditsState", "credits", "creditsMaxValueState", "creditsMaxValue", "creditsMinValueState",
                        "creditsMinValue", "creditsStepState", "creditsStep", "duplicatesState", "duplicates",
                        "durationHoursState", "durationHours", "durationHoursMaxValueState", "durationHoursMaxValue",
                        "durationHoursMinValueState", "durationHoursMinValue", "focusXpCostCostState",
                        "focusXpCostCost", "focusXpCostPolarityState", "focusXpCostPolarity", "itemPricesState",
                        "itemReferencesState", "numRandomItemPricesState", "numRandomItemPrices", "platinumState",
                        "platinum", "platinumMaxValueState", "platinumMaxValue", "platinumMinValueState",
                        "platinumMinValue", "probabilityState", "probability", "purchaseLimitState", "purchaseLimit",
                        "quantityState", "quantity", "rotatedWeeklyState", "rotatedWeekly", "storeItemState",
                        "storeItem", "syndicateIncreasePerNegativeRankState", "syndicateIncreasePerNegativeRank",
                        "syndicateMinRankState", "syndicateMinRank", "syndicateReductionPerPositiveRankState",
                        "syndicateReductionPerPositiveRank", "syndicateStandingCostState", "syndicateStandingCost",
                        "syndicateTagState", "syndicateTag"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.alwaysOfferedState, row.alwaysOffered,
                        row.binState, row.bin, row.creditsState, row.credits, row.creditsMaxValueState,
                        row.creditsMaxValue, row.creditsMinValueState, row.creditsMinValue, row.creditsStepState,
                        row.creditsStep, row.duplicatesState, row.duplicates, row.durationHoursState, row.durationHours,
                        row.durationHoursMaxValueState, row.durationHoursMaxValue, row.durationHoursMinValueState,
                        row.durationHoursMinValue, row.focusXpCostCostState, row.focusXpCostCost,
                        row.focusXpCostPolarityState, row.focusXpCostPolarity, row.itemPricesState,
                        row.itemReferencesState, row.numRandomItemPricesState, row.numRandomItemPrices,
                        row.platinumState, row.platinum, row.platinumMaxValueState, row.platinumMaxValue,
                        row.platinumMinValueState, row.platinumMinValue, row.probabilityState, row.probability,
                        row.purchaseLimitState, row.purchaseLimit, row.quantityState, row.quantity,
                        row.rotatedWeeklyState, row.rotatedWeekly, row.storeItemState, row.storeItem,
                        row.syndicateIncreasePerNegativeRankState, row.syndicateIncreasePerNegativeRank,
                        row.syndicateMinRankState, row.syndicateMinRank, row.syndicateReductionPerPositiveRankState,
                        row.syndicateReductionPerPositiveRank, row.syndicateStandingCostState,
                        row.syndicateStandingCost, row.syndicateTagState, row.syndicateTag));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_vendor_items_items_itemprices_items" -> {
                QOfficialVendorItemsItemsItemPricesItems row = new QOfficialVendorItemsItemsItemPricesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "itemCountState", "itemCount", "itemReferencesState",
                        "itemTypeState", "itemType"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemCountState, row.itemCount,
                        row.itemReferencesState, row.itemTypeState, row.itemType));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_vendor_items_items_itemprices_items_item_1d5b0c9f7b" -> {
                QOfficialVendorItemsItemsItemPricesItemsItemReferencesItems row =
                    new QOfficialVendorItemsItemsItemPricesItemsItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field",
                        "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState",
                        "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState",
                        "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState,
                        row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState,
                        row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState,
                        row.resolution, row.uniqueNameState, row.uniqueName));
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
            case "wf_official_tileset_missiondefinitions_items_advance_5b88cc826b" -> {
                QOfficialTilesetMissionDefinitionsItemsAdvancedSpawnersItems row =
                    new QOfficialTilesetMissionDefinitionsItemsAdvancedSpawnersItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_tileset_missiondefinitions_items_enemyspecs_items" -> {
                QOfficialTilesetMissionDefinitionsItemsEnemySpecsItems row =
                    new QOfficialTilesetMissionDefinitionsItemsEnemySpecsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_tileset_missiondefinitions_items_extraen_5acf4c8d13" -> {
                QOfficialTilesetMissionDefinitionsItemsExtraEnemySpecsItems row =
                    new QOfficialTilesetMissionDefinitionsItemsExtraEnemySpecsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_tileset_missiondefinitions_items_itemref_fd49f61252" -> {
                QOfficialTilesetMissionDefinitionsItemsItemReferencesItems row =
                    new QOfficialTilesetMissionDefinitionsItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_vendor_items_items" -> {
                QOfficialVendorItemsItems row = new QOfficialVendorItemsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_vendor_items_items_itemprices_items" -> {
                QOfficialVendorItemsItemsItemPricesItems row = new QOfficialVendorItemsItemsItemPricesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_vendor_items_items_itemprices_items_item_1d5b0c9f7b" -> {
                QOfficialVendorItemsItemsItemPricesItemsItemReferencesItems row =
                    new QOfficialVendorItemsItemsItemPricesItemsItemReferencesItems(ROW);
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
            case "wf_official_tileset_missiondefinitions_items_advance_5b88cc826b" -> {
                QOfficialTilesetMissionDefinitionsItemsAdvancedSpawnersItems row =
                    new QOfficialTilesetMissionDefinitionsItemsAdvancedSpawnersItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_tileset_missiondefinitions_items_enemyspecs_items" -> {
                QOfficialTilesetMissionDefinitionsItemsEnemySpecsItems row =
                    new QOfficialTilesetMissionDefinitionsItemsEnemySpecsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_tileset_missiondefinitions_items_extraen_5acf4c8d13" -> {
                QOfficialTilesetMissionDefinitionsItemsExtraEnemySpecsItems row =
                    new QOfficialTilesetMissionDefinitionsItemsExtraEnemySpecsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_tileset_missiondefinitions_items_itemref_fd49f61252" -> {
                QOfficialTilesetMissionDefinitionsItemsItemReferencesItems row =
                    new QOfficialTilesetMissionDefinitionsItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_vendor_root" -> {
                QOfficialVendorRoot row = new QOfficialVendorRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_vendor_items_items" -> {
                QOfficialVendorItemsItems row = new QOfficialVendorItemsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_vendor_items_items_itemprices_items" -> {
                QOfficialVendorItemsItemsItemPricesItems row = new QOfficialVendorItemsItemsItemPricesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_vendor_items_items_itemprices_items_item_1d5b0c9f7b" -> {
                QOfficialVendorItemsItemsItemPricesItemsItemReferencesItems row =
                    new QOfficialVendorItemsItemsItemPricesItemsItemReferencesItems(ROW);
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
