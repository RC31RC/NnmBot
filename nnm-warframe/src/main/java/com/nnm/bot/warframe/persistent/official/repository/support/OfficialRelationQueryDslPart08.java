package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialCreditBundleRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialCustomsHexColoursItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialCustomsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialCustomsLegacyColoursItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialCustomsRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialDecorationItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialDecorationRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialFusionBundlesRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 8；由模型生成器维护。 */
final class OfficialRelationQueryDslPart08 {
    private OfficialRelationQueryDslPart08() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_credit_bundle_root" -> {
                QOfficialCreditBundleRoot row = new QOfficialCreditBundleRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "creditsState", "credits", "definitionStatusState", "definitionStatus", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameKeyState", "nameKey", "nameZhState", "nameZh", "platinumCostState", "platinumCost", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.creditsState, row.credits, row.definitionStatusState, row.definitionStatus, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameKeyState, row.nameKey, row.nameZhState, row.nameZh, row.platinumCostState, row.platinumCost, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_customs_root" -> {
                QOfficialCustomsRoot row = new QOfficialCustomsRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "alwaysAvailableState", "alwaysAvailable", "baseState", "base", "codexSecretState", "codexSecret", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "excludeFromCodexState", "excludeFromCodexValue", "excludeFromMarketState", "excludeFromMarket", "hexColoursState", "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "legacyColoursState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "platinumCostState", "platinumCost", "recordKeyState", "recordKey", "titleTagState", "titleTag", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.alwaysAvailableState, row.alwaysAvailable, row.baseState, row.base, row.codexSecretState, row.codexSecret, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.excludeFromCodexState, row.excludeFromCodexValue, row.excludeFromMarketState, row.excludeFromMarket, row.hexColoursState, row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.legacyColoursState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.platinumCostState, row.platinumCost, row.recordKeyState, row.recordKey, row.titleTagState, row.titleTag, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_customs_hexcolours_items" -> {
                QOfficialCustomsHexColoursItems row = new QOfficialCustomsHexColoursItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "valueState", "value"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.valueState, row.value));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_customs_itemreferences_items" -> {
                QOfficialCustomsItemReferencesItems row = new QOfficialCustomsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_customs_legacycolours_items" -> {
                QOfficialCustomsLegacyColoursItems row = new QOfficialCustomsLegacyColoursItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "valueState", "value"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.valueState, row.value));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_decoration_root" -> {
                QOfficialDecorationRoot row = new QOfficialDecorationRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "capacityCostState", "capacityCost", "codexSecretState", "codexSecret", "decoState", "deco", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "dojoCapacityCostState", "dojoCapacityCost", "excludeFromCodexState", "excludeFromCodexValue", "excludeFromMarketState", "excludeFromMarket", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "oneTimePurchasableState", "oneTimePurchasable", "parentNameState", "parentNameValue", "platinumCostState", "platinumCost", "productCategoryState", "productCategoryValue", "recordKeyState", "recordKey", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.capacityCostState, row.capacityCost, row.codexSecretState, row.codexSecret, row.decoState, row.deco, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.dojoCapacityCostState, row.dojoCapacityCost, row.excludeFromCodexState, row.excludeFromCodexValue, row.excludeFromMarketState, row.excludeFromMarket, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.oneTimePurchasableState, row.oneTimePurchasable, row.parentNameState, row.parentNameValue, row.platinumCostState, row.platinumCost, row.productCategoryState, row.productCategoryValue, row.recordKeyState, row.recordKey, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_decoration_itemreferences_items" -> {
                QOfficialDecorationItemReferencesItems row = new QOfficialDecorationItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_fusion_bundles_root" -> {
                QOfficialFusionBundlesRoot row = new QOfficialFusionBundlesRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "codexSecretState", "codexSecret", "currencyState", "currency", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "displayNameEnState", "displayNameEn", "displayNameZhState", "displayNameZh", "fusionPointsState", "fusionPoints", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameZhState", "nameZh", "quantityState", "quantity", "recordKeyState", "recordKey", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.codexSecretState, row.codexSecret, row.currencyState, row.currency, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.displayNameEnState, row.displayNameEn, row.displayNameZhState, row.displayNameZh, row.fusionPointsState, row.fusionPoints, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameZhState, row.nameZh, row.quantityState, row.quantity, row.recordKeyState, row.recordKey, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_customs_hexcolours_items" -> {
                QOfficialCustomsHexColoursItems row = new QOfficialCustomsHexColoursItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_customs_itemreferences_items" -> {
                QOfficialCustomsItemReferencesItems row = new QOfficialCustomsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_customs_legacycolours_items" -> {
                QOfficialCustomsLegacyColoursItems row = new QOfficialCustomsLegacyColoursItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_decoration_itemreferences_items" -> {
                QOfficialDecorationItemReferencesItems row = new QOfficialDecorationItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_credit_bundle_root" -> {
                QOfficialCreditBundleRoot row = new QOfficialCreditBundleRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_customs_root" -> {
                QOfficialCustomsRoot row = new QOfficialCustomsRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_customs_hexcolours_items" -> {
                QOfficialCustomsHexColoursItems row = new QOfficialCustomsHexColoursItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_customs_itemreferences_items" -> {
                QOfficialCustomsItemReferencesItems row = new QOfficialCustomsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_customs_legacycolours_items" -> {
                QOfficialCustomsLegacyColoursItems row = new QOfficialCustomsLegacyColoursItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_decoration_root" -> {
                QOfficialDecorationRoot row = new QOfficialDecorationRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_decoration_itemreferences_items" -> {
                QOfficialDecorationItemReferencesItems row = new QOfficialDecorationItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_fusion_bundles_root" -> {
                QOfficialFusionBundlesRoot row = new QOfficialFusionBundlesRoot("row");
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
