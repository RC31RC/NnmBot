package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStagesItemsGroupsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStagesItemsGroupsItemsGroupsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStageReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStageReferencesItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStagesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItems;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 15；由模型生成器维护。 */
final class OfficialRelationQueryDslPart15 {
    private OfficialRelationQueryDslPart15() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_bounty_root" -> {
                QOfficialBountyRoot row = new QOfficialBountyRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh", "iconState", "icon", "iconAssetState", "iconAsset", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "poolKeyState", "poolKey", "stageReferencesState", "stagesState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh, row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.poolKeyState, row.poolKey, row.stageReferencesState, row.stagesState));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_bounty_stagereferences_items" -> {
                QOfficialBountyStageReferencesItems row = new QOfficialBountyStageReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bounty_stagereferences_items_items" -> {
                QOfficialBountyStageReferencesItemsItems row = new QOfficialBountyStageReferencesItemsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "iconStatusState", "iconStatus", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.iconStatusState, row.iconStatus, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bounty_stages_items" -> {
                QOfficialBountyStagesItems row = new QOfficialBountyStagesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "activationProbabilityState", "activationProbability", "categoryState", "category", "drawCountState", "drawCount", "drawModeState", "drawMode", "groupsState", "poolIdState", "poolId", "rewardsState", "sourceIdentityResolutionState", "sourceIdentityResolution", "sourceNameState", "sourceName", "sourceNameEnState", "sourceNameEn", "sourceNameZhState", "sourceNameZh"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.activationProbabilityState, row.activationProbability, row.categoryState, row.category, row.drawCountState, row.drawCount, row.drawModeState, row.drawMode, row.groupsState, row.poolIdState, row.poolId, row.rewardsState, row.sourceIdentityResolutionState, row.sourceIdentityResolution, row.sourceNameState, row.sourceName, row.sourceNameEnState, row.sourceNameEn, row.sourceNameZhState, row.sourceNameZh));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bounty_stages_items_groups_items" -> {
                QOfficialBountyStagesItemsGroupsItems row = new QOfficialBountyStagesItemsGroupsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "groupsState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "rewardsState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.groupsState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.rewardsState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bounty_stages_items_groups_items_groups_items" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "groupsState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "rewardsState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.groupsState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.rewardsState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bounty_stages_items_groups_items_groups__20e875307f" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "groupsState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "rewardsState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.groupsState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.rewardsState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bounty_stages_items_groups_items_groups__575ef48c93" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "groupsState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "rewardsState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.groupsState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.rewardsState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_bounty_stagereferences_items" -> {
                QOfficialBountyStageReferencesItems row = new QOfficialBountyStageReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bounty_stagereferences_items_items" -> {
                QOfficialBountyStageReferencesItemsItems row = new QOfficialBountyStageReferencesItemsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bounty_stages_items" -> {
                QOfficialBountyStagesItems row = new QOfficialBountyStagesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bounty_stages_items_groups_items" -> {
                QOfficialBountyStagesItemsGroupsItems row = new QOfficialBountyStagesItemsGroupsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bounty_stages_items_groups_items_groups_items" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bounty_stages_items_groups_items_groups__20e875307f" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bounty_stages_items_groups_items_groups__575ef48c93" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_bounty_root" -> {
                QOfficialBountyRoot row = new QOfficialBountyRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stagereferences_items" -> {
                QOfficialBountyStageReferencesItems row = new QOfficialBountyStageReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stagereferences_items_items" -> {
                QOfficialBountyStageReferencesItemsItems row = new QOfficialBountyStageReferencesItemsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stages_items" -> {
                QOfficialBountyStagesItems row = new QOfficialBountyStagesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stages_items_groups_items" -> {
                QOfficialBountyStagesItemsGroupsItems row = new QOfficialBountyStagesItemsGroupsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stages_items_groups_items_groups_items" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stages_items_groups_items_groups__20e875307f" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stages_items_groups_items_groups__575ef48c93" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems row = new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems("row");
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
