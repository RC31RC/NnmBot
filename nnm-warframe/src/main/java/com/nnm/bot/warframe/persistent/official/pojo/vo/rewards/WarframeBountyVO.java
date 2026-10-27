package com.nnm.bot.warframe.persistent.official.pojo.vo.rewards;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeBountyDefinitionDetailsDTO.StageOptionDTO;
import java.util.List;

public record WarframeBountyVO(String query, int page, int totalPages, int totalCount, List<EntryVO> items) {
    /**
     * 构造赏金展示对象，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     * @param page 从一开始的页码。
     * @param totalPages 总数页数。
     * @param totalCount 总数数量。
     * @param items 当前物品或成员列表。
     */
    public WarframeBountyVO {
        items = List.copyOf(items);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param query 当前业务的查询条件。
     * @param entries 条目集合。
     * @param requestedPage 请求的分页。
     * @return 赏金静态定义的分页视图。
     */
    public static WarframeBountyVO page(String query, List<EntryVO> entries, int requestedPage) {
        int pages = Math.max(1, (int) ((entries.size() + 3L) / 4));
        int page = Math.max(1, Math.min(requestedPage, pages));
        int from = (page - 1) * 4;
        return new WarframeBountyVO(query, page, pages, entries.size(),
                entries.subList(from, Math.min(from + 4, entries.size())));
    }

    /**
     * 生成当前数据对应的文本内容。
     *
     * @return 按当前对象字段组成的文本内容。
     */
    public String text() {
        StringBuilder result = new StringBuilder("赏金静态定义：").append(page).append("/")
                .append(totalPages).append(" 页，共 ").append(totalCount).append(" 条");
        for (EntryVO entry : items) {
            result.append("\n\n").append(entry.name());
            if (StrUtil.isNotBlank(entry.description())) {
                result.append("\n").append(entry.description());
            }
            if (CollUtil.isEmpty(entry.stages())) result.append("\n阶段未提供或未采纳");
            for (StageOptionDTO option : entry.stages()) {
                result.append("\n阶段 ").append(option.stage()).append(" · 选项 ").append(option.option())
                        .append("：").append(option.jobReference());
            }
        }
        if (CollUtil.isEmpty(items)) result.append("\n没有找到匹配的赏金定义");
        return result.toString();
    }

    public record EntryVO(String jobType, String name, String description, String imageReference, String iconUrl,
            String sourceCode, String resolutionMode, List<StageOptionDTO> stages) {
        /**
         * 构造条目展示对象，并保存初始化参数。
         *
         * @param jobType 任务类型。
         * @param name 当前业务对象的名称。
         * @param description 说明。
         * @param imageReference 图片引用。
         * @param iconUrl 图标地址。
         * @param sourceCode 来源代码。
         * @param resolutionMode 解析状态模式。
         * @param stages 阶段集合。
         */
        public EntryVO {
            stages = List.copyOf(stages);
        }
    }
}
