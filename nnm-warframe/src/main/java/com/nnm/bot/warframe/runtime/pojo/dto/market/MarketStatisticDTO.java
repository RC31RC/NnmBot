package com.nnm.bot.warframe.runtime.pojo.dto.market;

import java.math.BigDecimal;

/** 已绑定和校验的市场成交统计，不向服务调用方暴露原始协议对象。 */
public record MarketStatisticDTO(
        /** 统计记录的时间。 */
        String datetime,
        /** 参与统计的成交量。 */
        Integer volume,
        /** 订单方向。 */
        String orderType,
        /** 最低成交价格。 */
        BigDecimal minimum,
        /** 最高成交价格。 */
        BigDecimal maximum,
        /** 平均成交价格。 */
        BigDecimal average,
        /** 加权平均成交价格。 */
        BigDecimal weightedAverage,
        /** 成交价格中位数。 */
        BigDecimal median,
        /** 参与统计的物品等级。 */
        Integer modRank) {
}
