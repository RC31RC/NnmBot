package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialAnimalConservationItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialAnimalRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialGearRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialSentinelsDefaultUpgradesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialSentinelsDefaultUpgradesItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialSentinelsExaltedItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialSentinelsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialSentinelsRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 2；由模型生成器维护。 */
final class OfficialRelationQueryDslPart02 {
    private OfficialRelationQueryDslPart02() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_animal_root" -> {
                QOfficialAnimalRoot row = new QOfficialAnimalRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "conservationItemReferencesState", "conservationItemRewardState", "conservationItemReward", "conservationStandingRewardState", "conservationStandingReward", "conservationWoundedAnimalRewardState", "conservationWoundedAnimalReward", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "healthState", "health", "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "killXPRewardState", "killXPReward", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.conservationItemReferencesState, row.conservationItemRewardState, row.conservationItemReward, row.conservationStandingRewardState, row.conservationStandingReward, row.conservationWoundedAnimalRewardState, row.conservationWoundedAnimalReward, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.healthState, row.health, row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.killXPRewardState, row.killXPReward, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_animal_conservation_itemreferences_items" -> {
                QOfficialAnimalConservationItemReferencesItems row = new QOfficialAnimalConservationItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_sentinels_root" -> {
                QOfficialSentinelsRoot row = new QOfficialSentinelsRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "armorState", "armor", "codexSecretState", "codexSecret", "defaultUpgradesState", "defaultWeaponState", "defaultWeapon", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "exaltedState", "excludeFromCodexState", "excludeFromCodexValue", "excludeFromMarketState", "excludeFromMarket", "healthState", "health", "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "platinumCostState", "platinumCost", "powerState", "power", "productCategoryState", "productCategoryValue", "recordKeyState", "recordKey", "shieldState", "shield", "staminaState", "stamina", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.armorState, row.armor, row.codexSecretState, row.codexSecret, row.defaultUpgradesState, row.defaultWeaponState, row.defaultWeapon, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.exaltedState, row.excludeFromCodexState, row.excludeFromCodexValue, row.excludeFromMarketState, row.excludeFromMarket, row.healthState, row.health, row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.platinumCostState, row.platinumCost, row.powerState, row.power, row.productCategoryState, row.productCategoryValue, row.recordKeyState, row.recordKey, row.shieldState, row.shield, row.staminaState, row.stamina, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_sentinels_defaultupgrades_items" -> {
                QOfficialSentinelsDefaultUpgradesItems row = new QOfficialSentinelsDefaultUpgradesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemReferencesState", "itemTypeState", "itemType", "slotState", "slot"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemReferencesState, row.itemTypeState, row.itemType, row.slotState, row.slot));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_sentinels_defaultupgrades_items_itemrefe_139910031e" -> {
                QOfficialSentinelsDefaultUpgradesItemsItemReferencesItems row = new QOfficialSentinelsDefaultUpgradesItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_sentinels_exalted_items" -> {
                QOfficialSentinelsExaltedItems row = new QOfficialSentinelsExaltedItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_sentinels_itemreferences_items" -> {
                QOfficialSentinelsItemReferencesItems row = new QOfficialSentinelsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_gear_root" -> {
                QOfficialGearRoot row = new QOfficialGearRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "codexSecretState", "codexSecret", "creditsCostState", "creditsCost", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "excludeFromMarketState", "excludeFromMarket", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "parentNameState", "parentNameValue", "platinumCostState", "platinumCost", "purchaseQuantityState", "purchaseQuantity", "recordKeyState", "recordKey", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.codexSecretState, row.codexSecret, row.creditsCostState, row.creditsCost, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.excludeFromMarketState, row.excludeFromMarket, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.parentNameState, row.parentNameValue, row.platinumCostState, row.platinumCost, row.purchaseQuantityState, row.purchaseQuantity, row.recordKeyState, row.recordKey, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_animal_conservation_itemreferences_items" -> {
                QOfficialAnimalConservationItemReferencesItems row = new QOfficialAnimalConservationItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_sentinels_defaultupgrades_items" -> {
                QOfficialSentinelsDefaultUpgradesItems row = new QOfficialSentinelsDefaultUpgradesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_sentinels_defaultupgrades_items_itemrefe_139910031e" -> {
                QOfficialSentinelsDefaultUpgradesItemsItemReferencesItems row = new QOfficialSentinelsDefaultUpgradesItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_sentinels_exalted_items" -> {
                QOfficialSentinelsExaltedItems row = new QOfficialSentinelsExaltedItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_sentinels_itemreferences_items" -> {
                QOfficialSentinelsItemReferencesItems row = new QOfficialSentinelsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_animal_root" -> {
                QOfficialAnimalRoot row = new QOfficialAnimalRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_animal_conservation_itemreferences_items" -> {
                QOfficialAnimalConservationItemReferencesItems row = new QOfficialAnimalConservationItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_sentinels_root" -> {
                QOfficialSentinelsRoot row = new QOfficialSentinelsRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_sentinels_defaultupgrades_items" -> {
                QOfficialSentinelsDefaultUpgradesItems row = new QOfficialSentinelsDefaultUpgradesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_sentinels_defaultupgrades_items_itemrefe_139910031e" -> {
                QOfficialSentinelsDefaultUpgradesItemsItemReferencesItems row = new QOfficialSentinelsDefaultUpgradesItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_sentinels_exalted_items" -> {
                QOfficialSentinelsExaltedItems row = new QOfficialSentinelsExaltedItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_sentinels_itemreferences_items" -> {
                QOfficialSentinelsItemReferencesItems row = new QOfficialSentinelsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_gear_root" -> {
                QOfficialGearRoot row = new QOfficialGearRoot("row");
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
