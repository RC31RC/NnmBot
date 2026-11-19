package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialUpgradeModSetValuesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialUpgradeRanksItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialUpgradeRanksItemsStatsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialUpgradeUpgradeEntriesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialUpgradeUpgradeEntriesItemsUpgradeValuesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenAttributeRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenAttributeExclusiveToItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenAttributeOfficialEntriesItems;

/** 官方关系表 QueryDSL 标量投影分片 24；由模型生成器维护。 */
final class OfficialRelationQueryDslPart24 {
    private OfficialRelationQueryDslPart24() { }

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
            case "wf_official_upgrade_modsetvalues_items" -> {
                QOfficialUpgradeModSetValuesItems row = new QOfficialUpgradeModSetValuesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_upgrade_ranks_items" -> {
                QOfficialUpgradeRanksItems row = new QOfficialUpgradeRanksItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "rankState", "rank", "statsState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.rankState, row.rank, row.statsState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_upgrade_ranks_items_stats_items" -> {
                QOfficialUpgradeRanksItemsStatsItems row = new QOfficialUpgradeRanksItemsStatsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "lineIndexState", "lineIndex", "textState", "text", "zhTextState", "zhText"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.lineIndexState, row.lineIndex, row.textState, row.text, row.zhTextState, row.zhText));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_upgrade_upgradeentries_items" -> {
                QOfficialUpgradeUpgradeEntriesItems row = new QOfficialUpgradeUpgradeEntriesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "prefixTagState", "prefixTag", "suffixTagState", "suffixTag", "tagState", "tag", "upgradeValuesState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.prefixTagState, row.prefixTag, row.suffixTagState, row.suffixTag, row.tagState, row.tag, row.upgradeValuesState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_upgrade_upgradeentries_items_upgradevalues_items" -> {
                QOfficialUpgradeUpgradeEntriesItemsUpgradeValuesItems row = new QOfficialUpgradeUpgradeEntriesItemsUpgradeValuesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "locTagState", "locTag", "reverseValueSymbolState", "reverseValueSymbol", "valueState", "value"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.locTagState, row.locTag, row.reverseValueSymbolState, row.reverseValueSymbol, row.valueState, row.value));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_riven_attribute_root" -> {
                QOfficialRivenAttributeRoot row = new QOfficialRivenAttributeRoot(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "definitionResolutionState", "definitionResolution", "effectState", "effect", "effectZhState", "effectZh", "exclusiveToState", "gameRefState", "gameRef", "groupState", "group", "iconReasonState", "iconReason", "iconStatusState", "iconStatus", "idState", "idValue", "idsState", "ids", "nameState", "name", "nameEnState", "nameEn", "negativeOnlyState", "negativeOnly", "negativeOnlyState1f16bbf25f", "negativeOnlyc24601d326", "officialEntriesState", "positiveIsNegativeState", "positiveIsNegative", "positiveIsNegativeState971bce76c4", "positiveIsNegative15b9913200", "positiveOnlyState", "positiveOnly", "prefixState", "prefix", "recordKeyState", "recordKey", "searchOnlyState", "searchOnly", "slugState", "slug", "suffixState", "suffix", "templatesZhState", "unitState", "unit", "unitsState", "units", "urlNameState", "urlName", "valueSemanticsState", "valueSemantics", "zhNameState", "zhName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.definitionResolutionState, row.definitionResolution, row.effectState, row.effect, row.effectZhState, row.effectZh, row.exclusiveToState, row.gameRefState, row.gameRef, row.groupState, row.group, row.iconReasonState, row.iconReason, row.iconStatusState, row.iconStatus, row.idState, row.idValue, row.idsState, row.ids, row.nameState, row.name, row.nameEnState, row.nameEn, row.negativeOnlyState, row.negativeOnly, row.negativeOnlyState1f16bbf25f, row.negativeOnlyc24601d326, row.officialEntriesState, row.positiveIsNegativeState, row.positiveIsNegative, row.positiveIsNegativeState971bce76c4, row.positiveIsNegative15b9913200, row.positiveOnlyState, row.positiveOnly, row.prefixState, row.prefix, row.recordKeyState, row.recordKey, row.searchOnlyState, row.searchOnly, row.slugState, row.slug, row.suffixState, row.suffix, row.templatesZhState, row.unitState, row.unit, row.unitsState, row.units, row.urlNameState, row.urlName, row.valueSemanticsState, row.valueSemantics, row.zhNameState, row.zhName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_riven_attribute_exclusiveto_items" -> {
                QOfficialRivenAttributeExclusiveToItems row = new QOfficialRivenAttributeExclusiveToItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_riven_attribute_officialentries_items" -> {
                QOfficialRivenAttributeOfficialEntriesItems row = new QOfficialRivenAttributeOfficialEntriesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemReferencesState", "parameterSemanticsState", "parameterSemantics", "prefixTagState", "prefixTag", "rivenNameState", "rivenName", "rivenNameZhState", "rivenNameZh", "rivenUniqueNameState", "rivenUniqueName", "suffixTagState", "suffixTag", "tagState", "tag", "upgradeValuesState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemReferencesState, row.parameterSemanticsState, row.parameterSemantics, row.prefixTagState, row.prefixTag, row.rivenNameState, row.rivenName, row.rivenNameZhState, row.rivenNameZh, row.rivenUniqueNameState, row.rivenUniqueName, row.suffixTagState, row.suffixTag, row.tagState, row.tag, row.upgradeValuesState));
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
            case "wf_official_upgrade_modsetvalues_items" -> {
                QOfficialUpgradeModSetValuesItems row = new QOfficialUpgradeModSetValuesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_upgrade_ranks_items" -> {
                QOfficialUpgradeRanksItems row = new QOfficialUpgradeRanksItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_upgrade_ranks_items_stats_items" -> {
                QOfficialUpgradeRanksItemsStatsItems row = new QOfficialUpgradeRanksItemsStatsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_upgrade_upgradeentries_items" -> {
                QOfficialUpgradeUpgradeEntriesItems row = new QOfficialUpgradeUpgradeEntriesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_upgrade_upgradeentries_items_upgradevalues_items" -> {
                QOfficialUpgradeUpgradeEntriesItemsUpgradeValuesItems row = new QOfficialUpgradeUpgradeEntriesItemsUpgradeValuesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_riven_attribute_exclusiveto_items" -> {
                QOfficialRivenAttributeExclusiveToItems row = new QOfficialRivenAttributeExclusiveToItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_riven_attribute_officialentries_items" -> {
                QOfficialRivenAttributeOfficialEntriesItems row = new QOfficialRivenAttributeOfficialEntriesItems(ROW);
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
            case "wf_official_upgrade_modsetvalues_items" -> {
                QOfficialUpgradeModSetValuesItems row = new QOfficialUpgradeModSetValuesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_upgrade_ranks_items" -> {
                QOfficialUpgradeRanksItems row = new QOfficialUpgradeRanksItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_upgrade_ranks_items_stats_items" -> {
                QOfficialUpgradeRanksItemsStatsItems row = new QOfficialUpgradeRanksItemsStatsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_upgrade_upgradeentries_items" -> {
                QOfficialUpgradeUpgradeEntriesItems row = new QOfficialUpgradeUpgradeEntriesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_upgrade_upgradeentries_items_upgradevalues_items" -> {
                QOfficialUpgradeUpgradeEntriesItemsUpgradeValuesItems row = new QOfficialUpgradeUpgradeEntriesItemsUpgradeValuesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_attribute_root" -> {
                QOfficialRivenAttributeRoot row = new QOfficialRivenAttributeRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_attribute_exclusiveto_items" -> {
                QOfficialRivenAttributeExclusiveToItems row = new QOfficialRivenAttributeExclusiveToItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_attribute_officialentries_items" -> {
                QOfficialRivenAttributeOfficialEntriesItems row = new QOfficialRivenAttributeOfficialEntriesItems(ROW);
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
