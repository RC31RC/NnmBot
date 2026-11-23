package com.nnm.bot.warframe.persistent.curated.pojo.dto.worldstate;

import java.math.BigDecimal;
import java.util.List;

/** 已入库的楼层奖池与经官方资料核实的奖励名称和图片。 */
public record DescendiaRewardGroupDTO(int floor, String difficulty, String label, List<RewardOptionDTO> options,
    String note, boolean guaranteed, String sourceUrl) {
    /** 已确认的物品身份和候选显示信息，未公布的概率为空。 */
    public record RewardOptionDTO(String itemKey, String name, String imageUrl, BigDecimal count,
        BigDecimal chancePercent) {
    }
}
