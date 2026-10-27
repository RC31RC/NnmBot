package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.crafting.QOfficialDojoRecipeIngredientsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.crafting.QOfficialDojoRecipeIngredientsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.crafting.QOfficialDojoRecipeItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.crafting.QOfficialDojoRecipeRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.crafting.QOfficialRecipeIdentityAliasesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.crafting.QOfficialRecipeIngredientsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.crafting.QOfficialRecipeIngredientsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.crafting.QOfficialRecipeRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 0；由模型生成器维护。 */
final class OfficialRelationQueryDslPart00 {
    private OfficialRelationQueryDslPart00() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_dojo_recipe_root" -> {
                QOfficialDojoRecipeRoot row = new QOfficialDojoRecipeRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "capacityState", "capacity", "capacityCostState", "capacityCost", "colourValueState", "colourValue", "decoCapacityState", "decoCapacity", "descriptionState", "descriptionValue", "destructionTimeState", "destructionTime", "energyState", "energy", "guildXpValueState", "guildXpValue", "hiddenState", "hidden", "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "ingredientsState", "itemReferencesState", "nameState", "name", "priceState", "price", "replicatePriceState", "replicatePrice", "requiredInVaultState", "requiredInVault", "resultTypeState", "resultType", "skipTimePriceState", "skipTimePrice", "techPrereqState", "techPrereq", "timeState", "time"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.capacityState, row.capacity, row.capacityCostState, row.capacityCost, row.colourValueState, row.colourValue, row.decoCapacityState, row.decoCapacity, row.descriptionState, row.descriptionValue, row.destructionTimeState, row.destructionTime, row.energyState, row.energy, row.guildXpValueState, row.guildXpValue, row.hiddenState, row.hidden, row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.ingredientsState, row.itemReferencesState, row.nameState, row.name, row.priceState, row.price, row.replicatePriceState, row.replicatePrice, row.requiredInVaultState, row.requiredInVault, row.resultTypeState, row.resultType, row.skipTimePriceState, row.skipTimePrice, row.techPrereqState, row.techPrereq, row.timeState, row.time));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_dojo_recipe_ingredients_items" -> {
                QOfficialDojoRecipeIngredientsItems row = new QOfficialDojoRecipeIngredientsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemCountState", "itemCount", "itemReferencesState", "itemTypeState", "itemType"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemCountState, row.itemCount, row.itemReferencesState, row.itemTypeState, row.itemType));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_dojo_recipe_ingredients_items_itemreferences_items" -> {
                QOfficialDojoRecipeIngredientsItemsItemReferencesItems row = new QOfficialDojoRecipeIngredientsItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_dojo_recipe_itemreferences_items" -> {
                QOfficialDojoRecipeItemReferencesItems row = new QOfficialDojoRecipeItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_recipe_root" -> {
                QOfficialRecipeRoot row = new QOfficialRecipeRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "actionKeyState", "actionKey", "alwaysAvailableState", "alwaysAvailable", "buildPriceState", "buildPrice", "buildTimeState", "buildTime", "codexSecretState", "codexSecret", "consumeOnUseState", "consumeOnUse", "creditsCostState", "creditsCost", "excludeFromCodexState", "excludeFromCodexValue", "excludeFromMarketState", "excludeFromMarket", "hiddenState", "hidden", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "identityAliasesState", "ingredientsState", "itemReferencesState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "numState", "num", "oneTimePurchasableState", "oneTimePurchasable", "platinumCostState", "platinumCost", "primeSellingPriceState", "primeSellingPrice", "quantityBasisState", "quantityBasis", "recipeKindState", "recipeKind", "recordKeyState", "recordKey", "resultItemIconStatusState", "resultItemIconStatus", "resultItemIconUrlState", "resultItemIconUrl", "resultItemNameState", "resultItemName", "resultItemNameEnState", "resultItemNameEn", "resultItemNameZhState", "resultItemNameZh", "resultItemUniqueNameState", "resultItemUniqueName", "resultTypeState", "resultType", "resultTypeNameZhState", "resultTypeNameZh", "secretIngredientActionState", "secretIngredientAction", "secretIngredientsState", "skipBuildTimePriceState", "skipBuildTimePrice", "syndicateStandingChangeTagState", "syndicateStandingChangeTag", "syndicateStandingChangeValueState", "syndicateStandingChangeValue", "tradableState", "tradable", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.actionKeyState, row.actionKey, row.alwaysAvailableState, row.alwaysAvailable, row.buildPriceState, row.buildPrice, row.buildTimeState, row.buildTime, row.codexSecretState, row.codexSecret, row.consumeOnUseState, row.consumeOnUse, row.creditsCostState, row.creditsCost, row.excludeFromCodexState, row.excludeFromCodexValue, row.excludeFromMarketState, row.excludeFromMarket, row.hiddenState, row.hidden, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.identityAliasesState, row.ingredientsState, row.itemReferencesState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.numState, row.num, row.oneTimePurchasableState, row.oneTimePurchasable, row.platinumCostState, row.platinumCost, row.primeSellingPriceState, row.primeSellingPrice, row.quantityBasisState, row.quantityBasis, row.recipeKindState, row.recipeKind, row.recordKeyState, row.recordKey, row.resultItemIconStatusState, row.resultItemIconStatus, row.resultItemIconUrlState, row.resultItemIconUrl, row.resultItemNameState, row.resultItemName, row.resultItemNameEnState, row.resultItemNameEn, row.resultItemNameZhState, row.resultItemNameZh, row.resultItemUniqueNameState, row.resultItemUniqueName, row.resultTypeState, row.resultType, row.resultTypeNameZhState, row.resultTypeNameZh, row.secretIngredientActionState, row.secretIngredientAction, row.secretIngredientsState, row.skipBuildTimePriceState, row.skipBuildTimePrice, row.syndicateStandingChangeTagState, row.syndicateStandingChangeTag, row.syndicateStandingChangeValueState, row.syndicateStandingChangeValue, row.tradableState, row.tradable, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_recipe_identityaliases_items" -> {
                QOfficialRecipeIdentityAliasesItems row = new QOfficialRecipeIdentityAliasesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_recipe_ingredients_items" -> {
                QOfficialRecipeIngredientsItems row = new QOfficialRecipeIngredientsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "ingredientOrderState", "ingredientOrder", "itemIconStatusState", "itemIconStatus", "itemIconUrlState", "itemIconUrl", "itemNameState", "itemName", "itemNameEnState", "itemNameEn", "itemNameZhState", "itemNameZh", "itemUniqueNameState", "itemUniqueName", "itemCountState", "itemCount", "itemReferencesState", "itemTypeState", "itemType", "itemTypeNameZhState", "itemTypeNameZh", "productCategoryState", "productCategory"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.ingredientOrderState, row.ingredientOrder, row.itemIconStatusState, row.itemIconStatus, row.itemIconUrlState, row.itemIconUrl, row.itemNameState, row.itemName, row.itemNameEnState, row.itemNameEn, row.itemNameZhState, row.itemNameZh, row.itemUniqueNameState, row.itemUniqueName, row.itemCountState, row.itemCount, row.itemReferencesState, row.itemTypeState, row.itemType, row.itemTypeNameZhState, row.itemTypeNameZh, row.productCategoryState, row.productCategory));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_recipe_ingredients_items_itemreferences_items" -> {
                QOfficialRecipeIngredientsItemsItemReferencesItems row = new QOfficialRecipeIngredientsItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_dojo_recipe_ingredients_items" -> {
                QOfficialDojoRecipeIngredientsItems row = new QOfficialDojoRecipeIngredientsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_dojo_recipe_ingredients_items_itemreferences_items" -> {
                QOfficialDojoRecipeIngredientsItemsItemReferencesItems row = new QOfficialDojoRecipeIngredientsItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_dojo_recipe_itemreferences_items" -> {
                QOfficialDojoRecipeItemReferencesItems row = new QOfficialDojoRecipeItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_recipe_identityaliases_items" -> {
                QOfficialRecipeIdentityAliasesItems row = new QOfficialRecipeIdentityAliasesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_recipe_ingredients_items" -> {
                QOfficialRecipeIngredientsItems row = new QOfficialRecipeIngredientsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_recipe_ingredients_items_itemreferences_items" -> {
                QOfficialRecipeIngredientsItemsItemReferencesItems row = new QOfficialRecipeIngredientsItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_dojo_recipe_root" -> {
                QOfficialDojoRecipeRoot row = new QOfficialDojoRecipeRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_dojo_recipe_ingredients_items" -> {
                QOfficialDojoRecipeIngredientsItems row = new QOfficialDojoRecipeIngredientsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_dojo_recipe_ingredients_items_itemreferences_items" -> {
                QOfficialDojoRecipeIngredientsItemsItemReferencesItems row = new QOfficialDojoRecipeIngredientsItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_dojo_recipe_itemreferences_items" -> {
                QOfficialDojoRecipeItemReferencesItems row = new QOfficialDojoRecipeItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_recipe_root" -> {
                QOfficialRecipeRoot row = new QOfficialRecipeRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_recipe_identityaliases_items" -> {
                QOfficialRecipeIdentityAliasesItems row = new QOfficialRecipeIdentityAliasesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_recipe_ingredients_items" -> {
                QOfficialRecipeIngredientsItems row = new QOfficialRecipeIngredientsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_recipe_ingredients_items_itemreferences_items" -> {
                QOfficialRecipeIngredientsItemsItemReferencesItems row = new QOfficialRecipeIngredientsItemsItemReferencesItems("row");
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
