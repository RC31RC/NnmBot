package com.nnm.bot.warframe.application;

import com.nnm.bot.warframe.constant.WarframeReferenceType;
import com.nnm.bot.warframe.pojo.WarframeWorldStateCodebookEntry;

import java.util.Optional;

/**
 * 提供 WorldState 专用代码簿查询能力。
 *
 * <p>WorldState 中的任务类型、裂隙时代和突击变体不是物品别名，
 * 因此必须通过业务类型查询，避免被通用物品解析误判。</p>
 */
public interface WarframeWorldStateCodebookService {

    Optional<WarframeWorldStateCodebookEntry> find(
            WarframeReferenceType type, String reference);

    /**
     * 查询 WorldState 语言键，简体中文优先、英文兜底。
     */
    default Optional<WarframeWorldStateCodebookEntry> findLanguage(String reference) {
        return Optional.empty();
    }

    /** 数据刷新完成后重建 JVM 索引，不重新读取动态 WorldState。 */
    default void reload() {
    }

    default Optional<WarframeWorldStateCodebookEntry> findMissionType(String reference) {
        return find(WarframeReferenceType.MISSION_TYPE, reference);
    }

    default Optional<WarframeWorldStateCodebookEntry> findNode(String reference) {
        return find(WarframeReferenceType.NODE, reference);
    }

    default Optional<WarframeWorldStateCodebookEntry> findFaction(String reference) {
        return find(WarframeReferenceType.FACTION, reference);
    }

    default Optional<WarframeWorldStateCodebookEntry> findModifier(String reference) {
        return find(WarframeReferenceType.MODIFIER, reference);
    }

    default Optional<WarframeWorldStateCodebookEntry> findFissureTier(String reference) {
        return find(WarframeReferenceType.FISSURE_TIER, reference);
    }

    default Optional<WarframeWorldStateCodebookEntry> findSortieBoss(String reference) {
        return find(WarframeReferenceType.SORTIE_BOSS, reference);
    }

    default Optional<WarframeWorldStateCodebookEntry> findSyndicate(String reference) {
        return find(WarframeReferenceType.SYNDICATE, reference);
    }

    default Optional<WarframeWorldStateCodebookEntry> findOperationType(String reference) {
        return find(WarframeReferenceType.OPERATION_TYPE, reference);
    }

    default Optional<WarframeWorldStateCodebookEntry> findUpgradeType(String reference) {
        return find(WarframeReferenceType.UPGRADE_TYPE, reference);
    }

    default Optional<WarframeWorldStateCodebookEntry> findPersistentEnemy(String reference) {
        return find(WarframeReferenceType.PERSISTENT_ENEMY, reference);
    }
}
