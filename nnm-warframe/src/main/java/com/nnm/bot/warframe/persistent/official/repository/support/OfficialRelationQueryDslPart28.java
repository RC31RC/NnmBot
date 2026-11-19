package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialEnemyStagesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialEnemyStagesItemsItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialEnemyStagesItemsItemsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialEnemyUnhandledProcTypesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialInboxRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialInboxMessageAttachmentsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialInboxMessageCountedAttachmentsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialInboxMessageItemReferencesItems;

/** 官方关系表 QueryDSL 标量投影分片 28；由模型生成器维护。 */
final class OfficialRelationQueryDslPart28 {
    private OfficialRelationQueryDslPart28() { }

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
            case "wf_official_enemy_stages_items" -> {
                QOfficialEnemyStagesItems row = new QOfficialEnemyStagesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "activationProbabilityState", "activationProbability", "drawCountState", "drawCount", "drawModeState", "drawMode", "itemsState", "probabilityUnitState", "probabilityUnit", "typeState", "type"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.activationProbabilityState, row.activationProbability, row.drawCountState, row.drawCount, row.drawModeState, row.drawMode, row.itemsState, row.probabilityUnitState, row.probabilityUnit, row.typeState, row.type));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_enemy_stages_items_items_items" -> {
                QOfficialEnemyStagesItemsItemsItems row = new QOfficialEnemyStagesItemsItemsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemReferencesState", "probabilityState", "probability", "typeState", "type"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemReferencesState, row.probabilityState, row.probability, row.typeState, row.type));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_enemy_stages_items_items_items_itemreferences_items" -> {
                QOfficialEnemyStagesItemsItemsItemsItemReferencesItems row = new QOfficialEnemyStagesItemsItemsItemsItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_enemy_unhandledproctypes_items" -> {
                QOfficialEnemyUnhandledProcTypesItems row = new QOfficialEnemyUnhandledProcTypesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_inbox_root" -> {
                QOfficialInboxRoot row = new QOfficialInboxRoot(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "displayNameEnState", "displayNameEn", "displayNameZhState", "displayNameZh", "messageAttachmentsState", "messageBodyState", "messageBody", "messageBodyEnState", "messageBodyEn", "messageBodyZhState", "messageBodyZh", "messageCinematicState", "messageCinematic", "messageCountedAttachmentsState", "messageCustomDataState", "messageCustomData", "messageHighPriorityState", "messageHighPriority", "messageIconState", "messageIcon", "messageIconAssetState", "messageIconAsset", "messageIconStatusState", "messageIconStatus", "messageIconUrlState", "messageIconUrl", "messageItemReferencesState", "messageSenderState", "messageSender", "messageSenderEnState", "messageSenderEn", "messageSenderZhState", "messageSenderZh", "messageTitleState", "messageTitle", "messageTitleEnState", "messageTitleEn", "messageTitleZhState", "messageTitleZh", "messageTransmissionState", "messageTransmission", "sendOnlyOnceState", "sendOnlyOnce"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.displayNameEnState, row.displayNameEn, row.displayNameZhState, row.displayNameZh, row.messageAttachmentsState, row.messageBodyState, row.messageBody, row.messageBodyEnState, row.messageBodyEn, row.messageBodyZhState, row.messageBodyZh, row.messageCinematicState, row.messageCinematic, row.messageCountedAttachmentsState, row.messageCustomDataState, row.messageCustomData, row.messageHighPriorityState, row.messageHighPriority, row.messageIconState, row.messageIcon, row.messageIconAssetState, row.messageIconAsset, row.messageIconStatusState, row.messageIconStatus, row.messageIconUrlState, row.messageIconUrl, row.messageItemReferencesState, row.messageSenderState, row.messageSender, row.messageSenderEnState, row.messageSenderEn, row.messageSenderZhState, row.messageSenderZh, row.messageTitleState, row.messageTitle, row.messageTitleEnState, row.messageTitleEn, row.messageTitleZhState, row.messageTitleZh, row.messageTransmissionState, row.messageTransmission, row.sendOnlyOnceState, row.sendOnlyOnce));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_inbox_message_attachments_items" -> {
                QOfficialInboxMessageAttachmentsItems row = new QOfficialInboxMessageAttachmentsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_inbox_message_countedattachments_items" -> {
                QOfficialInboxMessageCountedAttachmentsItems row = new QOfficialInboxMessageCountedAttachmentsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_inbox_message_itemreferences_items" -> {
                QOfficialInboxMessageItemReferencesItems row = new QOfficialInboxMessageItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
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
            case "wf_official_enemy_stages_items" -> {
                QOfficialEnemyStagesItems row = new QOfficialEnemyStagesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_enemy_stages_items_items_items" -> {
                QOfficialEnemyStagesItemsItemsItems row = new QOfficialEnemyStagesItemsItemsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_enemy_stages_items_items_items_itemreferences_items" -> {
                QOfficialEnemyStagesItemsItemsItemsItemReferencesItems row = new QOfficialEnemyStagesItemsItemsItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_enemy_unhandledproctypes_items" -> {
                QOfficialEnemyUnhandledProcTypesItems row = new QOfficialEnemyUnhandledProcTypesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_inbox_message_attachments_items" -> {
                QOfficialInboxMessageAttachmentsItems row = new QOfficialInboxMessageAttachmentsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_inbox_message_countedattachments_items" -> {
                QOfficialInboxMessageCountedAttachmentsItems row = new QOfficialInboxMessageCountedAttachmentsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_inbox_message_itemreferences_items" -> {
                QOfficialInboxMessageItemReferencesItems row = new QOfficialInboxMessageItemReferencesItems(ROW);
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
            case "wf_official_enemy_stages_items" -> {
                QOfficialEnemyStagesItems row = new QOfficialEnemyStagesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_enemy_stages_items_items_items" -> {
                QOfficialEnemyStagesItemsItemsItems row = new QOfficialEnemyStagesItemsItemsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_enemy_stages_items_items_items_itemreferences_items" -> {
                QOfficialEnemyStagesItemsItemsItemsItemReferencesItems row = new QOfficialEnemyStagesItemsItemsItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_enemy_unhandledproctypes_items" -> {
                QOfficialEnemyUnhandledProcTypesItems row = new QOfficialEnemyUnhandledProcTypesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_inbox_root" -> {
                QOfficialInboxRoot row = new QOfficialInboxRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_inbox_message_attachments_items" -> {
                QOfficialInboxMessageAttachmentsItems row = new QOfficialInboxMessageAttachmentsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_inbox_message_countedattachments_items" -> {
                QOfficialInboxMessageCountedAttachmentsItems row = new QOfficialInboxMessageCountedAttachmentsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_inbox_message_itemreferences_items" -> {
                QOfficialInboxMessageItemReferencesItems row = new QOfficialInboxMessageItemReferencesItems(ROW);
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
