package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysChainStagesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysChainStagesItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysChainStagesItemsItemsToGiveWhenTriggeredItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredAttachmentsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredItemReferencesItems;

/** 官方关系表 QueryDSL 标量投影分片 29；由模型生成器维护。 */
final class OfficialRelationQueryDslPart29 {
    private OfficialRelationQueryDslPart29() { }

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
            case "wf_official_keys_root" -> {
                QOfficialKeysRoot row = new QOfficialKeysRoot(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "cacheRewardManifestState", "cacheRewardManifest", "chainStagesState", "codexSecretState", "codexSecret", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "excludeFromCodexState", "excludeFromCodexValue", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "missionState", "missionMaxEnemyLevelState", "missionMaxEnemyLevel", "missionMinEnemyLevelState", "missionMinEnemyLevel", "missionRewardCountedItemsState", "missionRewardCountedStoreItemsState", "missionRewardCreditsState", "missionRewardCredits", "missionRewardDroptableState", "missionRewardDroptable", "missionRewardItemReferencesState", "missionRewardItemsState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "parentNameState", "parentNameValue", "recordKeyState", "recordKey", "replayableState", "replayable", "rewardsState", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.cacheRewardManifestState, row.cacheRewardManifest, row.chainStagesState, row.codexSecretState, row.codexSecret, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.excludeFromCodexState, row.excludeFromCodexValue, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.missionState, row.missionMaxEnemyLevelState, row.missionMaxEnemyLevel, row.missionMinEnemyLevelState, row.missionMinEnemyLevel, row.missionRewardCountedItemsState, row.missionRewardCountedStoreItemsState, row.missionRewardCreditsState, row.missionRewardCredits, row.missionRewardDroptableState, row.missionRewardDroptable, row.missionRewardItemReferencesState, row.missionRewardItemsState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.parentNameState, row.parentNameValue, row.recordKeyState, row.recordKey, row.replayableState, row.replayable, row.rewardsState, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_keys_chainstages_items" -> {
                QOfficialKeysChainStagesItems row = new QOfficialKeysChainStagesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemReferencesState", "itemsToGiveWhenTriggeredState", "keyState", "key", "messageToSendWhenTriggeredAttachmentsState", "messageToSendWhenTriggeredBodyState", "messageToSendWhenTriggeredBody", "messageToSendWhenTriggeredBodyEnState", "messageToSendWhenTriggeredBodyEn", "messageToSendWhenTriggeredBodyZhState", "messageToSendWhenTriggeredBodyZh", "messageToSendWhenTriggeredCountedAttachmentsState", "messageToSendWhenTriggeredHighPriorityState", "messageToSendWhenTriggeredHighPriority", "messageToSendWhenTriggeredIconState", "messageToSendWhenTriggeredIcon", "messageToSendWhenTriggeredIconAssetState", "messageToSendWhenTriggeredIconAsset", "messageToSendWhenTriggeredIconStatusState", "messageToSendWhenTriggeredIconStatus", "messageToSendWhenTriggeredIconUrlState", "messageToSendWhenTriggeredIconUrl", "messageToSendWhenTriggeredItemReferencesState", "messageToSendWhenTriggeredSenderState", "messageToSendWhenTriggeredSender", "messageToSendWhenTriggeredSenderEnState", "messageToSendWhenTriggeredSenderEn", "messageToSendWhenTriggeredSenderZhState", "messageToSendWhenTriggeredSenderZh", "messageToSendWhenTriggeredTitleState", "messageToSendWhenTriggeredTitle", "messageToSendWhenTriggeredTitleEnState", "messageToSendWhenTriggeredTitleEn", "messageToSendWhenTriggeredTitleZhState", "messageToSendWhenTriggeredTitleZh", "messageToSendWhenTriggeredTransmissionState", "messageToSendWhenTriggeredTransmission"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemReferencesState, row.itemsToGiveWhenTriggeredState, row.keyState, row.key, row.messageToSendWhenTriggeredAttachmentsState, row.messageToSendWhenTriggeredBodyState, row.messageToSendWhenTriggeredBody, row.messageToSendWhenTriggeredBodyEnState, row.messageToSendWhenTriggeredBodyEn, row.messageToSendWhenTriggeredBodyZhState, row.messageToSendWhenTriggeredBodyZh, row.messageToSendWhenTriggeredCountedAttachmentsState, row.messageToSendWhenTriggeredHighPriorityState, row.messageToSendWhenTriggeredHighPriority, row.messageToSendWhenTriggeredIconState, row.messageToSendWhenTriggeredIcon, row.messageToSendWhenTriggeredIconAssetState, row.messageToSendWhenTriggeredIconAsset, row.messageToSendWhenTriggeredIconStatusState, row.messageToSendWhenTriggeredIconStatus, row.messageToSendWhenTriggeredIconUrlState, row.messageToSendWhenTriggeredIconUrl, row.messageToSendWhenTriggeredItemReferencesState, row.messageToSendWhenTriggeredSenderState, row.messageToSendWhenTriggeredSender, row.messageToSendWhenTriggeredSenderEnState, row.messageToSendWhenTriggeredSenderEn, row.messageToSendWhenTriggeredSenderZhState, row.messageToSendWhenTriggeredSenderZh, row.messageToSendWhenTriggeredTitleState, row.messageToSendWhenTriggeredTitle, row.messageToSendWhenTriggeredTitleEnState, row.messageToSendWhenTriggeredTitleEn, row.messageToSendWhenTriggeredTitleZhState, row.messageToSendWhenTriggeredTitleZh, row.messageToSendWhenTriggeredTransmissionState, row.messageToSendWhenTriggeredTransmission));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_chainstages_items_itemreferences_items" -> {
                QOfficialKeysChainStagesItemsItemReferencesItems row = new QOfficialKeysChainStagesItemsItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_chainstages_items_itemstogivewhentr_d7e64375fb" -> {
                QOfficialKeysChainStagesItemsItemsToGiveWhenTriggeredItems row = new QOfficialKeysChainStagesItemsItemsToGiveWhenTriggeredItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_cbd479e087" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredAttachmentsItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredAttachmentsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_c826d7fe1f" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemCountState", "itemCount", "itemReferencesState", "itemTypeState", "itemType"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemCountState, row.itemCount, row.itemReferencesState, row.itemTypeState, row.itemType));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_5ec60f729f" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItemsItemReferencesItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItemsItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_dc0fe50668" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredItemReferencesItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredItemReferencesItems(ROW);
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
            case "wf_official_keys_chainstages_items" -> {
                QOfficialKeysChainStagesItems row = new QOfficialKeysChainStagesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_chainstages_items_itemreferences_items" -> {
                QOfficialKeysChainStagesItemsItemReferencesItems row = new QOfficialKeysChainStagesItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_chainstages_items_itemstogivewhentr_d7e64375fb" -> {
                QOfficialKeysChainStagesItemsItemsToGiveWhenTriggeredItems row = new QOfficialKeysChainStagesItemsItemsToGiveWhenTriggeredItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_cbd479e087" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredAttachmentsItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredAttachmentsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_c826d7fe1f" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_5ec60f729f" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItemsItemReferencesItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_dc0fe50668" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredItemReferencesItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredItemReferencesItems(ROW);
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
            case "wf_official_keys_root" -> {
                QOfficialKeysRoot row = new QOfficialKeysRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_chainstages_items" -> {
                QOfficialKeysChainStagesItems row = new QOfficialKeysChainStagesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_chainstages_items_itemreferences_items" -> {
                QOfficialKeysChainStagesItemsItemReferencesItems row = new QOfficialKeysChainStagesItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_chainstages_items_itemstogivewhentr_d7e64375fb" -> {
                QOfficialKeysChainStagesItemsItemsToGiveWhenTriggeredItems row = new QOfficialKeysChainStagesItemsItemsToGiveWhenTriggeredItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_cbd479e087" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredAttachmentsItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredAttachmentsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_c826d7fe1f" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_5ec60f729f" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItemsItemReferencesItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_dc0fe50668" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredItemReferencesItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredItemReferencesItems(ROW);
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
