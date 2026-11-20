package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenAttributeOfficialEntriesItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenAttributeOfficialEntriesItemsUpgradeValuesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenAttributeTemplatesZhItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenDefinitionRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenDefinitionAvailableChallengesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenDefinitionAvailableChallengesItemsItemReferencesItems;

/** 官方关系表 QueryDSL 标量投影分片 25；由模型生成器维护。 */
final class OfficialRelationQueryDslPart25 {
    private OfficialRelationQueryDslPart25() {}

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
            case "wf_official_riven_attribute_officialentries_items_it_79b125e9c1" -> {
                QOfficialRivenAttributeOfficialEntriesItemsItemReferencesItems row =
                    new QOfficialRivenAttributeOfficialEntriesItemsItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field",
                        "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState",
                        "nameEn", "nameZhState", "nameZh", "resolutionState", "resolution", "uniqueNameState",
                        "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState,
                        row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState,
                        row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.resolutionState,
                        row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_riven_attribute_officialentries_items_up_66a2e31130" -> {
                QOfficialRivenAttributeOfficialEntriesItemsUpgradeValuesItems row =
                    new QOfficialRivenAttributeOfficialEntriesItemsUpgradeValuesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "locTagState", "locTag", "locTagZhState", "locTagZh",
                        "reverseValueSymbolState", "reverseValueSymbol", "valueState", "value"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.locTagState, row.locTag,
                        row.locTagZhState, row.locTagZh, row.reverseValueSymbolState, row.reverseValueSymbol,
                        row.valueState, row.value));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_riven_attribute_templateszh_items" -> {
                QOfficialRivenAttributeTemplatesZhItems row = new QOfficialRivenAttributeTemplatesZhItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_riven_definition_root" -> {
                QOfficialRivenDefinitionRoot row = new QOfficialRivenDefinitionRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "availableChallengesState", "baseDrainState", "baseDrain",
                        "codexSecretState", "codexSecret", "fusionLimitState", "fusionLimit", "iconStatusState",
                        "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn",
                        "nameZhState", "nameZh", "polarityState", "polarity", "rarityState", "rarity", "recordKeyState",
                        "recordKey", "uniqueNameState", "uniqueName", "upgradeEntriesState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.availableChallengesState,
                        row.baseDrainState, row.baseDrain, row.codexSecretState, row.codexSecret, row.fusionLimitState,
                        row.fusionLimit, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl,
                        row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh,
                        row.polarityState, row.polarity, row.rarityState, row.rarity, row.recordKeyState, row.recordKey,
                        row.uniqueNameState, row.uniqueName, row.upgradeEntriesState));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_riven_definition_availablechallenges_items" -> {
                QOfficialRivenDefinitionAvailableChallengesItems row =
                    new QOfficialRivenDefinitionAvailableChallengesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "complicationsState", "descriptionState", "description",
                        "descriptionZhState", "descriptionZh", "fullNameState", "fullName", "itemReferencesState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.complicationsState,
                        row.descriptionState, row.description, row.descriptionZhState, row.descriptionZh,
                        row.fullNameState, row.fullName, row.itemReferencesState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_riven_definition_availablechallenges_ite_60794493f7" -> {
                QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItems row =
                    new QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "descriptionState", "description", "descriptionZhState",
                        "descriptionZh", "fullNameState", "fullName", "itemReferencesState", "overrideTagState",
                        "overrideTag"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.descriptionState, row.description,
                        row.descriptionZhState, row.descriptionZh, row.fullNameState, row.fullName,
                        row.itemReferencesState, row.overrideTagState, row.overrideTag));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_riven_definition_availablechallenges_ite_915f85dea9" -> {
                QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItemsItemReferencesItems row =
                    new QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItemsItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "fieldState", "field", "iconStatusState", "iconStatus",
                        "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState",
                        "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.fieldState, row.field,
                        row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn,
                        row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState,
                        row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_riven_definition_availablechallenges_ite_0751326f03" -> {
                QOfficialRivenDefinitionAvailableChallengesItemsItemReferencesItems row =
                    new QOfficialRivenDefinitionAvailableChallengesItemsItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "fieldState", "field", "iconStatusState", "iconStatus",
                        "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState",
                        "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.fieldState, row.field,
                        row.iconStatusState, row.iconStatus, row.nameState, row.name, row.nameEnState, row.nameEn,
                        row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState,
                        row.resolution, row.uniqueNameState, row.uniqueName));
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
            case "wf_official_riven_attribute_officialentries_items_it_79b125e9c1" -> {
                QOfficialRivenAttributeOfficialEntriesItemsItemReferencesItems row =
                    new QOfficialRivenAttributeOfficialEntriesItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_riven_attribute_officialentries_items_up_66a2e31130" -> {
                QOfficialRivenAttributeOfficialEntriesItemsUpgradeValuesItems row =
                    new QOfficialRivenAttributeOfficialEntriesItemsUpgradeValuesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_riven_attribute_templateszh_items" -> {
                QOfficialRivenAttributeTemplatesZhItems row = new QOfficialRivenAttributeTemplatesZhItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_riven_definition_availablechallenges_items" -> {
                QOfficialRivenDefinitionAvailableChallengesItems row =
                    new QOfficialRivenDefinitionAvailableChallengesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_riven_definition_availablechallenges_ite_60794493f7" -> {
                QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItems row =
                    new QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_riven_definition_availablechallenges_ite_915f85dea9" -> {
                QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItemsItemReferencesItems row =
                    new QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_riven_definition_availablechallenges_ite_0751326f03" -> {
                QOfficialRivenDefinitionAvailableChallengesItemsItemReferencesItems row =
                    new QOfficialRivenDefinitionAvailableChallengesItemsItemReferencesItems(ROW);
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
            case "wf_official_riven_attribute_officialentries_items_it_79b125e9c1" -> {
                QOfficialRivenAttributeOfficialEntriesItemsItemReferencesItems row =
                    new QOfficialRivenAttributeOfficialEntriesItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_attribute_officialentries_items_up_66a2e31130" -> {
                QOfficialRivenAttributeOfficialEntriesItemsUpgradeValuesItems row =
                    new QOfficialRivenAttributeOfficialEntriesItemsUpgradeValuesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_attribute_templateszh_items" -> {
                QOfficialRivenAttributeTemplatesZhItems row = new QOfficialRivenAttributeTemplatesZhItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_definition_root" -> {
                QOfficialRivenDefinitionRoot row = new QOfficialRivenDefinitionRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_definition_availablechallenges_items" -> {
                QOfficialRivenDefinitionAvailableChallengesItems row =
                    new QOfficialRivenDefinitionAvailableChallengesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_definition_availablechallenges_ite_60794493f7" -> {
                QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItems row =
                    new QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_definition_availablechallenges_ite_915f85dea9" -> {
                QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItemsItemReferencesItems row =
                    new QOfficialRivenDefinitionAvailableChallengesItemsComplicationsItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_definition_availablechallenges_ite_0751326f03" -> {
                QOfficialRivenDefinitionAvailableChallengesItemsItemReferencesItems row =
                    new QOfficialRivenDefinitionAvailableChallengesItemsItemReferencesItems(ROW);
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
