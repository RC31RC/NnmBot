package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.reference.QOfficialAbilityOwnerLocalizationAbilitiesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.reference.QOfficialImageRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.reference.QOfficialItemLocalizationRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.reference.QOfficialLanguageStringRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.reference.QOfficialTextIconItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.reference.QOfficialTextIconRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.reference.QOfficialUpgradeLocalizationDescriptionsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.reference.QOfficialUpgradeLocalizationRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 14；由模型生成器维护。 */
final class OfficialRelationQueryDslPart14 {
    private OfficialRelationQueryDslPart14() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_ability_owner_localization_abilities_items" -> {
                QOfficialAbilityOwnerLocalizationAbilitiesItems row = new QOfficialAbilityOwnerLocalizationAbilitiesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "abilityNameState", "abilityName", "abilityUniqueNameState", "abilityUniqueName", "descriptionState", "description", "zhAbilityNameState", "zhAbilityName", "zhDescriptionState", "zhDescription"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.abilityNameState, row.abilityName, row.abilityUniqueNameState, row.abilityUniqueName, row.descriptionState, row.description, row.zhAbilityNameState, row.zhAbilityName, row.zhDescriptionState, row.zhDescription));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_image_root" -> {
                QOfficialImageRoot row = new QOfficialImageRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "contentHashState", "contentHash", "forumNameState", "forumName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.contentHashState, row.contentHash, row.forumNameState, row.forumName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_item_localization_root" -> {
                QOfficialItemLocalizationRoot row = new QOfficialItemLocalizationRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "descriptionState", "descriptionValue", "iconUrlState", "iconUrl", "nameState", "name", "uniqueNameState", "uniqueName", "zhDescriptionState", "zhDescription", "zhNameState", "zhName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.descriptionState, row.descriptionValue, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.uniqueNameState, row.uniqueName, row.zhDescriptionState, row.zhDescription, row.zhNameState, row.zhName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_language_string_root" -> {
                QOfficialLanguageStringRoot row = new QOfficialLanguageStringRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "keyState", "key", "textState", "text", "zhTextState", "zhText"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.keyState, row.key, row.textState, row.text, row.zhTextState, row.zhText));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_text_icon_root" -> {
                QOfficialTextIconRoot row = new QOfficialTextIconRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "ditAgnosticState", "ditAgnostic", "ditAndroidState", "ditAndroid", "ditAutoState", "ditAuto", "ditIosState", "ditIos", "ditPcState", "ditPc", "ditPs4State", "ditPs4", "ditPs5State", "ditPs5", "ditSteamState", "ditSteam", "ditSwitchState", "ditSwitch", "ditXboneState", "ditXbone", "itemReferencesState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.ditAgnosticState, row.ditAgnostic, row.ditAndroidState, row.ditAndroid, row.ditAutoState, row.ditAuto, row.ditIosState, row.ditIos, row.ditPcState, row.ditPc, row.ditPs4State, row.ditPs4, row.ditPs5State, row.ditPs5, row.ditSteamState, row.ditSteam, row.ditSwitchState, row.ditSwitch, row.ditXboneState, row.ditXbone, row.itemReferencesState));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_text_icon_itemreferences_items" -> {
                QOfficialTextIconItemReferencesItems row = new QOfficialTextIconItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_upgrade_localization_root" -> {
                QOfficialUpgradeLocalizationRoot row = new QOfficialUpgradeLocalizationRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "descriptionsState", "iconUrlState", "iconUrl", "nameState", "name", "uniqueNameState", "uniqueName", "zhNameState", "zhName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.descriptionsState, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.uniqueNameState, row.uniqueName, row.zhNameState, row.zhName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_upgrade_localization_descriptions_items" -> {
                QOfficialUpgradeLocalizationDescriptionsItems row = new QOfficialUpgradeLocalizationDescriptionsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "lineIndexState", "lineIndex", "textState", "text", "zhTextState", "zhText"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.lineIndexState, row.lineIndex, row.textState, row.text, row.zhTextState, row.zhText));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_ability_owner_localization_abilities_items" -> {
                QOfficialAbilityOwnerLocalizationAbilitiesItems row = new QOfficialAbilityOwnerLocalizationAbilitiesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_text_icon_itemreferences_items" -> {
                QOfficialTextIconItemReferencesItems row = new QOfficialTextIconItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_upgrade_localization_descriptions_items" -> {
                QOfficialUpgradeLocalizationDescriptionsItems row = new QOfficialUpgradeLocalizationDescriptionsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_ability_owner_localization_abilities_items" -> {
                QOfficialAbilityOwnerLocalizationAbilitiesItems row = new QOfficialAbilityOwnerLocalizationAbilitiesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_image_root" -> {
                QOfficialImageRoot row = new QOfficialImageRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_item_localization_root" -> {
                QOfficialItemLocalizationRoot row = new QOfficialItemLocalizationRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_language_string_root" -> {
                QOfficialLanguageStringRoot row = new QOfficialLanguageStringRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_text_icon_root" -> {
                QOfficialTextIconRoot row = new QOfficialTextIconRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_text_icon_itemreferences_items" -> {
                QOfficialTextIconItemReferencesItems row = new QOfficialTextIconItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_upgrade_localization_root" -> {
                QOfficialUpgradeLocalizationRoot row = new QOfficialUpgradeLocalizationRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_upgrade_localization_descriptions_items" -> {
                QOfficialUpgradeLocalizationDescriptionsItems row = new QOfficialUpgradeLocalizationDescriptionsItems("row");
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
