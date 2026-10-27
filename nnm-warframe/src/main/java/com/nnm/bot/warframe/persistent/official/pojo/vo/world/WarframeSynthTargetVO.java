package com.nnm.bot.warframe.persistent.official.pojo.vo.world;

import java.time.LocalDate;
import java.util.List;

public record WarframeSynthTargetVO(String query, List<EntryVO> items, int page, int totalPages, int totalCount) {
    /**
     * 构造结合仪式目标展示对象，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     * @param items 当前物品或成员列表。
     * @param page 从一开始的页码。
     * @param totalPages 总数页数。
     * @param totalCount 总数数量。
     */
    public WarframeSynthTargetVO {
        items = List.copyOf(items);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param query 当前业务的查询条件。
     * @param entries 条目集合。
     * @param page 从一开始的页码。
     * @return 结合仪式目标的分页视图。
     */
    public static WarframeSynthTargetVO page(String query, List<EntryVO> entries, int page) {
        if (page < 1) throw new IllegalArgumentException("Page must be positive");
        int from = (int) Math.min((long) (page - 1) * 4, entries.size());
        return new WarframeSynthTargetVO(query, entries.subList(from, Math.min(from + 4, entries.size())),
                page, Math.max(1, (int) ((entries.size() + 3L) / 4)), entries.size());
    }

    /**
     * 生成当前数据的分页说明。
     *
     * @return 当前数据的分页说明对应的文本。
     */
    public String pagination() {
        return page + "/" + totalPages + " 页，共 " + totalCount + " 个目标";
    }

    public record EntryVO(String imageKey, String name, String locale, List<LocationVO> locations,
                        String sourceCode, String resolutionMode) {
        /**
         * 构造条目展示对象，并保存初始化参数。
         *
         * @param imageKey 图片键。
         * @param name 当前业务对象的名称。
         * @param locale 当前语言区域代码。
         * @param locations 位置集合。
         * @param sourceCode 来源代码。
         * @param resolutionMode 解析状态模式。
         */
        public EntryVO {
            locations = List.copyOf(locations);
        }
    }

    public record LocationVO(String mission, String planet, String missionType, String faction, String level,
                           String spawnRate, LocalDate lastVerified) {
    }
}
