package com.nnm.bot.warframe.persistent.official.pojo.vo.equipment;

import java.math.BigDecimal;
import java.util.List;

public record WarframeEquipmentVO(
        String query, String category, List<EntryVO> items, int page, int totalPages, int totalCount) {
    /**
     * 构造装备展示对象，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     * @param category 分类。
     * @param items 当前物品或成员列表。
     * @param page 从一开始的页码。
     * @param totalPages 总数页数。
     * @param totalCount 总数数量。
     */
    public WarframeEquipmentVO {
        items = List.copyOf(items);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param query 当前业务的查询条件。
     * @param category 分类。
     * @param entries 条目集合。
     * @param page 从一开始的页码。
     * @return 战甲或武器资料的分页视图。
     */
    public static WarframeEquipmentVO page(String query, String category, List<EntryVO> entries, int page) {
        if (page < 1) {
            throw new IllegalArgumentException("Page must be positive");
        }
        int pages = Math.max(1, (int) ((entries.size() + 5L) / 6));
        int from = (int) Math.min((long) (page - 1) * 6, entries.size());
        return new WarframeEquipmentVO(query, category,
                entries.subList(from, Math.min(from + 6, entries.size())), page, pages, entries.size());
    }

    /**
     * 生成当前数据的分页说明。
     *
     * @return 当前数据的分页说明对应的文本。
     */
    public String pagination() {
        return page + "/" + totalPages + " 页，共 " + totalCount + " 条";
    }

    public record EntryVO(
            String uniqueName, String name, String description, String imageReference, String iconUrl,
            String sourceCode, String resolutionMode, List<MetricVO> metrics, List<AbilityVO> abilities) {
        /**
         * 构造条目展示对象，并保存初始化参数。
         *
         * @param uniqueName 唯一名称。
         * @param name 当前业务对象的名称。
         * @param description 说明。
         * @param imageReference 图片引用。
         * @param iconUrl 图标地址。
         * @param sourceCode 来源代码。
         * @param resolutionMode 解析状态模式。
         * @param metrics 统计指标集合。
         * @param abilities 技能集合。
         */
        public EntryVO {
            metrics = List.copyOf(metrics);
            abilities = List.copyOf(abilities);
        }
    }

    public record MetricVO(String label, BigDecimal value) {}

    public record AbilityVO(
            int order, String uniqueName, String name, String description, BigDecimal energy,
            String imageReference, String iconUrl, String sourceCode) {}
}
