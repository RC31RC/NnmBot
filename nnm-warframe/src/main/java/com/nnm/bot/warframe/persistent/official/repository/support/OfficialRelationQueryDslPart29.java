package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysChainStagesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysChainStagesItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysChainStagesItemsItemsToGiveWhenTriggeredItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredAttachmentsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 29；由模型生成器维护。 */
final class OfficialRelationQueryDslPart29 {
    private OfficialRelationQueryDslPart29() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_keys_root" -> {
                QOfficialKeysRoot row = new QOfficialKeysRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "cacheRewardManifestState", "cacheRewardManifest", "chainStagesState", "codexSecretState", "codexSecret", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "excludeFromCodexState", "excludeFromCodexValue", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "missionState", "missionMaxEnemyLevelState", "missionMaxEnemyLevel", "missionMinEnemyLevelState", "missionMinEnemyLevel", "missionRewardCountedItemsState", "missionRewardCountedStoreItemsState", "missionRewardCreditsState", "missionRewardCredits", "missionRewardDroptableState", "missionRewardDroptable", "missionRewardItemReferencesState", "missionRewardItemsState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "parentNameState", "parentNameValue", "recordKeyState", "recordKey", "replayableState", "replayable", "rewardsState", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.cacheRewardManifestState, row.cacheRewardManifest, row.chainStagesState, row.codexSecretState, row.codexSecret, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.excludeFromCodexState, row.excludeFromCodexValue, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.missionState, row.missionMaxEnemyLevelState, row.missionMaxEnemyLevel, row.missionMinEnemyLevelState, row.missionMinEnemyLevel, row.missionRewardCountedItemsState, row.missionRewardCountedStoreItemsState, row.missionRewardCreditsState, row.missionRewardCredits, row.missionRewardDroptableState, row.missionRewardDroptable, row.missionRewardItemReferencesState, row.missionRewardItemsState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.parentNameState, row.parentNameValue, row.recordKeyState, row.recordKey, row.replayableState, row.replayable, row.rewardsState, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_keys_chainstages_items" -> {
                QOfficialKeysChainStagesItems row = new QOfficialKeysChainStagesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemReferencesState", "itemsToGiveWhenTriggeredState", "keyState", "key", "messageToSendWhenTriggeredAttachmentsState", "messageToSendWhenTriggeredBodyState", "messageToSendWhenTriggeredBody", "messageToSendWhenTriggeredBodyEnState", "messageToSendWhenTriggeredBodyEn", "messageToSendWhenTriggeredBodyZhState", "messageToSendWhenTriggeredBodyZh", "messageToSendWhenTriggeredCountedAttachmentsState", "messageToSendWhenTriggeredHighPriorityState", "messageToSendWhenTriggeredHighPriority", "messageToSendWhenTriggeredIconState", "messageToSendWhenTriggeredIcon", "messageToSendWhenTriggeredIconAssetState", "messageToSendWhenTriggeredIconAsset", "messageToSendWhenTriggeredIconStatusState", "messageToSendWhenTriggeredIconStatus", "messageToSendWhenTriggeredIconUrlState", "messageToSendWhenTriggeredIconUrl", "messageToSendWhenTriggeredItemReferencesState", "messageToSendWhenTriggeredSenderState", "messageToSendWhenTriggeredSender", "messageToSendWhenTriggeredSenderEnState", "messageToSendWhenTriggeredSenderEn", "messageToSendWhenTriggeredSenderZhState", "messageToSendWhenTriggeredSenderZh", "messageToSendWhenTriggeredTitleState", "messageToSendWhenTriggeredTitle", "messageToSendWhenTriggeredTitleEnState", "messageToSendWhenTriggeredTitleEn", "messageToSendWhenTriggeredTitleZhState", "messageToSendWhenTriggeredTitleZh", "messageToSendWhenTriggeredTransmissionState", "messageToSendWhenTriggeredTransmission"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemReferencesState, row.itemsToGiveWhenTriggeredState, row.keyState, row.key, row.messageToSendWhenTriggeredAttachmentsState, row.messageToSendWhenTriggeredBodyState, row.messageToSendWhenTriggeredBody, row.messageToSendWhenTriggeredBodyEnState, row.messageToSendWhenTriggeredBodyEn, row.messageToSendWhenTriggeredBodyZhState, row.messageToSendWhenTriggeredBodyZh, row.messageToSendWhenTriggeredCountedAttachmentsState, row.messageToSendWhenTriggeredHighPriorityState, row.messageToSendWhenTriggeredHighPriority, row.messageToSendWhenTriggeredIconState, row.messageToSendWhenTriggeredIcon, row.messageToSendWhenTriggeredIconAssetState, row.messageToSendWhenTriggeredIconAsset, row.messageToSendWhenTriggeredIconStatusState, row.messageToSendWhenTriggeredIconStatus, row.messageToSendWhenTriggeredIconUrlState, row.messageToSendWhenTriggeredIconUrl, row.messageToSendWhenTriggeredItemReferencesState, row.messageToSendWhenTriggeredSenderState, row.messageToSendWhenTriggeredSender, row.messageToSendWhenTriggeredSenderEnState, row.messageToSendWhenTriggeredSenderEn, row.messageToSendWhenTriggeredSenderZhState, row.messageToSendWhenTriggeredSenderZh, row.messageToSendWhenTriggeredTitleState, row.messageToSendWhenTriggeredTitle, row.messageToSendWhenTriggeredTitleEnState, row.messageToSendWhenTriggeredTitleEn, row.messageToSendWhenTriggeredTitleZhState, row.messageToSendWhenTriggeredTitleZh, row.messageToSendWhenTriggeredTransmissionState, row.messageToSendWhenTriggeredTransmission));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_chainstages_items_itemreferences_items" -> {
                QOfficialKeysChainStagesItemsItemReferencesItems row = new QOfficialKeysChainStagesItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_chainstages_items_itemstogivewhentr_d7e64375fb" -> {
                QOfficialKeysChainStagesItemsItemsToGiveWhenTriggeredItems row = new QOfficialKeysChainStagesItemsItemsToGiveWhenTriggeredItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_cbd479e087" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredAttachmentsItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredAttachmentsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_c826d7fe1f" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemCountState", "itemCount", "itemReferencesState", "itemTypeState", "itemType"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemCountState, row.itemCount, row.itemReferencesState, row.itemTypeState, row.itemType));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_5ec60f729f" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItemsItemReferencesItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_dc0fe50668" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredItemReferencesItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_keys_chainstages_items" -> {
                QOfficialKeysChainStagesItems row = new QOfficialKeysChainStagesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_chainstages_items_itemreferences_items" -> {
                QOfficialKeysChainStagesItemsItemReferencesItems row = new QOfficialKeysChainStagesItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_chainstages_items_itemstogivewhentr_d7e64375fb" -> {
                QOfficialKeysChainStagesItemsItemsToGiveWhenTriggeredItems row = new QOfficialKeysChainStagesItemsItemsToGiveWhenTriggeredItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_cbd479e087" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredAttachmentsItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredAttachmentsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_c826d7fe1f" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_5ec60f729f" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItemsItemReferencesItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_dc0fe50668" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredItemReferencesItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_keys_root" -> {
                QOfficialKeysRoot row = new QOfficialKeysRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_chainstages_items" -> {
                QOfficialKeysChainStagesItems row = new QOfficialKeysChainStagesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_chainstages_items_itemreferences_items" -> {
                QOfficialKeysChainStagesItemsItemReferencesItems row = new QOfficialKeysChainStagesItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_chainstages_items_itemstogivewhentr_d7e64375fb" -> {
                QOfficialKeysChainStagesItemsItemsToGiveWhenTriggeredItems row = new QOfficialKeysChainStagesItemsItemsToGiveWhenTriggeredItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_cbd479e087" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredAttachmentsItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredAttachmentsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_c826d7fe1f" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_5ec60f729f" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItemsItemReferencesItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredCountedAttachmentsItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_chainstages_items_messagetosendwhen_dc0fe50668" -> {
                QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredItemReferencesItems row = new QOfficialKeysChainStagesItemsMessageToSendWhenTriggeredItemReferencesItems("row");
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
