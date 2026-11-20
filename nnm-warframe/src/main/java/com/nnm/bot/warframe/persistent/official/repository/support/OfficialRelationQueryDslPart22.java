package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialFocusRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialFocusRanksItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialFocusRanksItemsStatsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialModSetRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialModSetMembersItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialModSetRanksItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialUpgradeRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialUpgradeAvailableChallengesItems;

/** 官方关系表 QueryDSL 标量投影分片 22；由模型生成器维护。 */
final class OfficialRelationQueryDslPart22 {
    private OfficialRelationQueryDslPart22() {}

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
            case "wf_official_focus_root" -> {
                QOfficialFocusRoot row = new QOfficialFocusRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "baseDrainState", "baseDrain", "baseFocusPointCostState",
                        "baseFocusPointCost", "codexSecretState", "codexSecret", "descriptionState", "descriptionValue",
                        "descriptionEnState", "descriptionEn", "descriptionZhState", "descriptionZh",
                        "excludeFromCodexState", "excludeFromCodexValue", "fusionLimitState", "fusionLimit",
                        "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState",
                        "name", "nameEnState", "nameEn", "nameKeyState", "nameKey", "nameZhState", "nameZh",
                        "polarityState", "polarity", "ranksState", "rarityState", "rarity", "recordKeyState",
                        "recordKey", "uniqueNameState", "uniqueName", "upgradeTypeState", "upgradeType"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.baseDrainState, row.baseDrain,
                        row.baseFocusPointCostState, row.baseFocusPointCost, row.codexSecretState, row.codexSecret,
                        row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn,
                        row.descriptionZhState, row.descriptionZh, row.excludeFromCodexState, row.excludeFromCodexValue,
                        row.fusionLimitState, row.fusionLimit, row.iconState, row.icon, row.iconStatusState,
                        row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState,
                        row.nameEn, row.nameKeyState, row.nameKey, row.nameZhState, row.nameZh, row.polarityState,
                        row.polarity, row.ranksState, row.rarityState, row.rarity, row.recordKeyState, row.recordKey,
                        row.uniqueNameState, row.uniqueName, row.upgradeTypeState, row.upgradeType));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_focus_ranks_items" -> {
                QOfficialFocusRanksItems row = new QOfficialFocusRanksItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "rankState", "rank", "statsState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.rankState, row.rank, row.statsState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_focus_ranks_items_stats_items" -> {
                QOfficialFocusRanksItemsStatsItems row = new QOfficialFocusRanksItemsStatsItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "lineIndexState", "lineIndex", "textState", "text",
                        "zhTextState", "zhText"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.lineIndexState, row.lineIndex,
                        row.textState, row.text, row.zhTextState, row.zhText));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_mod_set_root" -> {
                QOfficialModSetRoot row = new QOfficialModSetRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "buffSetState", "buffSet", "descriptionState",
                        "descriptionValue", "descriptionEnState", "descriptionEn", "descriptionZhState",
                        "descriptionZh", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState",
                        "iconUrl", "membersState", "numUpgradesInSetState", "numUpgradesInSet", "ranksState",
                        "recordKeyState", "recordKey", "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.buffSetState, row.buffSet,
                        row.descriptionState, row.descriptionValue, row.descriptionEnState, row.descriptionEn,
                        row.descriptionZhState, row.descriptionZh, row.iconState, row.icon, row.iconStatusState,
                        row.iconStatus, row.iconUrlState, row.iconUrl, row.membersState, row.numUpgradesInSetState,
                        row.numUpgradesInSet, row.ranksState, row.recordKeyState, row.recordKey, row.uniqueNameState,
                        row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_mod_set_members_items" -> {
                QOfficialModSetMembersItems row = new QOfficialModSetMembersItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "iconState", "icon", "iconStatusState", "iconStatus",
                        "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState",
                        "nameZh", "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.iconState, row.icon,
                        row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name,
                        row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_mod_set_ranks_items" -> {
                QOfficialModSetRanksItems row = new QOfficialModSetRanksItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "rankState", "rank", "statsState", "stats", "statsZhState",
                        "statsZh"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.rankState, row.rank, row.statsState,
                        row.stats, row.statsZhState, row.statsZh));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_upgrade_root" -> {
                QOfficialUpgradeRoot row = new QOfficialUpgradeRoot(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState",
                    "availableChallengesState", "avatarUrlState", "avatarUrl", "baseDrainState", "baseDrain",
                    "codexSecretState", "codexSecret", "compatState", "compat", "compatibilityTagsState",
                    "compatibleItemsState", "compatNameState", "compatName", "compatNameZhState", "compatNameZh",
                    "descriptionState", "descriptionValue", "descriptionEnState", "descriptionEn",
                    "descriptionLinesState", "descriptionZhState", "descriptionZh", "excludeFromCodexState",
                    "excludeFromCodexValue", "fusionLimitState", "fusionLimit", "iconState", "icon", "iconAssetState",
                    "iconAsset", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "incompatibilityTagsState",
                    "introducedAtState", "introducedAt", "isFrivolousState", "isFrivolous", "isStarterState",
                    "isStarter", "isUtilityState", "isUtility", "itemReferencesState", "modSetState", "modSet",
                    "modSetValuesState", "nameState", "name", "nameEnState", "nameEn", "nameKeyState", "nameKey",
                    "nameZhState", "nameZh", "polarityState", "polarity", "ranksState", "rarityState", "rarity",
                    "recordKeyState", "recordKey", "subtypeState", "subtype", "tradableState", "tradable", "typeState",
                    "type", "uniqueNameState", "uniqueName", "upgradeEntriesState", "upgradeTypeState", "upgradeType"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.availableChallengesState,
                        row.avatarUrlState, row.avatarUrl, row.baseDrainState, row.baseDrain, row.codexSecretState,
                        row.codexSecret, row.compatState, row.compat, row.compatibilityTagsState,
                        row.compatibleItemsState, row.compatNameState, row.compatName, row.compatNameZhState,
                        row.compatNameZh, row.descriptionState, row.descriptionValue, row.descriptionEnState,
                        row.descriptionEn, row.descriptionLinesState, row.descriptionZhState, row.descriptionZh,
                        row.excludeFromCodexState, row.excludeFromCodexValue, row.fusionLimitState, row.fusionLimit,
                        row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus,
                        row.iconUrlState, row.iconUrl, row.incompatibilityTagsState, row.introducedAtState,
                        row.introducedAt, row.isFrivolousState, row.isFrivolous, row.isStarterState, row.isStarter,
                        row.isUtilityState, row.isUtility, row.itemReferencesState, row.modSetState, row.modSet,
                        row.modSetValuesState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameKeyState,
                        row.nameKey, row.nameZhState, row.nameZh, row.polarityState, row.polarity, row.ranksState,
                        row.rarityState, row.rarity, row.recordKeyState, row.recordKey, row.subtypeState, row.subtype,
                        row.tradableState, row.tradable, row.typeState, row.type, row.uniqueNameState, row.uniqueName,
                        row.upgradeEntriesState, row.upgradeTypeState, row.upgradeType));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_upgrade_availablechallenges_items" -> {
                QOfficialUpgradeAvailableChallengesItems row = new QOfficialUpgradeAvailableChallengesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "complicationsState", "descriptionState", "description",
                        "fullNameState", "fullName", "itemReferencesState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.complicationsState,
                        row.descriptionState, row.description, row.fullNameState, row.fullName,
                        row.itemReferencesState));
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
            case "wf_official_focus_ranks_items" -> {
                QOfficialFocusRanksItems row = new QOfficialFocusRanksItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_focus_ranks_items_stats_items" -> {
                QOfficialFocusRanksItemsStatsItems row = new QOfficialFocusRanksItemsStatsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_mod_set_members_items" -> {
                QOfficialModSetMembersItems row = new QOfficialModSetMembersItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_mod_set_ranks_items" -> {
                QOfficialModSetRanksItems row = new QOfficialModSetRanksItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_upgrade_availablechallenges_items" -> {
                QOfficialUpgradeAvailableChallengesItems row = new QOfficialUpgradeAvailableChallengesItems(ROW);
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
            case "wf_official_focus_root" -> {
                QOfficialFocusRoot row = new QOfficialFocusRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_focus_ranks_items" -> {
                QOfficialFocusRanksItems row = new QOfficialFocusRanksItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_focus_ranks_items_stats_items" -> {
                QOfficialFocusRanksItemsStatsItems row = new QOfficialFocusRanksItemsStatsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_mod_set_root" -> {
                QOfficialModSetRoot row = new QOfficialModSetRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_mod_set_members_items" -> {
                QOfficialModSetMembersItems row = new QOfficialModSetMembersItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_mod_set_ranks_items" -> {
                QOfficialModSetRanksItems row = new QOfficialModSetRanksItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_upgrade_root" -> {
                QOfficialUpgradeRoot row = new QOfficialUpgradeRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_upgrade_availablechallenges_items" -> {
                QOfficialUpgradeAvailableChallengesItems row = new QOfficialUpgradeAvailableChallengesItems(ROW);
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
