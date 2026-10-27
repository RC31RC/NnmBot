package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.items.QOfficialSupplyDropItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.market.QOfficialMarketItemRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.market.QOfficialMarketItemSubtypesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.market.QOfficialMarketItemTagsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.progression.QOfficialAchievementChildrenItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.progression.QOfficialAchievementRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.progression.QOfficialChallengeCountedRewardsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.progression.QOfficialChallengeRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 12；由模型生成器维护。 */
final class OfficialRelationQueryDslPart12 {
    private OfficialRelationQueryDslPart12() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_supply_drop_itemreferences_items" -> {
                QOfficialSupplyDropItemReferencesItems row = new QOfficialSupplyDropItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_market_item_root" -> {
                QOfficialMarketItemRoot row = new QOfficialMarketItemRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "baseEndoState", "baseEndo", "bulkTradableState", "bulkTradable", "ducatsState", "ducats", "endoMultiplierState", "endoMultiplier", "gameItemState", "gameItemIconStatusState", "gameItemIconStatus", "gameItemIconUrlState", "gameItemIconUrl", "gameItemNameState", "gameItemName", "gameItemNameEnState", "gameItemNameEn", "gameItemNameZhState", "gameItemNameZh", "gameItemResolutionState", "gameItemResolution", "gameItemUniqueNameState", "gameItemUniqueName", "gameRefState", "gameRef", "gameRefStatusState", "gameRefStatus", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "idState", "idValue", "maxAmberStarsState", "maxAmberStars", "maxCyanStarsState", "maxCyanStars", "maxRankState", "maxRank", "nameState", "name", "nameEnState", "nameEn", "recordKeyState", "recordKey", "slugState", "slug", "subIconState", "subIcon", "subtypesState", "tagsState", "thumbState", "thumb", "thumbUrlState", "thumbUrl", "urlNameState", "urlName", "vaultedState", "vaulted", "zhIconState", "zhNameState", "zhName", "zhSubIconState", "zhThumbState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.baseEndoState, row.baseEndo, row.bulkTradableState, row.bulkTradable, row.ducatsState, row.ducats, row.endoMultiplierState, row.endoMultiplier, row.gameItemState, row.gameItemIconStatusState, row.gameItemIconStatus, row.gameItemIconUrlState, row.gameItemIconUrl, row.gameItemNameState, row.gameItemName, row.gameItemNameEnState, row.gameItemNameEn, row.gameItemNameZhState, row.gameItemNameZh, row.gameItemResolutionState, row.gameItemResolution, row.gameItemUniqueNameState, row.gameItemUniqueName, row.gameRefState, row.gameRef, row.gameRefStatusState, row.gameRefStatus, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.idState, row.idValue, row.maxAmberStarsState, row.maxAmberStars, row.maxCyanStarsState, row.maxCyanStars, row.maxRankState, row.maxRank, row.nameState, row.name, row.nameEnState, row.nameEn, row.recordKeyState, row.recordKey, row.slugState, row.slug, row.subIconState, row.subIcon, row.subtypesState, row.tagsState, row.thumbState, row.thumb, row.thumbUrlState, row.thumbUrl, row.urlNameState, row.urlName, row.vaultedState, row.vaulted, row.zhIconState, row.zhNameState, row.zhName, row.zhSubIconState, row.zhThumbState));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_market_item_subtypes_items" -> {
                QOfficialMarketItemSubtypesItems row = new QOfficialMarketItemSubtypesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_market_item_tags_items" -> {
                QOfficialMarketItemTagsItems row = new QOfficialMarketItemTagsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_achievement_root" -> {
                QOfficialAchievementRoot row = new QOfficialAchievementRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "childrenState", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "hiddenState", "hidden", "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "progressIndicatorFreqState", "progressIndicatorFreq", "requiredCountState", "requiredCount", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.childrenState, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.hiddenState, row.hidden, row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.progressIndicatorFreqState, row.progressIndicatorFreq, row.requiredCountState, row.requiredCount, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_achievement_children_items" -> {
                QOfficialAchievementChildrenItems row = new QOfficialAchievementChildrenItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_challenge_root" -> {
                QOfficialChallengeRoot row = new QOfficialChallengeRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "countedRewardsState", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "flavourState", "flavour", "flavourEnState", "flavourEn", "flavourZhState", "flavourZh", "hintState", "hint", "hintEnState", "hintEn", "hintZhState", "hintZh", "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "messageAttachmentsState", "messageBodyState", "messageBody", "messageBodyEnState", "messageBodyEn", "messageBodyZhState", "messageBodyZh", "messageCountedAttachmentsState", "messageCustomDataState", "messageCustomData", "messageHighPriorityState", "messageHighPriority", "messageIconState", "messageIcon", "messageIconAssetState", "messageIconAsset", "messageIconStatusState", "messageIconStatus", "messageSenderState", "messageSender", "messageSenderEnState", "messageSenderEn", "messageSenderZhState", "messageSenderZh", "messageTitleState", "messageTitle", "messageTitleEnState", "messageTitleEn", "messageTitleZhState", "messageTitleZh", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "requiredCountState", "requiredCount", "standingState", "standing"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.countedRewardsState, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.flavourState, row.flavour, row.flavourEnState, row.flavourEn, row.flavourZhState, row.flavourZh, row.hintState, row.hint, row.hintEnState, row.hintEn, row.hintZhState, row.hintZh, row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.messageAttachmentsState, row.messageBodyState, row.messageBody, row.messageBodyEnState, row.messageBodyEn, row.messageBodyZhState, row.messageBodyZh, row.messageCountedAttachmentsState, row.messageCustomDataState, row.messageCustomData, row.messageHighPriorityState, row.messageHighPriority, row.messageIconState, row.messageIcon, row.messageIconAssetState, row.messageIconAsset, row.messageIconStatusState, row.messageIconStatus, row.messageSenderState, row.messageSender, row.messageSenderEnState, row.messageSenderEn, row.messageSenderZhState, row.messageSenderZh, row.messageTitleState, row.messageTitle, row.messageTitleEnState, row.messageTitleEn, row.messageTitleZhState, row.messageTitleZh, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.requiredCountState, row.requiredCount, row.standingState, row.standing));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_challenge_countedrewards_items" -> {
                QOfficialChallengeCountedRewardsItems row = new QOfficialChallengeCountedRewardsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemCountState", "itemCount", "itemReferencesState", "storeItemState", "storeItem"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemCountState, row.itemCount, row.itemReferencesState, row.storeItemState, row.storeItem));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_supply_drop_itemreferences_items" -> {
                QOfficialSupplyDropItemReferencesItems row = new QOfficialSupplyDropItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_market_item_subtypes_items" -> {
                QOfficialMarketItemSubtypesItems row = new QOfficialMarketItemSubtypesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_market_item_tags_items" -> {
                QOfficialMarketItemTagsItems row = new QOfficialMarketItemTagsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_achievement_children_items" -> {
                QOfficialAchievementChildrenItems row = new QOfficialAchievementChildrenItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_challenge_countedrewards_items" -> {
                QOfficialChallengeCountedRewardsItems row = new QOfficialChallengeCountedRewardsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_supply_drop_itemreferences_items" -> {
                QOfficialSupplyDropItemReferencesItems row = new QOfficialSupplyDropItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_market_item_root" -> {
                QOfficialMarketItemRoot row = new QOfficialMarketItemRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_market_item_subtypes_items" -> {
                QOfficialMarketItemSubtypesItems row = new QOfficialMarketItemSubtypesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_market_item_tags_items" -> {
                QOfficialMarketItemTagsItems row = new QOfficialMarketItemTagsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_achievement_root" -> {
                QOfficialAchievementRoot row = new QOfficialAchievementRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_achievement_children_items" -> {
                QOfficialAchievementChildrenItems row = new QOfficialAchievementChildrenItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_challenge_root" -> {
                QOfficialChallengeRoot row = new QOfficialChallengeRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_challenge_countedrewards_items" -> {
                QOfficialChallengeCountedRewardsItems row = new QOfficialChallengeCountedRewardsItems("row");
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
