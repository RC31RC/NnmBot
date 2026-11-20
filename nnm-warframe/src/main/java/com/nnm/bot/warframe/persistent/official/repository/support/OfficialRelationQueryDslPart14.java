package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.reference.QOfficialAbilityOwnerLocalizationAbilitiesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.reference.QOfficialImageRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.reference.QOfficialItemLocalizationRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.reference.QOfficialLanguageStringRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.reference.QOfficialTextIconRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.reference.QOfficialTextIconItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.reference.QOfficialUpgradeLocalizationRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.reference.QOfficialUpgradeLocalizationDescriptionsItems;

/** 官方关系表 QueryDSL 标量投影分片 14；由模型生成器维护。 */
final class OfficialRelationQueryDslPart14 {
    private OfficialRelationQueryDslPart14() {}

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
            case "wf_official_ability_owner_localization_abilities_items" -> {
                QOfficialAbilityOwnerLocalizationAbilitiesItems row =
                    new QOfficialAbilityOwnerLocalizationAbilitiesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "abilityNameState", "abilityName", "abilityUniqueNameState",
                        "abilityUniqueName", "descriptionState", "description", "zhAbilityNameState", "zhAbilityName",
                        "zhDescriptionState", "zhDescription"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.abilityNameState, row.abilityName,
                        row.abilityUniqueNameState, row.abilityUniqueName, row.descriptionState, row.description,
                        row.zhAbilityNameState, row.zhAbilityName, row.zhDescriptionState, row.zhDescription));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_image_root" -> {
                QOfficialImageRoot row = new QOfficialImageRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "contentHashState", "contentHash", "forumNameState",
                        "forumName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.contentHashState, row.contentHash,
                        row.forumNameState, row.forumName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_item_localization_root" -> {
                QOfficialItemLocalizationRoot row = new QOfficialItemLocalizationRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "descriptionState", "descriptionValue", "iconUrlState",
                        "iconUrl", "nameState", "name", "uniqueNameState", "uniqueName", "zhDescriptionState",
                        "zhDescription", "zhNameState", "zhName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.descriptionState, row.descriptionValue,
                        row.iconUrlState, row.iconUrl, row.nameState, row.name, row.uniqueNameState, row.uniqueName,
                        row.zhDescriptionState, row.zhDescription, row.zhNameState, row.zhName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_language_string_root" -> {
                QOfficialLanguageStringRoot row = new QOfficialLanguageStringRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "keyState", "key", "textState", "text", "zhTextState", "zhText"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.keyState, row.key, row.textState,
                        row.text, row.zhTextState, row.zhText));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_text_icon_root" -> {
                QOfficialTextIconRoot row = new QOfficialTextIconRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "ditAgnosticState", "ditAgnostic", "ditAndroidState",
                        "ditAndroid", "ditAutoState", "ditAuto", "ditIosState", "ditIos", "ditPcState", "ditPc",
                        "ditPs4State", "ditPs4", "ditPs5State", "ditPs5", "ditSteamState", "ditSteam", "ditSwitchState",
                        "ditSwitch", "ditXboneState", "ditXbone", "itemReferencesState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.ditAgnosticState, row.ditAgnostic,
                        row.ditAndroidState, row.ditAndroid, row.ditAutoState, row.ditAuto, row.ditIosState, row.ditIos,
                        row.ditPcState, row.ditPc, row.ditPs4State, row.ditPs4, row.ditPs5State, row.ditPs5,
                        row.ditSteamState, row.ditSteam, row.ditSwitchState, row.ditSwitch, row.ditXboneState,
                        row.ditXbone, row.itemReferencesState));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_text_icon_itemreferences_items" -> {
                QOfficialTextIconItemReferencesItems row = new QOfficialTextIconItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field",
                        "iconStatusState", "iconStatus", "nameState", "name", "nameEnState", "nameEn", "nameZhState",
                        "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution",
                        "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState,
                        row.field, row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState,
                        row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind,
                        row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_upgrade_localization_root" -> {
                QOfficialUpgradeLocalizationRoot row = new QOfficialUpgradeLocalizationRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "descriptionsState", "iconUrlState", "iconUrl", "nameState",
                        "name", "uniqueNameState", "uniqueName", "zhNameState", "zhName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.descriptionsState, row.iconUrlState,
                        row.iconUrl, row.nameState, row.name, row.uniqueNameState, row.uniqueName, row.zhNameState,
                        row.zhName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_upgrade_localization_descriptions_items" -> {
                QOfficialUpgradeLocalizationDescriptionsItems row =
                    new QOfficialUpgradeLocalizationDescriptionsItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "lineIndexState", "lineIndex", "textState", "text",
                        "zhTextState", "zhText"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.lineIndexState, row.lineIndex,
                        row.textState, row.text, row.zhTextState, row.zhText));
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
            case "wf_official_ability_owner_localization_abilities_items" -> {
                QOfficialAbilityOwnerLocalizationAbilitiesItems row =
                    new QOfficialAbilityOwnerLocalizationAbilitiesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_text_icon_itemreferences_items" -> {
                QOfficialTextIconItemReferencesItems row = new QOfficialTextIconItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_upgrade_localization_descriptions_items" -> {
                QOfficialUpgradeLocalizationDescriptionsItems row =
                    new QOfficialUpgradeLocalizationDescriptionsItems(ROW);
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
            case "wf_official_ability_owner_localization_abilities_items" -> {
                QOfficialAbilityOwnerLocalizationAbilitiesItems row =
                    new QOfficialAbilityOwnerLocalizationAbilitiesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_image_root" -> {
                QOfficialImageRoot row = new QOfficialImageRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_item_localization_root" -> {
                QOfficialItemLocalizationRoot row = new QOfficialItemLocalizationRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_language_string_root" -> {
                QOfficialLanguageStringRoot row = new QOfficialLanguageStringRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_text_icon_root" -> {
                QOfficialTextIconRoot row = new QOfficialTextIconRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_text_icon_itemreferences_items" -> {
                QOfficialTextIconItemReferencesItems row = new QOfficialTextIconItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_upgrade_localization_root" -> {
                QOfficialUpgradeLocalizationRoot row = new QOfficialUpgradeLocalizationRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_upgrade_localization_descriptions_items" -> {
                QOfficialUpgradeLocalizationDescriptionsItems row =
                    new QOfficialUpgradeLocalizationDescriptionsItems(ROW);
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
