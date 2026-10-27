package com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades;

import cn.hutool.core.collection.CollUtil;
import java.util.List;

public record WarframeRivenUnrollableVO(
        String query, int page, int totalPages, int totalCount, List<EntryVO> items) {
    private static final int PAGE_SIZE = 8;

    /**
     * 构造紫卡不可洗词条展示对象，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     * @param page 从一开始的页码。
     * @param totalPages 总数页数。
     * @param totalCount 总数数量。
     * @param items 当前物品或成员列表。
     */
    public WarframeRivenUnrollableVO {
        items = List.copyOf(items);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param query 当前业务的查询条件。
     * @param entries 条目集合。
     * @param requested 请求的。
     * @return 不可洗紫卡属性的分页视图。
     */
    public static WarframeRivenUnrollableVO page(String query, List<EntryVO> entries, int requested) {
        int pages = Math.max(1, (entries.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        int page = Math.max(1, Math.min(requested, pages));
        int start = (page - 1) * PAGE_SIZE;
        return new WarframeRivenUnrollableVO(query, page, pages, entries.size(),
                entries.subList(start, Math.min(start + PAGE_SIZE, entries.size())));
    }

    /**
     * 生成当前数据对应的文本内容。
     *
     * @return 按当前对象字段组成的文本内容。
     */
    public String text() {
        StringBuilder text = new StringBuilder("紫卡不可洗属性：").append(page).append('/').append(totalPages)
                .append(" 页，共 ").append(totalCount).append(" 条");
        for (EntryVO entry : items) {
            text.append('\n').append(entry.weaponName()).append(" · ").append(entry.attributeTag());
        }
        if (CollUtil.isEmpty(items)) {
            text.append("\n未找到已采纳的限制关系；不能据此判断属性可洗");
        }
        return text.toString();
    }

    public record EntryVO(String weaponKey, String weaponName, String attributeTag,
                        String sourceCode, String resolutionMode) {
    }
}
