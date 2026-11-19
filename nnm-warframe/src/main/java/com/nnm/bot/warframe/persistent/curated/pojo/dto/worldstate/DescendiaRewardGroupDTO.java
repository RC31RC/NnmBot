package com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate;

import java.math.BigDecimal;
import java.util.List;

/** 已入库的楼层奖池与经官方资料核实的奖励名称和图片。 */
public record DescendiaRewardGroupDTO(int floor, String difficulty, String label, List<RewardOptionDTO> options,
    String note, boolean guaranteed, String sourceUrl) {
    public record RewardOptionDTO(String name, String imageUrl, BigDecimal count, BigDecimal chancePercent) {
    }
}
