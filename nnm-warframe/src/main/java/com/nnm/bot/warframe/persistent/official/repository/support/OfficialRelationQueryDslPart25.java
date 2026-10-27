package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenAttributeOfficialEntriesItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenAttributeOfficialEntriesItemsUpgradeValuesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenAttributeTemplatesZhItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenDefinitionAvailableChallengesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenDefinitionAvailableChallengesItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenDefinitionRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 25；由模型生成器维护。 */
final class OfficialRelationQueryDslPart25 {
    private OfficialRelationQueryDslPart25() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_riven_attribute_officialentries_items_it_79b125e9c1" -> {
                QOfficialRivenAttributeOfficialEntriesItemsItemReferencesItems row = new QOfficialRivenAttributeOfficialEntriesItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_riven_attribute_officialentries_items_up_66a2e31130" -> {
                QOfficialRivenAttributeOfficialEntriesItemsUpgradeValuesItems row = new QOfficialRivenAttributeOfficialEntriesItemsUpgradeValuesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "locTagState", "locTag", "locTagZhState", "locTagZh", "reverseValueSymbolState", "reverseValueSymbol", "valueState", "value"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.locTagState, row.locTag, row.locTagZhState, row.locTagZh, row.reverseValueSymbolState, row.reverseValueSymbol, row.valueState, row.value));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_riven_attribute_templateszh_items" -> {
                QOfficialRivenAttributeTemplatesZhItems row = new QOfficialRivenAttributeTemplatesZhItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_riven_definition_root" -> {
                QOfficialRivenDefinitionRoot row = new QOfficialRivenDefinitionRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "availableChallengesState", "baseDrainState", "baseDrain", "codexSecretState", "codexSecret", "fusionLimitState", "fusionLimit", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "polarityState", "polarity", "rarityState", "rarity", "recordKeyState", "recordKey", "uniqueNameState", "uniqueName", "upgradeEntriesState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.availableChallengesState, row.baseDrainState, row.baseDrain, row.codexSecretState, row.codexSecret, row.fusionLimitState, row.fusionLimit, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.polarityState, row.polarity, row.rarityState, row.rarity, row.recordKeyState, row.recordKey, row.uniqueNameState, row.uniqueName, row.upgradeEntriesState));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_riven_definition_availablechallenges_items" -> {
                QOfficialRivenDefinitionAvailableChallengesItems row = new QOfficialRivenDefinitionAvailableChallengesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "complicationsState", "descriptionState", "description", "descriptionZhState", "descriptionZh", "fullNameState", "fullName", "itemReferencesState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.complicationsState, row.descriptionState, row.description, row.descriptionZhState, row.descriptionZh, row.fullNameState, row.fullName, row.itemReferencesState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_riven_definition_availablechallenges_ite_60794493f7" -> {
                QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItems row = new QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "descriptionState", "description", "descriptionZhState", "descriptionZh", "fullNameState", "fullName", "itemReferencesState", "overrideTagState", "overrideTag"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.descriptionState, row.description, row.descriptionZhState, row.descriptionZh, row.fullNameState, row.fullName, row.itemReferencesState, row.overrideTagState, row.overrideTag));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_riven_definition_availablechallenges_ite_915f85dea9" -> {
                QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItemsItemReferencesItems row = new QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_riven_definition_availablechallenges_ite_0751326f03" -> {
                QOfficialRivenDefinitionAvailableChallengesItemsItemReferencesItems row = new QOfficialRivenDefinitionAvailableChallengesItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_riven_attribute_officialentries_items_it_79b125e9c1" -> {
                QOfficialRivenAttributeOfficialEntriesItemsItemReferencesItems row = new QOfficialRivenAttributeOfficialEntriesItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_riven_attribute_officialentries_items_up_66a2e31130" -> {
                QOfficialRivenAttributeOfficialEntriesItemsUpgradeValuesItems row = new QOfficialRivenAttributeOfficialEntriesItemsUpgradeValuesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_riven_attribute_templateszh_items" -> {
                QOfficialRivenAttributeTemplatesZhItems row = new QOfficialRivenAttributeTemplatesZhItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_riven_definition_availablechallenges_items" -> {
                QOfficialRivenDefinitionAvailableChallengesItems row = new QOfficialRivenDefinitionAvailableChallengesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_riven_definition_availablechallenges_ite_60794493f7" -> {
                QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItems row = new QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_riven_definition_availablechallenges_ite_915f85dea9" -> {
                QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItemsItemReferencesItems row = new QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_riven_definition_availablechallenges_ite_0751326f03" -> {
                QOfficialRivenDefinitionAvailableChallengesItemsItemReferencesItems row = new QOfficialRivenDefinitionAvailableChallengesItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_riven_attribute_officialentries_items_it_79b125e9c1" -> {
                QOfficialRivenAttributeOfficialEntriesItemsItemReferencesItems row = new QOfficialRivenAttributeOfficialEntriesItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_attribute_officialentries_items_up_66a2e31130" -> {
                QOfficialRivenAttributeOfficialEntriesItemsUpgradeValuesItems row = new QOfficialRivenAttributeOfficialEntriesItemsUpgradeValuesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_attribute_templateszh_items" -> {
                QOfficialRivenAttributeTemplatesZhItems row = new QOfficialRivenAttributeTemplatesZhItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_definition_root" -> {
                QOfficialRivenDefinitionRoot row = new QOfficialRivenDefinitionRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_definition_availablechallenges_items" -> {
                QOfficialRivenDefinitionAvailableChallengesItems row = new QOfficialRivenDefinitionAvailableChallengesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_definition_availablechallenges_ite_60794493f7" -> {
                QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItems row = new QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_definition_availablechallenges_ite_915f85dea9" -> {
                QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItemsItemReferencesItems row = new QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_definition_availablechallenges_ite_0751326f03" -> {
                QOfficialRivenDefinitionAvailableChallengesItemsItemReferencesItems row = new QOfficialRivenDefinitionAvailableChallengesItemsItemReferencesItems("row");
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
