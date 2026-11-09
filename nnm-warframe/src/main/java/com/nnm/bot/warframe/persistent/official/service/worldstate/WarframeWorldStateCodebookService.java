package com.nnm.bot.warframe.persistent.official.service.worldstate;

import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceType;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeWorldStateCodebookEntryDTO;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface WarframeWorldStateCodebookService {
    /**
     * 从所属小代码表查询引用。
     *
     * @param type 引用类型。
     * @param reference 原始引用键。
     * @return 明确找到的显示投影。
     */
    Optional<WarframeWorldStateCodebookEntryDTO> find(WarframeReferenceType type, String reference);

    /**
     * 批量查询语言键，未命中键按数据集批量补查数据库。
     *
     * @param references 本次使用的语言引用。
     * @return 以原始请求键组织的显示投影。
     */
    Map<String, WarframeWorldStateCodebookEntryDTO> findLanguages(Collection<String> references);

    /**
     * 查询单个语言键，与批量入口共享有界缓存。
     *
     * @param reference 语言键。
     * @return 已找到的显示投影。
     */
    default Optional<WarframeWorldStateCodebookEntryDTO> findLanguage(String reference) {
        return reference == null ? Optional.empty()
            : Optional.ofNullable(findLanguages(List.of(reference)).get(reference));
    }

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
