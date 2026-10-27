package com.nnm.bot.warframe.runtime.pojo.vo.worldstate;

import java.util.List;

/** 管理端世界数据响应，不直接暴露原始 WorldStateDTO。 */
public record WarframeWorldStateManagementVO(
        boolean available,
        String source,
        String fetchedAt,
        String providerTimestamp,
        boolean stale,
        String updatedAt,
        List<WarframeWorldStateSectionVO> sections
) {
}
