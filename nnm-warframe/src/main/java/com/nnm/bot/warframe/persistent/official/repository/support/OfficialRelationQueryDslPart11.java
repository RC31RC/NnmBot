package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialSkinRandomisedUpgradesItemsRangeItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialSkinExcludedPlatformsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialSkinItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialSkinRandomisedUpgradesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialSkinSubroutinesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialSongItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialSongRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialSupplyDropRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 11；由模型生成器维护。 */
final class OfficialRelationQueryDslPart11 {
    private OfficialRelationQueryDslPart11() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_skin_excludedplatforms_items" -> {
                QOfficialSkinExcludedPlatformsItems row = new QOfficialSkinExcludedPlatformsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_skin_itemreferences_items" -> {
                QOfficialSkinItemReferencesItems row = new QOfficialSkinItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_skin_randomisedupgrades_items" -> {
                QOfficialSkinRandomisedUpgradesItems row = new QOfficialSkinRandomisedUpgradesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "rangeState", "tagState", "tag"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.rangeState, row.tagState, row.tag));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_skin_randomisedupgrades_items_range_items" -> {
                QOfficialSkinRandomisedUpgradesItemsRangeItems row = new QOfficialSkinRandomisedUpgradesItemsRangeItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_skin_subroutines_items" -> {
                QOfficialSkinSubroutinesItems row = new QOfficialSkinSubroutinesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_song_root" -> {
                QOfficialSongRoot row = new QOfficialSongRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "codexSecretState", "codexSecret", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "excludeFromCodexState", "excludeFromCodexValue", "excludeFromMarketState", "excludeFromMarket", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "oneTimePurchasableState", "oneTimePurchasable", "parentNameState", "parentNameValue", "productCategoryState", "productCategoryValue", "recordKeyState", "recordKey", "showInInventoryState", "showInInventory", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.codexSecretState, row.codexSecret, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.excludeFromCodexState, row.excludeFromCodexValue, row.excludeFromMarketState, row.excludeFromMarket, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.oneTimePurchasableState, row.oneTimePurchasable, row.parentNameState, row.parentNameValue, row.productCategoryState, row.productCategoryValue, row.recordKeyState, row.recordKey, row.showInInventoryState, row.showInInventory, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_song_itemreferences_items" -> {
                QOfficialSongItemReferencesItems row = new QOfficialSongItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_supply_drop_root" -> {
                QOfficialSupplyDropRoot row = new QOfficialSupplyDropRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "codexSecretState", "codexSecret", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "excludeFromCodexState", "excludeFromCodexValue", "excludeFromMarketState", "excludeFromMarket", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "parentNameState", "parentNameValue", "productCategoryState", "productCategoryValue", "recordKeyState", "recordKey", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.codexSecretState, row.codexSecret, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.excludeFromCodexState, row.excludeFromCodexValue, row.excludeFromMarketState, row.excludeFromMarket, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.parentNameState, row.parentNameValue, row.productCategoryState, row.productCategoryValue, row.recordKeyState, row.recordKey, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_skin_excludedplatforms_items" -> {
                QOfficialSkinExcludedPlatformsItems row = new QOfficialSkinExcludedPlatformsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_skin_itemreferences_items" -> {
                QOfficialSkinItemReferencesItems row = new QOfficialSkinItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_skin_randomisedupgrades_items" -> {
                QOfficialSkinRandomisedUpgradesItems row = new QOfficialSkinRandomisedUpgradesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_skin_randomisedupgrades_items_range_items" -> {
                QOfficialSkinRandomisedUpgradesItemsRangeItems row = new QOfficialSkinRandomisedUpgradesItemsRangeItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_skin_subroutines_items" -> {
                QOfficialSkinSubroutinesItems row = new QOfficialSkinSubroutinesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_song_itemreferences_items" -> {
                QOfficialSongItemReferencesItems row = new QOfficialSongItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_skin_excludedplatforms_items" -> {
                QOfficialSkinExcludedPlatformsItems row = new QOfficialSkinExcludedPlatformsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_skin_itemreferences_items" -> {
                QOfficialSkinItemReferencesItems row = new QOfficialSkinItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_skin_randomisedupgrades_items" -> {
                QOfficialSkinRandomisedUpgradesItems row = new QOfficialSkinRandomisedUpgradesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_skin_randomisedupgrades_items_range_items" -> {
                QOfficialSkinRandomisedUpgradesItemsRangeItems row = new QOfficialSkinRandomisedUpgradesItemsRangeItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_skin_subroutines_items" -> {
                QOfficialSkinSubroutinesItems row = new QOfficialSkinSubroutinesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_song_root" -> {
                QOfficialSongRoot row = new QOfficialSongRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_song_itemreferences_items" -> {
                QOfficialSongItemReferencesItems row = new QOfficialSongItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_supply_drop_root" -> {
                QOfficialSupplyDropRoot row = new QOfficialSupplyDropRoot("row");
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
