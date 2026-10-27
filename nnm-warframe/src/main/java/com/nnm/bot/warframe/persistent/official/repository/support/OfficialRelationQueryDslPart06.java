package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialWeaponDefaultUpgradesItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialWeaponItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialAyatanItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialAyatanRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialAyatanSocketsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialBoosterItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialBoosterRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialBundleRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 6；由模型生成器维护。 */
final class OfficialRelationQueryDslPart06 {
    private OfficialRelationQueryDslPart06() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_weapon_defaultupgrades_items_itemreferences_items" -> {
                QOfficialWeaponDefaultUpgradesItemsItemReferencesItems row = new QOfficialWeaponDefaultUpgradesItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_weapon_itemreferences_items" -> {
                QOfficialWeaponItemReferencesItems row = new QOfficialWeaponItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_ayatan_root" -> {
                QOfficialAyatanRoot row = new QOfficialAyatanRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "capacityCostState", "capacityCost", "codexSecretState", "codexSecret", "decoState", "deco", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "dojoCapacityCostState", "dojoCapacityCost", "excludeFromMarketState", "excludeFromMarket", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "parentNameState", "parentNameValue", "productCategoryState", "productCategoryValue", "rarityState", "rarity", "recordKeyState", "recordKey", "socketsState", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.capacityCostState, row.capacityCost, row.codexSecretState, row.codexSecret, row.decoState, row.deco, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.dojoCapacityCostState, row.dojoCapacityCost, row.excludeFromMarketState, row.excludeFromMarket, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.parentNameState, row.parentNameValue, row.productCategoryState, row.productCategoryValue, row.rarityState, row.rarity, row.recordKeyState, row.recordKey, row.socketsState, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_ayatan_itemreferences_items" -> {
                QOfficialAyatanItemReferencesItems row = new QOfficialAyatanItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_ayatan_sockets_items" -> {
                QOfficialAyatanSocketsItems row = new QOfficialAyatanSocketsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_booster_root" -> {
                QOfficialBoosterRoot row = new QOfficialBoosterRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus", "itemReferencesState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "typeNameState", "typeName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.itemReferencesState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.typeNameState, row.typeName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_booster_itemreferences_items" -> {
                QOfficialBoosterItemReferencesItems row = new QOfficialBoosterItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bundle_root" -> {
                QOfficialBundleRoot row = new QOfficialBundleRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "bundledPlatinumState", "bundledPlatinum", "componentsState", "creditsCostState", "creditsCost", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "excludedPlatformsState", "excludeFromCodexState", "excludeFromCodexValue", "excludeFromMarketState", "excludeFromMarket", "giftingBonusState", "giftingBonus", "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "oneTimePurchasableState", "oneTimePurchasable", "packageDiscountState", "packageDiscount", "platinumBundlePairState", "platinumBundlePair", "platinumCostState", "platinumCost"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.bundledPlatinumState, row.bundledPlatinum, row.componentsState, row.creditsCostState, row.creditsCost, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.excludedPlatformsState, row.excludeFromCodexState, row.excludeFromCodexValue, row.excludeFromMarketState, row.excludeFromMarket, row.giftingBonusState, row.giftingBonus, row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.oneTimePurchasableState, row.oneTimePurchasable, row.packageDiscountState, row.packageDiscount, row.platinumBundlePairState, row.platinumBundlePair, row.platinumCostState, row.platinumCost));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_weapon_defaultupgrades_items_itemreferences_items" -> {
                QOfficialWeaponDefaultUpgradesItemsItemReferencesItems row = new QOfficialWeaponDefaultUpgradesItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_weapon_itemreferences_items" -> {
                QOfficialWeaponItemReferencesItems row = new QOfficialWeaponItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_ayatan_itemreferences_items" -> {
                QOfficialAyatanItemReferencesItems row = new QOfficialAyatanItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_ayatan_sockets_items" -> {
                QOfficialAyatanSocketsItems row = new QOfficialAyatanSocketsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_booster_itemreferences_items" -> {
                QOfficialBoosterItemReferencesItems row = new QOfficialBoosterItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_weapon_defaultupgrades_items_itemreferences_items" -> {
                QOfficialWeaponDefaultUpgradesItemsItemReferencesItems row = new QOfficialWeaponDefaultUpgradesItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_weapon_itemreferences_items" -> {
                QOfficialWeaponItemReferencesItems row = new QOfficialWeaponItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_ayatan_root" -> {
                QOfficialAyatanRoot row = new QOfficialAyatanRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_ayatan_itemreferences_items" -> {
                QOfficialAyatanItemReferencesItems row = new QOfficialAyatanItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_ayatan_sockets_items" -> {
                QOfficialAyatanSocketsItems row = new QOfficialAyatanSocketsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_booster_root" -> {
                QOfficialBoosterRoot row = new QOfficialBoosterRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_booster_itemreferences_items" -> {
                QOfficialBoosterItemReferencesItems row = new QOfficialBoosterItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bundle_root" -> {
                QOfficialBundleRoot row = new QOfficialBundleRoot("row");
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
