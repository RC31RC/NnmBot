package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialResourcesHelminthSnackItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialResourcesItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialRewardPackRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialRewardPackComponentsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialRewardPackComponentsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialRewardPackRarityWeightsPerRollItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialSkinRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialSkinAdditionalItemsItems;

/** 官方关系表 QueryDSL 标量投影分片 10；由模型生成器维护。 */
final class OfficialRelationQueryDslPart10 {
    private OfficialRelationQueryDslPart10() {}

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
            case "wf_official_resources_helminthsnack_itemreferences_items" -> {
                QOfficialResourcesHelminthSnackItemReferencesItems row =
                    new QOfficialResourcesHelminthSnackItemReferencesItems(ROW);
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
            case "wf_official_resources_itemreferences_items" -> {
                QOfficialResourcesItemReferencesItems row = new QOfficialResourcesItemReferencesItems(ROW);
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
            case "wf_official_reward_pack_root" -> {
                QOfficialRewardPackRoot row = new QOfficialRewardPackRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "canGiveDuplicatesState", "canGiveDuplicates", "componentsState",
                        "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn",
                        "descriptionZhState", "descriptionZh", "excludeFromMarketState", "excludeFromMarket",
                        "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus",
                        "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState",
                        "nameZh", "platinumCostState", "platinumCost", "rarityWeightsPerRollState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.canGiveDuplicatesState,
                        row.canGiveDuplicates, row.componentsState, row.descriptionState, row.descriptionValue,
                        row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh,
                        row.excludeFromMarketState, row.excludeFromMarket, row.iconState, row.icon, row.iconAssetState,
                        row.iconAsset, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl,
                        row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh,
                        row.platinumCostState, row.platinumCost, row.rarityWeightsPerRollState));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_reward_pack_components_items" -> {
                QOfficialRewardPackComponentsItems row = new QOfficialRewardPackComponentsItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "amountState", "amount", "itemState", "item",
                        "itemReferencesState", "pityIncreaseRateState", "pityIncreaseRate", "probabilityState",
                        "probability", "rarityState", "rarity"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.amountState, row.amount, row.itemState,
                        row.item, row.itemReferencesState, row.pityIncreaseRateState, row.pityIncreaseRate,
                        row.probabilityState, row.probability, row.rarityState, row.rarity));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_reward_pack_components_items_itemreferences_items" -> {
                QOfficialRewardPackComponentsItemsItemReferencesItems row =
                    new QOfficialRewardPackComponentsItemsItemReferencesItems(ROW);
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
            case "wf_official_reward_pack_rarityweightsperroll_items" -> {
                QOfficialRewardPackRarityWeightsPerRollItems row =
                    new QOfficialRewardPackRarityWeightsPerRollItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "commonState", "common", "legendaryState", "legendary",
                        "rareState", "rare", "uncommonState", "uncommon"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.commonState, row.common,
                        row.legendaryState, row.legendary, row.rareState, row.rare, row.uncommonState, row.uncommon));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_skin_root" -> {
                QOfficialSkinRoot row = new QOfficialSkinRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "additionalItemsState", "alwaysAvailableState",
                        "alwaysAvailable", "baseState", "base", "codexSecretState", "codexSecret", "descriptionState",
                        "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState",
                        "descriptionZh", "excludedPlatformsState", "excludeFromCodexState", "excludeFromCodexValue",
                        "excludeFromMarketState", "excludeFromMarket", "iconState", "icon", "iconAssetState",
                        "iconAsset", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState",
                        "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "platinumCostState",
                        "platinumCost", "productCategoryState", "productCategoryValue", "randomisedUpgradesState",
                        "recordKeyState", "recordKey", "requirementState", "requirement", "subroutinesState",
                        "tradableState", "tradable", "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.additionalItemsState,
                        row.alwaysAvailableState, row.alwaysAvailable, row.baseState, row.base, row.codexSecretState,
                        row.codexSecret, row.descriptionState, row.descriptionValue, row.descriptionEnState,
                        row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.excludedPlatformsState,
                        row.excludeFromCodexState, row.excludeFromCodexValue, row.excludeFromMarketState,
                        row.excludeFromMarket, row.iconState, row.icon, row.iconAssetState, row.iconAsset,
                        row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState,
                        row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh,
                        row.platinumCostState, row.platinumCost, row.productCategoryState, row.productCategoryValue,
                        row.randomisedUpgradesState, row.recordKeyState, row.recordKey, row.requirementState,
                        row.requirement, row.subroutinesState, row.tradableState, row.tradable, row.uniqueNameState,
                        row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_skin_additionalitems_items" -> {
                QOfficialSkinAdditionalItemsItems row = new QOfficialSkinAdditionalItemsItems(ROW);
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
            case "wf_official_resources_helminthsnack_itemreferences_items" -> {
                QOfficialResourcesHelminthSnackItemReferencesItems row =
                    new QOfficialResourcesHelminthSnackItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_resources_itemreferences_items" -> {
                QOfficialResourcesItemReferencesItems row = new QOfficialResourcesItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_reward_pack_components_items" -> {
                QOfficialRewardPackComponentsItems row = new QOfficialRewardPackComponentsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_reward_pack_components_items_itemreferences_items" -> {
                QOfficialRewardPackComponentsItemsItemReferencesItems row =
                    new QOfficialRewardPackComponentsItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_reward_pack_rarityweightsperroll_items" -> {
                QOfficialRewardPackRarityWeightsPerRollItems row =
                    new QOfficialRewardPackRarityWeightsPerRollItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_skin_additionalitems_items" -> {
                QOfficialSkinAdditionalItemsItems row = new QOfficialSkinAdditionalItemsItems(ROW);
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
            case "wf_official_resources_helminthsnack_itemreferences_items" -> {
                QOfficialResourcesHelminthSnackItemReferencesItems row =
                    new QOfficialResourcesHelminthSnackItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_resources_itemreferences_items" -> {
                QOfficialResourcesItemReferencesItems row = new QOfficialResourcesItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_reward_pack_root" -> {
                QOfficialRewardPackRoot row = new QOfficialRewardPackRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_reward_pack_components_items" -> {
                QOfficialRewardPackComponentsItems row = new QOfficialRewardPackComponentsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_reward_pack_components_items_itemreferences_items" -> {
                QOfficialRewardPackComponentsItemsItemReferencesItems row =
                    new QOfficialRewardPackComponentsItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_reward_pack_rarityweightsperroll_items" -> {
                QOfficialRewardPackRarityWeightsPerRollItems row =
                    new QOfficialRewardPackRarityWeightsPerRollItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_skin_root" -> {
                QOfficialSkinRoot row = new QOfficialSkinRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_skin_additionalitems_items" -> {
                QOfficialSkinAdditionalItemsItems row = new QOfficialSkinAdditionalItemsItems(ROW);
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
