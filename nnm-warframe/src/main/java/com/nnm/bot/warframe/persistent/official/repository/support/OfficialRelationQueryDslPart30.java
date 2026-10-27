package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysItemReferencesItemsRotationsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysItemReferencesItemsRotationsItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysMissionRewardCountedItemsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysMissionRewardCountedStoreItemsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysMissionRewardCountedItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysMissionRewardCountedStoreItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysMissionRewardItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 30；由模型生成器维护。 */
final class OfficialRelationQueryDslPart30 {
    private OfficialRelationQueryDslPart30() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_keys_itemreferences_items" -> {
                QOfficialKeysItemReferencesItems row = new QOfficialKeysItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "probabilityUnitState", "probabilityUnit", "referenceKindState", "referenceKind", "resolutionState", "resolution", "rotationsState", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.probabilityUnitState, row.probabilityUnit, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.rotationsState, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_itemreferences_items_rotations_items" -> {
                QOfficialKeysItemReferencesItemsRotationsItems row = new QOfficialKeysItemReferencesItemsRotationsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_itemreferences_items_rotations_items_items" -> {
                QOfficialKeysItemReferencesItemsRotationsItemsItems row = new QOfficialKeysItemReferencesItemsRotationsItemsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemDomainState", "itemDomain", "itemIconStatusState", "itemIconStatus", "itemIconUrlState", "itemIconUrl", "itemNameState", "itemName", "itemNameEnState", "itemNameEn", "itemNameZhState", "itemNameZh", "itemResolutionState", "itemResolution", "itemUniqueNameState", "itemUniqueName", "itemCountState", "itemCount", "probabilityState", "probability", "typeState", "type"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemDomainState, row.itemDomain, row.itemIconStatusState, row.itemIconStatus, row.itemIconUrlState, row.itemIconUrl, row.itemNameState, row.itemName, row.itemNameEnState, row.itemNameEn, row.itemNameZhState, row.itemNameZh, row.itemResolutionState, row.itemResolution, row.itemUniqueNameState, row.itemUniqueName, row.itemCountState, row.itemCount, row.probabilityState, row.probability, row.typeState, row.type));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_missionreward_counteditems_items" -> {
                QOfficialKeysMissionRewardCountedItemsItems row = new QOfficialKeysMissionRewardCountedItemsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemCountState", "itemCount", "itemReferencesState", "itemTypeState", "itemType"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemCountState, row.itemCount, row.itemReferencesState, row.itemTypeState, row.itemType));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_missionreward_counteditems_items_it_61068887b8" -> {
                QOfficialKeysMissionRewardCountedItemsItemsItemReferencesItems row = new QOfficialKeysMissionRewardCountedItemsItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_missionreward_countedstoreitems_items" -> {
                QOfficialKeysMissionRewardCountedStoreItemsItems row = new QOfficialKeysMissionRewardCountedStoreItemsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemCountState", "itemCount", "itemReferencesState", "storeItemState", "storeItem"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemCountState, row.itemCount, row.itemReferencesState, row.storeItemState, row.storeItem));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_missionreward_countedstoreitems_ite_4e2205c8cc" -> {
                QOfficialKeysMissionRewardCountedStoreItemsItemsItemReferencesItems row = new QOfficialKeysMissionRewardCountedStoreItemsItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_missionreward_itemreferences_items" -> {
                QOfficialKeysMissionRewardItemReferencesItems row = new QOfficialKeysMissionRewardItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "probabilityUnitState", "probabilityUnit", "referenceKindState", "referenceKind", "resolutionState", "resolution", "rotationsState", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.probabilityUnitState, row.probabilityUnit, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.rotationsState, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_keys_itemreferences_items" -> {
                QOfficialKeysItemReferencesItems row = new QOfficialKeysItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_itemreferences_items_rotations_items" -> {
                QOfficialKeysItemReferencesItemsRotationsItems row = new QOfficialKeysItemReferencesItemsRotationsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_itemreferences_items_rotations_items_items" -> {
                QOfficialKeysItemReferencesItemsRotationsItemsItems row = new QOfficialKeysItemReferencesItemsRotationsItemsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_missionreward_counteditems_items" -> {
                QOfficialKeysMissionRewardCountedItemsItems row = new QOfficialKeysMissionRewardCountedItemsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_missionreward_counteditems_items_it_61068887b8" -> {
                QOfficialKeysMissionRewardCountedItemsItemsItemReferencesItems row = new QOfficialKeysMissionRewardCountedItemsItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_missionreward_countedstoreitems_items" -> {
                QOfficialKeysMissionRewardCountedStoreItemsItems row = new QOfficialKeysMissionRewardCountedStoreItemsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_missionreward_countedstoreitems_ite_4e2205c8cc" -> {
                QOfficialKeysMissionRewardCountedStoreItemsItemsItemReferencesItems row = new QOfficialKeysMissionRewardCountedStoreItemsItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_missionreward_itemreferences_items" -> {
                QOfficialKeysMissionRewardItemReferencesItems row = new QOfficialKeysMissionRewardItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_keys_itemreferences_items" -> {
                QOfficialKeysItemReferencesItems row = new QOfficialKeysItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_itemreferences_items_rotations_items" -> {
                QOfficialKeysItemReferencesItemsRotationsItems row = new QOfficialKeysItemReferencesItemsRotationsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_itemreferences_items_rotations_items_items" -> {
                QOfficialKeysItemReferencesItemsRotationsItemsItems row = new QOfficialKeysItemReferencesItemsRotationsItemsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_missionreward_counteditems_items" -> {
                QOfficialKeysMissionRewardCountedItemsItems row = new QOfficialKeysMissionRewardCountedItemsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_missionreward_counteditems_items_it_61068887b8" -> {
                QOfficialKeysMissionRewardCountedItemsItemsItemReferencesItems row = new QOfficialKeysMissionRewardCountedItemsItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_missionreward_countedstoreitems_items" -> {
                QOfficialKeysMissionRewardCountedStoreItemsItems row = new QOfficialKeysMissionRewardCountedStoreItemsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_missionreward_countedstoreitems_ite_4e2205c8cc" -> {
                QOfficialKeysMissionRewardCountedStoreItemsItemsItemReferencesItems row = new QOfficialKeysMissionRewardCountedStoreItemsItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_missionreward_itemreferences_items" -> {
                QOfficialKeysMissionRewardItemReferencesItems row = new QOfficialKeysMissionRewardItemReferencesItems("row");
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
