package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialArcaneRanksItemsStatsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialAvionicRanksItemsStatsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.statistics.QOfficialUsage2024Root;
import com.nnm.bot.warframe.persistent.official.pojo.entity.statistics.QOfficialUsage2025Root;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialArcaneRanksItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialArcaneRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialAvionicRanksItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialAvionicRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 21；由模型生成器维护。 */
final class OfficialRelationQueryDslPart21 {
    private OfficialRelationQueryDslPart21() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_usage_2024_root" -> {
                QOfficialUsage2024Root row = new QOfficialUsage2024Root("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "categoryState", "category", "itemState", "item", "platformState", "platform", "rankState", "rank", "usageState", "usage", "yearState", "year"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.categoryState, row.category, row.itemState, row.item, row.platformState, row.platform, row.rankState, row.rank, row.usageState, row.usage, row.yearState, row.year));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_usage_2025_root" -> {
                QOfficialUsage2025Root row = new QOfficialUsage2025Root("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "categoryState", "category", "itemState", "item", "platformState", "platform", "rankState", "rank", "usageState", "usage", "yearState", "year"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.categoryState, row.category, row.itemState, row.item, row.platformState, row.platform, row.rankState, row.rank, row.usageState, row.usage, row.yearState, row.year));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_arcane_root" -> {
                QOfficialArcaneRoot row = new QOfficialArcaneRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "codexSecretState", "codexSecret", "distillPointValueState", "distillPointValue", "excludeFromCodexState", "excludeFromCodexValue", "fusionLimitState", "fusionLimit", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameKeyState", "nameKey", "nameZhState", "nameZh", "ranksState", "rarityState", "rarity", "recordKeyState", "recordKey", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.codexSecretState, row.codexSecret, row.distillPointValueState, row.distillPointValue, row.excludeFromCodexState, row.excludeFromCodexValue, row.fusionLimitState, row.fusionLimit, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameKeyState, row.nameKey, row.nameZhState, row.nameZh, row.ranksState, row.rarityState, row.rarity, row.recordKeyState, row.recordKey, row.uniqueNameState, row.uniqueName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_arcane_ranks_items" -> {
                QOfficialArcaneRanksItems row = new QOfficialArcaneRanksItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "rankState", "rank", "statsState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.rankState, row.rank, row.statsState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_arcane_ranks_items_stats_items" -> {
                QOfficialArcaneRanksItemsStatsItems row = new QOfficialArcaneRanksItemsStatsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "lineIndexState", "lineIndex", "textState", "text", "zhTextState", "zhText"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.lineIndexState, row.lineIndex, row.textState, row.text, row.zhTextState, row.zhText));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_avionic_root" -> {
                QOfficialAvionicRoot row = new QOfficialAvionicRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "baseDrainState", "baseDrain", "codexSecretState", "codexSecret", "excludeFromCodexState", "excludeFromCodexValue", "fusionLimitState", "fusionLimit", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameKeyState", "nameKey", "nameZhState", "nameZh", "polarityState", "polarity", "ranksState", "rarityState", "rarity", "recordKeyState", "recordKey", "uniqueNameState", "uniqueName", "upgradeTypeState", "upgradeType"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.baseDrainState, row.baseDrain, row.codexSecretState, row.codexSecret, row.excludeFromCodexState, row.excludeFromCodexValue, row.fusionLimitState, row.fusionLimit, row.iconState, row.icon, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameKeyState, row.nameKey, row.nameZhState, row.nameZh, row.polarityState, row.polarity, row.ranksState, row.rarityState, row.rarity, row.recordKeyState, row.recordKey, row.uniqueNameState, row.uniqueName, row.upgradeTypeState, row.upgradeType));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_avionic_ranks_items" -> {
                QOfficialAvionicRanksItems row = new QOfficialAvionicRanksItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "rankState", "rank", "statsState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.rankState, row.rank, row.statsState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_avionic_ranks_items_stats_items" -> {
                QOfficialAvionicRanksItemsStatsItems row = new QOfficialAvionicRanksItemsStatsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "lineIndexState", "lineIndex", "textState", "text", "zhTextState", "zhText"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.lineIndexState, row.lineIndex, row.textState, row.text, row.zhTextState, row.zhText));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_arcane_ranks_items" -> {
                QOfficialArcaneRanksItems row = new QOfficialArcaneRanksItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_arcane_ranks_items_stats_items" -> {
                QOfficialArcaneRanksItemsStatsItems row = new QOfficialArcaneRanksItemsStatsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_avionic_ranks_items" -> {
                QOfficialAvionicRanksItems row = new QOfficialAvionicRanksItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_avionic_ranks_items_stats_items" -> {
                QOfficialAvionicRanksItemsStatsItems row = new QOfficialAvionicRanksItemsStatsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_usage_2024_root" -> {
                QOfficialUsage2024Root row = new QOfficialUsage2024Root("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_usage_2025_root" -> {
                QOfficialUsage2025Root row = new QOfficialUsage2025Root("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_arcane_root" -> {
                QOfficialArcaneRoot row = new QOfficialArcaneRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_arcane_ranks_items" -> {
                QOfficialArcaneRanksItems row = new QOfficialArcaneRanksItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_arcane_ranks_items_stats_items" -> {
                QOfficialArcaneRanksItemsStatsItems row = new QOfficialArcaneRanksItemsStatsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_avionic_root" -> {
                QOfficialAvionicRoot row = new QOfficialAvionicRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_avionic_ranks_items" -> {
                QOfficialAvionicRanksItems row = new QOfficialAvionicRanksItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_avionic_ranks_items_stats_items" -> {
                QOfficialAvionicRanksItemsStatsItems row = new QOfficialAvionicRanksItemsStatsItems("row");
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
