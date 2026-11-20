package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenDefinitionUpgradeEntriesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenDefinitionUpgradeEntriesItemsUpgradeValuesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenWeaponRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenWeaponMarketListingsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.upgrades.QOfficialRivenWeaponUnresolvedMarketWeaponsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialCodexRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialCodexItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialCodexSecretTransmissionItemReferencesItems;

/** 官方关系表 QueryDSL 标量投影分片 26；由模型生成器维护。 */
final class OfficialRelationQueryDslPart26 {
    private OfficialRelationQueryDslPart26() {}

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
            case "wf_official_riven_definition_upgradeentries_items" -> {
                QOfficialRivenDefinitionUpgradeEntriesItems row = new QOfficialRivenDefinitionUpgradeEntriesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "prefixTagState", "prefixTag", "suffixTagState", "suffixTag",
                        "tagState", "tag", "upgradeValuesState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.prefixTagState, row.prefixTag,
                        row.suffixTagState, row.suffixTag, row.tagState, row.tag, row.upgradeValuesState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_riven_definition_upgradeentries_items_up_8ca075a8e7" -> {
                QOfficialRivenDefinitionUpgradeEntriesItemsUpgradeValuesItems row =
                    new QOfficialRivenDefinitionUpgradeEntriesItemsUpgradeValuesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "locTagState", "locTag", "locTagZhState", "locTagZh",
                        "reverseValueSymbolState", "reverseValueSymbol", "valueState", "value"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.locTagState, row.locTag,
                        row.locTagZhState, row.locTagZh, row.reverseValueSymbolState, row.reverseValueSymbol,
                        row.valueState, row.value));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_riven_weapon_root" -> {
                QOfficialRivenWeaponRoot row = new QOfficialRivenWeaponRoot(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState",
                    "dispositionState", "disposition", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl",
                    "marketListingsState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh",
                    "qualityDispositionCountState", "qualityDispositionCount", "qualityMarketListingCountState",
                    "qualityMarketListingCount", "qualityMarketListingsAttachedState", "qualityMarketListingsAttached",
                    "qualityMarketListingsWithDispositionState", "qualityMarketListingsWithDisposition",
                    "qualityMarketListingsWithoutDispositionState", "qualityMarketListingsWithoutDisposition",
                    "qualityUnresolvedMarketWeaponsState", "qualityUnresolvedMarketWeapons", "qualityWeaponCountState",
                    "qualityWeaponCount", "uniqueNameState", "uniqueName", "unresolvedMarketWeaponsState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.dispositionState, row.disposition,
                        row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.marketListingsState,
                        row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh,
                        row.qualityDispositionCountState, row.qualityDispositionCount,
                        row.qualityMarketListingCountState, row.qualityMarketListingCount,
                        row.qualityMarketListingsAttachedState, row.qualityMarketListingsAttached,
                        row.qualityMarketListingsWithDispositionState, row.qualityMarketListingsWithDisposition,
                        row.qualityMarketListingsWithoutDispositionState, row.qualityMarketListingsWithoutDisposition,
                        row.qualityUnresolvedMarketWeaponsState, row.qualityUnresolvedMarketWeapons,
                        row.qualityWeaponCountState, row.qualityWeaponCount, row.uniqueNameState, row.uniqueName,
                        row.unresolvedMarketWeaponsState));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_riven_weapon_marketlistings_items" -> {
                QOfficialRivenWeaponMarketListingsItems row = new QOfficialRivenWeaponMarketListingsItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "dispositionState", "disposition", "gameRefState", "gameRef",
                        "groupState", "group", "iconState", "icon", "iconStatusState", "iconStatus", "iconUrlState",
                        "iconUrl", "idState", "idValue", "nameState", "name", "nameEnState", "nameEn", "recordKeyState",
                        "recordKey", "reqMasteryRankState", "reqMasteryRank", "rivenTypeState", "rivenType",
                        "slugState", "slug", "thumbState", "thumb", "urlNameState", "urlName", "zhIconState",
                        "zhNameState", "zhName", "zhThumbState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.dispositionState, row.disposition,
                        row.gameRefState, row.gameRef, row.groupState, row.group, row.iconState, row.icon,
                        row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.idState, row.idValue,
                        row.nameState, row.name, row.nameEnState, row.nameEn, row.recordKeyState, row.recordKey,
                        row.reqMasteryRankState, row.reqMasteryRank, row.rivenTypeState, row.rivenType, row.slugState,
                        row.slug, row.thumbState, row.thumb, row.urlNameState, row.urlName, row.zhIconState,
                        row.zhNameState, row.zhName, row.zhThumbState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_riven_weapon_unresolvedmarketweapons_items" -> {
                QOfficialRivenWeaponUnresolvedMarketWeaponsItems row =
                    new QOfficialRivenWeaponUnresolvedMarketWeaponsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_codex_root" -> {
                QOfficialCodexRoot row = new QOfficialCodexRoot(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "descriptionState", "descriptionValue", "descriptionEnState",
                        "descriptionEn", "descriptionZhState", "descriptionZh", "iconState", "icon", "iconAssetState",
                        "iconAsset", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "imageState", "image",
                        "itemReferencesState", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh",
                        "reqScansState", "reqScans", "secretState", "secret", "secretTransmissionItemReferencesState",
                        "secretTransmissionSoundState", "secretTransmissionSound", "secretTransmissionTextState",
                        "secretTransmissionText", "secretTransmissionTextEnState", "secretTransmissionTextEn",
                        "secretTransmissionTextZhState", "secretTransmissionTextZh", "songState", "song", "suitState",
                        "suit"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.descriptionState, row.descriptionValue,
                        row.descriptionEnState, row.descriptionEn, row.descriptionZhState, row.descriptionZh,
                        row.iconState, row.icon, row.iconAssetState, row.iconAsset, row.iconStatusState, row.iconStatus,
                        row.iconUrlState, row.iconUrl, row.imageState, row.image, row.itemReferencesState,
                        row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh,
                        row.reqScansState, row.reqScans, row.secretState, row.secret,
                        row.secretTransmissionItemReferencesState, row.secretTransmissionSoundState,
                        row.secretTransmissionSound, row.secretTransmissionTextState, row.secretTransmissionText,
                        row.secretTransmissionTextEnState, row.secretTransmissionTextEn,
                        row.secretTransmissionTextZhState, row.secretTransmissionTextZh, row.songState, row.song,
                        row.suitState, row.suit));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_codex_itemreferences_items" -> {
                QOfficialCodexItemReferencesItems row = new QOfficialCodexItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(
                    List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field",
                        "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState",
                        "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState",
                        "resolution", "uniqueNameState", "uniqueName"),
                    List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState,
                        row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState,
                        row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState,
                        row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_codex_secrettransmission_itemreferences_items" -> {
                QOfficialCodexSecretTransmissionItemReferencesItems row =
                    new QOfficialCodexSecretTransmissionItemReferencesItems(ROW);
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
            case "wf_official_riven_definition_upgradeentries_items" -> {
                QOfficialRivenDefinitionUpgradeEntriesItems row = new QOfficialRivenDefinitionUpgradeEntriesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_riven_definition_upgradeentries_items_up_8ca075a8e7" -> {
                QOfficialRivenDefinitionUpgradeEntriesItemsUpgradeValuesItems row =
                    new QOfficialRivenDefinitionUpgradeEntriesItemsUpgradeValuesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_riven_weapon_marketlistings_items" -> {
                QOfficialRivenWeaponMarketListingsItems row = new QOfficialRivenWeaponMarketListingsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_riven_weapon_unresolvedmarketweapons_items" -> {
                QOfficialRivenWeaponUnresolvedMarketWeaponsItems row =
                    new QOfficialRivenWeaponUnresolvedMarketWeaponsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_codex_itemreferences_items" -> {
                QOfficialCodexItemReferencesItems row = new QOfficialCodexItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_codex_secrettransmission_itemreferences_items" -> {
                QOfficialCodexSecretTransmissionItemReferencesItems row =
                    new QOfficialCodexSecretTransmissionItemReferencesItems(ROW);
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
            case "wf_official_riven_definition_upgradeentries_items" -> {
                QOfficialRivenDefinitionUpgradeEntriesItems row = new QOfficialRivenDefinitionUpgradeEntriesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_definition_upgradeentries_items_up_8ca075a8e7" -> {
                QOfficialRivenDefinitionUpgradeEntriesItemsUpgradeValuesItems row =
                    new QOfficialRivenDefinitionUpgradeEntriesItemsUpgradeValuesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_weapon_root" -> {
                QOfficialRivenWeaponRoot row = new QOfficialRivenWeaponRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_weapon_marketlistings_items" -> {
                QOfficialRivenWeaponMarketListingsItems row = new QOfficialRivenWeaponMarketListingsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_riven_weapon_unresolvedmarketweapons_items" -> {
                QOfficialRivenWeaponUnresolvedMarketWeaponsItems row =
                    new QOfficialRivenWeaponUnresolvedMarketWeaponsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_codex_root" -> {
                QOfficialCodexRoot row = new QOfficialCodexRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_codex_itemreferences_items" -> {
                QOfficialCodexItemReferencesItems row = new QOfficialCodexItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_codex_secrettransmission_itemreferences_items" -> {
                QOfficialCodexSecretTransmissionItemReferencesItems row =
                    new QOfficialCodexSecretTransmissionItemReferencesItems(ROW);
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
