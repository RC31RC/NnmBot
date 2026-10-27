package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialVendorRandomItemPricesPerBinItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialVendorRandomItemPricesPerBinItemsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialVendorItemsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialVendorNumItemsPerBinItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialVendorRandomItemPricesPerBinItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialActivityRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialBossRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialBossShardCategoryVariantsItems;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 35；由模型生成器维护。 */
final class OfficialRelationQueryDslPart35 {
    private OfficialRelationQueryDslPart35() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_vendor_items_items_itemreferences_items" -> {
                QOfficialVendorItemsItemsItemReferencesItems row = new QOfficialVendorItemsItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_vendor_numitemsperbin_items" -> {
                QOfficialVendorNumItemsPerBinItems row = new QOfficialVendorNumItemsPerBinItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_vendor_randomitempricesperbin_items" -> {
                QOfficialVendorRandomItemPricesPerBinItems row = new QOfficialVendorRandomItemPricesPerBinItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_vendor_randomitempricesperbin_items_items" -> {
                QOfficialVendorRandomItemPricesPerBinItemsItems row = new QOfficialVendorRandomItemPricesPerBinItemsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "countMaxValueState", "countMaxValue", "countMinValueState", "countMinValue", "itemReferencesState", "typeState", "type"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.countMaxValueState, row.countMaxValue, row.countMinValueState, row.countMinValue, row.itemReferencesState, row.typeState, row.type));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_vendor_randomitempricesperbin_items_item_1a10245a72" -> {
                QOfficialVendorRandomItemPricesPerBinItemsItemsItemReferencesItems row = new QOfficialVendorRandomItemPricesPerBinItemsItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_activity_root" -> {
                QOfficialActivityRoot row = new QOfficialActivityRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "descriptionEnState", "descriptionEn", "descriptionKeyState", "descriptionKey", "descriptionZhState", "descriptionZh", "nameEnState", "nameEn", "nameKeyState", "nameKey", "nameZhState", "nameZh"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.descriptionEnState, row.descriptionEn, row.descriptionKeyState, row.descriptionKey, row.descriptionZhState, row.descriptionZh, row.nameEnState, row.nameEn, row.nameKeyState, row.nameKey, row.nameZhState, row.nameZh));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_boss_root" -> {
                QOfficialBossRoot row = new QOfficialBossRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "nameState", "name", "nameEnState", "nameEn", "nameKeyState", "nameKey", "nameZhState", "nameZh", "shardCategorySelectionState", "shardCategorySelection", "shardCategoryVariantsState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameKeyState, row.nameKey, row.nameZhState, row.nameZh, row.shardCategorySelectionState, row.shardCategorySelection, row.shardCategoryVariantsState));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_boss_shardcategory_variants_items" -> {
                QOfficialBossShardCategoryVariantsItems row = new QOfficialBossShardCategoryVariantsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "nameEnState", "nameEn", "nameKeyState", "nameKey", "nameZhState", "nameZh", "tauforgedState", "tauforged"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.nameEnState, row.nameEn, row.nameKeyState, row.nameKey, row.nameZhState, row.nameZh, row.tauforgedState, row.tauforged));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_vendor_items_items_itemreferences_items" -> {
                QOfficialVendorItemsItemsItemReferencesItems row = new QOfficialVendorItemsItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_vendor_numitemsperbin_items" -> {
                QOfficialVendorNumItemsPerBinItems row = new QOfficialVendorNumItemsPerBinItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_vendor_randomitempricesperbin_items" -> {
                QOfficialVendorRandomItemPricesPerBinItems row = new QOfficialVendorRandomItemPricesPerBinItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_vendor_randomitempricesperbin_items_items" -> {
                QOfficialVendorRandomItemPricesPerBinItemsItems row = new QOfficialVendorRandomItemPricesPerBinItemsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_vendor_randomitempricesperbin_items_item_1a10245a72" -> {
                QOfficialVendorRandomItemPricesPerBinItemsItemsItemReferencesItems row = new QOfficialVendorRandomItemPricesPerBinItemsItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_boss_shardcategory_variants_items" -> {
                QOfficialBossShardCategoryVariantsItems row = new QOfficialBossShardCategoryVariantsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_vendor_items_items_itemreferences_items" -> {
                QOfficialVendorItemsItemsItemReferencesItems row = new QOfficialVendorItemsItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_vendor_numitemsperbin_items" -> {
                QOfficialVendorNumItemsPerBinItems row = new QOfficialVendorNumItemsPerBinItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_vendor_randomitempricesperbin_items" -> {
                QOfficialVendorRandomItemPricesPerBinItems row = new QOfficialVendorRandomItemPricesPerBinItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_vendor_randomitempricesperbin_items_items" -> {
                QOfficialVendorRandomItemPricesPerBinItemsItems row = new QOfficialVendorRandomItemPricesPerBinItemsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_vendor_randomitempricesperbin_items_item_1a10245a72" -> {
                QOfficialVendorRandomItemPricesPerBinItemsItemsItemReferencesItems row = new QOfficialVendorRandomItemPricesPerBinItemsItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_activity_root" -> {
                QOfficialActivityRoot row = new QOfficialActivityRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_boss_root" -> {
                QOfficialBossRoot row = new QOfficialBossRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_boss_shardcategory_variants_items" -> {
                QOfficialBossShardCategoryVariantsItems row = new QOfficialBossShardCategoryVariantsItems("row");
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
