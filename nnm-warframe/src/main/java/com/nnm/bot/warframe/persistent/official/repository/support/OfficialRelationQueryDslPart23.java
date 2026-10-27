package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialUpgradeAvailableChallengesItemsComplicationsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialUpgradeAvailableChallengesItemsComplicationsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialUpgradeAvailableChallengesItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialUpgradeCompatibilityTagsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialUpgradeCompatibleItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialUpgradeDescriptionLinesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialUpgradeIncompatibilityTagsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialUpgradeItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 23；由模型生成器维护。 */
final class OfficialRelationQueryDslPart23 {
    private OfficialRelationQueryDslPart23() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_upgrade_availablechallenges_items_compli_83e830471d" -> {
                QOfficialUpgradeAvailableChallengesItemsComplicationsItems row = new QOfficialUpgradeAvailableChallengesItemsComplicationsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "descriptionState", "description", "fullNameState", "fullName", "itemReferencesState", "overrideTagState", "overrideTag"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.descriptionState, row.description, row.fullNameState, row.fullName, row.itemReferencesState, row.overrideTagState, row.overrideTag));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_upgrade_availablechallenges_items_compli_d54e06cd05" -> {
                QOfficialUpgradeAvailableChallengesItemsComplicationsItemsItemReferencesItems row = new QOfficialUpgradeAvailableChallengesItemsComplicationsItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_upgrade_availablechallenges_items_itemre_2bfddfa1fb" -> {
                QOfficialUpgradeAvailableChallengesItemsItemReferencesItems row = new QOfficialUpgradeAvailableChallengesItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_upgrade_compatibilitytags_items" -> {
                QOfficialUpgradeCompatibilityTagsItems row = new QOfficialUpgradeCompatibilityTagsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_upgrade_compatibleitems_items" -> {
                QOfficialUpgradeCompatibleItemsItems row = new QOfficialUpgradeCompatibleItemsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_upgrade_descriptionlines_items" -> {
                QOfficialUpgradeDescriptionLinesItems row = new QOfficialUpgradeDescriptionLinesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "lineIndexState", "lineIndex", "textState", "text", "zhTextState", "zhText"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.lineIndexState, row.lineIndex, row.textState, row.text, row.zhTextState, row.zhText));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_upgrade_incompatibilitytags_items" -> {
                QOfficialUpgradeIncompatibilityTagsItems row = new QOfficialUpgradeIncompatibilityTagsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_upgrade_itemreferences_items" -> {
                QOfficialUpgradeItemReferencesItems row = new QOfficialUpgradeItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_upgrade_availablechallenges_items_compli_83e830471d" -> {
                QOfficialUpgradeAvailableChallengesItemsComplicationsItems row = new QOfficialUpgradeAvailableChallengesItemsComplicationsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_upgrade_availablechallenges_items_compli_d54e06cd05" -> {
                QOfficialUpgradeAvailableChallengesItemsComplicationsItemsItemReferencesItems row = new QOfficialUpgradeAvailableChallengesItemsComplicationsItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_upgrade_availablechallenges_items_itemre_2bfddfa1fb" -> {
                QOfficialUpgradeAvailableChallengesItemsItemReferencesItems row = new QOfficialUpgradeAvailableChallengesItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_upgrade_compatibilitytags_items" -> {
                QOfficialUpgradeCompatibilityTagsItems row = new QOfficialUpgradeCompatibilityTagsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_upgrade_compatibleitems_items" -> {
                QOfficialUpgradeCompatibleItemsItems row = new QOfficialUpgradeCompatibleItemsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_upgrade_descriptionlines_items" -> {
                QOfficialUpgradeDescriptionLinesItems row = new QOfficialUpgradeDescriptionLinesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_upgrade_incompatibilitytags_items" -> {
                QOfficialUpgradeIncompatibilityTagsItems row = new QOfficialUpgradeIncompatibilityTagsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_upgrade_itemreferences_items" -> {
                QOfficialUpgradeItemReferencesItems row = new QOfficialUpgradeItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_upgrade_availablechallenges_items_compli_83e830471d" -> {
                QOfficialUpgradeAvailableChallengesItemsComplicationsItems row = new QOfficialUpgradeAvailableChallengesItemsComplicationsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_upgrade_availablechallenges_items_compli_d54e06cd05" -> {
                QOfficialUpgradeAvailableChallengesItemsComplicationsItemsItemReferencesItems row = new QOfficialUpgradeAvailableChallengesItemsComplicationsItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_upgrade_availablechallenges_items_itemre_2bfddfa1fb" -> {
                QOfficialUpgradeAvailableChallengesItemsItemReferencesItems row = new QOfficialUpgradeAvailableChallengesItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_upgrade_compatibilitytags_items" -> {
                QOfficialUpgradeCompatibilityTagsItems row = new QOfficialUpgradeCompatibilityTagsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_upgrade_compatibleitems_items" -> {
                QOfficialUpgradeCompatibleItemsItems row = new QOfficialUpgradeCompatibleItemsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_upgrade_descriptionlines_items" -> {
                QOfficialUpgradeDescriptionLinesItems row = new QOfficialUpgradeDescriptionLinesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_upgrade_incompatibilitytags_items" -> {
                QOfficialUpgradeIncompatibilityTagsItems row = new QOfficialUpgradeIncompatibilityTagsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_upgrade_itemreferences_items" -> {
                QOfficialUpgradeItemReferencesItems row = new QOfficialUpgradeItemReferencesItems("row");
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
