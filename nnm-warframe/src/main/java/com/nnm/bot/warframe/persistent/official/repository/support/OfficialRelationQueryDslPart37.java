package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialSyndicateInitiationSacrificeItemsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialSyndicateItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialSyndicateMedallionsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialSyndicateMedallionsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialSyndicateTitlesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialSyndicateTitlesItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialSyndicateTitlesItemsRewardItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialSyndicateTitlesItemsSacrificeItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 37；由模型生成器维护。 */
final class OfficialRelationQueryDslPart37 {
    private OfficialRelationQueryDslPart37() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_syndicate_initiationsacrifice_items_item_807f86dba4" -> {
                QOfficialSyndicateInitiationSacrificeItemsItemsItemReferencesItems row = new QOfficialSyndicateInitiationSacrificeItemsItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_syndicate_itemreferences_items" -> {
                QOfficialSyndicateItemReferencesItems row = new QOfficialSyndicateItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_syndicate_medallions_items" -> {
                QOfficialSyndicateMedallionsItems row = new QOfficialSyndicateMedallionsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemReferencesState", "itemTypeState", "itemType", "standingState", "standing"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemReferencesState, row.itemTypeState, row.itemType, row.standingState, row.standing));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_syndicate_medallions_items_itemreferences_items" -> {
                QOfficialSyndicateMedallionsItemsItemReferencesItems row = new QOfficialSyndicateMedallionsItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_syndicate_titles_items" -> {
                QOfficialSyndicateTitlesItems row = new QOfficialSyndicateTitlesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "descriptionState", "description", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "itemReferencesState", "levelState", "level", "maxStandingState", "maxStanding", "minStandingState", "minStanding", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "rewardItemCountState", "rewardItemCount", "rewardItemReferencesState", "rewardItemTypeState", "rewardItemType", "sacrificeCreditsState", "sacrificeCredits", "sacrificeItemsState", "storeItemRewardState", "storeItemReward"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.descriptionState, row.description, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.itemReferencesState, row.levelState, row.level, row.maxStandingState, row.maxStanding, row.minStandingState, row.minStanding, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.rewardItemCountState, row.rewardItemCount, row.rewardItemReferencesState, row.rewardItemTypeState, row.rewardItemType, row.sacrificeCreditsState, row.sacrificeCredits, row.sacrificeItemsState, row.storeItemRewardState, row.storeItemReward));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_syndicate_titles_items_itemreferences_items" -> {
                QOfficialSyndicateTitlesItemsItemReferencesItems row = new QOfficialSyndicateTitlesItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_syndicate_titles_items_reward_itemreferences_items" -> {
                QOfficialSyndicateTitlesItemsRewardItemReferencesItems row = new QOfficialSyndicateTitlesItemsRewardItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_syndicate_titles_items_sacrifice_items_items" -> {
                QOfficialSyndicateTitlesItemsSacrificeItemsItems row = new QOfficialSyndicateTitlesItemsSacrificeItemsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemCountState", "itemCount", "itemReferencesState", "itemTypeState", "itemType"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemCountState, row.itemCount, row.itemReferencesState, row.itemTypeState, row.itemType));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_syndicate_initiationsacrifice_items_item_807f86dba4" -> {
                QOfficialSyndicateInitiationSacrificeItemsItemsItemReferencesItems row = new QOfficialSyndicateInitiationSacrificeItemsItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_syndicate_itemreferences_items" -> {
                QOfficialSyndicateItemReferencesItems row = new QOfficialSyndicateItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_syndicate_medallions_items" -> {
                QOfficialSyndicateMedallionsItems row = new QOfficialSyndicateMedallionsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_syndicate_medallions_items_itemreferences_items" -> {
                QOfficialSyndicateMedallionsItemsItemReferencesItems row = new QOfficialSyndicateMedallionsItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_syndicate_titles_items" -> {
                QOfficialSyndicateTitlesItems row = new QOfficialSyndicateTitlesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_syndicate_titles_items_itemreferences_items" -> {
                QOfficialSyndicateTitlesItemsItemReferencesItems row = new QOfficialSyndicateTitlesItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_syndicate_titles_items_reward_itemreferences_items" -> {
                QOfficialSyndicateTitlesItemsRewardItemReferencesItems row = new QOfficialSyndicateTitlesItemsRewardItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_syndicate_titles_items_sacrifice_items_items" -> {
                QOfficialSyndicateTitlesItemsSacrificeItemsItems row = new QOfficialSyndicateTitlesItemsSacrificeItemsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_syndicate_initiationsacrifice_items_item_807f86dba4" -> {
                QOfficialSyndicateInitiationSacrificeItemsItemsItemReferencesItems row = new QOfficialSyndicateInitiationSacrificeItemsItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_syndicate_itemreferences_items" -> {
                QOfficialSyndicateItemReferencesItems row = new QOfficialSyndicateItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_syndicate_medallions_items" -> {
                QOfficialSyndicateMedallionsItems row = new QOfficialSyndicateMedallionsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_syndicate_medallions_items_itemreferences_items" -> {
                QOfficialSyndicateMedallionsItemsItemReferencesItems row = new QOfficialSyndicateMedallionsItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_syndicate_titles_items" -> {
                QOfficialSyndicateTitlesItems row = new QOfficialSyndicateTitlesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_syndicate_titles_items_itemreferences_items" -> {
                QOfficialSyndicateTitlesItemsItemReferencesItems row = new QOfficialSyndicateTitlesItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_syndicate_titles_items_reward_itemreferences_items" -> {
                QOfficialSyndicateTitlesItemsRewardItemReferencesItems row = new QOfficialSyndicateTitlesItemsRewardItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_syndicate_titles_items_sacrifice_items_items" -> {
                QOfficialSyndicateTitlesItemsSacrificeItemsItems row = new QOfficialSyndicateTitlesItemsSacrificeItemsItems("row");
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
