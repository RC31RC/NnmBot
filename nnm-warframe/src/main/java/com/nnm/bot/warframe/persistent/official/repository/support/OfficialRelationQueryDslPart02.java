package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialAnimalRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialAnimalConservationItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialSentinelsRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialSentinelsDefaultUpgradesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialSentinelsDefaultUpgradesItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialSentinelsExaltedItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialSentinelsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialGearRoot;

/** 官方关系表 QueryDSL 标量投影分片 2；由模型生成器维护。 */
final class OfficialRelationQueryDslPart02 {
    private OfficialRelationQueryDslPart02() {}

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
            case "wf_official_animal_root" -> {
                QOfficialAnimalRoot row = new QOfficialAnimalRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "conservationItemReferencesState", "conservationItemRewardState",
                        "conservationItemReward", "conservationStandingRewardState", "conservationStandingReward",
                        "conservationWoundedAnimalRewardState", "conservationWoundedAnimalReward", "descriptionState",
                        "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState",
                        "descriptionZh", "healthState", "health", "iconState", "icon", "iconAssetState", "iconAsset",
                        "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "killXPRewardState", "killXPReward",
                        "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.conservationItemReferencesState,
                        row.conservationItemRewardState, row.conservationItemReward,
                        row.conservationStandingRewardState, row.conservationStandingReward,
                        row.conservationWoundedAnimalRewardState, row.conservationWoundedAnimalReward,
                        row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn,
                        row.descriptionZhState, row.descriptionZh, row.healthState, row.health, row.iconState, row.icon,
                        row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.iconUrlState,
                        row.iconUrl, row.killXPRewardState, row.killXPReward, row.nameState, row.name, row.nameEnState,
                        row.nameEn, row.nameZhState, row.nameZh));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_animal_conservation_itemreferences_items" -> {
                QOfficialAnimalConservationItemReferencesItems row =
                    new QOfficialAnimalConservationItemReferencesItems(ROW);
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
            case "wf_official_sentinels_root" -> {
                QOfficialSentinelsRoot row = new QOfficialSentinelsRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "armorState", "armor", "codexSecretState", "codexSecret",
                        "defaultUpgradesState", "defaultWeaponState", "defaultWeapon", "descriptionState",
                        "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState",
                        "descriptionZh", "exaltedState", "excludeFromCodexState", "excludeFromCodexValue",
                        "excludeFromMarketState", "excludeFromMarket", "healthState", "health", "iconState", "icon",
                        "iconAssetState", "iconAsset", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl",
                        "itemReferencesState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh",
                        "platinumCostState", "platinumCost", "powerState", "power", "productCategoryState",
                        "productCategoryValue", "recordKeyState", "recordKey", "shieldState", "shield", "staminaState",
                        "stamina", "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.armorState, row.armor,
                        row.codexSecretState, row.codexSecret, row.defaultUpgradesState, row.defaultWeaponState,
                        row.defaultWeapon, row.descriptionState, row.descriptionValue, row.descriptionEnState,
                        row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.exaltedState,
                        row.excludeFromCodexState, row.excludeFromCodexValue, row.excludeFromMarketState,
                        row.excludeFromMarket, row.healthState, row.health, row.iconState, row.icon, row.iconAssetState,
                        row.iconAsset, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl,
                        row.itemReferencesState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState,
                        row.nameZh, row.platinumCostState, row.platinumCost, row.powerState, row.power,
                        row.productCategoryState, row.productCategoryValue, row.recordKeyState, row.recordKey,
                        row.shieldState, row.shield, row.staminaState, row.stamina, row.uniqueNameState,
                        row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_sentinels_defaultupgrades_items" -> {
                QOfficialSentinelsDefaultUpgradesItems row = new QOfficialSentinelsDefaultUpgradesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "itemReferencesState", "itemTypeState", "itemType", "slotState",
                        "slot"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemReferencesState, row.itemTypeState,
                        row.itemType, row.slotState, row.slot));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_sentinels_defaultupgrades_items_itemrefe_139910031e" -> {
                QOfficialSentinelsDefaultUpgradesItemsItemReferencesItems row =
                    new QOfficialSentinelsDefaultUpgradesItemsItemReferencesItems(ROW);
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
            case "wf_official_sentinels_exalted_items" -> {
                QOfficialSentinelsExaltedItems row = new QOfficialSentinelsExaltedItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_sentinels_itemreferences_items" -> {
                QOfficialSentinelsItemReferencesItems row = new QOfficialSentinelsItemReferencesItems(ROW);
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
            case "wf_official_gear_root" -> {
                QOfficialGearRoot row = new QOfficialGearRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "codexSecretState", "codexSecret", "creditsCostState",
                        "creditsCost", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn",
                        "descriptionZhState", "descriptionZh", "excludeFromMarketState", "excludeFromMarket",
                        "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl",
                        "itemReferencesState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh",
                        "parentNameState", "parentNameValue", "platinumCostState", "platinumCost",
                        "purchaseQuantityState", "purchaseQuantity", "recordKeyState", "recordKey", "uniqueNameState",
                        "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.codexSecretState, row.codexSecret,
                        row.creditsCostState, row.creditsCost, row.descriptionState, row.descriptionValue,
                        row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh,
                        row.excludeFromMarketState, row.excludeFromMarket, row.iconState, row.icon, row.iconStatusState,
                        row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.nameState, row.name,
                        row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.parentNameState,
                        row.parentNameValue, row.platinumCostState, row.platinumCost, row.purchaseQuantityState,
                        row.purchaseQuantity, row.recordKeyState, row.recordKey, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
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
            case "wf_official_animal_conservation_itemreferences_items" -> {
                QOfficialAnimalConservationItemReferencesItems row =
                    new QOfficialAnimalConservationItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_sentinels_defaultupgrades_items" -> {
                QOfficialSentinelsDefaultUpgradesItems row = new QOfficialSentinelsDefaultUpgradesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_sentinels_defaultupgrades_items_itemrefe_139910031e" -> {
                QOfficialSentinelsDefaultUpgradesItemsItemReferencesItems row =
                    new QOfficialSentinelsDefaultUpgradesItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_sentinels_exalted_items" -> {
                QOfficialSentinelsExaltedItems row = new QOfficialSentinelsExaltedItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_sentinels_itemreferences_items" -> {
                QOfficialSentinelsItemReferencesItems row = new QOfficialSentinelsItemReferencesItems(ROW);
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
            case "wf_official_animal_root" -> {
                QOfficialAnimalRoot row = new QOfficialAnimalRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_animal_conservation_itemreferences_items" -> {
                QOfficialAnimalConservationItemReferencesItems row =
                    new QOfficialAnimalConservationItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_sentinels_root" -> {
                QOfficialSentinelsRoot row = new QOfficialSentinelsRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_sentinels_defaultupgrades_items" -> {
                QOfficialSentinelsDefaultUpgradesItems row = new QOfficialSentinelsDefaultUpgradesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_sentinels_defaultupgrades_items_itemrefe_139910031e" -> {
                QOfficialSentinelsDefaultUpgradesItemsItemReferencesItems row =
                    new QOfficialSentinelsDefaultUpgradesItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_sentinels_exalted_items" -> {
                QOfficialSentinelsExaltedItems row = new QOfficialSentinelsExaltedItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_sentinels_itemreferences_items" -> {
                QOfficialSentinelsItemReferencesItems row = new QOfficialSentinelsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_gear_root" -> {
                QOfficialGearRoot row = new QOfficialGearRoot(ROW);
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
