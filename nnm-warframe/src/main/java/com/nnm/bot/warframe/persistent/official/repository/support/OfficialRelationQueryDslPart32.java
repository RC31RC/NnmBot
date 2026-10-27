package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionItemReferencesItemsRotationsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionItemReferencesItemsRotationsItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionMissionRewardCountedItemsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionFoundersItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionMiscItemFeeItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionMissionRewardCountedItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionMissionRewardItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 32；由模型生成器维护。 */
final class OfficialRelationQueryDslPart32 {
    private OfficialRelationQueryDslPart32() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_region_founders_items" -> {
                QOfficialRegionFoundersItems row = new QOfficialRegionFoundersItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_itemreferences_items" -> {
                QOfficialRegionItemReferencesItems row = new QOfficialRegionItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "probabilityUnitState", "probabilityUnit", "referenceKindState", "referenceKind", "resolutionState", "resolution", "rotationsState", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.probabilityUnitState, row.probabilityUnit, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.rotationsState, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_itemreferences_items_rotations_items" -> {
                QOfficialRegionItemReferencesItemsRotationsItems row = new QOfficialRegionItemReferencesItemsRotationsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_itemreferences_items_rotations_items_items" -> {
                QOfficialRegionItemReferencesItemsRotationsItemsItems row = new QOfficialRegionItemReferencesItemsRotationsItemsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemDomainState", "itemDomain", "itemIconStatusState", "itemIconStatus", "itemIconUrlState", "itemIconUrl", "itemNameState", "itemName", "itemNameEnState", "itemNameEn", "itemNameZhState", "itemNameZh", "itemResolutionState", "itemResolution", "itemUniqueNameState", "itemUniqueName", "itemCountState", "itemCount", "probabilityState", "probability", "typeState", "type"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemDomainState, row.itemDomain, row.itemIconStatusState, row.itemIconStatus, row.itemIconUrlState, row.itemIconUrl, row.itemNameState, row.itemName, row.itemNameEnState, row.itemNameEn, row.itemNameZhState, row.itemNameZh, row.itemResolutionState, row.itemResolution, row.itemUniqueNameState, row.itemUniqueName, row.itemCountState, row.itemCount, row.probabilityState, row.probability, row.typeState, row.type));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_miscitemfee_itemreferences_items" -> {
                QOfficialRegionMiscItemFeeItemReferencesItems row = new QOfficialRegionMiscItemFeeItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_missionreward_counteditems_items" -> {
                QOfficialRegionMissionRewardCountedItemsItems row = new QOfficialRegionMissionRewardCountedItemsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemCountState", "itemCount", "itemReferencesState", "itemTypeState", "itemType"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemCountState, row.itemCount, row.itemReferencesState, row.itemTypeState, row.itemType));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_missionreward_counteditems_items__2f2c059ad8" -> {
                QOfficialRegionMissionRewardCountedItemsItemsItemReferencesItems row = new QOfficialRegionMissionRewardCountedItemsItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_missionreward_items_items" -> {
                QOfficialRegionMissionRewardItemsItems row = new QOfficialRegionMissionRewardItemsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_region_founders_items" -> {
                QOfficialRegionFoundersItems row = new QOfficialRegionFoundersItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_itemreferences_items" -> {
                QOfficialRegionItemReferencesItems row = new QOfficialRegionItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_itemreferences_items_rotations_items" -> {
                QOfficialRegionItemReferencesItemsRotationsItems row = new QOfficialRegionItemReferencesItemsRotationsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_itemreferences_items_rotations_items_items" -> {
                QOfficialRegionItemReferencesItemsRotationsItemsItems row = new QOfficialRegionItemReferencesItemsRotationsItemsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_miscitemfee_itemreferences_items" -> {
                QOfficialRegionMiscItemFeeItemReferencesItems row = new QOfficialRegionMiscItemFeeItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_missionreward_counteditems_items" -> {
                QOfficialRegionMissionRewardCountedItemsItems row = new QOfficialRegionMissionRewardCountedItemsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_missionreward_counteditems_items__2f2c059ad8" -> {
                QOfficialRegionMissionRewardCountedItemsItemsItemReferencesItems row = new QOfficialRegionMissionRewardCountedItemsItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_missionreward_items_items" -> {
                QOfficialRegionMissionRewardItemsItems row = new QOfficialRegionMissionRewardItemsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_region_founders_items" -> {
                QOfficialRegionFoundersItems row = new QOfficialRegionFoundersItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_itemreferences_items" -> {
                QOfficialRegionItemReferencesItems row = new QOfficialRegionItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_itemreferences_items_rotations_items" -> {
                QOfficialRegionItemReferencesItemsRotationsItems row = new QOfficialRegionItemReferencesItemsRotationsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_itemreferences_items_rotations_items_items" -> {
                QOfficialRegionItemReferencesItemsRotationsItemsItems row = new QOfficialRegionItemReferencesItemsRotationsItemsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_miscitemfee_itemreferences_items" -> {
                QOfficialRegionMiscItemFeeItemReferencesItems row = new QOfficialRegionMiscItemFeeItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_missionreward_counteditems_items" -> {
                QOfficialRegionMissionRewardCountedItemsItems row = new QOfficialRegionMissionRewardCountedItemsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_missionreward_counteditems_items__2f2c059ad8" -> {
                QOfficialRegionMissionRewardCountedItemsItemsItemReferencesItems row = new QOfficialRegionMissionRewardCountedItemsItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_missionreward_items_items" -> {
                QOfficialRegionMissionRewardItemsItems row = new QOfficialRegionMissionRewardItemsItems("row");
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
