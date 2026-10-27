package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.crafting.QOfficialRecipeItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.crafting.QOfficialRecipeSecretIngredientsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.crafting.QOfficialRecipeSecretIngredientsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialAbilityOwnerBkeysItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialAbilityRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialArchwingAbilitiesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialArchwingItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.equipment.QOfficialArchwingRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 1；由模型生成器维护。 */
final class OfficialRelationQueryDslPart01 {
    private OfficialRelationQueryDslPart01() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_recipe_itemreferences_items" -> {
                QOfficialRecipeItemReferencesItems row = new QOfficialRecipeItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_recipe_secretingredients_items" -> {
                QOfficialRecipeSecretIngredientsItems row = new QOfficialRecipeSecretIngredientsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemCountState", "itemCount", "itemReferencesState", "itemTypeState", "itemType"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemCountState, row.itemCount, row.itemReferencesState, row.itemTypeState, row.itemType));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_recipe_secretingredients_items_itemreferences_items" -> {
                QOfficialRecipeSecretIngredientsItemsItemReferencesItems row = new QOfficialRecipeSecretIngredientsItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_ability_root" -> {
                QOfficialAbilityRoot row = new QOfficialAbilityRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "abilityIconUrlState", "abilityIconUrl", "abilityNameState", "abilityName", "abilityNameZhState", "abilityNameZh", "abilityUniqueNameState", "abilityUniqueName", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "energyConsumptionOverTimeState", "energyConsumptionOverTime", "energyRequiredToActivateState", "energyRequiredToActivate", "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameKeyState", "nameKey", "nameZhState", "nameZh", "ownerBkeysState", "recordKeyState", "recordKey", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.abilityIconUrlState, row.abilityIconUrl, row.abilityNameState, row.abilityName, row.abilityNameZhState, row.abilityNameZh, row.abilityUniqueNameState, row.abilityUniqueName, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.energyConsumptionOverTimeState, row.energyConsumptionOverTime, row.energyRequiredToActivateState, row.energyRequiredToActivate, row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameKeyState, row.nameKey, row.nameZhState, row.nameZh, row.ownerBkeysState, row.recordKeyState, row.recordKey, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_ability_ownerbkeys_items" -> {
                QOfficialAbilityOwnerBkeysItems row = new QOfficialAbilityOwnerBkeysItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_archwing_root" -> {
                QOfficialArchwingRoot row = new QOfficialArchwingRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "abilitiesState", "armorState", "armor", "codexSecretState", "codexSecret", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "excludeFromMarketState", "excludeFromMarket", "healthState", "health", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "introducedAtState", "introducedAt", "itemReferencesState", "masteryReqState", "masteryReq", "nameState", "name", "nameEnState", "nameEn", "nameKeyState", "nameKey", "nameZhState", "nameZh", "ownerTypeState", "ownerType", "parentNameState", "parentNameValue", "platinumCostState", "platinumCost", "powerState", "power", "productCategoryState", "productCategoryValue", "recordKeyState", "recordKey", "shieldState", "shield", "sprintSpeedState", "sprintSpeed", "staminaState", "stamina", "uniqueNameState", "uniqueName", "variantTypeState", "variantType"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.abilitiesState, row.armorState, row.armor, row.codexSecretState, row.codexSecret, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.excludeFromMarketState, row.excludeFromMarket, row.healthState, row.health, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.introducedAtState, row.introducedAt, row.itemReferencesState, row.masteryReqState, row.masteryReq, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameKeyState, row.nameKey, row.nameZhState, row.nameZh, row.ownerTypeState, row.ownerType, row.parentNameState, row.parentNameValue, row.platinumCostState, row.platinumCost, row.powerState, row.power, row.productCategoryState, row.productCategoryValue, row.recordKeyState, row.recordKey, row.shieldState, row.shield, row.sprintSpeedState, row.sprintSpeed, row.staminaState, row.stamina, row.uniqueNameState, row.uniqueName, row.variantTypeState, row.variantType));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_archwing_abilities_items" -> {
                QOfficialArchwingAbilitiesItems row = new QOfficialArchwingAbilitiesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "abilityIconUrlState", "abilityIconUrl", "abilityNameState", "abilityName", "abilityNameZhState", "abilityNameZh", "abilityUniqueNameState", "abilityUniqueName", "descriptionState", "description", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "energyConsumptionOverTimeState", "energyConsumptionOverTime", "energyRequiredToActivateState", "energyRequiredToActivate", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "recordKeyState", "recordKey", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.abilityIconUrlState, row.abilityIconUrl, row.abilityNameState, row.abilityName, row.abilityNameZhState, row.abilityNameZh, row.abilityUniqueNameState, row.abilityUniqueName, row.descriptionState, row.description, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.energyConsumptionOverTimeState, row.energyConsumptionOverTime, row.energyRequiredToActivateState, row.energyRequiredToActivate, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.recordKeyState, row.recordKey, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_archwing_itemreferences_items" -> {
                QOfficialArchwingItemReferencesItems row = new QOfficialArchwingItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_recipe_itemreferences_items" -> {
                QOfficialRecipeItemReferencesItems row = new QOfficialRecipeItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_recipe_secretingredients_items" -> {
                QOfficialRecipeSecretIngredientsItems row = new QOfficialRecipeSecretIngredientsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_recipe_secretingredients_items_itemreferences_items" -> {
                QOfficialRecipeSecretIngredientsItemsItemReferencesItems row = new QOfficialRecipeSecretIngredientsItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_ability_ownerbkeys_items" -> {
                QOfficialAbilityOwnerBkeysItems row = new QOfficialAbilityOwnerBkeysItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_archwing_abilities_items" -> {
                QOfficialArchwingAbilitiesItems row = new QOfficialArchwingAbilitiesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_archwing_itemreferences_items" -> {
                QOfficialArchwingItemReferencesItems row = new QOfficialArchwingItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_recipe_itemreferences_items" -> {
                QOfficialRecipeItemReferencesItems row = new QOfficialRecipeItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_recipe_secretingredients_items" -> {
                QOfficialRecipeSecretIngredientsItems row = new QOfficialRecipeSecretIngredientsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_recipe_secretingredients_items_itemreferences_items" -> {
                QOfficialRecipeSecretIngredientsItemsItemReferencesItems row = new QOfficialRecipeSecretIngredientsItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_ability_root" -> {
                QOfficialAbilityRoot row = new QOfficialAbilityRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_ability_ownerbkeys_items" -> {
                QOfficialAbilityOwnerBkeysItems row = new QOfficialAbilityOwnerBkeysItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_archwing_root" -> {
                QOfficialArchwingRoot row = new QOfficialArchwingRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_archwing_abilities_items" -> {
                QOfficialArchwingAbilitiesItems row = new QOfficialArchwingAbilitiesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_archwing_itemreferences_items" -> {
                QOfficialArchwingItemReferencesItems row = new QOfficialArchwingItemReferencesItems("row");
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
