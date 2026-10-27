package com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades;

import java.util.List;

public record WarframeUpgradeVO(
        String query, String category, List<EntryVO> items, int page, int totalPages, int totalCount) {
    /**
     * 构造升级展示对象，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     * @param category 分类。
     * @param items 当前物品或成员列表。
     * @param page 从一开始的页码。
     * @param totalPages 总数页数。
     * @param totalCount 总数数量。
     */
    public WarframeUpgradeVO {
        items = List.copyOf(items);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param query 当前业务的查询条件。
     * @param category 分类。
     * @param entries 条目集合。
     * @param page 从一开始的页码。
     * @return 振幅晶体及升级定义的分页视图。
     */
    public static WarframeUpgradeVO page(String query, String category, List<EntryVO> entries, int page) {
        if (page < 1) throw new IllegalArgumentException("Page must be positive");
        int pages = Math.max(1, (int) ((entries.size() + 3L) / 4));
        int from = (int) Math.min((long) (page - 1) * 4, entries.size());
        return new WarframeUpgradeVO(query, category, entries.subList(from, Math.min(from + 4, entries.size())),
                page, pages, entries.size());
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
            String uniqueName, String name, String description, String upgradeType, String rarity,
            Integer baseDrain, String polarity, String compatibility, Integer fusionLimit,
            Integer rank, String rankUnit, List<String> effects, String imageReference, String iconUrl,
            String sourceCode, String resolutionMode) {
        /**
         * 构造条目展示对象，并保存初始化参数。
         *
         * @param uniqueName 唯一名称。
         * @param name 当前业务对象的名称。
         * @param description 说明。
         * @param upgradeType 升级类型。
         * @param rarity 稀有度。
         * @param baseDrain 基础消耗。
         * @param polarity 极性。
         * @param compatibility 适用武器。
         * @param fusionLimit 融合上限。
         * @param rank 等级。
         * @param rankUnit 等级单位。
         * @param effects 效果集合。
         * @param imageReference 图片引用。
         * @param iconUrl 图标地址。
         * @param sourceCode 来源代码。
         * @param resolutionMode 解析状态模式。
         */
        public EntryVO {
            effects = List.copyOf(effects);
        }
    }
}
