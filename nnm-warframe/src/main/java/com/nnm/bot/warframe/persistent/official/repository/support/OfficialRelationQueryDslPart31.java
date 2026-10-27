package com.nnm.bot.warframe.persistent.official.repository.support;

import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysMissionRewardItemReferencesItemsRotationsItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysRewardsItemsItemReferencesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysMissionRewardItemReferencesItemsRotationsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysMissionRewardItemsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialKeysRewardsItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionChallengesItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionCustomAdvancedSpawnersItems;
import com.nnm.bot.warframe.persistent.official.pojo.entity.world.QOfficialRegionRoot;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialRelationRowDTO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import java.util.List;

/** 官方关系表 QueryDSL 标量投影分片 31；由模型生成器维护。 */
final class OfficialRelationQueryDslPart31 {
    private OfficialRelationQueryDslPart31() { }

    static List<OfficialRelationRowDTO> select(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_keys_missionreward_itemreferences_items__a46ffc0dfe" -> {
                QOfficialKeysMissionRewardItemReferencesItemsRotationsItems row = new QOfficialKeysMissionRewardItemReferencesItemsRotationsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_missionreward_itemreferences_items__7aa64fee7f" -> {
                QOfficialKeysMissionRewardItemReferencesItemsRotationsItemsItems row = new QOfficialKeysMissionRewardItemReferencesItemsRotationsItemsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "itemDomainState", "itemDomain", "itemIconStatusState", "itemIconStatus", "itemIconUrlState", "itemIconUrl", "itemNameState", "itemName", "itemNameEnState", "itemNameEn", "itemNameZhState", "itemNameZh", "itemResolutionState", "itemResolution", "itemUniqueNameState", "itemUniqueName", "itemCountState", "itemCount", "probabilityState", "probability", "rarityState", "rarity", "typeState", "type"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.itemDomainState, row.itemDomain, row.itemIconStatusState, row.itemIconStatus, row.itemIconUrlState, row.itemIconUrl, row.itemNameState, row.itemName, row.itemNameEnState, row.itemNameEn, row.itemNameZhState, row.itemNameZh, row.itemResolutionState, row.itemResolution, row.itemUniqueNameState, row.itemUniqueName, row.itemCountState, row.itemCount, row.probabilityState, row.probability, row.rarityState, row.rarity, row.typeState, row.type));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_missionreward_items_items" -> {
                QOfficialKeysMissionRewardItemsItems row = new QOfficialKeysMissionRewardItemsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_rewards_items" -> {
                QOfficialKeysRewardsItems row = new QOfficialKeysRewardsItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "amountState", "amount", "itemReferencesState", "itemTypeState", "itemType", "rewardTypeState", "rewardType"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.amountState, row.amount, row.itemReferencesState, row.itemTypeState, row.itemType, row.rewardTypeState, row.rewardType));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_keys_rewards_items_itemreferences_items" -> {
                QOfficialKeysRewardsItemsItemReferencesItems row = new QOfficialKeysRewardsItemsItemReferencesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "domainState", "domain", "fieldState", "field", "iconStatusState", "iconStatus", "iconUrlState", "iconUrl", "nameState", "name", "nameEnState", "nameEn", "nameZhState", "nameZh", "referenceKindState", "referenceKind", "resolutionState", "resolution", "uniqueNameState", "uniqueName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.domainState, row.domain, row.fieldState, row.field, row.iconStatusState, row.iconStatus, row.iconUrlState, row.iconUrl, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameZhState, row.nameZh, row.referenceKindState, row.referenceKind, row.resolutionState, row.resolution, row.uniqueNameState, row.uniqueName));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_root" -> {
                QOfficialRegionRoot row = new QOfficialRegionRoot("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "cacheRewardManifestState", "cacheRewardManifest", "challengesState", "customAdvancedSpawnersState", "darkSectorDataResourceBonusState", "darkSectorDataResourceBonus", "darkSectorDataWeaponXpBonusForState", "darkSectorDataWeaponXpBonusFor", "darkSectorDataWeaponXpBonusValState", "darkSectorDataWeaponXpBonusVal", "darkSectorDataXpBonusState", "darkSectorDataXpBonus", "enemySpecState", "enemySpec", "extraEnemySpecState", "extraEnemySpec", "factionState", "faction", "factionNameState", "factionName", "factionNameZhState", "factionNameZh", "foundersState", "hiddenState", "hidden", "iconStatusState", "iconStatus", "itemReferencesState", "levelOverrideState", "levelOverride", "masteryExpState", "masteryExp", "masteryReqState", "masteryReq", "maxEnemyLevelState", "maxEnemyLevel", "minEnemyLevelState", "minEnemyLevel", "miscItemFeeItemCountState", "miscItemFeeItemCount", "miscItemFeeItemReferencesState", "miscItemFeeItemTypeState", "miscItemFeeItemType", "missionNameState", "missionName", "missionNameEnState", "missionNameEn", "missionNameKeyState", "missionNameKey", "missionNameZhState", "missionNameZh", "missionRewardCountedItemsState", "missionRewardCreditsState", "missionRewardCredits", "missionRewardItemsState", "missionTypeState", "missionType", "missionTypeNameState", "missionTypeName", "missionTypeNameZhState", "missionTypeNameZh", "nameState", "name", "nameEnState", "nameEn", "nameKeyState", "nameKey", "nextNodesState", "nodeCountState", "nodeCount", "nodeKeyState", "nodeKey", "nodeTypeState", "nodeType", "questReqState", "questReq", "questReqsState", "rewardManifestsState", "secondaryFactionState", "secondaryFaction", "secondaryFactionNameState", "secondaryFactionName", "secondaryFactionNameZhState", "secondaryFactionNameZh", "systemCountState", "systemCount", "systemIndexState", "systemIndex", "systemNameState", "systemName", "systemNameKeyState", "systemNameKey", "systemsState", "tilesetState", "tileset", "uniqueNameState", "uniqueName", "vipAgentState", "vipAgent", "zhNameState", "zhName", "zhSystemNameState", "zhSystemName"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.cacheRewardManifestState, row.cacheRewardManifest, row.challengesState, row.customAdvancedSpawnersState, row.darkSectorDataResourceBonusState, row.darkSectorDataResourceBonus, row.darkSectorDataWeaponXpBonusForState, row.darkSectorDataWeaponXpBonusFor, row.darkSectorDataWeaponXpBonusValState, row.darkSectorDataWeaponXpBonusVal, row.darkSectorDataXpBonusState, row.darkSectorDataXpBonus, row.enemySpecState, row.enemySpec, row.extraEnemySpecState, row.extraEnemySpec, row.factionState, row.faction, row.factionNameState, row.factionName, row.factionNameZhState, row.factionNameZh, row.foundersState, row.hiddenState, row.hidden, row.iconStatusState, row.iconStatus, row.itemReferencesState, row.levelOverrideState, row.levelOverride, row.masteryExpState, row.masteryExp, row.masteryReqState, row.masteryReq, row.maxEnemyLevelState, row.maxEnemyLevel, row.minEnemyLevelState, row.minEnemyLevel, row.miscItemFeeItemCountState, row.miscItemFeeItemCount, row.miscItemFeeItemReferencesState, row.miscItemFeeItemTypeState, row.miscItemFeeItemType, row.missionNameState, row.missionName, row.missionNameEnState, row.missionNameEn, row.missionNameKeyState, row.missionNameKey, row.missionNameZhState, row.missionNameZh, row.missionRewardCountedItemsState, row.missionRewardCreditsState, row.missionRewardCredits, row.missionRewardItemsState, row.missionTypeState, row.missionType, row.missionTypeNameState, row.missionTypeName, row.missionTypeNameZhState, row.missionTypeNameZh, row.nameState, row.name, row.nameEnState, row.nameEn, row.nameKeyState, row.nameKey, row.nextNodesState, row.nodeCountState, row.nodeCount, row.nodeKeyState, row.nodeKey, row.nodeTypeState, row.nodeType, row.questReqState, row.questReq, row.questReqsState, row.rewardManifestsState, row.secondaryFactionState, row.secondaryFaction, row.secondaryFactionNameState, row.secondaryFactionName, row.secondaryFactionNameZhState, row.secondaryFactionNameZh, row.systemCountState, row.systemCount, row.systemIndexState, row.systemIndex, row.systemNameState, row.systemName, row.systemNameKeyState, row.systemNameKey, row.systemsState, row.tilesetState, row.tileset, row.uniqueNameState, row.uniqueName, row.vipAgentState, row.vipAgent, row.zhNameState, row.zhName, row.zhSystemNameState, row.zhSystemName));
                yield selectRoot(query, row, row.id, ids, projection);
            }
            case "wf_official_region_challenges_items" -> {
                QOfficialRegionChallengesItems row = new QOfficialRegionChallengesItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            case "wf_official_region_customadvancedspawners_items" -> {
                QOfficialRegionCustomAdvancedSpawnersItems row = new QOfficialRegionCustomAdvancedSpawnersItems("row");
                var projection = new OfficialRelationRowProjection(List.of("id", "bkey", "rootState", "root"),
                        List.<Expression<?>>of(row.id, row.bkey, row.rootState, row.root));
                yield selectChildren(query, row, row.id, row.bkey, row.sortOrder, ids, projection);
            }
            default -> throw new IllegalArgumentException("Unsupported official relation table: " + table);
        };
    }

    static List<Long> selectChildIds(JPAQueryFactory query, String table, Collection<Long> parentIds) {
        return switch (table) {
            case "wf_official_keys_missionreward_itemreferences_items__a46ffc0dfe" -> {
                QOfficialKeysMissionRewardItemReferencesItemsRotationsItems row = new QOfficialKeysMissionRewardItemReferencesItemsRotationsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_missionreward_itemreferences_items__7aa64fee7f" -> {
                QOfficialKeysMissionRewardItemReferencesItemsRotationsItemsItems row = new QOfficialKeysMissionRewardItemReferencesItemsRotationsItemsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_missionreward_items_items" -> {
                QOfficialKeysMissionRewardItemsItems row = new QOfficialKeysMissionRewardItemsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_rewards_items" -> {
                QOfficialKeysRewardsItems row = new QOfficialKeysRewardsItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_keys_rewards_items_itemreferences_items" -> {
                QOfficialKeysRewardsItemsItemReferencesItems row = new QOfficialKeysRewardsItemsItemReferencesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_challenges_items" -> {
                QOfficialRegionChallengesItems row = new QOfficialRegionChallengesItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            case "wf_official_region_customadvancedspawners_items" -> {
                QOfficialRegionCustomAdvancedSpawnersItems row = new QOfficialRegionCustomAdvancedSpawnersItems("row");
                yield query.select(row.id).from(row).where(row.bkey.in(parentIds)).fetch();
            }
            default -> throw new IllegalArgumentException("Not an official child table: " + table);
        };
    }

    static long deleteByIds(JPAQueryFactory query, String table, Collection<Long> ids) {
        return switch (table) {
            case "wf_official_keys_missionreward_itemreferences_items__a46ffc0dfe" -> {
                QOfficialKeysMissionRewardItemReferencesItemsRotationsItems row = new QOfficialKeysMissionRewardItemReferencesItemsRotationsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_missionreward_itemreferences_items__7aa64fee7f" -> {
                QOfficialKeysMissionRewardItemReferencesItemsRotationsItemsItems row = new QOfficialKeysMissionRewardItemReferencesItemsRotationsItemsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_missionreward_items_items" -> {
                QOfficialKeysMissionRewardItemsItems row = new QOfficialKeysMissionRewardItemsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_rewards_items" -> {
                QOfficialKeysRewardsItems row = new QOfficialKeysRewardsItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_keys_rewards_items_itemreferences_items" -> {
                QOfficialKeysRewardsItemsItemReferencesItems row = new QOfficialKeysRewardsItemsItemReferencesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_root" -> {
                QOfficialRegionRoot row = new QOfficialRegionRoot("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_challenges_items" -> {
                QOfficialRegionChallengesItems row = new QOfficialRegionChallengesItems("row");
                yield query.delete(row).where(row.id.in(ids)).execute();
            }
            case "wf_official_region_customadvancedspawners_items" -> {
                QOfficialRegionCustomAdvancedSpawnersItems row = new QOfficialRegionCustomAdvancedSpawnersItems("row");
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
