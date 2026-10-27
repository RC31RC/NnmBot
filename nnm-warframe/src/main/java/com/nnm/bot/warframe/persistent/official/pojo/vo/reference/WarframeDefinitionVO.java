package com.nnm.bot.warframe.persistent.official.pojo.vo.reference;

import cn.hutool.core.collection.CollUtil;
import java.util.List;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;

public record WarframeDefinitionVO(String dataset, String title, String query, int page, int totalPages,
        int totalCount, List<EntryVO> items) {
    /**
     * 构造定义展示对象，并保存初始化参数。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param title 标题。
     * @param query 当前业务的查询条件。
     * @param page 从一开始的页码。
     * @param totalPages 总数页数。
     * @param totalCount 总数数量。
     * @param items 当前物品或成员列表。
     */
    public WarframeDefinitionVO {
        items = List.copyOf(items);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param dataset 本次操作所属的数据文件或社区资料类型。
     * @param title 标题。
     * @param query 当前业务的查询条件。
     * @param matches 匹配集合。
     * @param requested 请求的。
     * @return 官方基础定义的分页视图。
     */
    public static WarframeDefinitionVO page(String dataset, String title, String query, List<EntryVO> matches,
            int requested) {
        int pages = Math.max(1, (int) ((matches.size() + 3L) / 4));
        int page = Math.max(1, Math.min(requested, pages));
        int from = (page - 1) * 4;
        return new WarframeDefinitionVO(dataset, title, query, page, pages, matches.size(),
                matches.subList(from, Math.min(from + 4, matches.size())));
    }

    /**
     * 生成当前数据对应的文本内容。
     *
     * @return 按当前对象字段组成的文本内容。
     */
    public String text() {
        StringBuilder text = new StringBuilder(title).append("：").append(page).append("/").append(totalPages)
                .append(" 页，共 ").append(totalCount).append(" 条");
        for (EntryVO entry : items) {
            text.append("\n\n").append(entry.name()).append("\n").append(entry.key());
            if (entry.description() != null) text.append("\n").append(entry.description());
            entry.facts().forEach(fact -> text.append("\n").append(fact.label()).append("：").append(fact.value()));
        }
        if (CollUtil.isEmpty(items)) text.append("\n没有找到匹配的").append(title);
        return text.toString();
    }

    public record EntryVO(String key, String name, String description, String imageReference, String iconUrl,
            String sourceCode, String resolutionMode, OfficialObjectDTO details, List<FactVO> facts) {
        /**
         * 构造条目展示对象，并保存初始化参数。
         *
         * @param key 当前记录或字段的查找键。
         * @param name 当前业务对象的名称。
         * @param description 说明。
         * @param imageReference 图片引用。
         * @param iconUrl 图标地址。
         * @param sourceCode 来源代码。
         * @param resolutionMode 解析状态模式。
         * @param details 详情。
         * @param facts 属性事实集合。
         */
        public EntryVO {
            facts = List.copyOf(facts);
        }
    }

    public record FactVO(String label, String reference, String value) {
    }
}
