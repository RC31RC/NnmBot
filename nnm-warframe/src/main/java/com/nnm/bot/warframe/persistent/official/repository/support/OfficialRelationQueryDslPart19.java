package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicStatesEntriesItemReferencesItemsRotationsItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicStatesEntriesRewardsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicStatesEntriesRewardsItemsDropTableEntriesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicStatesEntriesRewardsItemsDropTableEntriesItemsGroupsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicStatesEntriesRewardsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicStatesEntriesUnresolvedDropRewardsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicUnresolvedDropRowsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRelicUnresolvedLocationRowsItems;

/** 官方关系表 QueryDSL 标量投影分片 19；由模型生成器维护。 */
final class OfficialRelationQueryDslPart19 {
    private OfficialRelationQueryDslPart19() { }

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
            case "wf_official_relic_states_entries_itemreferences_item_29f51a750c" -> {
                QOfficialRelicStatesEntriesItemReferencesItemsRotationsItemsItems row = new QOfficialRelicStatesEntriesItemReferencesItemsRotationsItemsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemDomainState", "itemDomain", "itemIconStatusState", "itemIconStatus", "itemIconUrlState", "itemIconUrl", "itemNameState", "itemName", "itemNameEnState", "itemNameEn", "itemNameZhState", "itemNameZh", "itemResolutionState", "itemResolution", "itemUniqueNameState", "itemUniqueName", "itemCountState", "itemCount", "rarityState", "rarity", "typeState", "type"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemDomainState, row.itemDomain, row.itemIconStatusState, row.itemIconStatus, row.itemIconUrlState, row.itemIconUrl, row.itemNameState, row.itemName, row.itemNameEnState, row.itemNameEn, row.itemNameZhState, row.itemNameZh, row.itemResolutionState, row.itemResolution, row.itemUniqueNameState, row.itemUniqueName, row.itemCountState, row.itemCount, row.rarityState, row.rarity, row.typeState, row.type));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_relic_states_entries_rewards_items" -> {
                QOfficialRelicStatesEntriesRewardsItems row = new QOfficialRelicStatesEntriesRewardsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "dropChancePercentState", "dropChancePercent", "dropRarityState", "dropRarity", "dropTableEntriesState", "itemIconStatusState", "itemIconStatus", "itemIconUrlState", "itemIconUrl", "itemMarketSlugState", "itemMarketSlug", "itemNameState", "itemName", "itemNameEnState", "itemNameEn", "itemNameZhState", "itemNameZh", "itemResolutionState", "itemResolution", "itemUniqueNameState", "itemUniqueName", "itemCountState", "itemCount", "itemReferencesState", "probabilityState", "probability", "rarityState", "rarity", "rewardNameState", "rewardName", "tierState", "tier"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.dropChancePercentState, row.dropChancePercent, row.dropRarityState, row.dropRarity, row.dropTableEntriesState, row.itemIconStatusState, row.itemIconStatus, row.itemIconUrlState, row.itemIconUrl, row.itemMarketSlugState, row.itemMarketSlug, row.itemNameState, row.itemName, row.itemNameEnState, row.itemNameEn, row.itemNameZhState, row.itemNameZh, row.itemResolutionState, row.itemResolution, row.itemUniqueNameState, row.itemUniqueName, row.itemCountState, row.itemCount, row.itemReferencesState, row.probabilityState, row.probability, row.rarityState, row.rarity, row.rewardNameState, row.rewardName, row.tierState, row.tier));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_relic_states_entries_rewards_items_dropt_5dab4ce4aa" -> {
                QOfficialRelicStatesEntriesRewardsItemsDropTableEntriesItems row = new QOfficialRelicStatesEntriesRewardsItemsDropTableEntriesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "categoryState", "category", "chanceResolutionState", "chanceResolution", "dropChancePercentState", "dropChancePercent", "groupsState", "itemIconStatusState", "itemIconStatus", "itemIconUrlState", "itemIconUrl", "itemItemCountState", "itemItemCount", "itemNameState", "itemName", "itemNameEnState", "itemNameEn", "itemNameZhState", "itemNameZh", "itemResolutionState", "itemResolution", "itemUniqueNameState", "itemUniqueName", "probabilityState", "probability", "rarityState", "rarity", "sourceState", "source"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.categoryState, row.category, row.chanceResolutionState, row.chanceResolution, row.dropChancePercentState, row.dropChancePercent, row.groupsState, row.itemIconStatusState, row.itemIconStatus, row.itemIconUrlState, row.itemIconUrl, row.itemItemCountState, row.itemItemCount, row.itemNameState, row.itemName, row.itemNameEnState, row.itemNameEn, row.itemNameZhState, row.itemNameZh, row.itemResolutionState, row.itemResolution, row.itemUniqueNameState, row.itemUniqueName, row.probabilityState, row.probability, row.rarityState, row.rarity, row.sourceState, row.source));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_relic_states_entries_rewards_items_dropt_0252b10b41" -> {
                QOfficialRelicStatesEntriesRewardsItemsDropTableEntriesItemsGroupsItems row = new QOfficialRelicStatesEntriesRewardsItemsDropTableEntriesItemsGroupsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_relic_states_entries_rewards_items_itemr_8a9c8ba3ab" -> {
                QOfficialRelicStatesEntriesRewardsItemsItemReferencesItems row = new QOfficialRelicStatesEntriesRewardsItemsItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_relic_states_entries_unresolveddroprewards_items" -> {
                QOfficialRelicStatesEntriesUnresolvedDropRewardsItems row = new QOfficialRelicStatesEntriesUnresolvedDropRewardsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_relic_unresolveddroprows_items" -> {
                QOfficialRelicUnresolvedDropRowsItems row = new QOfficialRelicUnresolvedDropRowsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_relic_unresolvedlocationrows_items" -> {
                QOfficialRelicUnresolvedLocationRowsItems row = new QOfficialRelicUnresolvedLocationRowsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState));
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
            case "wf_official_relic_states_entries_itemreferences_item_29f51a750c" -> {
                QOfficialRelicStatesEntriesItemReferencesItemsRotationsItemsItems row = new QOfficialRelicStatesEntriesItemReferencesItemsRotationsItemsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_relic_states_entries_rewards_items" -> {
                QOfficialRelicStatesEntriesRewardsItems row = new QOfficialRelicStatesEntriesRewardsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_relic_states_entries_rewards_items_dropt_5dab4ce4aa" -> {
                QOfficialRelicStatesEntriesRewardsItemsDropTableEntriesItems row = new QOfficialRelicStatesEntriesRewardsItemsDropTableEntriesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_relic_states_entries_rewards_items_dropt_0252b10b41" -> {
                QOfficialRelicStatesEntriesRewardsItemsDropTableEntriesItemsGroupsItems row = new QOfficialRelicStatesEntriesRewardsItemsDropTableEntriesItemsGroupsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_relic_states_entries_rewards_items_itemr_8a9c8ba3ab" -> {
                QOfficialRelicStatesEntriesRewardsItemsItemReferencesItems row = new QOfficialRelicStatesEntriesRewardsItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_relic_states_entries_unresolveddroprewards_items" -> {
                QOfficialRelicStatesEntriesUnresolvedDropRewardsItems row = new QOfficialRelicStatesEntriesUnresolvedDropRewardsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_relic_unresolveddroprows_items" -> {
                QOfficialRelicUnresolvedDropRowsItems row = new QOfficialRelicUnresolvedDropRowsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_relic_unresolvedlocationrows_items" -> {
                QOfficialRelicUnresolvedLocationRowsItems row = new QOfficialRelicUnresolvedLocationRowsItems(ROW);
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
            case "wf_official_relic_states_entries_itemreferences_item_29f51a750c" -> {
                QOfficialRelicStatesEntriesItemReferencesItemsRotationsItemsItems row = new QOfficialRelicStatesEntriesItemReferencesItemsRotationsItemsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_states_entries_rewards_items" -> {
                QOfficialRelicStatesEntriesRewardsItems row = new QOfficialRelicStatesEntriesRewardsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_states_entries_rewards_items_dropt_5dab4ce4aa" -> {
                QOfficialRelicStatesEntriesRewardsItemsDropTableEntriesItems row = new QOfficialRelicStatesEntriesRewardsItemsDropTableEntriesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_states_entries_rewards_items_dropt_0252b10b41" -> {
                QOfficialRelicStatesEntriesRewardsItemsDropTableEntriesItemsGroupsItems row = new QOfficialRelicStatesEntriesRewardsItemsDropTableEntriesItemsGroupsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_states_entries_rewards_items_itemr_8a9c8ba3ab" -> {
                QOfficialRelicStatesEntriesRewardsItemsItemReferencesItems row = new QOfficialRelicStatesEntriesRewardsItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_states_entries_unresolveddroprewards_items" -> {
                QOfficialRelicStatesEntriesUnresolvedDropRewardsItems row = new QOfficialRelicStatesEntriesUnresolvedDropRewardsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_unresolveddroprows_items" -> {
                QOfficialRelicUnresolvedDropRowsItems row = new QOfficialRelicUnresolvedDropRowsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_relic_unresolvedlocationrows_items" -> {
                QOfficialRelicUnresolvedLocationRowsItems row = new QOfficialRelicUnresolvedLocationRowsItems(ROW);
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
