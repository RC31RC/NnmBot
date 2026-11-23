package com.nnm.bot.warframe.runtime.pojo.dto.riven;

/** 同一武器紫卡的周成交统计；不同洗练状态作为同一记录的两个价格组。 */
public record RivenWeeklyEntryDTO(
    /** 紫卡适用的武器英文名称；未揭示类别记录可为空。 */
    String compatibility,
    /** 紫卡所属的武器类别。 */
    String itemType,
    /** 未洗紫卡成交统计；上游缺少时为空。 */
    RivenWeeklyStatisticsDTO unrolled,
    /** 已洗紫卡成交统计；上游缺少时为空。 */
    RivenWeeklyStatisticsDTO rerolled) {
}
