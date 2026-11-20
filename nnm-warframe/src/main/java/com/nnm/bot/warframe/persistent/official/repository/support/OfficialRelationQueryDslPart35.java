package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialVendorItemsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialVendorNumItemsPerBinItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialVendorRandomItemPricesPerBinItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialVendorRandomItemPricesPerBinItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialVendorRandomItemPricesPerBinItemsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialActivityRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialBossRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialBossShardCategoryVariantsItems;

/** 官方关系表 QueryDSL 标量投影分片 35；由模型生成器维护。 */
final class OfficialRelationQueryDslPart35 {
    private OfficialRelationQueryDslPart35() {}

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
            case "wf_official_vendor_items_items_itemreferences_items" -> {
                QOfficialVendorItemsItemsItemReferencesItems row =
                    new QOfficialVendorItemsItemsItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field",
                        "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState",
                        "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState",
                        "resolution", "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState,
                        row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState,
                        row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState,
                        row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_vendor_numitemsperbin_items" -> {
                QOfficialVendorNumItemsPerBinItems row = new QOfficialVendorNumItemsPerBinItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_vendor_randomitempricesperbin_items" -> {
                QOfficialVendorRandomItemPricesPerBinItems row = new QOfficialVendorRandomItemPricesPerBinItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_vendor_randomitempricesperbin_items_items" -> {
                QOfficialVendorRandomItemPricesPerBinItemsItems row =
                    new QOfficialVendorRandomItemPricesPerBinItemsItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "countMaxValueState", "countMaxValue", "countMinValueState",
                        "countMinValue", "itemReferencesState", "typeState", "type"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.countMaxValueState, row.countMaxValue,
                        row.countMinValueState, row.countMinValue, row.itemReferencesState, row.typeState, row.type));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_vendor_randomitempricesperbin_items_item_1a10245a72" -> {
                QOfficialVendorRandomItemPricesPerBinItemsItemsItemReferencesItems row =
                    new QOfficialVendorRandomItemPricesPerBinItemsItemsItemReferencesItems(ROW);
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
            case "wf_official_activity_root" -> {
                QOfficialActivityRoot row = new QOfficialActivityRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "descriptionEnState", "descriptionEn", "descriptionKeyState",
                        "descriptionKey", "descriptionZhState", "descriptionZh", "nameEnState", "nameEn",
                        "nameKeyState", "nameKey", "nameZhState", "nameZh"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.descriptionEnState, row.descriptionEn,
                        row.descriptionKeyState, row.descriptionKey, row.descriptionZhState, row.descriptionZh,
                        row.nameEnState, row.nameEn, row.nameKeyState, row.nameKey, row.nameZhState, row.nameZh));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_boss_root" -> {
                QOfficialBossRoot row = new QOfficialBossRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "nameState", "name", "nameEnState", "nameEn", "nameKeyState",
                        "nameKey", "nameZhState", "nameZh", "shardCategorySelectionState", "shardCategorySelection",
                        "shardCategoryVariantsState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.nameState, row.name, row.nameEnState,
                        row.nameEn, row.nameKeyState, row.nameKey, row.nameZhState, row.nameZh,
                        row.shardCategorySelectionState, row.shardCategorySelection, row.shardCategoryVariantsState));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_boss_shardcategory_variants_items" -> {
                QOfficialBossShardCategoryVariantsItems row = new QOfficialBossShardCategoryVariantsItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "nameEnState", "nameEn", "nameKeyState", "nameKey",
                        "nameZhState", "nameZh", "tauforgedState", "tauforged"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.nameEnState, row.nameEn,
                        row.nameKeyState, row.nameKey, row.nameZhState, row.nameZh, row.tauforgedState, row.tauforged));
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
            case "wf_official_vendor_items_items_itemreferences_items" -> {
                QOfficialVendorItemsItemsItemReferencesItems row =
                    new QOfficialVendorItemsItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_vendor_numitemsperbin_items" -> {
                QOfficialVendorNumItemsPerBinItems row = new QOfficialVendorNumItemsPerBinItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_vendor_randomitempricesperbin_items" -> {
                QOfficialVendorRandomItemPricesPerBinItems row = new QOfficialVendorRandomItemPricesPerBinItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_vendor_randomitempricesperbin_items_items" -> {
                QOfficialVendorRandomItemPricesPerBinItemsItems row =
                    new QOfficialVendorRandomItemPricesPerBinItemsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_vendor_randomitempricesperbin_items_item_1a10245a72" -> {
                QOfficialVendorRandomItemPricesPerBinItemsItemsItemReferencesItems row =
                    new QOfficialVendorRandomItemPricesPerBinItemsItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_boss_shardcategory_variants_items" -> {
                QOfficialBossShardCategoryVariantsItems row = new QOfficialBossShardCategoryVariantsItems(ROW);
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
            case "wf_official_vendor_items_items_itemreferences_items" -> {
                QOfficialVendorItemsItemsItemReferencesItems row =
                    new QOfficialVendorItemsItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_vendor_numitemsperbin_items" -> {
                QOfficialVendorNumItemsPerBinItems row = new QOfficialVendorNumItemsPerBinItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_vendor_randomitempricesperbin_items" -> {
                QOfficialVendorRandomItemPricesPerBinItems row = new QOfficialVendorRandomItemPricesPerBinItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_vendor_randomitempricesperbin_items_items" -> {
                QOfficialVendorRandomItemPricesPerBinItemsItems row =
                    new QOfficialVendorRandomItemPricesPerBinItemsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_vendor_randomitempricesperbin_items_item_1a10245a72" -> {
                QOfficialVendorRandomItemPricesPerBinItemsItemsItemReferencesItems row =
                    new QOfficialVendorRandomItemPricesPerBinItemsItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_activity_root" -> {
                QOfficialActivityRoot row = new QOfficialActivityRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_boss_root" -> {
                QOfficialBossRoot row = new QOfficialBossRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_boss_shardcategory_variants_items" -> {
                QOfficialBossShardCategoryVariantsItems row = new QOfficialBossShardCategoryVariantsItems(ROW);
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
