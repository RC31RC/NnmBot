package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialCreditBundleRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialCustomsRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialCustomsHexColoursItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialCustomsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialCustomsLegacyColoursItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialDecorationRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialDecorationItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialFusionBundlesRoot;

/** 官方关系表 QueryDSL 标量投影分片 8；由模型生成器维护。 */
final class OfficialRelationQueryDslPart08 {
    private OfficialRelationQueryDslPart08() {}

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
            case "wf_official_credit_bundle_root" -> {
                QOfficialCreditBundleRoot row = new QOfficialCreditBundleRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "creditsState", "credits", "definitionStatusState",
                        "definitionStatus", "descriptionState", "descriptionValue", "descriptionEnState",
                        "descriptionEn", "descriptionZhState", "descriptionZh", "iconState", "icon", "iconAssetState",
                        "iconAsset", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn",
                        "nameKeyState", "nameKey", "nameZhState", "nameZh", "platinumCostState", "platinumCost",
                        "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.creditsState, row.credits,
                        row.definitionStatusState, row.definitionStatus, row.descriptionState, row.descriptionValue,
                        row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh,
                        row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus,
                        row.nameState, row.name, row.nameEnState, row.nameEn, row.nameKeyState, row.nameKey,
                        row.nameZhState, row.nameZh, row.platinumCostState, row.platinumCost, row.uniqueNameState,
                        row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_customs_root" -> {
                QOfficialCustomsRoot row = new QOfficialCustomsRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "alwaysAvailableState", "alwaysAvailable", "baseState", "base",
                        "codexSecretState", "codexSecret", "descriptionState", "descriptionValue", "descriptionEnState",
                        "descriptionEn", "descriptionZhState", "descriptionZh", "excludeFromCodexState",
                        "excludeFromCodexValue", "excludeFromMarketState", "excludeFromMarket", "hexColoursState",
                        "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus",
                        "iconUrlState", "iconUrl", "itemReferencesState", "legacyColoursState", "nameState", "name",
                        "nameEnState", "nameEn", "nameZhState", "nameZh", "platinumCostState", "platinumCost",
                        "recordKeyState", "recordKey", "titleTagState", "titleTag", "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.alwaysAvailableState,
                        row.alwaysAvailable, row.baseState, row.base, row.codexSecretState, row.codexSecret,
                        row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn,
                        row.descriptionZhState, row.descriptionZh, row.excludeFromCodexState, row.excludeFromCodexValue,
                        row.excludeFromMarketState, row.excludeFromMarket, row.hexColoursState, row.iconState, row.icon,
                        row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.iconUrlState,
                        row.iconUrl, row.itemReferencesState, row.legacyColoursState, row.nameState, row.name,
                        row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.platinumCostState,
                        row.platinumCost, row.recordKeyState, row.recordKey, row.titleTagState, row.titleTag,
                        row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_customs_hexcolours_items" -> {
                QOfficialCustomsHexColoursItems row = new QOfficialCustomsHexColoursItems(ROW);
                var projection =
                    new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "valueState", "value"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.valueState, row.value));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_customs_itemreferences_items" -> {
                QOfficialCustomsItemReferencesItems row = new QOfficialCustomsItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field",
                        "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState",
                        "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution",
                        "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState,
                        row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState,
                        row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind,
                        row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_customs_legacycolours_items" -> {
                QOfficialCustomsLegacyColoursItems row = new QOfficialCustomsLegacyColoursItems(ROW);
                var projection =
                    new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "valueState", "value"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.valueState, row.value));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_decoration_root" -> {
                QOfficialDecorationRoot row = new QOfficialDecorationRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "capacityCostState", "capacityCost", "codexSecretState",
                        "codexSecret", "decoState", "deco", "descriptionState", "descriptionValue",
                        "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh",
                        "dojoCapacityCostState", "dojoCapacityCost", "excludeFromCodexState", "excludeFromCodexValue",
                        "excludeFromMarketState", "excludeFromMarket", "iconState", "icon", "iconStatusState",
                        "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "nameState", "name",
                        "nameEnState", "nameEn", "nameZhState", "nameZh", "oneTimePurchasableState",
                        "oneTimePurchasable", "parentNameState", "parentNameValue", "platinumCostState", "platinumCost",
                        "productCategoryState", "productCategoryValue", "recordKeyState", "recordKey",
                        "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.capacityCostState, row.capacityCost,
                        row.codexSecretState, row.codexSecret, row.decoState, row.deco, row.descriptionState,
                        row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState,
                        row.descriptionZh, row.dojoCapacityCostState, row.dojoCapacityCost, row.excludeFromCodexState,
                        row.excludeFromCodexValue, row.excludeFromMarketState, row.excludeFromMarket, row.iconState,
                        row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl,
                        row.itemReferencesState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState,
                        row.nameZh, row.oneTimePurchasableState, row.oneTimePurchasable, row.parentNameState,
                        row.parentNameValue, row.platinumCostState, row.platinumCost, row.productCategoryState,
                        row.productCategoryValue, row.recordKeyState, row.recordKey, row.uniqueNameState,
                        row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_decoration_itemreferences_items" -> {
                QOfficialDecorationItemReferencesItems row = new QOfficialDecorationItemReferencesItems(ROW);
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
            case "wf_official_fusion_bundles_root" -> {
                QOfficialFusionBundlesRoot row = new QOfficialFusionBundlesRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "codexSecretState", "codexSecret", "currencyState", "currency",
                        "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn",
                        "descriptionZhState", "descriptionZh", "displayNameEnState", "displayNameEn",
                        "displayNameZhState", "displayNameZh", "fusionPointsState", "fusionPoints", "iconState", "icon",
                        "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameZhState",
                        "nameZh", "quantityState", "quantity", "recordKeyState", "recordKey", "uniqueNameState",
                        "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.codexSecretState, row.codexSecret,
                        row.currencyState, row.currency, row.descriptionState, row.descriptionValue,
                        row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh,
                        row.displayNameEnState, row.displayNameEn, row.displayNameZhState, row.displayNameZh,
                        row.fusionPointsState, row.fusionPoints, row.iconState, row.icon, row.iconStatusState,
                        row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameZhState,
                        row.nameZh, row.quantityState, row.quantity, row.recordKeyState, row.recordKey,
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
            case "wf_official_customs_hexcolours_items" -> {
                QOfficialCustomsHexColoursItems row = new QOfficialCustomsHexColoursItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_customs_itemreferences_items" -> {
                QOfficialCustomsItemReferencesItems row = new QOfficialCustomsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_customs_legacycolours_items" -> {
                QOfficialCustomsLegacyColoursItems row = new QOfficialCustomsLegacyColoursItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_decoration_itemreferences_items" -> {
                QOfficialDecorationItemReferencesItems row = new QOfficialDecorationItemReferencesItems(ROW);
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
            case "wf_official_credit_bundle_root" -> {
                QOfficialCreditBundleRoot row = new QOfficialCreditBundleRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_customs_root" -> {
                QOfficialCustomsRoot row = new QOfficialCustomsRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_customs_hexcolours_items" -> {
                QOfficialCustomsHexColoursItems row = new QOfficialCustomsHexColoursItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_customs_itemreferences_items" -> {
                QOfficialCustomsItemReferencesItems row = new QOfficialCustomsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_customs_legacycolours_items" -> {
                QOfficialCustomsLegacyColoursItems row = new QOfficialCustomsLegacyColoursItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_decoration_root" -> {
                QOfficialDecorationRoot row = new QOfficialDecorationRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_decoration_itemreferences_items" -> {
                QOfficialDecorationItemReferencesItems row = new QOfficialDecorationItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_fusion_bundles_root" -> {
                QOfficialFusionBundlesRoot row = new QOfficialFusionBundlesRoot(ROW);
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
