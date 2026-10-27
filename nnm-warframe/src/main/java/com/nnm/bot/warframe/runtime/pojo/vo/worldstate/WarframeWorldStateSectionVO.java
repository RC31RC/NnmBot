package com.nnm.bot.warframe.runtime.pojo.vo.worldstate;

import java.util.List;
import java.util.Map;

/** 管理端世界数据中的一个业务分区。 */
public record WarframeWorldStateSectionVO(
        String key,
        String title,
        boolean available,
        int itemCount,
        List<Map<String, Object>> items
) {
}
