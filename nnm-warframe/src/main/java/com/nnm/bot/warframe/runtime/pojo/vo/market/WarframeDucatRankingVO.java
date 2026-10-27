package com.nnm.bot.warframe.runtime.pojo.vo.market;

import cn.hutool.core.collection.CollUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/** 杜卡德金币的动态成交统计；身份和名称由已采纳商品目录关联。 */
public record WarframeDucatRankingVO(List<PeriodVO> periods, int page, String notice) {
    /**
     * 构造杜卡德金币排行展示对象，并保存初始化参数。
     *
     * @param periods 统计时段集合。
     * @param page 从一开始的页码。
     * @param notice 提示。
     */
    public WarframeDucatRankingVO {
        periods = List.copyOf(periods);
    }

    /**
     * 生成当前业务数据的简要说明。
     *
     * @return 当前业务数据的简要说明对应的文本。
     */
    public String summary() {
        return periods.stream().map(period -> period.name() + "（第 " + page + " 页）\n"
                + (CollUtil.isEmpty(period.items()) ? "此页没有匹配的成交统计" : period.items().stream().map(item ->
                        item.name() + "：" + item.ducats() + " 杜卡德金币；加权均价 " + decimal(item.weightedPrice())
                                + " 铂金；金币/铂金 " + decimal(item.ducatsPerPlatinum()) + "；成交量 " + item.volume())
                .collect(Collectors.joining("\n")))).collect(Collectors.joining("\n\n")) + "\n" + notice;
    }

    /**
     * 读取并保留输入的精确小数值。
     *
     * @param value 待校验的精确数值；缺失值保持为空。
     * @return 并保留输入的精确小数值对应的文本。
     */
    private static String decimal(BigDecimal value) {
        return value == null ? "未知" : value.stripTrailingZeros().toPlainString();
    }

    public record PeriodVO(String name, List<ItemVO> items) {
        /**
         * 构造时段展示对象，并保存初始化参数。
         *
         * @param name 当前业务对象的名称。
         * @param items 当前物品或成员列表。
         */
        public PeriodVO { items = List.copyOf(items); }
    }

    public record ItemVO(String itemId, String name, String slug, int ducats, BigDecimal weightedPrice,
                       BigDecimal ducatsPerPlatinum, long volume, String statisticsAt) {
    }
}
