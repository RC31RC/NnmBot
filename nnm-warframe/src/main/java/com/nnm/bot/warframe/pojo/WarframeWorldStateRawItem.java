package com.nnm.bot.warframe.pojo;

import java.util.List;

/** 管理端用于排查上游字段变化的最后一次成功 WorldState 原始响应。 */
public record WarframeWorldStateRawItem(
        boolean available,
        String source,
        String fetchedAt,
        String contentHash,
        List<String> topLevelFields,
        String rawJson) {
}
