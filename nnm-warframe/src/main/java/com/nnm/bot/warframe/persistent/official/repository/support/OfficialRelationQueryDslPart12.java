package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialSupplyDropItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.market.QOfficialMarketItemRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.market.QOfficialMarketItemSubtypesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.market.QOfficialMarketItemTagsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.progression.QOfficialAchievementRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.progression.QOfficialAchievementChildrenItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.progression.QOfficialChallengeRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.progression.QOfficialChallengeCountedRewardsItems;

/** 官方关系表 QueryDSL 标量投影分片 12；由模型生成器维护。 */
final class OfficialRelationQueryDslPart12 {
    private OfficialRelationQueryDslPart12() {}

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
            case "wf_official_supply_drop_itemreferences_items" -> {
                QOfficialSupplyDropItemReferencesItems row = new QOfficialSupplyDropItemReferencesItems(ROW);
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
            case "wf_official_market_item_root" -> {
                QOfficialMarketItemRoot row = new QOfficialMarketItemRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "baseEndoState", "baseEndo", "bulkTradableState", "bulkTradable",
                        "ducatsState", "ducats", "endoMultiplierState", "endoMultiplier", "gameItemState",
                        "gameItemIconStatusState", "gameItemIconStatus", "gameItemIconUrlState", "gameItemIconUrl",
                        "gameItemNameState", "gameItemName", "gameItemNameEnState", "gameItemNameEn",
                        "gameItemNameZhState", "gameItemNameZh", "gameItemResolutionState", "gameItemResolution",
                        "gameItemUniqueNameState", "gameItemUniqueName", "gameRefState", "gameRef",
                        "gameRefStatusState", "gameRefStatus", "iconState", "icon", "iconStatusState", "iconStatus",
                        "iconUrlState", "iconUrl", "idState", "idValue", "maxAmberStarsState", "maxAmberStars",
                        "maxCyanStarsState", "maxCyanStars", "maxRankState", "maxRank", "nameState", "name",
                        "nameEnState", "nameEn", "recordKeyState", "recordKey", "slugState", "slug", "subIconState",
                        "subIcon", "subtypesState", "tagsState", "thumbState", "thumb", "thumbUrlState", "thumbUrl",
                        "urlNameState", "urlName", "vaultedState", "vaulted", "zhIconState", "zhNameState", "zhName",
                        "zhSubIconState", "zhThumbState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.baseEndoState, row.baseEndo,
                        row.bulkTradableState, row.bulkTradable, row.ducatsState, row.ducats, row.endoMultiplierState,
                        row.endoMultiplier, row.gameItemState, row.gameItemIconStatusState, row.gameItemIconStatus,
                        row.gameItemIconUrlState, row.gameItemIconUrl, row.gameItemNameState, row.gameItemName,
                        row.gameItemNameEnState, row.gameItemNameEn, row.gameItemNameZhState, row.gameItemNameZh,
                        row.gameItemResolutionState, row.gameItemResolution, row.gameItemUniqueNameState,
                        row.gameItemUniqueName, row.gameRefState, row.gameRef, row.gameRefStatusState,
                        row.gameRefStatus, row.iconState, row.icon, row.iconStatusState, row.iconStatus,
                        row.iconUrlState, row.iconUrl, row.idState, row.idValue, row.maxAmberStarsState,
                        row.maxAmberStars, row.maxCyanStarsState, row.maxCyanStars, row.maxRankState, row.maxRank,
                        row.nameState, row.name, row.nameEnState, row.nameEn, row.recordKeyState, row.recordKey,
                        row.slugState, row.slug, row.subIconState, row.subIcon, row.subtypesState, row.tagsState,
                        row.thumbState, row.thumb, row.thumbUrlState, row.thumbUrl, row.urlNameState, row.urlName,
                        row.vaultedState, row.vaulted, row.zhIconState, row.zhNameState, row.zhName, row.zhSubIconState,
                        row.zhThumbState));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_market_item_subtypes_items" -> {
                QOfficialMarketItemSubtypesItems row = new QOfficialMarketItemSubtypesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_market_item_tags_items" -> {
                QOfficialMarketItemTagsItems row = new QOfficialMarketItemTagsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_achievement_root" -> {
                QOfficialAchievementRoot row = new QOfficialAchievementRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "childrenState", "descriptionState", "descriptionValue",
                        "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "hiddenState",
                        "hidden", "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus",
                        "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh",
                        "progressIndicatorFreqState", "progressIndicatorFreq", "requiredCountState", "requiredCount",
                        "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.childrenState, row.descriptionState,
                        row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState,
                        row.descriptionZh, row.hiddenState, row.hidden, row.iconState, row.icon, row.iconAssetState,
                        row.iconAsset, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState,
                        row.nameEn, row.nameZhState, row.nameZh, row.progressIndicatorFreqState,
                        row.progressIndicatorFreq, row.requiredCountState, row.requiredCount, row.uniqueNameState,
                        row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_achievement_children_items" -> {
                QOfficialAchievementChildrenItems row = new QOfficialAchievementChildrenItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_challenge_root" -> {
                QOfficialChallengeRoot row = new QOfficialChallengeRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "countedRewardsState", "descriptionState", "descriptionValue",
                        "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "flavourState",
                        "flavour", "flavourEnState", "flavourEn", "flavourZhState", "flavourZh", "hintState", "hint",
                        "hintEnState", "hintEn", "hintZhState", "hintZh", "iconState", "icon", "iconAssetState",
                        "iconAsset", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl",
                        "messageAttachmentsState", "messageBodyState", "messageBody", "messageBodyEnState",
                        "messageBodyEn", "messageBodyZhState", "messageBodyZh", "messageCountedAttachmentsState",
                        "messageCustomDataState", "messageCustomData", "messageHighPriorityState",
                        "messageHighPriority", "messageIconState", "messageIcon", "messageIconAssetState",
                        "messageIconAsset", "messageIconStatusState", "messageIconStatus", "messageSenderState",
                        "messageSender", "messageSenderEnState", "messageSenderEn", "messageSenderZhState",
                        "messageSenderZh", "messageTitleState", "messageTitle", "messageTitleEnState", "messageTitleEn",
                        "messageTitleZhState", "messageTitleZh", "nameState", "name", "nameEnState", "nameEn",
                        "nameZhState", "nameZh", "requiredCountState", "requiredCount", "standingState", "standing"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.countedRewardsState,
                        row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn,
                        row.descriptionZhState, row.descriptionZh, row.flavourState, row.flavour, row.flavourEnState,
                        row.flavourEn, row.flavourZhState, row.flavourZh, row.hintState, row.hint, row.hintEnState,
                        row.hintEn, row.hintZhState, row.hintZh, row.iconState, row.icon, row.iconAssetState,
                        row.iconAsset, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl,
                        row.messageAttachmentsState, row.messageBodyState, row.messageBody, row.messageBodyEnState,
                        row.messageBodyEn, row.messageBodyZhState, row.messageBodyZh,
                        row.messageCountedAttachmentsState, row.messageCustomDataState, row.messageCustomData,
                        row.messageHighPriorityState, row.messageHighPriority, row.messageIconState, row.messageIcon,
                        row.messageIconAssetState, row.messageIconAsset, row.messageIconStatusState,
                        row.messageIconStatus, row.messageSenderState, row.messageSender, row.messageSenderEnState,
                        row.messageSenderEn, row.messageSenderZhState, row.messageSenderZh, row.messageTitleState,
                        row.messageTitle, row.messageTitleEnState, row.messageTitleEn, row.messageTitleZhState,
                        row.messageTitleZh, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState,
                        row.nameZh, row.requiredCountState, row.requiredCount, row.standingState, row.standing));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_challenge_countedrewards_items" -> {
                QOfficialChallengeCountedRewardsItems row = new QOfficialChallengeCountedRewardsItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "itemCountState", "itemCount", "itemReferencesState",
                        "storeItemState", "storeItem"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemCountState, row.itemCount,
                        row.itemReferencesState, row.storeItemState, row.storeItem));
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
            case "wf_official_supply_drop_itemreferences_items" -> {
                QOfficialSupplyDropItemReferencesItems row = new QOfficialSupplyDropItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_market_item_subtypes_items" -> {
                QOfficialMarketItemSubtypesItems row = new QOfficialMarketItemSubtypesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_market_item_tags_items" -> {
                QOfficialMarketItemTagsItems row = new QOfficialMarketItemTagsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_achievement_children_items" -> {
                QOfficialAchievementChildrenItems row = new QOfficialAchievementChildrenItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_challenge_countedrewards_items" -> {
                QOfficialChallengeCountedRewardsItems row = new QOfficialChallengeCountedRewardsItems(ROW);
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
            case "wf_official_supply_drop_itemreferences_items" -> {
                QOfficialSupplyDropItemReferencesItems row = new QOfficialSupplyDropItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_market_item_root" -> {
                QOfficialMarketItemRoot row = new QOfficialMarketItemRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_market_item_subtypes_items" -> {
                QOfficialMarketItemSubtypesItems row = new QOfficialMarketItemSubtypesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_market_item_tags_items" -> {
                QOfficialMarketItemTagsItems row = new QOfficialMarketItemTagsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_achievement_root" -> {
                QOfficialAchievementRoot row = new QOfficialAchievementRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_achievement_children_items" -> {
                QOfficialAchievementChildrenItems row = new QOfficialAchievementChildrenItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_challenge_root" -> {
                QOfficialChallengeRoot row = new QOfficialChallengeRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_challenge_countedrewards_items" -> {
                QOfficialChallengeCountedRewardsItems row = new QOfficialChallengeCountedRewardsItems(ROW);
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
