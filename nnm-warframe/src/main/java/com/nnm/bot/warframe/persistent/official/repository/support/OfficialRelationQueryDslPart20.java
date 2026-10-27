package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRewardPoolStagesItemsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRewardPoolRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRewardPoolStagesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.rewards.QOfficialRewardPoolStagesItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.statistics.QOfficialUsage2020Root;
import com.nnm.bot.warframe.persistent.official.pojo.entity.statistics.QOfficialUsage2021Root;
import com.nnm.bot.warframe.persistent.official.pojo.entity.statistics.QOfficialUsage2022Root;
import com.nnm.bot.warframe.persistent.official.pojo.entity.statistics.QOfficialUsage2023Root;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 20；由模型生成器维护。 */
final class OfficialRelationQueryDslPart20 {
    private OfficialRelationQueryDslPart20() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_reward_pool_root" -> {
                QOfficialRewardPoolRoot row = new QOfficialRewardPoolRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "poolKeyState", "poolKey", "stagesState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.poolKeyState, row.poolKey, row.stagesState));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_reward_pool_stages_items" -> {
                QOfficialRewardPoolStagesItems row = new QOfficialRewardPoolStagesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_reward_pool_stages_items_items" -> {
                QOfficialRewardPoolStagesItemsItems row = new QOfficialRewardPoolStagesItemsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemCountState", "itemCount", "itemReferencesState", "probabilityState", "probability", "rarityState", "rarity", "typeState", "type"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemCountState, row.itemCount, row.itemReferencesState, row.probabilityState, row.probability, row.rarityState, row.rarity, row.typeState, row.type));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_reward_pool_stages_items_items_itemreferences_items" -> {
                QOfficialRewardPoolStagesItemsItemsItemReferencesItems row = new QOfficialRewardPoolStagesItemsItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_usage_2020_root" -> {
                QOfficialUsage2020Root row = new QOfficialUsage2020Root("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "categoryState", "category", "itemState", "item", "platformState", "platform", "rankState", "rank", "usageState", "usage", "yearState", "year"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.categoryState, row.category, row.itemState, row.item, row.platformState, row.platform, row.rankState, row.rank, row.usageState, row.usage, row.yearState, row.year));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_usage_2021_root" -> {
                QOfficialUsage2021Root row = new QOfficialUsage2021Root("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "categoryState", "category", "itemState", "item", "platformState", "platform", "rankState", "rank", "usageState", "usage", "yearState", "year"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.categoryState, row.category, row.itemState, row.item, row.platformState, row.platform, row.rankState, row.rank, row.usageState, row.usage, row.yearState, row.year));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_usage_2022_root" -> {
                QOfficialUsage2022Root row = new QOfficialUsage2022Root("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "categoryState", "category", "itemState", "item", "platformState", "platform", "rankState", "rank", "usageState", "usage", "yearState", "year"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.categoryState, row.category, row.itemState, row.item, row.platformState, row.platform, row.rankState, row.rank, row.usageState, row.usage, row.yearState, row.year));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_usage_2023_root" -> {
                QOfficialUsage2023Root row = new QOfficialUsage2023Root("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "categoryState", "category", "itemState", "item", "platformState", "platform", "rankState", "rank", "usageState", "usage", "yearState", "year"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.categoryState, row.category, row.itemState, row.item, row.platformState, row.platform, row.rankState, row.rank, row.usageState, row.usage, row.yearState, row.year));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_reward_pool_stages_items" -> {
                QOfficialRewardPoolStagesItems row = new QOfficialRewardPoolStagesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_reward_pool_stages_items_items" -> {
                QOfficialRewardPoolStagesItemsItems row = new QOfficialRewardPoolStagesItemsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_reward_pool_stages_items_items_itemreferences_items" -> {
                QOfficialRewardPoolStagesItemsItemsItemReferencesItems row = new QOfficialRewardPoolStagesItemsItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_reward_pool_root" -> {
                QOfficialRewardPoolRoot row = new QOfficialRewardPoolRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_reward_pool_stages_items" -> {
                QOfficialRewardPoolStagesItems row = new QOfficialRewardPoolStagesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_reward_pool_stages_items_items" -> {
                QOfficialRewardPoolStagesItemsItems row = new QOfficialRewardPoolStagesItemsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_reward_pool_stages_items_items_itemreferences_items" -> {
                QOfficialRewardPoolStagesItemsItemsItemReferencesItems row = new QOfficialRewardPoolStagesItemsItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_usage_2020_root" -> {
                QOfficialUsage2020Root row = new QOfficialUsage2020Root("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_usage_2021_root" -> {
                QOfficialUsage2021Root row = new QOfficialUsage2021Root("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_usage_2022_root" -> {
                QOfficialUsage2022Root row = new QOfficialUsage2022Root("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_usage_2023_root" -> {
                QOfficialUsage2023Root row = new QOfficialUsage2023Root("row");
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
