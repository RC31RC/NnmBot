package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialDronesCapacityMultiplierItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialDronesRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialDronesSpecialitiesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialShipItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialShipRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialWarframeAbilitiesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialWarframeAdditionalItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialWarframeRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 4；由模型生成器维护。 */
final class OfficialRelationQueryDslPart04 {
    private OfficialRelationQueryDslPart04() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_drones_root" -> {
                QOfficialDronesRoot row = new QOfficialDronesRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "binCapacityState", "binCapacity", "binCountState", "binCount", "capacityMultiplierState", "capacityMultipliersState", "capacityMultipliersCommonState", "capacityMultipliersCommon", "capacityMultipliersLegendaryState", "capacityMultipliersLegendary", "capacityMultipliersRareState", "capacityMultipliersRare", "capacityMultipliersUncommonState", "capacityMultipliersUncommon", "codexSecretState", "codexSecret", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "durabilityState", "durability", "fillRateState", "fillRate", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "probabilitiesState", "probabilitiesCommonState", "probabilitiesCommon", "probabilitiesLegendaryState", "probabilitiesLegendary", "probabilitiesRareState", "probabilitiesRare", "probabilitiesUncommonState", "probabilitiesUncommon", "recordKeyState", "recordKey", "repairRateState", "repairRate", "specialitiesState", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.binCapacityState, row.binCapacity, row.binCountState, row.binCount, row.capacityMultiplierState, row.capacityMultipliersState, row.capacityMultipliersCommonState, row.capacityMultipliersCommon, row.capacityMultipliersLegendaryState, row.capacityMultipliersLegendary, row.capacityMultipliersRareState, row.capacityMultipliersRare, row.capacityMultipliersUncommonState, row.capacityMultipliersUncommon, row.codexSecretState, row.codexSecret, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.durabilityState, row.durability, row.fillRateState, row.fillRate, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.probabilitiesState, row.probabilitiesCommonState, row.probabilitiesCommon, row.probabilitiesLegendaryState, row.probabilitiesLegendary, row.probabilitiesRareState, row.probabilitiesRare, row.probabilitiesUncommonState, row.probabilitiesUncommon, row.recordKeyState, row.recordKey, row.repairRateState, row.repairRate, row.specialitiesState, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_drones_capacitymultiplier_items" -> {
                QOfficialDronesCapacityMultiplierItems row = new QOfficialDronesCapacityMultiplierItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_drones_specialities_items" -> {
                QOfficialDronesSpecialitiesItems row = new QOfficialDronesSpecialitiesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_ship_root" -> {
                QOfficialShipRoot row = new QOfficialShipRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "codexSecretState", "codexSecret", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "excludeFromCodexState", "excludeFromCodexValue", "excludeFromMarketState", "excludeFromMarket", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "parentNameState", "parentNameValue", "platinumCostState", "platinumCost", "productCategoryState", "productCategoryValue", "recordKeyState", "recordKey", "showInInventoryState", "showInInventory", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.codexSecretState, row.codexSecret, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.excludeFromCodexState, row.excludeFromCodexValue, row.excludeFromMarketState, row.excludeFromMarket, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.parentNameState, row.parentNameValue, row.platinumCostState, row.platinumCost, row.productCategoryState, row.productCategoryValue, row.recordKeyState, row.recordKey, row.showInInventoryState, row.showInInventory, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_ship_itemreferences_items" -> {
                QOfficialShipItemReferencesItems row = new QOfficialShipItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_warframe_root" -> {
                QOfficialWarframeRoot row = new QOfficialWarframeRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "abilitiesState", "additionalItemsState", "armorState", "armor", "avatarUrlState", "avatarUrl", "codexSecretState", "codexSecret", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "exaltedState", "excludeFromCodexState", "excludeFromCodexValue", "excludeFromMarketState", "excludeFromMarket", "healthState", "health", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "introducedAtState", "introducedAt", "itemReferencesState", "longDescriptionState", "longDescriptionValue", "masteryReqState", "masteryReq", "maxLevelCapState", "maxLevelCap", "mediaDioramaVideoUrlState", "mediaDioramaVideoUrl", "mediaSplashImageUrlState", "mediaSplashImageUrl", "nameState", "name", "nameEnState", "nameEn", "nameKeyState", "nameKey", "nameZhState", "nameZh", "nemesisUpgradeTagState", "nemesisUpgradeTag", "ownerTypeState", "ownerType", "parentNameState", "parentNameValue", "passiveDescriptionState", "passiveDescription", "passiveDescriptionEnState", "passiveDescriptionEn", "passiveDescriptionZhState", "passiveDescriptionZh", "platinumCostState", "platinumCost", "powerState", "power", "productCategoryState", "productCategoryValue", "recordKeyState", "recordKey", "shieldState", "shield", "sprintSpeedState", "sprintSpeed", "staminaState", "stamina", "uniqueNameState", "uniqueName", "variantTypeState", "variantType"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.abilitiesState, row.additionalItemsState, row.armorState, row.armor, row.avatarUrlState, row.avatarUrl, row.codexSecretState, row.codexSecret, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.exaltedState, row.excludeFromCodexState, row.excludeFromCodexValue, row.excludeFromMarketState, row.excludeFromMarket, row.healthState, row.health, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.introducedAtState, row.introducedAt, row.itemReferencesState, row.longDescriptionState, row.longDescriptionValue, row.masteryReqState, row.masteryReq, row.maxLevelCapState, row.maxLevelCap, row.mediaDioramaVideoUrlState, row.mediaDioramaVideoUrl, row.mediaSplashImageUrlState, row.mediaSplashImageUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameKeyState, row.nameKey, row.nameZhState, row.nameZh, row.nemesisUpgradeTagState, row.nemesisUpgradeTag, row.ownerTypeState, row.ownerType, row.parentNameState, row.parentNameValue, row.passiveDescriptionState, row.passiveDescription, row.passiveDescriptionEnState, row.passiveDescriptionEn, row.passiveDescriptionZhState, row.passiveDescriptionZh, row.platinumCostState, row.platinumCost, row.powerState, row.power, row.productCategoryState, row.productCategoryValue, row.recordKeyState, row.recordKey, row.shieldState, row.shield, row.sprintSpeedState, row.sprintSpeed, row.staminaState, row.stamina, row.uniqueNameState, row.uniqueName, row.variantTypeState, row.variantType));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_warframe_abilities_items" -> {
                QOfficialWarframeAbilitiesItems row = new QOfficialWarframeAbilitiesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "abilityIconUrlState", "abilityIconUrl", "abilityNameState", "abilityName", "abilityNameZhState", "abilityNameZh", "abilityUniqueNameState", "abilityUniqueName", "descriptionState", "description", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "energyConsumptionOverTimeState", "energyConsumptionOverTime", "energyRequiredToActivateState", "energyRequiredToActivate", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "mediaShowcaseImageUrlState", "mediaShowcaseImageUrl", "mediaShowcaseVideoUrlState", "mediaShowcaseVideoUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "recordKeyState", "recordKey", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.abilityIconUrlState, row.abilityIconUrl, row.abilityNameState, row.abilityName, row.abilityNameZhState, row.abilityNameZh, row.abilityUniqueNameState, row.abilityUniqueName, row.descriptionState, row.description, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.energyConsumptionOverTimeState, row.energyConsumptionOverTime, row.energyRequiredToActivateState, row.energyRequiredToActivate, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.mediaShowcaseImageUrlState, row.mediaShowcaseImageUrl, row.mediaShowcaseVideoUrlState, row.mediaShowcaseVideoUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.recordKeyState, row.recordKey, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_warframe_additionalitems_items" -> {
                QOfficialWarframeAdditionalItemsItems row = new QOfficialWarframeAdditionalItemsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_drones_capacitymultiplier_items" -> {
                QOfficialDronesCapacityMultiplierItems row = new QOfficialDronesCapacityMultiplierItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_drones_specialities_items" -> {
                QOfficialDronesSpecialitiesItems row = new QOfficialDronesSpecialitiesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_ship_itemreferences_items" -> {
                QOfficialShipItemReferencesItems row = new QOfficialShipItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_warframe_abilities_items" -> {
                QOfficialWarframeAbilitiesItems row = new QOfficialWarframeAbilitiesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_warframe_additionalitems_items" -> {
                QOfficialWarframeAdditionalItemsItems row = new QOfficialWarframeAdditionalItemsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_drones_root" -> {
                QOfficialDronesRoot row = new QOfficialDronesRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_drones_capacitymultiplier_items" -> {
                QOfficialDronesCapacityMultiplierItems row = new QOfficialDronesCapacityMultiplierItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_drones_specialities_items" -> {
                QOfficialDronesSpecialitiesItems row = new QOfficialDronesSpecialitiesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_ship_root" -> {
                QOfficialShipRoot row = new QOfficialShipRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_ship_itemreferences_items" -> {
                QOfficialShipItemReferencesItems row = new QOfficialShipItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_warframe_root" -> {
                QOfficialWarframeRoot row = new QOfficialWarframeRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_warframe_abilities_items" -> {
                QOfficialWarframeAbilitiesItems row = new QOfficialWarframeAbilitiesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_warframe_additionalitems_items" -> {
                QOfficialWarframeAdditionalItemsItems row = new QOfficialWarframeAdditionalItemsItems("row");
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
