package com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate;

import java.time.Instant;
import java.util.List;

/** 当前周的官方商人商品身份和轮换时间；具体物品数据由官方 Vendor 表提供。 */
public record SteelPathRotationDTO(String storeItem, int index, Instant activation, Instant expiry,
    List<String> orderedItems) {
    public SteelPathRotationDTO {
        orderedItems = List.copyOf(orderedItems);
    }
}
