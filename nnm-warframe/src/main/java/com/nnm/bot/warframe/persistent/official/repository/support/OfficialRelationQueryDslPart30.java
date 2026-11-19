package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysItemReferencesItemsRotationsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysItemReferencesItemsRotationsItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysMissionRewardCountedItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysMissionRewardCountedItemsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysMissionRewardCountedStoreItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysMissionRewardCountedStoreItemsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysMissionRewardItemReferencesItems;

/** 官方关系表 QueryDSL 标量投影分片 30；由模型生成器维护。 */
final class OfficialRelationQueryDslPart30 {
    private OfficialRelationQueryDslPart30() { }

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
            case "wf_official_keys_itemreferences_items" -> {
                QOfficialKeysItemReferencesItems row = new QOfficialKeysItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "probabilityUnitState", "probabilityUnit", "referenceKindState", "referenceKind", "resolutionState", "resolution", "rotationsState", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.probabilityUnitState, row.probabilityUnit, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.rotationsState, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_itemreferences_items_rotations_items" -> {
                QOfficialKeysItemReferencesItemsRotationsItems row = new QOfficialKeysItemReferencesItemsRotationsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_itemreferences_items_rotations_items_items" -> {
                QOfficialKeysItemReferencesItemsRotationsItemsItems row = new QOfficialKeysItemReferencesItemsRotationsItemsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemDomainState", "itemDomain", "itemIconStatusState", "itemIconStatus", "itemIconUrlState", "itemIconUrl", "itemNameState", "itemName", "itemNameEnState", "itemNameEn", "itemNameZhState", "itemNameZh", "itemResolutionState", "itemResolution", "itemUniqueNameState", "itemUniqueName", "itemCountState", "itemCount", "probabilityState", "probability", "typeState", "type"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemDomainState, row.itemDomain, row.itemIconStatusState, row.itemIconStatus, row.itemIconUrlState, row.itemIconUrl, row.itemNameState, row.itemName, row.itemNameEnState, row.itemNameEn, row.itemNameZhState, row.itemNameZh, row.itemResolutionState, row.itemResolution, row.itemUniqueNameState, row.itemUniqueName, row.itemCountState, row.itemCount, row.probabilityState, row.probability, row.typeState, row.type));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_missionreward_counteditems_items" -> {
                QOfficialKeysMissionRewardCountedItemsItems row = new QOfficialKeysMissionRewardCountedItemsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemCountState", "itemCount", "itemReferencesState", "itemTypeState", "itemType"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemCountState, row.itemCount, row.itemReferencesState, row.itemTypeState, row.itemType));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_missionreward_counteditems_items_it_61068887b8" -> {
                QOfficialKeysMissionRewardCountedItemsItemsItemReferencesItems row = new QOfficialKeysMissionRewardCountedItemsItemsItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_missionreward_countedstoreitems_items" -> {
                QOfficialKeysMissionRewardCountedStoreItemsItems row = new QOfficialKeysMissionRewardCountedStoreItemsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemCountState", "itemCount", "itemReferencesState", "storeItemState", "storeItem"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemCountState, row.itemCount, row.itemReferencesState, row.storeItemState, row.storeItem));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_missionreward_countedstoreitems_ite_4e2205c8cc" -> {
                QOfficialKeysMissionRewardCountedStoreItemsItemsItemReferencesItems row = new QOfficialKeysMissionRewardCountedStoreItemsItemsItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_missionreward_itemreferences_items" -> {
                QOfficialKeysMissionRewardItemReferencesItems row = new QOfficialKeysMissionRewardItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "probabilityUnitState", "probabilityUnit", "referenceKindState", "referenceKind", "resolutionState", "resolution", "rotationsState", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.probabilityUnitState, row.probabilityUnit, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.rotationsState, row.uniqueNameState, row.uniqueName));
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
            case "wf_official_keys_itemreferences_items" -> {
                QOfficialKeysItemReferencesItems row = new QOfficialKeysItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_itemreferences_items_rotations_items" -> {
                QOfficialKeysItemReferencesItemsRotationsItems row = new QOfficialKeysItemReferencesItemsRotationsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_itemreferences_items_rotations_items_items" -> {
                QOfficialKeysItemReferencesItemsRotationsItemsItems row = new QOfficialKeysItemReferencesItemsRotationsItemsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_missionreward_counteditems_items" -> {
                QOfficialKeysMissionRewardCountedItemsItems row = new QOfficialKeysMissionRewardCountedItemsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_missionreward_counteditems_items_it_61068887b8" -> {
                QOfficialKeysMissionRewardCountedItemsItemsItemReferencesItems row = new QOfficialKeysMissionRewardCountedItemsItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_missionreward_countedstoreitems_items" -> {
                QOfficialKeysMissionRewardCountedStoreItemsItems row = new QOfficialKeysMissionRewardCountedStoreItemsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_missionreward_countedstoreitems_ite_4e2205c8cc" -> {
                QOfficialKeysMissionRewardCountedStoreItemsItemsItemReferencesItems row = new QOfficialKeysMissionRewardCountedStoreItemsItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_missionreward_itemreferences_items" -> {
                QOfficialKeysMissionRewardItemReferencesItems row = new QOfficialKeysMissionRewardItemReferencesItems(ROW);
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
            case "wf_official_keys_itemreferences_items" -> {
                QOfficialKeysItemReferencesItems row = new QOfficialKeysItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_itemreferences_items_rotations_items" -> {
                QOfficialKeysItemReferencesItemsRotationsItems row = new QOfficialKeysItemReferencesItemsRotationsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_itemreferences_items_rotations_items_items" -> {
                QOfficialKeysItemReferencesItemsRotationsItemsItems row = new QOfficialKeysItemReferencesItemsRotationsItemsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_missionreward_counteditems_items" -> {
                QOfficialKeysMissionRewardCountedItemsItems row = new QOfficialKeysMissionRewardCountedItemsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_missionreward_counteditems_items_it_61068887b8" -> {
                QOfficialKeysMissionRewardCountedItemsItemsItemReferencesItems row = new QOfficialKeysMissionRewardCountedItemsItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_missionreward_countedstoreitems_items" -> {
                QOfficialKeysMissionRewardCountedStoreItemsItems row = new QOfficialKeysMissionRewardCountedStoreItemsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_missionreward_countedstoreitems_ite_4e2205c8cc" -> {
                QOfficialKeysMissionRewardCountedStoreItemsItemsItemReferencesItems row = new QOfficialKeysMissionRewardCountedStoreItemsItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_missionreward_itemreferences_items" -> {
                QOfficialKeysMissionRewardItemReferencesItems row = new QOfficialKeysMissionRewardItemReferencesItems(ROW);
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
