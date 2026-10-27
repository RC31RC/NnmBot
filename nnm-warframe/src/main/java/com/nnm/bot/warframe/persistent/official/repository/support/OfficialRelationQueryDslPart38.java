package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialSyndicateTitlesItemsSacrificeItemsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialSyndicateWeeklyChallengesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.worldstate.QOfficialUpgradeTypeRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 38；由模型生成器维护。 */
final class OfficialRelationQueryDslPart38 {
    private OfficialRelationQueryDslPart38() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_syndicate_titles_items_sacrifice_items_i_e36fee76db" -> {
                QOfficialSyndicateTitlesItemsSacrificeItemsItemsItemReferencesItems row = new QOfficialSyndicateTitlesItemsSacrificeItemsItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_syndicate_weeklychallenges_items" -> {
                QOfficialSyndicateWeeklyChallengesItems row = new QOfficialSyndicateWeeklyChallengesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_upgrade_type_root" -> {
                QOfficialUpgradeTypeRoot row = new QOfficialUpgradeTypeRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_syndicate_titles_items_sacrifice_items_i_e36fee76db" -> {
                QOfficialSyndicateTitlesItemsSacrificeItemsItemsItemReferencesItems row = new QOfficialSyndicateTitlesItemsSacrificeItemsItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_syndicate_weeklychallenges_items" -> {
                QOfficialSyndicateWeeklyChallengesItems row = new QOfficialSyndicateWeeklyChallengesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_syndicate_titles_items_sacrifice_items_i_e36fee76db" -> {
                QOfficialSyndicateTitlesItemsSacrificeItemsItemsItemReferencesItems row = new QOfficialSyndicateTitlesItemsSacrificeItemsItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_syndicate_weeklychallenges_items" -> {
                QOfficialSyndicateWeeklyChallengesItems row = new QOfficialSyndicateWeeklyChallengesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_upgrade_type_root" -> {
                QOfficialUpgradeTypeRoot row = new QOfficialUpgradeTypeRoot("row");
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
