package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStageReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStageReferencesItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStagesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStagesItemsGroupsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStagesItemsGroupsItemsGroupsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems;

/** 官方关系表 QueryDSL 标量投影分片 15；由模型生成器维护。 */
final class OfficialRelationQueryDslPart15 {
    private OfficialRelationQueryDslPart15() {}

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
            case "wf_official_bounty_root" -> {
                QOfficialBountyRoot row = new QOfficialBountyRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "descriptionState", "descriptionValue", "descriptionEnState",
                        "descriptionEn", "descriptionZhState", "descriptionZh", "iconState", "icon", "iconAssetState",
                        "iconAsset", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn",
                        "nameZhState", "nameZh", "poolKeyState", "poolKey", "stageReferencesState", "stagesState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.descriptionState, row.descriptionValue,
                        row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh,
                        row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus,
                        row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh,
                        row.poolKeyState, row.poolKey, row.stageReferencesState, row.stagesState));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_bounty_stagereferences_items" -> {
                QOfficialBountyStageReferencesItems row = new QOfficialBountyStageReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bounty_stagereferences_items_items" -> {
                QOfficialBountyStageReferencesItemsItems row = new QOfficialBountyStageReferencesItemsItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "iconStatusState", "iconStatus", "resolutionState", "resolution",
                        "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.iconStatusState, row.iconStatus,
                        row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bounty_stages_items" -> {
                QOfficialBountyStagesItems row = new QOfficialBountyStagesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "activationProbabilityState", "activationProbability",
                        "categoryState", "category", "drawCountState", "drawCount", "drawModeState", "drawMode",
                        "groupsState", "poolIdState", "poolId", "rewardsState", "sourceIdentityResolutionState",
                        "sourceIdentityResolution", "sourceNameState", "sourceName", "sourceNameEnState",
                        "sourceNameEn", "sourceNameZhState", "sourceNameZh"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.activationProbabilityState,
                        row.activationProbability, row.categoryState, row.category, row.drawCountState, row.drawCount,
                        row.drawModeState, row.drawMode, row.groupsState, row.poolIdState, row.poolId, row.rewardsState,
                        row.sourceIdentityResolutionState, row.sourceIdentityResolution, row.sourceNameState,
                        row.sourceName, row.sourceNameEnState, row.sourceNameEn, row.sourceNameZhState,
                        row.sourceNameZh));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bounty_stages_items_groups_items" -> {
                QOfficialBountyStagesItemsGroupsItems row = new QOfficialBountyStagesItemsGroupsItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "groupsState", "nameState", "name", "nameEnState", "nameEn",
                        "nameZhState", "nameZh", "rewardsState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.groupsState, row.nameState, row.name,
                        row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.rewardsState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bounty_stages_items_groups_items_groups_items" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItems row =
                    new QOfficialBountyStagesItemsGroupsItemsGroupsItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "groupsState", "nameState", "name", "nameEnState", "nameEn",
                        "nameZhState", "nameZh", "rewardsState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.groupsState, row.nameState, row.name,
                        row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.rewardsState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bounty_stages_items_groups_items_groups__20e875307f" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItems row =
                    new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "groupsState", "nameState", "name", "nameEnState", "nameEn",
                        "nameZhState", "nameZh", "rewardsState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.groupsState, row.nameState, row.name,
                        row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.rewardsState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_bounty_stages_items_groups_items_groups__575ef48c93" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems row =
                    new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "groupsState", "nameState", "name", "nameEnState", "nameEn",
                        "nameZhState", "nameZh", "rewardsState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.groupsState, row.nameState, row.name,
                        row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.rewardsState));
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
            case "wf_official_bounty_stagereferences_items" -> {
                QOfficialBountyStageReferencesItems row = new QOfficialBountyStageReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bounty_stagereferences_items_items" -> {
                QOfficialBountyStageReferencesItemsItems row = new QOfficialBountyStageReferencesItemsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bounty_stages_items" -> {
                QOfficialBountyStagesItems row = new QOfficialBountyStagesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bounty_stages_items_groups_items" -> {
                QOfficialBountyStagesItemsGroupsItems row = new QOfficialBountyStagesItemsGroupsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bounty_stages_items_groups_items_groups_items" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItems row =
                    new QOfficialBountyStagesItemsGroupsItemsGroupsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bounty_stages_items_groups_items_groups__20e875307f" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItems row =
                    new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_bounty_stages_items_groups_items_groups__575ef48c93" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems row =
                    new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems(ROW);
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
            case "wf_official_bounty_root" -> {
                QOfficialBountyRoot row = new QOfficialBountyRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stagereferences_items" -> {
                QOfficialBountyStageReferencesItems row = new QOfficialBountyStageReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stagereferences_items_items" -> {
                QOfficialBountyStageReferencesItemsItems row = new QOfficialBountyStageReferencesItemsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stages_items" -> {
                QOfficialBountyStagesItems row = new QOfficialBountyStagesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stages_items_groups_items" -> {
                QOfficialBountyStagesItemsGroupsItems row = new QOfficialBountyStagesItemsGroupsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stages_items_groups_items_groups_items" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItems row =
                    new QOfficialBountyStagesItemsGroupsItemsGroupsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stages_items_groups_items_groups__20e875307f" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItems row =
                    new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_bounty_stages_items_groups_items_groups__575ef48c93" -> {
                QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems row =
                    new QOfficialBountyStagesItemsGroupsItemsGroupsItemsGroupsItemsGroupsItems(ROW);
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
