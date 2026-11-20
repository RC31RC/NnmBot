package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialDronesRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialDronesCapacityMultiplierItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialDronesSpecialitiesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialShipRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialShipItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialWarframeRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialWarframeAbilitiesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialWarframeAdditionalItemsItems;

/** 官方关系表 QueryDSL 标量投影分片 4；由模型生成器维护。 */
final class OfficialRelationQueryDslPart04 {
    private OfficialRelationQueryDslPart04() {}

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
            case "wf_official_drones_root" -> {
                QOfficialDronesRoot row = new QOfficialDronesRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "binCapacityState", "binCapacity", "binCountState", "binCount",
                        "capacityMultiplierState", "capacityMultipliersState", "capacityMultipliersCommonState",
                        "capacityMultipliersCommon", "capacityMultipliersLegendaryState",
                        "capacityMultipliersLegendary", "capacityMultipliersRareState", "capacityMultipliersRare",
                        "capacityMultipliersUncommonState", "capacityMultipliersUncommon", "codexSecretState",
                        "codexSecret", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn",
                        "descriptionZhState", "descriptionZh", "durabilityState", "durability", "fillRateState",
                        "fillRate", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl",
                        "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "probabilitiesState",
                        "probabilitiesCommonState", "probabilitiesCommon", "probabilitiesLegendaryState",
                        "probabilitiesLegendary", "probabilitiesRareState", "probabilitiesRare",
                        "probabilitiesUncommonState", "probabilitiesUncommon", "recordKeyState", "recordKey",
                        "repairRateState", "repairRate", "specialitiesState", "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.binCapacityState, row.binCapacity,
                        row.binCountState, row.binCount, row.capacityMultiplierState, row.capacityMultipliersState,
                        row.capacityMultipliersCommonState, row.capacityMultipliersCommon,
                        row.capacityMultipliersLegendaryState, row.capacityMultipliersLegendary,
                        row.capacityMultipliersRareState, row.capacityMultipliersRare,
                        row.capacityMultipliersUncommonState, row.capacityMultipliersUncommon, row.codexSecretState,
                        row.codexSecret, row.descriptionState, row.descriptionValue, row.descriptionEnState,
                        row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.durabilityState,
                        row.durability, row.fillRateState, row.fillRate, row.iconState, row.icon, row.iconStatusState,
                        row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState,
                        row.nameEn, row.nameZhState, row.nameZh, row.probabilitiesState, row.probabilitiesCommonState,
                        row.probabilitiesCommon, row.probabilitiesLegendaryState, row.probabilitiesLegendary,
                        row.probabilitiesRareState, row.probabilitiesRare, row.probabilitiesUncommonState,
                        row.probabilitiesUncommon, row.recordKeyState, row.recordKey, row.repairRateState,
                        row.repairRate, row.specialitiesState, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_drones_capacitymultiplier_items" -> {
                QOfficialDronesCapacityMultiplierItems row = new QOfficialDronesCapacityMultiplierItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_drones_specialities_items" -> {
                QOfficialDronesSpecialitiesItems row = new QOfficialDronesSpecialitiesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_ship_root" -> {
                QOfficialShipRoot row = new QOfficialShipRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "codexSecretState", "codexSecret", "descriptionState",
                        "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState",
                        "descriptionZh", "excludeFromCodexState", "excludeFromCodexValue", "excludeFromMarketState",
                        "excludeFromMarket", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState",
                        "iconUrl", "itemReferencesState", "nameState", "name", "nameEnState", "nameEn", "nameZhState",
                        "nameZh", "parentNameState", "parentNameValue", "platinumCostState", "platinumCost",
                        "productCategoryState", "productCategoryValue", "recordKeyState", "recordKey",
                        "showInInventoryState", "showInInventory", "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.codexSecretState, row.codexSecret,
                        row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn,
                        row.descriptionZhState, row.descriptionZh, row.excludeFromCodexState, row.excludeFromCodexValue,
                        row.excludeFromMarketState, row.excludeFromMarket, row.iconState, row.icon, row.iconStatusState,
                        row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.nameState, row.name,
                        row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.parentNameState,
                        row.parentNameValue, row.platinumCostState, row.platinumCost, row.productCategoryState,
                        row.productCategoryValue, row.recordKeyState, row.recordKey, row.showInInventoryState,
                        row.showInInventory, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_ship_itemreferences_items" -> {
                QOfficialShipItemReferencesItems row = new QOfficialShipItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "fieldState", "field", "iconStatusState", "iconStatus",
                        "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState",
                        "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.fieldState, row.field,
                        row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn,
                        row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState,
                        row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_warframe_root" -> {
                QOfficialWarframeRoot row = new QOfficialWarframeRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "abilitiesState", "additionalItemsState", "armorState", "armor",
                        "avatarUrlState", "avatarUrl", "codexSecretState", "codexSecret", "descriptionState",
                        "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState",
                        "descriptionZh", "exaltedState", "excludeFromCodexState", "excludeFromCodexValue",
                        "excludeFromMarketState", "excludeFromMarket", "healthState", "health", "iconState", "icon",
                        "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "introducedAtState", "introducedAt",
                        "itemReferencesState", "longDescriptionState", "longDescriptionValue", "masteryReqState",
                        "masteryReq", "maxLevelCapState", "maxLevelCap", "mediaDioramaVideoUrlState",
                        "mediaDioramaVideoUrl", "mediaSplashImageUrlState", "mediaSplashImageUrl", "nameState", "name",
                        "nameEnState", "nameEn", "nameKeyState", "nameKey", "nameZhState", "nameZh",
                        "nemesisUpgradeTagState", "nemesisUpgradeTag", "ownerTypeState", "ownerType", "parentNameState",
                        "parentNameValue", "passiveDescriptionState", "passiveDescription", "passiveDescriptionEnState",
                        "passiveDescriptionEn", "passiveDescriptionZhState", "passiveDescriptionZh",
                        "platinumCostState", "platinumCost", "powerState", "power", "productCategoryState",
                        "productCategoryValue", "recordKeyState", "recordKey", "shieldState", "shield",
                        "sprintSpeedState", "sprintSpeed", "staminaState", "stamina", "uniqueNameState", "uniqueName",
                        "variantTypeState", "variantType"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.abilitiesState,
                        row.additionalItemsState, row.armorState, row.armor, row.avatarUrlState, row.avatarUrl,
                        row.codexSecretState, row.codexSecret, row.descriptionState, row.descriptionValue,
                        row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh,
                        row.exaltedState, row.excludeFromCodexState, row.excludeFromCodexValue,
                        row.excludeFromMarketState, row.excludeFromMarket, row.healthState, row.health, row.iconState,
                        row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl,
                        row.introducedAtState, row.introducedAt, row.itemReferencesState, row.longDescriptionState,
                        row.longDescriptionValue, row.masteryReqState, row.masteryReq, row.maxLevelCapState,
                        row.maxLevelCap, row.mediaDioramaVideoUrlState, row.mediaDioramaVideoUrl,
                        row.mediaSplashImageUrlState, row.mediaSplashImageUrl, row.nameState, row.name, row.nameEnState,
                        row.nameEn, row.nameKeyState, row.nameKey, row.nameZhState, row.nameZh,
                        row.nemesisUpgradeTagState, row.nemesisUpgradeTag, row.ownerTypeState, row.ownerType,
                        row.parentNameState, row.parentNameValue, row.passiveDescriptionState, row.passiveDescription,
                        row.passiveDescriptionEnState, row.passiveDescriptionEn, row.passiveDescriptionZhState,
                        row.passiveDescriptionZh, row.platinumCostState, row.platinumCost, row.powerState, row.power,
                        row.productCategoryState, row.productCategoryValue, row.recordKeyState, row.recordKey,
                        row.shieldState, row.shield, row.sprintSpeedState, row.sprintSpeed, row.staminaState,
                        row.stamina, row.uniqueNameState, row.uniqueName, row.variantTypeState, row.variantType));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_warframe_abilities_items" -> {
                QOfficialWarframeAbilitiesItems row = new QOfficialWarframeAbilitiesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "abilityIconUrlState", "abilityIconUrl", "abilityNameState",
                        "abilityName", "abilityNameZhState", "abilityNameZh", "abilityUniqueNameState",
                        "abilityUniqueName", "descriptionState", "description", "descriptionEnState", "descriptionEn",
                        "descriptionZhState", "descriptionZh", "energyConsumptionOverTimeState",
                        "energyConsumptionOverTime", "energyRequiredToActivateState", "energyRequiredToActivate",
                        "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl",
                        "mediaShowcaseImageUrlState", "mediaShowcaseImageUrl", "mediaShowcaseVideoUrlState",
                        "mediaShowcaseVideoUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh",
                        "recordKeyState", "recordKey", "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.abilityIconUrlState, row.abilityIconUrl,
                        row.abilityNameState, row.abilityName, row.abilityNameZhState, row.abilityNameZh,
                        row.abilityUniqueNameState, row.abilityUniqueName, row.descriptionState, row.description,
                        row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh,
                        row.energyConsumptionOverTimeState, row.energyConsumptionOverTime,
                        row.energyRequiredToActivateState, row.energyRequiredToActivate, row.iconState, row.icon,
                        row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl,
                        row.mediaShowcaseImageUrlState, row.mediaShowcaseImageUrl, row.mediaShowcaseVideoUrlState,
                        row.mediaShowcaseVideoUrl, row.nameState, row.name, row.nameEnState, row.nameEn,
                        row.nameZhState, row.nameZh, row.recordKeyState, row.recordKey, row.uniqueNameState,
                        row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_warframe_additionalitems_items" -> {
                QOfficialWarframeAdditionalItemsItems row = new QOfficialWarframeAdditionalItemsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
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
            case "wf_official_drones_capacitymultiplier_items" -> {
                QOfficialDronesCapacityMultiplierItems row = new QOfficialDronesCapacityMultiplierItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_drones_specialities_items" -> {
                QOfficialDronesSpecialitiesItems row = new QOfficialDronesSpecialitiesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_ship_itemreferences_items" -> {
                QOfficialShipItemReferencesItems row = new QOfficialShipItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_warframe_abilities_items" -> {
                QOfficialWarframeAbilitiesItems row = new QOfficialWarframeAbilitiesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_warframe_additionalitems_items" -> {
                QOfficialWarframeAdditionalItemsItems row = new QOfficialWarframeAdditionalItemsItems(ROW);
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
            case "wf_official_drones_root" -> {
                QOfficialDronesRoot row = new QOfficialDronesRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_drones_capacitymultiplier_items" -> {
                QOfficialDronesCapacityMultiplierItems row = new QOfficialDronesCapacityMultiplierItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_drones_specialities_items" -> {
                QOfficialDronesSpecialitiesItems row = new QOfficialDronesSpecialitiesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_ship_root" -> {
                QOfficialShipRoot row = new QOfficialShipRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_ship_itemreferences_items" -> {
                QOfficialShipItemReferencesItems row = new QOfficialShipItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_warframe_root" -> {
                QOfficialWarframeRoot row = new QOfficialWarframeRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_warframe_abilities_items" -> {
                QOfficialWarframeAbilitiesItems row = new QOfficialWarframeAbilitiesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_warframe_additionalitems_items" -> {
                QOfficialWarframeAdditionalItemsItems row = new QOfficialWarframeAdditionalItemsItems(ROW);
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
