package com.nnm.bot.warframe.persistent.official.pojo.vo.world;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.constant.WarframeCatalogConstants.EnemySection;
import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeDefinitionVO.FactVO;
import java.util.List;
import com.nnm.bot.warframe.persistent.official.pojo.dto.support.OfficialObjectDTO;

public record WarframeEnemyVO(EnemySection section, String query, int page, int totalPages, int totalCount,
        List<EntryVO> items) {
    /**
     * 构造敌人展示对象，并保存初始化参数。
     *
     * @param section 分区。
     * @param query 当前业务的查询条件。
     * @param page 从一开始的页码。
     * @param totalPages 总数页数。
     * @param totalCount 总数数量。
     * @param items 当前物品或成员列表。
     */
    public WarframeEnemyVO {
        items = List.copyOf(items);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param query 当前业务的查询条件。
     * @param section 分区。
     * @param matches 匹配集合。
     * @param requested 请求的。
     * @return 敌人资料的分页视图。
     */
    public static WarframeEnemyVO page(String query, EnemySection section, List<EntryVO> matches, int requested) {
        int pages = Math.max(1, (int) ((matches.size() + 3L) / 4));
        int page = Math.max(1, Math.min(requested, pages));
        int from = (page - 1) * 4;
        return new WarframeEnemyVO(section, query, page, pages, matches.size(),
                matches.subList(from, Math.min(from + 4, matches.size())));
    }

    /**
     * 生成当前数据对应的文本内容。
     *
     * @return 按当前对象字段组成的文本内容。
     */
    public String text() {
        StringBuilder text = new StringBuilder(section.domain().label()).append("：").append(page).append("/")
                .append(totalPages).append(" 页，共 ").append(totalCount).append(" 条");
        for (EntryVO entry : items) {
            text.append("\n\n").append(entry.name()).append("\n").append(entry.key());
            if (entry.description() != null) text.append("\n").append(entry.description());
            entry.facts().forEach(fact -> text.append("\n").append(fact.label()).append("：").append(fact.value()));
            for (ChildVO child : entry.children()) {
                child.facts().forEach(fact -> text.append("\n").append(fact.label()).append("：").append(fact.value()));
                child.children().forEach(item -> item.facts().forEach(fact ->
                        text.append("\n  ").append(fact.label()).append("：").append(fact.value())));
            }
        }
        if (CollUtil.isEmpty(items)) text.append("\n没有匹配的").append(section.domain().label());
        return text.toString();
    }

    public record EntryVO(Long id, String key, String name, String description, String imageReference, String iconUrl,
            String sourceCode, String resolutionMode, OfficialObjectDTO details,
            List<FactVO> facts, List<ChildVO> children) {
        /**
         * 构造条目展示对象，并保存初始化参数。
         *
         * @param id 目标记录的本地主键。
         * @param key 当前记录或字段的查找键。
         * @param name 当前业务对象的名称。
         * @param description 说明。
         * @param imageReference 图片引用。
         * @param iconUrl 图标地址。
         * @param sourceCode 来源代码。
         * @param resolutionMode 解析状态模式。
         * @param details 详情。
         * @param facts 属性事实集合。
         * @param children 子记录集合。
         */
        public EntryVO {
            facts = List.copyOf(facts);
            children = List.copyOf(children);
        }
    }

    public record ChildVO(Long id, String key, Long parentId, String parentBkey, String sourceCode, int order,
            OfficialObjectDTO details, List<FactVO> facts, List<ChildVO> children) {
        /**
         * 构造子记录展示对象，并保存初始化参数。
         *
         * @param id 目标记录的本地主键。
         * @param key 当前记录或字段的查找键。
         * @param parentId 直接父记录的本地主键。
         * @param parentBkey 父记录业务关联键。
         * @param sourceCode 来源代码。
         * @param order 成员在输入集合中的位置。
         * @param details 详情。
         * @param facts 属性事实集合。
         * @param children 子记录集合。
         */
        public ChildVO {
            facts = List.copyOf(facts);
            children = List.copyOf(children);
        }
    }
}
