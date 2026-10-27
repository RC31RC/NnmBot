package com.nnm.bot.warframe.persistent.official.pojo.vo.rewards;

import java.math.BigDecimal;
import java.util.List;

public record WarframeRewardPoolVO(String query, List<EntryVO> items, int page, int totalPages, int totalCount) {
    /**
     * 构造奖励奖池展示对象，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     * @param items 当前物品或成员列表。
     * @param page 从一开始的页码。
     * @param totalPages 总数页数。
     * @param totalCount 总数数量。
     */
    public WarframeRewardPoolVO {
        items = List.copyOf(items);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param query 当前业务的查询条件。
     * @param entries 条目集合。
     * @param page 从一开始的页码。
     * @return 奖励池及奖励概率的分页视图。
     */
    public static WarframeRewardPoolVO page(String query, List<EntryVO> entries, int page) {
        if (page < 1) throw new IllegalArgumentException("Page must be positive");
        int from = (int) Math.min((long) (page - 1) * 12, entries.size());
        return new WarframeRewardPoolVO(query, entries.subList(from, Math.min(from + 12, entries.size())),
                page, Math.max(1, (int) ((entries.size() + 11L) / 12)), entries.size());
    }

    /**
     * 生成当前数据的分页说明。
     *
     * @return 当前数据的分页说明对应的文本。
     */
    public String pagination() {
        return page + "/" + totalPages + " 页，共 " + totalCount + " 条";
    }

    public record EntryVO(String poolKey, String poolName, Integer stage, String itemKey, String itemName,
                        Integer count, BigDecimal probability, String rarity, boolean emptyPool,
                        String sourceCode, String resolutionMode) {
    }
}
