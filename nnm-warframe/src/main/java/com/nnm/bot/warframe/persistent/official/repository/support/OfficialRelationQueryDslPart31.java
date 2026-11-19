package com.nnm.bot.warframe.persistent.official.repository.support;

import static com.nnm.bot.warframe.persistent.official.constant.OfficialQueryAliases.ROW;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysMissionRewardItemReferencesItemsRotationsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysMissionRewardItemReferencesItemsRotationsItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysMissionRewardItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysRewardsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysRewardsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionRoot;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionChallengesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionCustomAdvancedSpawnersItems;

/** 官方关系表 QueryDSL 标量投影分片 31；由模型生成器维护。 */
final class OfficialRelationQueryDslPart31 {
    private OfficialRelationQueryDslPart31() { }

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
            case "wf_official_keys_missionreward_itemreferences_items__a46ffc0dfe" -> {
                QOfficialKeysMissionRewardItemReferencesItemsRotationsItems row = new QOfficialKeysMissionRewardItemReferencesItemsRotationsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_missionreward_itemreferences_items__7aa64fee7f" -> {
                QOfficialKeysMissionRewardItemReferencesItemsRotationsItemsItems row = new QOfficialKeysMissionRewardItemReferencesItemsRotationsItemsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemDomainState", "itemDomain", "itemIconStatusState", "itemIconStatus", "itemIconUrlState", "itemIconUrl", "itemNameState", "itemName", "itemNameEnState", "itemNameEn", "itemNameZhState", "itemNameZh", "itemResolutionState", "itemResolution", "itemUniqueNameState", "itemUniqueName", "itemCountState", "itemCount", "probabilityState", "probability", "rarityState", "rarity", "typeState", "type"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemDomainState, row.itemDomain, row.itemIconStatusState, row.itemIconStatus, row.itemIconUrlState, row.itemIconUrl, row.itemNameState, row.itemName, row.itemNameEnState, row.itemNameEn, row.itemNameZhState, row.itemNameZh, row.itemResolutionState, row.itemResolution, row.itemUniqueNameState, row.itemUniqueName, row.itemCountState, row.itemCount, row.probabilityState, row.probability, row.rarityState, row.rarity, row.typeState, row.type));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_missionreward_items_items" -> {
                QOfficialKeysMissionRewardItemsItems row = new QOfficialKeysMissionRewardItemsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_rewards_items" -> {
                QOfficialKeysRewardsItems row = new QOfficialKeysRewardsItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "amountState", "amount", "itemReferencesState", "itemTypeState", "itemType", "rewardTypeState", "rewardType"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.amountState, row.amount, row.itemReferencesState, row.itemTypeState, row.itemType, row.rewardTypeState, row.rewardType));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_rewards_items_itemreferences_items" -> {
                QOfficialKeysRewardsItemsItemReferencesItems row = new QOfficialKeysRewardsItemsItemReferencesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_root" -> {
                QOfficialRegionRoot row = new QOfficialRegionRoot(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "cacheRewardManifestState", "cacheRewardManifest", "challengesState", "customAdvancedSpawnersState", "darkSectorDataResourceBonusState", "darkSectorDataResourceBonus", "darkSectorDataWeaponXpBonusForState", "darkSectorDataWeaponXpBonusFor", "darkSectorDataWeaponXpBonusValState", "darkSectorDataWeaponXpBonusVal", "darkSectorDataXpBonusState", "darkSectorDataXpBonus", "enemySpecState", "enemySpec", "extraEnemySpecState", "extraEnemySpec", "factionState", "faction", "factionNameState", "factionName", "factionNameZhState", "factionNameZh", "foundersState", "hiddenState", "hidden", "iconStatusState", "iconStatus", "itemReferencesState", "levelOverrideState", "levelOverride", "masteryExpState", "masteryExp", "masteryReqState", "masteryReq", "maxEnemyLevelState", "maxEnemyLevel", "minEnemyLevelState", "minEnemyLevel", "miscItemFeeItemCountState", "miscItemFeeItemCount", "miscItemFeeItemReferencesState", "miscItemFeeItemTypeState", "miscItemFeeItemType", "missionNameState", "missionName", "missionNameEnState", "missionNameEn", "missionNameKeyState", "missionNameKey", "missionNameZhState", "missionNameZh", "missionRewardCountedItemsState", "missionRewardCreditsState", "missionRewardCredits", "missionRewardItemsState", "missionTypeState", "missionType", "missionTypeNameState", "missionTypeName", "missionTypeNameZhState", "missionTypeNameZh", "nameState", "name", "nameEnState", "nameEn", "nameKeyState", "nameKey", "nextNodesState", "nodeCountState", "nodeCount", "nodeKeyState", "nodeKey", "nodeTypeState", "nodeType", "questReqState", "questReq", "questReqsState", "rewardManifestsState", "secondaryFactionState", "secondaryFaction", "secondaryFactionNameState", "secondaryFactionName", "secondaryFactionNameZhState", "secondaryFactionNameZh", "systemCountState", "systemCount", "systemIndexState", "systemIndex", "systemNameState", "systemName", "systemNameKeyState", "systemNameKey", "systemsState", "tilesetState", "tileset", "uniqueNameState", "uniqueName", "vipAgentState", "vipAgent", "zhNameState", "zhName", "zhSystemNameState", "zhSystemName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.cacheRewardManifestState, row.cacheRewardManifest, row.challengesState, row.customAdvancedSpawnersState, row.darkSectorDataResourceBonusState, row.darkSectorDataResourceBonus, row.darkSectorDataWeaponXpBonusForState, row.darkSectorDataWeaponXpBonusFor, row.darkSectorDataWeaponXpBonusValState, row.darkSectorDataWeaponXpBonusVal, row.darkSectorDataXpBonusState, row.darkSectorDataXpBonus, row.enemySpecState, row.enemySpec, row.extraEnemySpecState, row.extraEnemySpec, row.factionState, row.faction, row.factionNameState, row.factionName, row.factionNameZhState, row.factionNameZh, row.foundersState, row.hiddenState, row.hidden, row.iconStatusState, row.iconStatus, row.itemReferencesState, row.levelOverrideState, row.levelOverride, row.masteryExpState, row.masteryExp, row.masteryReqState, row.masteryReq, row.maxEnemyLevelState, row.maxEnemyLevel, row.minEnemyLevelState, row.minEnemyLevel, row.miscItemFeeItemCountState, row.miscItemFeeItemCount, row.miscItemFeeItemReferencesState, row.miscItemFeeItemTypeState, row.miscItemFeeItemType, row.missionNameState, row.missionName, row.missionNameEnState, row.missionNameEn, row.missionNameKeyState, row.missionNameKey, row.missionNameZhState, row.missionNameZh, row.missionRewardCountedItemsState, row.missionRewardCreditsState, row.missionRewardCredits, row.missionRewardItemsState, row.missionTypeState, row.missionType, row.missionTypeNameState, row.missionTypeName, row.missionTypeNameZhState, row.missionTypeNameZh, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameKeyState, row.nameKey, row.nextNodesState, row.nodeCountState, row.nodeCount, row.nodeKeyState, row.nodeKey, row.nodeTypeState, row.nodeType, row.questReqState, row.questReq, row.questReqsState, row.rewardManifestsState, row.secondaryFactionState, row.secondaryFaction, row.secondaryFactionNameState, row.secondaryFactionName, row.secondaryFactionNameZhState, row.secondaryFactionNameZh, row.systemCountState, row.systemCount, row.systemIndexState, row.systemIndex, row.systemNameState, row.systemName, row.systemNameKeyState, row.systemNameKey, row.systemsState, row.tilesetState, row.tileset, row.uniqueNameState, row.uniqueName, row.vipAgentState, row.vipAgent, row.zhNameState, row.zhName, row.zhSystemNameState, row.zhSystemName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_region_challenges_items" -> {
                QOfficialRegionChallengesItems row = new QOfficialRegionChallengesItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_customadvancedspawners_items" -> {
                QOfficialRegionCustomAdvancedSpawnersItems row = new QOfficialRegionCustomAdvancedSpawnersItems(ROW);
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
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
            case "wf_official_keys_missionreward_itemreferences_items__a46ffc0dfe" -> {
                QOfficialKeysMissionRewardItemReferencesItemsRotationsItems row = new QOfficialKeysMissionRewardItemReferencesItemsRotationsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_missionreward_itemreferences_items__7aa64fee7f" -> {
                QOfficialKeysMissionRewardItemReferencesItemsRotationsItemsItems row = new QOfficialKeysMissionRewardItemReferencesItemsRotationsItemsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_missionreward_items_items" -> {
                QOfficialKeysMissionRewardItemsItems row = new QOfficialKeysMissionRewardItemsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_rewards_items" -> {
                QOfficialKeysRewardsItems row = new QOfficialKeysRewardsItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_rewards_items_itemreferences_items" -> {
                QOfficialKeysRewardsItemsItemReferencesItems row = new QOfficialKeysRewardsItemsItemReferencesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_challenges_items" -> {
                QOfficialRegionChallengesItems row = new QOfficialRegionChallengesItems(ROW);
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_customadvancedspawners_items" -> {
                QOfficialRegionCustomAdvancedSpawnersItems row = new QOfficialRegionCustomAdvancedSpawnersItems(ROW);
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
            case "wf_official_keys_missionreward_itemreferences_items__a46ffc0dfe" -> {
                QOfficialKeysMissionRewardItemReferencesItemsRotationsItems row = new QOfficialKeysMissionRewardItemReferencesItemsRotationsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_missionreward_itemreferences_items__7aa64fee7f" -> {
                QOfficialKeysMissionRewardItemReferencesItemsRotationsItemsItems row = new QOfficialKeysMissionRewardItemReferencesItemsRotationsItemsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_missionreward_items_items" -> {
                QOfficialKeysMissionRewardItemsItems row = new QOfficialKeysMissionRewardItemsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_rewards_items" -> {
                QOfficialKeysRewardsItems row = new QOfficialKeysRewardsItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_rewards_items_itemreferences_items" -> {
                QOfficialKeysRewardsItemsItemReferencesItems row = new QOfficialKeysRewardsItemsItemReferencesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_root" -> {
                QOfficialRegionRoot row = new QOfficialRegionRoot(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_challenges_items" -> {
                QOfficialRegionChallengesItems row = new QOfficialRegionChallengesItems(ROW);
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_customadvancedspawners_items" -> {
                QOfficialRegionCustomAdvancedSpawnersItems row = new QOfficialRegionCustomAdvancedSpawnersItems(ROW);
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
