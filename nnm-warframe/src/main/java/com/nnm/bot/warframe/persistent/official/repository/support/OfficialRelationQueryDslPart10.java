package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialResourcesHelminthSnackItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialResourcesItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialRewardPackComponentsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialRewardPackComponentsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialRewardPackRarityWeightsPerRollItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialRewardPackRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialSkinAdditionalItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialSkinRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 10；由模型生成器维护。 */
final class OfficialRelationQueryDslPart10 {
    private OfficialRelationQueryDslPart10() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_resources_helminthsnack_itemreferences_items" -> {
                QOfficialResourcesHelminthSnackItemReferencesItems row = new QOfficialResourcesHelminthSnackItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_resources_itemreferences_items" -> {
                QOfficialResourcesItemReferencesItems row = new QOfficialResourcesItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_reward_pack_root" -> {
                QOfficialRewardPackRoot row = new QOfficialRewardPackRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "canGiveDuplicatesState", "canGiveDuplicates", "componentsState", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "excludeFromMarketState", "excludeFromMarket", "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "platinumCostState", "platinumCost", "rarityWeightsPerRollState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.canGiveDuplicatesState, row.canGiveDuplicates, row.componentsState, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.excludeFromMarketState, row.excludeFromMarket, row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.platinumCostState, row.platinumCost, row.rarityWeightsPerRollState));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_reward_pack_components_items" -> {
                QOfficialRewardPackComponentsItems row = new QOfficialRewardPackComponentsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "amountState", "amount", "itemState", "item", "itemReferencesState", "pityIncreaseRateState", "pityIncreaseRate", "probabilityState", "probability", "rarityState", "rarity"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.amountState, row.amount, row.itemState, row.item, row.itemReferencesState, row.pityIncreaseRateState, row.pityIncreaseRate, row.probabilityState, row.probability, row.rarityState, row.rarity));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_reward_pack_components_items_itemreferences_items" -> {
                QOfficialRewardPackComponentsItemsItemReferencesItems row = new QOfficialRewardPackComponentsItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_reward_pack_rarityweightsperroll_items" -> {
                QOfficialRewardPackRarityWeightsPerRollItems row = new QOfficialRewardPackRarityWeightsPerRollItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "commonState", "common", "legendaryState", "legendary", "rareState", "rare", "uncommonState", "uncommon"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.commonState, row.common, row.legendaryState, row.legendary, row.rareState, row.rare, row.uncommonState, row.uncommon));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_skin_root" -> {
                QOfficialSkinRoot row = new QOfficialSkinRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "additionalItemsState", "alwaysAvailableState", "alwaysAvailable", "baseState", "base", "codexSecretState", "codexSecret", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "excludedPlatformsState", "excludeFromCodexState", "excludeFromCodexValue", "excludeFromMarketState", "excludeFromMarket", "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "platinumCostState", "platinumCost", "productCategoryState", "productCategoryValue", "randomisedUpgradesState", "recordKeyState", "recordKey", "requirementState", "requirement", "subroutinesState", "tradableState", "tradable", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.additionalItemsState, row.alwaysAvailableState, row.alwaysAvailable, row.baseState, row.base, row.codexSecretState, row.codexSecret, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.excludedPlatformsState, row.excludeFromCodexState, row.excludeFromCodexValue, row.excludeFromMarketState, row.excludeFromMarket, row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.platinumCostState, row.platinumCost, row.productCategoryState, row.productCategoryValue, row.randomisedUpgradesState, row.recordKeyState, row.recordKey, row.requirementState, row.requirement, row.subroutinesState, row.tradableState, row.tradable, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_skin_additionalitems_items" -> {
                QOfficialSkinAdditionalItemsItems row = new QOfficialSkinAdditionalItemsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_resources_helminthsnack_itemreferences_items" -> {
                QOfficialResourcesHelminthSnackItemReferencesItems row = new QOfficialResourcesHelminthSnackItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_resources_itemreferences_items" -> {
                QOfficialResourcesItemReferencesItems row = new QOfficialResourcesItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_reward_pack_components_items" -> {
                QOfficialRewardPackComponentsItems row = new QOfficialRewardPackComponentsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_reward_pack_components_items_itemreferences_items" -> {
                QOfficialRewardPackComponentsItemsItemReferencesItems row = new QOfficialRewardPackComponentsItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_reward_pack_rarityweightsperroll_items" -> {
                QOfficialRewardPackRarityWeightsPerRollItems row = new QOfficialRewardPackRarityWeightsPerRollItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_skin_additionalitems_items" -> {
                QOfficialSkinAdditionalItemsItems row = new QOfficialSkinAdditionalItemsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_resources_helminthsnack_itemreferences_items" -> {
                QOfficialResourcesHelminthSnackItemReferencesItems row = new QOfficialResourcesHelminthSnackItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_resources_itemreferences_items" -> {
                QOfficialResourcesItemReferencesItems row = new QOfficialResourcesItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_reward_pack_root" -> {
                QOfficialRewardPackRoot row = new QOfficialRewardPackRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_reward_pack_components_items" -> {
                QOfficialRewardPackComponentsItems row = new QOfficialRewardPackComponentsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_reward_pack_components_items_itemreferences_items" -> {
                QOfficialRewardPackComponentsItemsItemReferencesItems row = new QOfficialRewardPackComponentsItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_reward_pack_rarityweightsperroll_items" -> {
                QOfficialRewardPackRarityWeightsPerRollItems row = new QOfficialRewardPackRarityWeightsPerRollItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_skin_root" -> {
                QOfficialSkinRoot row = new QOfficialSkinRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_skin_additionalitems_items" -> {
                QOfficialSkinAdditionalItemsItems row = new QOfficialSkinAdditionalItemsItems("row");
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
