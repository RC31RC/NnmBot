package com.nnm.bot.warframe.runtime.pojo.dto.riven;

import java.math.BigDecimal;

/** 单一洗练状态下的紫卡周成交价格统计。 */
public record RivenWeeklyStatisticsDTO(
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
