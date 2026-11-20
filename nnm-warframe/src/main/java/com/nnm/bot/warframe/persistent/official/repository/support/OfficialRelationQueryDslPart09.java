package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialInternalDefinitionRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialInternalDefinitionItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialMiscRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialMiscDissectionPartsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialMiscDissectionPartsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialMiscHelminthSnackItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialMiscItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialResourcesRoot;

/** 官方关系表 QueryDSL 标量投影分片 9；由模型生成器维护。 */
final class OfficialRelationQueryDslPart09 {
    private OfficialRelationQueryDslPart09() {}

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
            case "wf_official_internal_definition_root" -> {
                QOfficialInternalDefinitionRoot row = new QOfficialInternalDefinitionRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "itemReferencesState", "nameState", "name", "nameEnState",
                        "nameEn", "nameZhState", "nameZh", "parentNameState", "parentNameValue"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemReferencesState, row.nameState,
                        row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.parentNameState,
                        row.parentNameValue));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_internal_definition_itemreferences_items" -> {
                QOfficialInternalDefinitionItemReferencesItems row =
                    new QOfficialInternalDefinitionItemReferencesItems(ROW);
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
            case "wf_official_misc_root" -> {
                QOfficialMiscRoot row = new QOfficialMiscRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "codexSecretState", "codexSecret", "descriptionState",
                        "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState",
                        "descriptionZh", "dissectionPartsState", "excludeFromCodexState", "excludeFromCodexValue",
                        "excludeFromMarketState", "excludeFromMarket", "helminthSnackCountState", "helminthSnackCount",
                        "helminthSnackGainState", "helminthSnackGain", "helminthSnackItemReferencesState",
                        "helminthSnackTypeState", "helminthSnackType", "iconState", "icon", "iconStatusState",
                        "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "longDescriptionState",
                        "longDescriptionValue", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh",
                        "parentNameState", "parentNameValue", "pickupQuantityMaxValueState", "pickupQuantityMaxValue",
                        "pickupQuantityMinValueState", "pickupQuantityMinValue", "platinumCostState", "platinumCost",
                        "primeSellingPriceState", "primeSellingPrice", "productCategoryState", "productCategoryValue",
                        "purchaseQuantityState", "purchaseQuantity", "rarityState", "rarity", "recordKeyState",
                        "recordKey", "showInInventoryState", "showInInventory", "standingBonusState", "standingBonus",
                        "syndicateTagState", "syndicateTag", "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.codexSecretState, row.codexSecret,
                        row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn,
                        row.descriptionZhState, row.descriptionZh, row.dissectionPartsState, row.excludeFromCodexState,
                        row.excludeFromCodexValue, row.excludeFromMarketState, row.excludeFromMarket,
                        row.helminthSnackCountState, row.helminthSnackCount, row.helminthSnackGainState,
                        row.helminthSnackGain, row.helminthSnackItemReferencesState, row.helminthSnackTypeState,
                        row.helminthSnackType, row.iconState, row.icon, row.iconStatusState, row.iconStatus,
                        row.iconUrlState, row.iconUrl, row.itemReferencesState, row.longDescriptionState,
                        row.longDescriptionValue, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState,
                        row.nameZh, row.parentNameState, row.parentNameValue, row.pickupQuantityMaxValueState,
                        row.pickupQuantityMaxValue, row.pickupQuantityMinValueState, row.pickupQuantityMinValue,
                        row.platinumCostState, row.platinumCost, row.primeSellingPriceState, row.primeSellingPrice,
                        row.productCategoryState, row.productCategoryValue, row.purchaseQuantityState,
                        row.purchaseQuantity, row.rarityState, row.rarity, row.recordKeyState, row.recordKey,
                        row.showInInventoryState, row.showInInventory, row.standingBonusState, row.standingBonus,
                        row.syndicateTagState, row.syndicateTag, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_misc_dissectionparts_items" -> {
                QOfficialMiscDissectionPartsItems row = new QOfficialMiscDissectionPartsItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "itemCountState", "itemCount", "itemReferencesState",
                        "itemTypeState", "itemType"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemCountState, row.itemCount,
                        row.itemReferencesState, row.itemTypeState, row.itemType));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_misc_dissectionparts_items_itemreferences_items" -> {
                QOfficialMiscDissectionPartsItemsItemReferencesItems row =
                    new QOfficialMiscDissectionPartsItemsItemReferencesItems(ROW);
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
            case "wf_official_misc_helminthsnack_itemreferences_items" -> {
                QOfficialMiscHelminthSnackItemReferencesItems row =
                    new QOfficialMiscHelminthSnackItemReferencesItems(ROW);
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
            case "wf_official_misc_itemreferences_items" -> {
                QOfficialMiscItemReferencesItems row = new QOfficialMiscItemReferencesItems(ROW);
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
            case "wf_official_resources_root" -> {
                QOfficialResourcesRoot row = new QOfficialResourcesRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "codexSecretState", "codexSecret", "descriptionState",
                        "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState",
                        "descriptionZh", "excludeFromCodexState", "excludeFromCodexValue", "excludeFromMarketState",
                        "excludeFromMarket", "helminthSnackCountState", "helminthSnackCount", "helminthSnackGainState",
                        "helminthSnackGain", "helminthSnackItemReferencesState", "helminthSnackTypeState",
                        "helminthSnackType", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState",
                        "iconUrl", "itemReferencesState", "longDescriptionState", "longDescriptionValue", "nameState",
                        "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "parentNameState", "parentNameValue",
                        "pickupQuantityMaxValueState", "pickupQuantityMaxValue", "pickupQuantityMinValueState",
                        "pickupQuantityMinValue", "platinumCostState", "platinumCost", "productCategoryState",
                        "productCategoryValue", "purchaseQuantityState", "purchaseQuantity", "rarityState", "rarity",
                        "recordKeyState", "recordKey", "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.codexSecretState, row.codexSecret,
                        row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn,
                        row.descriptionZhState, row.descriptionZh, row.excludeFromCodexState, row.excludeFromCodexValue,
                        row.excludeFromMarketState, row.excludeFromMarket, row.helminthSnackCountState,
                        row.helminthSnackCount, row.helminthSnackGainState, row.helminthSnackGain,
                        row.helminthSnackItemReferencesState, row.helminthSnackTypeState, row.helminthSnackType,
                        row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl,
                        row.itemReferencesState, row.longDescriptionState, row.longDescriptionValue, row.nameState,
                        row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.parentNameState,
                        row.parentNameValue, row.pickupQuantityMaxValueState, row.pickupQuantityMaxValue,
                        row.pickupQuantityMinValueState, row.pickupQuantityMinValue, row.platinumCostState,
                        row.platinumCost, row.productCategoryState, row.productCategoryValue, row.purchaseQuantityState,
                        row.purchaseQuantity, row.rarityState, row.rarity, row.recordKeyState, row.recordKey,
                        row.uniqueNameState, row.uniqueName));
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
            case "wf_official_internal_definition_itemreferences_items" -> {
                QOfficialInternalDefinitionItemReferencesItems row =
                    new QOfficialInternalDefinitionItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_misc_dissectionparts_items" -> {
                QOfficialMiscDissectionPartsItems row = new QOfficialMiscDissectionPartsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_misc_dissectionparts_items_itemreferences_items" -> {
                QOfficialMiscDissectionPartsItemsItemReferencesItems row =
                    new QOfficialMiscDissectionPartsItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_misc_helminthsnack_itemreferences_items" -> {
                QOfficialMiscHelminthSnackItemReferencesItems row =
                    new QOfficialMiscHelminthSnackItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_misc_itemreferences_items" -> {
                QOfficialMiscItemReferencesItems row = new QOfficialMiscItemReferencesItems(ROW);
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
            case "wf_official_internal_definition_root" -> {
                QOfficialInternalDefinitionRoot row = new QOfficialInternalDefinitionRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_internal_definition_itemreferences_items" -> {
                QOfficialInternalDefinitionItemReferencesItems row =
                    new QOfficialInternalDefinitionItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_misc_root" -> {
                QOfficialMiscRoot row = new QOfficialMiscRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_misc_dissectionparts_items" -> {
                QOfficialMiscDissectionPartsItems row = new QOfficialMiscDissectionPartsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_misc_dissectionparts_items_itemreferences_items" -> {
                QOfficialMiscDissectionPartsItemsItemReferencesItems row =
                    new QOfficialMiscDissectionPartsItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_misc_helminthsnack_itemreferences_items" -> {
                QOfficialMiscHelminthSnackItemReferencesItems row =
                    new QOfficialMiscHelminthSnackItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_misc_itemreferences_items" -> {
                QOfficialMiscItemReferencesItems row = new QOfficialMiscItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_resources_root" -> {
                QOfficialResourcesRoot row = new QOfficialResourcesRoot(ROW);
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
