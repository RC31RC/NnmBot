package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.crafting.QOfficialRecipeItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.crafting.QOfficialRecipeSecretIngredientsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.crafting.QOfficialRecipeSecretIngredientsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialAbilityRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialAbilityOwnerBkeysItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialArchwingRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialArchwingAbilitiesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialArchwingItemReferencesItems;

/** 官方关系表 QueryDSL 标量投影分片 1；由模型生成器维护。 */
final class OfficialRelationQueryDslPart01 {
    private OfficialRelationQueryDslPart01() {}

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
            case "wf_official_recipe_itemreferences_items" -> {
                QOfficialRecipeItemReferencesItems row = new QOfficialRecipeItemReferencesItems(ROW);
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
            case "wf_official_recipe_secretingredients_items" -> {
                QOfficialRecipeSecretIngredientsItems row = new QOfficialRecipeSecretIngredientsItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "itemCountState", "itemCount", "itemReferencesState",
                        "itemTypeState", "itemType"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemCountState, row.itemCount,
                        row.itemReferencesState, row.itemTypeState, row.itemType));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_recipe_secretingredients_items_itemreferences_items" -> {
                QOfficialRecipeSecretIngredientsItemsItemReferencesItems row =
                    new QOfficialRecipeSecretIngredientsItemsItemReferencesItems(ROW);
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
            case "wf_official_ability_root" -> {
                QOfficialAbilityRoot row = new QOfficialAbilityRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "abilityIconUrlState", "abilityIconUrl", "abilityNameState",
                        "abilityName", "abilityNameZhState", "abilityNameZh", "abilityUniqueNameState",
                        "abilityUniqueName", "descriptionState", "descriptionValue", "descriptionEnState",
                        "descriptionEn", "descriptionZhState", "descriptionZh", "energyConsumptionOverTimeState",
                        "energyConsumptionOverTime", "energyRequiredToActivateState", "energyRequiredToActivate",
                        "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus",
                        "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameKeyState",
                        "nameKey", "nameZhState", "nameZh", "ownerBkeysState", "recordKeyState", "recordKey",
                        "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.abilityIconUrlState, row.abilityIconUrl,
                        row.abilityNameState, row.abilityName, row.abilityNameZhState, row.abilityNameZh,
                        row.abilityUniqueNameState, row.abilityUniqueName, row.descriptionState, row.descriptionValue,
                        row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh,
                        row.energyConsumptionOverTimeState, row.energyConsumptionOverTime,
                        row.energyRequiredToActivateState, row.energyRequiredToActivate, row.iconState, row.icon,
                        row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.iconUrlState,
                        row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameKeyState,
                        row.nameKey, row.nameZhState, row.nameZh, row.ownerBkeysState, row.recordKeyState,
                        row.recordKey, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_ability_ownerbkeys_items" -> {
                QOfficialAbilityOwnerBkeysItems row = new QOfficialAbilityOwnerBkeysItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_archwing_root" -> {
                QOfficialArchwingRoot row = new QOfficialArchwingRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "abilitiesState", "armorState", "armor", "codexSecretState",
                        "codexSecret", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn",
                        "descriptionZhState", "descriptionZh", "excludeFromMarketState", "excludeFromMarket",
                        "healthState", "health", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState",
                        "iconUrl", "introducedAtState", "introducedAt", "itemReferencesState", "masteryReqState",
                        "masteryReq", "nameState", "name", "nameEnState", "nameEn", "nameKeyState", "nameKey",
                        "nameZhState", "nameZh", "ownerTypeState", "ownerType", "parentNameState", "parentNameValue",
                        "platinumCostState", "platinumCost", "powerState", "power", "productCategoryState",
                        "productCategoryValue", "recordKeyState", "recordKey", "shieldState", "shield",
                        "sprintSpeedState", "sprintSpeed", "staminaState", "stamina", "uniqueNameState", "uniqueName",
                        "variantTypeState", "variantType"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.abilitiesState, row.armorState,
                        row.armor, row.codexSecretState, row.codexSecret, row.descriptionState, row.descriptionValue,
                        row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh,
                        row.excludeFromMarketState, row.excludeFromMarket, row.healthState, row.health, row.iconState,
                        row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl,
                        row.introducedAtState, row.introducedAt, row.itemReferencesState, row.masteryReqState,
                        row.masteryReq, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameKeyState,
                        row.nameKey, row.nameZhState, row.nameZh, row.ownerTypeState, row.ownerType,
                        row.parentNameState, row.parentNameValue, row.platinumCostState, row.platinumCost,
                        row.powerState, row.power, row.productCategoryState, row.productCategoryValue,
                        row.recordKeyState, row.recordKey, row.shieldState, row.shield, row.sprintSpeedState,
                        row.sprintSpeed, row.staminaState, row.stamina, row.uniqueNameState, row.uniqueName,
                        row.variantTypeState, row.variantType));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_archwing_abilities_items" -> {
                QOfficialArchwingAbilitiesItems row = new QOfficialArchwingAbilitiesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "abilityIconUrlState", "abilityIconUrl", "abilityNameState",
                        "abilityName", "abilityNameZhState", "abilityNameZh", "abilityUniqueNameState",
                        "abilityUniqueName", "descriptionState", "description", "descriptionEnState", "descriptionEn",
                        "descriptionZhState", "descriptionZh", "energyConsumptionOverTimeState",
                        "energyConsumptionOverTime", "energyRequiredToActivateState", "energyRequiredToActivate",
                        "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState",
                        "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "recordKeyState", "recordKey",
                        "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.abilityIconUrlState, row.abilityIconUrl,
                        row.abilityNameState, row.abilityName, row.abilityNameZhState, row.abilityNameZh,
                        row.abilityUniqueNameState, row.abilityUniqueName, row.descriptionState, row.description,
                        row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh,
                        row.energyConsumptionOverTimeState, row.energyConsumptionOverTime,
                        row.energyRequiredToActivateState, row.energyRequiredToActivate, row.iconState, row.icon,
                        row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name,
                        row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.recordKeyState, row.recordKey,
                        row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_archwing_itemreferences_items" -> {
                QOfficialArchwingItemReferencesItems row = new QOfficialArchwingItemReferencesItems(ROW);
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
            case "wf_official_recipe_itemreferences_items" -> {
                QOfficialRecipeItemReferencesItems row = new QOfficialRecipeItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_recipe_secretingredients_items" -> {
                QOfficialRecipeSecretIngredientsItems row = new QOfficialRecipeSecretIngredientsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_recipe_secretingredients_items_itemreferences_items" -> {
                QOfficialRecipeSecretIngredientsItemsItemReferencesItems row =
                    new QOfficialRecipeSecretIngredientsItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_ability_ownerbkeys_items" -> {
                QOfficialAbilityOwnerBkeysItems row = new QOfficialAbilityOwnerBkeysItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_archwing_abilities_items" -> {
                QOfficialArchwingAbilitiesItems row = new QOfficialArchwingAbilitiesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_archwing_itemreferences_items" -> {
                QOfficialArchwingItemReferencesItems row = new QOfficialArchwingItemReferencesItems(ROW);
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
            case "wf_official_recipe_itemreferences_items" -> {
                QOfficialRecipeItemReferencesItems row = new QOfficialRecipeItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_recipe_secretingredients_items" -> {
                QOfficialRecipeSecretIngredientsItems row = new QOfficialRecipeSecretIngredientsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_recipe_secretingredients_items_itemreferences_items" -> {
                QOfficialRecipeSecretIngredientsItemsItemReferencesItems row =
                    new QOfficialRecipeSecretIngredientsItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_ability_root" -> {
                QOfficialAbilityRoot row = new QOfficialAbilityRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_ability_ownerbkeys_items" -> {
                QOfficialAbilityOwnerBkeysItems row = new QOfficialAbilityOwnerBkeysItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_archwing_root" -> {
                QOfficialArchwingRoot row = new QOfficialArchwingRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_archwing_abilities_items" -> {
                QOfficialArchwingAbilitiesItems row = new QOfficialArchwingAbilitiesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_archwing_itemreferences_items" -> {
                QOfficialArchwingItemReferencesItems row = new QOfficialArchwingItemReferencesItems(ROW);
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
