package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialFactionRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialFissureModifierRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialMissionTypeRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialSyndicateDailyChallengesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialSyndicateFavoursItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialSyndicateFavoursItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialSyndicateInitiationSacrificeItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialSyndicateRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 36；由模型生成器维护。 */
final class OfficialRelationQueryDslPart36 {
    private OfficialRelationQueryDslPart36() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_faction_root" -> {
                QOfficialFactionRoot row = new QOfficialFactionRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "indexState", "index", "nameState", "name", "nameEnState", "nameEn", "nameKeyState", "nameKey", "nameZhState", "nameZh"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.indexState, row.index, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameKeyState, row.nameKey, row.nameZhState, row.nameZh));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_fissure_modifier_root" -> {
                QOfficialFissureModifierRoot row = new QOfficialFissureModifierRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "tierState", "tier"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.tierState, row.tier));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_mission_type_root" -> {
                QOfficialMissionTypeRoot row = new QOfficialMissionTypeRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "indexState", "index", "nameState", "name", "nameEnState", "nameEn", "nameKeyState", "nameKey", "nameZhState", "nameZh"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.indexState, row.index, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameKeyState, row.nameKey, row.nameZhState, row.nameZh));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_syndicate_root" -> {
                QOfficialSyndicateRoot row = new QOfficialSyndicateRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "alignmentsState", "alignmentsArbitersSyndicateState", "alignmentsArbitersSyndicate", "alignmentsCephalonSudaSyndicateState", "alignmentsCephalonSudaSyndicate", "alignmentsNewLokaSyndicateState", "alignmentsNewLokaSyndicate", "alignmentsPerrinSyndicateState", "alignmentsPerrinSyndicate", "alignmentsRedVeilSyndicateState", "alignmentsRedVeilSyndicate", "alignmentsSteelMeridianSyndicateState", "alignmentsSteelMeridianSyndicate", "backgroundColourValueState", "backgroundColourValue", "colourValueState", "colourValue", "dailyChallengesState", "dailyLimitBinState", "dailyLimitBin", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "favoursState", "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "initiationRewardState", "initiationReward", "initiationSacrificeCreditsState", "initiationSacrificeCredits", "initiationSacrificeItemsState", "itemReferencesState", "medallionsState", "medallionsCappedByDailyLimitState", "medallionsCappedByDailyLimit", "nameState", "name", "nameEnState", "nameEn", "nameKeyState", "nameKey", "nameZhState", "nameZh", "titlesState", "uniqueNameState", "uniqueName", "weeklyChallengesState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.alignmentsState, row.alignmentsArbitersSyndicateState, row.alignmentsArbitersSyndicate, row.alignmentsCephalonSudaSyndicateState, row.alignmentsCephalonSudaSyndicate, row.alignmentsNewLokaSyndicateState, row.alignmentsNewLokaSyndicate, row.alignmentsPerrinSyndicateState, row.alignmentsPerrinSyndicate, row.alignmentsRedVeilSyndicateState, row.alignmentsRedVeilSyndicate, row.alignmentsSteelMeridianSyndicateState, row.alignmentsSteelMeridianSyndicate, row.backgroundColourValueState, row.backgroundColourValue, row.colourValueState, row.colourValue, row.dailyChallengesState, row.dailyLimitBinState, row.dailyLimitBin, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.favoursState, row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.initiationRewardState, row.initiationReward, row.initiationSacrificeCreditsState, row.initiationSacrificeCredits, row.initiationSacrificeItemsState, row.itemReferencesState, row.medallionsState, row.medallionsCappedByDailyLimitState, row.medallionsCappedByDailyLimit, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameKeyState, row.nameKey, row.nameZhState, row.nameZh, row.titlesState, row.uniqueNameState, row.uniqueName, row.weeklyChallengesState));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_syndicate_dailychallenges_items" -> {
                QOfficialSyndicateDailyChallengesItems row = new QOfficialSyndicateDailyChallengesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_syndicate_favours_items" -> {
                QOfficialSyndicateFavoursItems row = new QOfficialSyndicateFavoursItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "creditsCostState", "creditsCost", "itemReferencesState", "rankUpRewardState", "rankUpReward", "requiredLevelState", "requiredLevel", "standingCostState", "standingCost", "storeItemState", "storeItem"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.creditsCostState, row.creditsCost, row.itemReferencesState, row.rankUpRewardState, row.rankUpReward, row.requiredLevelState, row.requiredLevel, row.standingCostState, row.standingCost, row.storeItemState, row.storeItem));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_syndicate_favours_items_itemreferences_items" -> {
                QOfficialSyndicateFavoursItemsItemReferencesItems row = new QOfficialSyndicateFavoursItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_syndicate_initiationsacrifice_items_items" -> {
                QOfficialSyndicateInitiationSacrificeItemsItems row = new QOfficialSyndicateInitiationSacrificeItemsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemCountState", "itemCount", "itemReferencesState", "itemTypeState", "itemType"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemCountState, row.itemCount, row.itemReferencesState, row.itemTypeState, row.itemType));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_syndicate_dailychallenges_items" -> {
                QOfficialSyndicateDailyChallengesItems row = new QOfficialSyndicateDailyChallengesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_syndicate_favours_items" -> {
                QOfficialSyndicateFavoursItems row = new QOfficialSyndicateFavoursItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_syndicate_favours_items_itemreferences_items" -> {
                QOfficialSyndicateFavoursItemsItemReferencesItems row = new QOfficialSyndicateFavoursItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_syndicate_initiationsacrifice_items_items" -> {
                QOfficialSyndicateInitiationSacrificeItemsItems row = new QOfficialSyndicateInitiationSacrificeItemsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_faction_root" -> {
                QOfficialFactionRoot row = new QOfficialFactionRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_fissure_modifier_root" -> {
                QOfficialFissureModifierRoot row = new QOfficialFissureModifierRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_mission_type_root" -> {
                QOfficialMissionTypeRoot row = new QOfficialMissionTypeRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_syndicate_root" -> {
                QOfficialSyndicateRoot row = new QOfficialSyndicateRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_syndicate_dailychallenges_items" -> {
                QOfficialSyndicateDailyChallengesItems row = new QOfficialSyndicateDailyChallengesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_syndicate_favours_items" -> {
                QOfficialSyndicateFavoursItems row = new QOfficialSyndicateFavoursItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_syndicate_favours_items_itemreferences_items" -> {
                QOfficialSyndicateFavoursItemsItemReferencesItems row = new QOfficialSyndicateFavoursItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_syndicate_initiationsacrifice_items_items" -> {
                QOfficialSyndicateInitiationSacrificeItemsItems row = new QOfficialSyndicateInitiationSacrificeItemsItems("row");
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
