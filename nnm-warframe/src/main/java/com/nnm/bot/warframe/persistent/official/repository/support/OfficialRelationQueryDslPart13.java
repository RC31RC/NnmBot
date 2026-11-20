package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.progression.QOfficialChallengeCountedRewardsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.progression.QOfficialChallengeMessageAttachmentsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.progression.QOfficialChallengeMessageCountedAttachmentsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.progression.QOfficialIntrinsicRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.progression.QOfficialIntrinsicRanksItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.progression.QOfficialNightwaveRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.progression.QOfficialNightwaveItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.reference.QOfficialAbilityOwnerLocalizationRoot;

/** 官方关系表 QueryDSL 标量投影分片 13；由模型生成器维护。 */
final class OfficialRelationQueryDslPart13 {
    private OfficialRelationQueryDslPart13() {}

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
            case "wf_official_challenge_countedrewards_items_itemreferences_items" -> {
                QOfficialChallengeCountedRewardsItemsItemReferencesItems row =
                    new QOfficialChallengeCountedRewardsItemsItemReferencesItems(ROW);
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
            case "wf_official_challenge_message_attachments_items" -> {
                QOfficialChallengeMessageAttachmentsItems row = new QOfficialChallengeMessageAttachmentsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_challenge_message_countedattachments_items" -> {
                QOfficialChallengeMessageCountedAttachmentsItems row =
                    new QOfficialChallengeMessageCountedAttachmentsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_intrinsic_root" -> {
                QOfficialIntrinsicRoot row = new QOfficialIntrinsicRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "descriptionState", "descriptionValue", "descriptionEnState",
                        "descriptionEn", "descriptionZhState", "descriptionZh", "iconState", "icon", "iconAssetState",
                        "iconAsset", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn",
                        "nameZhState", "nameZh", "ranksState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.descriptionState, row.descriptionValue,
                        row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh,
                        row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus,
                        row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh,
                        row.ranksState));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_intrinsic_ranks_items" -> {
                QOfficialIntrinsicRanksItems row = new QOfficialIntrinsicRanksItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "descriptionState", "description", "descriptionEnState",
                        "descriptionEn", "descriptionZhState", "descriptionZh", "nameState", "name", "nameEnState",
                        "nameEn", "nameZhState", "nameZh"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.descriptionState, row.description,
                        row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh,
                        row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_nightwave_root" -> {
                QOfficialNightwaveRoot row = new QOfficialNightwaveRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "affiliationTagState", "affiliationTag", "descriptionState",
                        "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState",
                        "descriptionZh", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState",
                        "iconUrl", "itemCountState", "itemCount", "itemReferencesState", "nameState", "name",
                        "nameEnState", "nameEn", "nameZhState", "nameZh", "requiredState", "required", "standingState",
                        "standing", "tipState", "tip", "tipEnState", "tipEn", "tipIconState", "tipIcon", "tipZhState",
                        "tipZh", "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.affiliationTagState, row.affiliationTag,
                        row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn,
                        row.descriptionZhState, row.descriptionZh, row.iconState, row.icon, row.iconStatusState,
                        row.iconStatus, row.iconUrlState, row.iconUrl, row.itemCountState, row.itemCount,
                        row.itemReferencesState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState,
                        row.nameZh, row.requiredState, row.required, row.standingState, row.standing, row.tipState,
                        row.tip, row.tipEnState, row.tipEn, row.tipIconState, row.tipIcon, row.tipZhState, row.tipZh,
                        row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_nightwave_itemreferences_items" -> {
                QOfficialNightwaveItemReferencesItems row = new QOfficialNightwaveItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "fieldState", "field", "iconStatusState", "iconStatus",
                        "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState",
                        "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.fieldState, row.field,
                        row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn,
                        row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState,
                        row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_ability_owner_localization_root" -> {
                QOfficialAbilityOwnerLocalizationRoot row = new QOfficialAbilityOwnerLocalizationRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "abilitiesState", "descriptionState", "descriptionValue",
                        "iconUrlState", "iconUrl", "nameState", "name", "uniqueNameState", "uniqueName",
                        "zhDescriptionState", "zhDescription", "zhNameState", "zhName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.abilitiesState, row.descriptionState,
                        row.descriptionValue, row.iconUrlState, row.iconUrl, row.nameState, row.name,
                        row.uniqueNameState, row.uniqueName, row.zhDescriptionState, row.zhDescription, row.zhNameState,
                        row.zhName));
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
            case "wf_official_challenge_countedrewards_items_itemreferences_items" -> {
                QOfficialChallengeCountedRewardsItemsItemReferencesItems row =
                    new QOfficialChallengeCountedRewardsItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_challenge_message_attachments_items" -> {
                QOfficialChallengeMessageAttachmentsItems row = new QOfficialChallengeMessageAttachmentsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_challenge_message_countedattachments_items" -> {
                QOfficialChallengeMessageCountedAttachmentsItems row =
                    new QOfficialChallengeMessageCountedAttachmentsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_intrinsic_ranks_items" -> {
                QOfficialIntrinsicRanksItems row = new QOfficialIntrinsicRanksItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_nightwave_itemreferences_items" -> {
                QOfficialNightwaveItemReferencesItems row = new QOfficialNightwaveItemReferencesItems(ROW);
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
            case "wf_official_challenge_countedrewards_items_itemreferences_items" -> {
                QOfficialChallengeCountedRewardsItemsItemReferencesItems row =
                    new QOfficialChallengeCountedRewardsItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_challenge_message_attachments_items" -> {
                QOfficialChallengeMessageAttachmentsItems row = new QOfficialChallengeMessageAttachmentsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_challenge_message_countedattachments_items" -> {
                QOfficialChallengeMessageCountedAttachmentsItems row =
                    new QOfficialChallengeMessageCountedAttachmentsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_intrinsic_root" -> {
                QOfficialIntrinsicRoot row = new QOfficialIntrinsicRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_intrinsic_ranks_items" -> {
                QOfficialIntrinsicRanksItems row = new QOfficialIntrinsicRanksItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_nightwave_root" -> {
                QOfficialNightwaveRoot row = new QOfficialNightwaveRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_nightwave_itemreferences_items" -> {
                QOfficialNightwaveItemReferencesItems row = new QOfficialNightwaveItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_ability_owner_localization_root" -> {
                QOfficialAbilityOwnerLocalizationRoot row = new QOfficialAbilityOwnerLocalizationRoot(ROW);
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
