package com.nnm.bot.warframe.persistent.official.constant;

/**
 * WorldStateDTO 字段引用的业务类型，防止任务代码被误当作物品路径解析。
 */
public enum WarframeReferenceType {
    ITEM,
    NODE,
    FACTION,
    MISSION_TYPE,
    MODIFIER,
    FISSURE_TIER,
    SORTIE_BOSS,
    SYNDICATE,
    OPERATION_TYPE,
    UPGRADE_TYPE,
    PERSISTENT_ENEMY
}
