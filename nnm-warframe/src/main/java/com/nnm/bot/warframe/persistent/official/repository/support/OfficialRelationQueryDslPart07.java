package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialBundleComponentsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialBundleComponentsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialBundleExcludedPlatformsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialBundleItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialCapturaItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialCapturaRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialComponentItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialComponentRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 7；由模型生成器维护。 */
final class OfficialRelationQueryDslPart07 {
    private OfficialRelationQueryDslPart07() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_bundle_components_items" -> {
                QOfficialBundleComponentsItems row = new QOfficialBundleComponentsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "durabilityDaysState", "durabilityDays", "giveMaxRankState", "giveMaxRank", "itemReferencesState", "purchaseQuantityState", "purchaseQuantity", "typeNameState", "typeName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.durabilityDaysState, row.durabilityDays, row.giveMaxRankState, row.giveMaxRank, row.itemReferencesState, row.purchaseQuantityState, row.purchaseQuantity, row.typeNameState, row.typeName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bundle_components_items_itemreferences_items" -> {
                QOfficialBundleComponentsItemsItemReferencesItems row = new QOfficialBundleComponentsItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bundle_excludedplatforms_items" -> {
                QOfficialBundleExcludedPlatformsItems row = new QOfficialBundleExcludedPlatformsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bundle_itemreferences_items" -> {
                QOfficialBundleItemReferencesItems row = new QOfficialBundleItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_captura_root" -> {
                QOfficialCapturaRoot row = new QOfficialCapturaRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "codexSecretState", "codexSecret", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "excludeFromCodexState", "excludeFromCodexValue", "excludeFromMarketState", "excludeFromMarket", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "parentNameState", "parentNameValue", "productCategoryState", "productCategoryValue", "recordKeyState", "recordKey", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.codexSecretState, row.codexSecret, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.excludeFromCodexState, row.excludeFromCodexValue, row.excludeFromMarketState, row.excludeFromMarket, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.parentNameState, row.parentNameValue, row.productCategoryState, row.productCategoryValue, row.recordKeyState, row.recordKey, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_captura_itemreferences_items" -> {
                QOfficialCapturaItemReferencesItems row = new QOfficialCapturaItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_component_root" -> {
                QOfficialComponentRoot row = new QOfficialComponentRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "codexSecretState", "codexSecret", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "excludeFromCodexState", "excludeFromCodexValue", "excludeFromMarketState", "excludeFromMarket", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "parentNameState", "parentNameValue", "primeSellingPriceState", "primeSellingPrice", "productCategoryState", "productCategoryValue", "recordKeyState", "recordKey", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.codexSecretState, row.codexSecret, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.excludeFromCodexState, row.excludeFromCodexValue, row.excludeFromMarketState, row.excludeFromMarket, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.parentNameState, row.parentNameValue, row.primeSellingPriceState, row.primeSellingPrice, row.productCategoryState, row.productCategoryValue, row.recordKeyState, row.recordKey, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_component_itemreferences_items" -> {
                QOfficialComponentItemReferencesItems row = new QOfficialComponentItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_bundle_components_items" -> {
                QOfficialBundleComponentsItems row = new QOfficialBundleComponentsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bundle_components_items_itemreferences_items" -> {
                QOfficialBundleComponentsItemsItemReferencesItems row = new QOfficialBundleComponentsItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bundle_excludedplatforms_items" -> {
                QOfficialBundleExcludedPlatformsItems row = new QOfficialBundleExcludedPlatformsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bundle_itemreferences_items" -> {
                QOfficialBundleItemReferencesItems row = new QOfficialBundleItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_captura_itemreferences_items" -> {
                QOfficialCapturaItemReferencesItems row = new QOfficialCapturaItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_component_itemreferences_items" -> {
                QOfficialComponentItemReferencesItems row = new QOfficialComponentItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_bundle_components_items" -> {
                QOfficialBundleComponentsItems row = new QOfficialBundleComponentsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bundle_components_items_itemreferences_items" -> {
                QOfficialBundleComponentsItemsItemReferencesItems row = new QOfficialBundleComponentsItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bundle_excludedplatforms_items" -> {
                QOfficialBundleExcludedPlatformsItems row = new QOfficialBundleExcludedPlatformsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bundle_itemreferences_items" -> {
                QOfficialBundleItemReferencesItems row = new QOfficialBundleItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_captura_root" -> {
                QOfficialCapturaRoot row = new QOfficialCapturaRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_captura_itemreferences_items" -> {
                QOfficialCapturaItemReferencesItems row = new QOfficialCapturaItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_component_root" -> {
                QOfficialComponentRoot row = new QOfficialComponentRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_component_itemreferences_items" -> {
                QOfficialComponentItemReferencesItems row = new QOfficialComponentItemReferencesItems("row");
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
