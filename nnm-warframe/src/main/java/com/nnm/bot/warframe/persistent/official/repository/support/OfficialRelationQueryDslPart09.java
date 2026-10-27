package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialMiscDissectionPartsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialInternalDefinitionItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialInternalDefinitionRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialMiscDissectionPartsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialMiscHelminthSnackItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialMiscItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialMiscRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialResourcesRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 9；由模型生成器维护。 */
final class OfficialRelationQueryDslPart09 {
    private OfficialRelationQueryDslPart09() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_internal_definition_root" -> {
                QOfficialInternalDefinitionRoot row = new QOfficialInternalDefinitionRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemReferencesState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "parentNameState", "parentNameValue"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemReferencesState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.parentNameState, row.parentNameValue));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_internal_definition_itemreferences_items" -> {
                QOfficialInternalDefinitionItemReferencesItems row = new QOfficialInternalDefinitionItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_misc_root" -> {
                QOfficialMiscRoot row = new QOfficialMiscRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "codexSecretState", "codexSecret", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "dissectionPartsState", "excludeFromCodexState", "excludeFromCodexValue", "excludeFromMarketState", "excludeFromMarket", "helminthSnackCountState", "helminthSnackCount", "helminthSnackGainState", "helminthSnackGain", "helminthSnackItemReferencesState", "helminthSnackTypeState", "helminthSnackType", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "longDescriptionState", "longDescriptionValue", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "parentNameState", "parentNameValue", "pickupQuantityMaxValueState", "pickupQuantityMaxValue", "pickupQuantityMinValueState", "pickupQuantityMinValue", "platinumCostState", "platinumCost", "primeSellingPriceState", "primeSellingPrice", "productCategoryState", "productCategoryValue", "purchaseQuantityState", "purchaseQuantity", "rarityState", "rarity", "recordKeyState", "recordKey", "showInInventoryState", "showInInventory", "standingBonusState", "standingBonus", "syndicateTagState", "syndicateTag", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.codexSecretState, row.codexSecret, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.dissectionPartsState, row.excludeFromCodexState, row.excludeFromCodexValue, row.excludeFromMarketState, row.excludeFromMarket, row.helminthSnackCountState, row.helminthSnackCount, row.helminthSnackGainState, row.helminthSnackGain, row.helminthSnackItemReferencesState, row.helminthSnackTypeState, row.helminthSnackType, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.longDescriptionState, row.longDescriptionValue, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.parentNameState, row.parentNameValue, row.pickupQuantityMaxValueState, row.pickupQuantityMaxValue, row.pickupQuantityMinValueState, row.pickupQuantityMinValue, row.platinumCostState, row.platinumCost, row.primeSellingPriceState, row.primeSellingPrice, row.productCategoryState, row.productCategoryValue, row.purchaseQuantityState, row.purchaseQuantity, row.rarityState, row.rarity, row.recordKeyState, row.recordKey, row.showInInventoryState, row.showInInventory, row.standingBonusState, row.standingBonus, row.syndicateTagState, row.syndicateTag, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_misc_dissectionparts_items" -> {
                QOfficialMiscDissectionPartsItems row = new QOfficialMiscDissectionPartsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemCountState", "itemCount", "itemReferencesState", "itemTypeState", "itemType"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemCountState, row.itemCount, row.itemReferencesState, row.itemTypeState, row.itemType));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_misc_dissectionparts_items_itemreferences_items" -> {
                QOfficialMiscDissectionPartsItemsItemReferencesItems row = new QOfficialMiscDissectionPartsItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_misc_helminthsnack_itemreferences_items" -> {
                QOfficialMiscHelminthSnackItemReferencesItems row = new QOfficialMiscHelminthSnackItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_misc_itemreferences_items" -> {
                QOfficialMiscItemReferencesItems row = new QOfficialMiscItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_resources_root" -> {
                QOfficialResourcesRoot row = new QOfficialResourcesRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "codexSecretState", "codexSecret", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "excludeFromCodexState", "excludeFromCodexValue", "excludeFromMarketState", "excludeFromMarket", "helminthSnackCountState", "helminthSnackCount", "helminthSnackGainState", "helminthSnackGain", "helminthSnackItemReferencesState", "helminthSnackTypeState", "helminthSnackType", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "longDescriptionState", "longDescriptionValue", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "parentNameState", "parentNameValue", "pickupQuantityMaxValueState", "pickupQuantityMaxValue", "pickupQuantityMinValueState", "pickupQuantityMinValue", "platinumCostState", "platinumCost", "productCategoryState", "productCategoryValue", "purchaseQuantityState", "purchaseQuantity", "rarityState", "rarity", "recordKeyState", "recordKey", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.codexSecretState, row.codexSecret, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.excludeFromCodexState, row.excludeFromCodexValue, row.excludeFromMarketState, row.excludeFromMarket, row.helminthSnackCountState, row.helminthSnackCount, row.helminthSnackGainState, row.helminthSnackGain, row.helminthSnackItemReferencesState, row.helminthSnackTypeState, row.helminthSnackType, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.longDescriptionState, row.longDescriptionValue, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.parentNameState, row.parentNameValue, row.pickupQuantityMaxValueState, row.pickupQuantityMaxValue, row.pickupQuantityMinValueState, row.pickupQuantityMinValue, row.platinumCostState, row.platinumCost, row.productCategoryState, row.productCategoryValue, row.purchaseQuantityState, row.purchaseQuantity, row.rarityState, row.rarity, row.recordKeyState, row.recordKey, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_internal_definition_itemreferences_items" -> {
                QOfficialInternalDefinitionItemReferencesItems row = new QOfficialInternalDefinitionItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_misc_dissectionparts_items" -> {
                QOfficialMiscDissectionPartsItems row = new QOfficialMiscDissectionPartsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_misc_dissectionparts_items_itemreferences_items" -> {
                QOfficialMiscDissectionPartsItemsItemReferencesItems row = new QOfficialMiscDissectionPartsItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_misc_helminthsnack_itemreferences_items" -> {
                QOfficialMiscHelminthSnackItemReferencesItems row = new QOfficialMiscHelminthSnackItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_misc_itemreferences_items" -> {
                QOfficialMiscItemReferencesItems row = new QOfficialMiscItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_internal_definition_root" -> {
                QOfficialInternalDefinitionRoot row = new QOfficialInternalDefinitionRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_internal_definition_itemreferences_items" -> {
                QOfficialInternalDefinitionItemReferencesItems row = new QOfficialInternalDefinitionItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_misc_root" -> {
                QOfficialMiscRoot row = new QOfficialMiscRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_misc_dissectionparts_items" -> {
                QOfficialMiscDissectionPartsItems row = new QOfficialMiscDissectionPartsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_misc_dissectionparts_items_itemreferences_items" -> {
                QOfficialMiscDissectionPartsItemsItemReferencesItems row = new QOfficialMiscDissectionPartsItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_misc_helminthsnack_itemreferences_items" -> {
                QOfficialMiscHelminthSnackItemReferencesItems row = new QOfficialMiscHelminthSnackItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_misc_itemreferences_items" -> {
                QOfficialMiscItemReferencesItems row = new QOfficialMiscItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_resources_root" -> {
                QOfficialResourcesRoot row = new QOfficialResourcesRoot("row");
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
