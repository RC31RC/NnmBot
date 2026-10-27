package com.nnm.bot.warframe.persistent.official.pojo.vo.items;

import cn.hutool.core.collection.CollUtil;
import java.util.List;

public record WarframeEmailVO(String query, int page, int totalPages, int totalCount, List<EntryVO> items) {
    /**
     * 构造邮件展示对象，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     * @param page 从一开始的页码。
     * @param totalPages 总数页数。
     * @param totalCount 总数数量。
     * @param items 当前物品或成员列表。
     */
    public WarframeEmailVO {
        items = List.copyOf(items);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param query 当前业务的查询条件。
     * @param matches 匹配集合。
     * @param requested 请求的。
     * @return 邮件定义的分页视图。
     */
    public static WarframeEmailVO page(String query, List<EntryVO> matches, int requested) {
        int pages = Math.max(1, (int) ((matches.size() + 3L) / 4));
        int page = Math.max(1, Math.min(requested, pages));
        int from = (page - 1) * 4;
        return new WarframeEmailVO(query, page, pages, matches.size(),
                matches.subList(from, Math.min(from + 4, matches.size())));
    }

    /**
     * 生成当前数据对应的文本内容。
     *
     * @return 按当前对象字段组成的文本内容。
     */
    public String text() {
        StringBuilder text = new StringBuilder("邮件定义：").append(page).append("/").append(totalPages)
                .append(" 页，共 ").append(totalCount).append(" 条");
        for (EntryVO entry : items) {
            text.append("\n\n").append(entry.message().text());
            if (entry.sendOnlyOnce() != null) text.append("\n仅发送一次：").append(entry.sendOnlyOnce() ? "是" : "否");
        }
        if (CollUtil.isEmpty(items)) text.append("\n没有找到匹配的邮件定义");
        return text.toString();
    }

    public record EntryVO(String key, String sourceCode, String resolutionMode,
            Boolean sendOnlyOnce, WarframeMailVO message) {
    }
}
