package com.nnm.bot.warframe.runtime.pojo.vo.worldstate;

import java.util.List;

/** 管理端用于排查上游字段变化的最后一次成功 WorldStateDTO 原始响应。 */
public record WarframeWorldStateRawVO(
        boolean available,
        String source,
        String fetchedAt,
        String contentHash,
        List<String> topLevelFields,
        String rawJson) {
}
