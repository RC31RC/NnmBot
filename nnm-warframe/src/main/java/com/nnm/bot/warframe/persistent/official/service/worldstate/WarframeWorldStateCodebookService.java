package com.nnm.bot.warframe.persistent.official.service.worldstate;

import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceType;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeWorldStateCodebookEntryDTO;
import java.util.Optional;

/**
 * 提供 WorldStateDTO 专用代码簿查询能力。
 *
 * <p>WorldStateDTO 中的任务类型、裂隙时代和突击变体不是物品别名，
 * 因此必须通过业务类型查询，避免被通用物品解析误判。</p>
 */
public interface WarframeWorldStateCodebookService {

    /**
     * 按给定条件查询业务数据。
     *
     * @param type 目标业务类型。
     * @param reference 引用。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    Optional<WarframeWorldStateCodebookEntryDTO> find(
            WarframeReferenceType type, String reference);

    /**
     * 查询 WorldStateDTO 语言键，简体中文优先、英文兜底。
     *
     * @param reference 引用。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    default Optional<WarframeWorldStateCodebookEntryDTO> findLanguage(String reference) {
        return Optional.empty();
    }

    /** 数据刷新完成后重建 JVM 索引，不重新读取动态 WorldStateDTO。 */
    void reload();

    /**
     * 查询任务类型。
     *
     * @param reference 引用。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    default Optional<WarframeWorldStateCodebookEntryDTO> findMissionType(String reference) {
        return find(WarframeReferenceType.MISSION_TYPE, reference);
    }

    /**
     * 查询节点。
     *
     * @param reference 引用。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    default Optional<WarframeWorldStateCodebookEntryDTO> findNode(String reference) {
        return find(WarframeReferenceType.NODE, reference);
    }

    /**
     * 查询阵营。
     *
     * @param reference 引用。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    default Optional<WarframeWorldStateCodebookEntryDTO> findFaction(String reference) {
        return find(WarframeReferenceType.FACTION, reference);
    }

    /**
     * 查询词缀。
     *
     * @param reference 引用。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    default Optional<WarframeWorldStateCodebookEntryDTO> findModifier(String reference) {
        return find(WarframeReferenceType.MODIFIER, reference);
    }

    /**
     * 查询裂隙层级。
     *
     * @param reference 引用。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    default Optional<WarframeWorldStateCodebookEntryDTO> findFissureTier(String reference) {
        return find(WarframeReferenceType.FISSURE_TIER, reference);
    }

    /**
     * 查询突击首领。
     *
     * @param reference 引用。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    default Optional<WarframeWorldStateCodebookEntryDTO> findSortieBoss(String reference) {
        return find(WarframeReferenceType.SORTIE_BOSS, reference);
    }

    /**
     * 查询集团。
     *
     * @param reference 引用。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    default Optional<WarframeWorldStateCodebookEntryDTO> findSyndicate(String reference) {
        return find(WarframeReferenceType.SYNDICATE, reference);
    }

    /**
     * 查询操作类型。
     *
     * @param reference 引用。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    default Optional<WarframeWorldStateCodebookEntryDTO> findOperationType(String reference) {
        return find(WarframeReferenceType.OPERATION_TYPE, reference);
    }

    /**
     * 查询升级类型。
     *
     * @param reference 引用。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    default Optional<WarframeWorldStateCodebookEntryDTO> findUpgradeType(String reference) {
        return find(WarframeReferenceType.UPGRADE_TYPE, reference);
    }

    /**
     * 查询常驻敌人。
     *
     * @param reference 引用。
     * @return 匹配的业务结果；没有有效数据时为空。
     */
    default Optional<WarframeWorldStateCodebookEntryDTO> findPersistentEnemy(String reference) {
        return find(WarframeReferenceType.PERSISTENT_ENEMY, reference);
    }
}
