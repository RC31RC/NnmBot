package com.nnm.bot.warframe.pojo;

import java.util.List;
import java.util.Map;

/** 管理端世界数据中的一个业务分区。 */
public record WarframeWorldStateSection(
        String key,
        String title,
        boolean available,
        int itemCount,
        List<Map<String, Object>> items
) {
}
