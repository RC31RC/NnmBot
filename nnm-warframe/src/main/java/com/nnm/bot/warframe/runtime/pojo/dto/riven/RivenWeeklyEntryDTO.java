package com.nnm.bot.warframe.runtime.pojo.dto.riven;

import java.math.BigDecimal;

/** 已校验的周紫卡成交统计；不携带命令展示名称或持久化状态。 */
public record RivenWeeklyEntryDTO(
        /** 紫卡适用的武器英文名称；未揭示类别记录可为空。 */
        String compatibility,
        /** 紫卡所属的武器类别。 */
        String itemType,
        /** 是否统计已经洗练的紫卡。 */
        boolean rerolled,
        /** 成交均价。 */
        BigDecimal avg,
        /** 成交价格中位数。 */
        BigDecimal median,
        /** 最低成交价格。 */
        BigDecimal min,
        /** 最高成交价格。 */
        BigDecimal max,
        /** 成交价格标准差。 */
        BigDecimal stddev,
        /** 相对交易热度。 */
        BigDecimal pop) {
}
