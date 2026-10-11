package com.nnm.bot.warframe.pojo;


import java.util.List;

/** 管理端世界数据响应，不直接暴露原始 WorldState。 */
public record WarframeWorldStateItem(
        boolean available,
        String source,
        String fetchedAt,
        String providerTimestamp,
        boolean stale,
        String updatedAt,
        List<WarframeWorldStateSection> sections
) {
}
