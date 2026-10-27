package com.nnm.bot.warframe.persistent.official.pojo.vo.items;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.pojo.vo.crafting.WarframeMaterialVO;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public record WarframeBundleVO(String query, String category, int page, int totalPages,
        int totalCount, List<EntryVO> items) {
    /**
     * 构造礼包展示对象，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     * @param category 分类。
     * @param page 从一开始的页码。
     * @param totalPages 总数页数。
     * @param totalCount 总数数量。
     * @param items 当前物品或成员列表。
     */
    public WarframeBundleVO {
        items = List.copyOf(items);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param query 当前业务的查询条件。
     * @param category 分类。
     * @param matches 匹配集合。
     * @param requestedPage 请求的分页。
     * @return 组合包定义的分页视图。
     */
    public static WarframeBundleVO page(String query, String category, List<EntryVO> matches, int requestedPage) {
        int pages = Math.max(1, (int) ((matches.size() + 3L) / 4));
        int page = Math.max(1, Math.min(requestedPage, pages));
        int from = (page - 1) * 4;
        return new WarframeBundleVO(query, category, page, pages, matches.size(),
                matches.subList(from, Math.min(from + 4, matches.size())));
    }

    /**
     * 生成当前数据对应的文本内容。
     *
     * @return 按当前对象字段组成的文本内容。
     */
    public String text() {
        StringBuilder text = new StringBuilder(category).append("：").append(page).append("/")
                .append(totalPages).append(" 页，共 ").append(totalCount).append(" 条");
        for (EntryVO item : items) {
            text.append("\n\n").append(item.definition().name());
            item.definition().attributes().forEach(value -> text.append("\n")
                    .append(value.label()).append("：").append(value.value()));
            if (CollUtil.isEmpty(item.components())) text.append("\n组件未提供或未采纳");
            for (ComponentVO component : item.components()) {
                text.append("\n").append(component.name());
                text.append(component.amount() == null ? " · 数量未提供" : " × " + component.amount());
                if (component.rarity() != null) text.append(" · ").append(component.rarity());
            }
            if ("booster_pack".equals(item.dataset())) {
                if (CollUtil.isEmpty(item.rolls())) text.append("\n抽取权重未提供或未采纳");
                for (RollVO roll : item.rolls()) {
                    text.append("\n第 ").append(roll.order() + 1).append(" 抽权重：");
                    text.append(CollUtil.isEmpty(roll.weights()) ? "未提供" : roll.weights().stream()
                            .map(value -> value.rarity() + " " + value.weight().stripTrailingZeros().toPlainString())
                            .collect(Collectors.joining(" · ")));
                }
            }
        }
        if (CollUtil.isEmpty(items)) text.append("\n没有找到匹配的礼包");
        return text.toString();
    }

    public record EntryVO(String dataset, WarframeMaterialVO.EntryVO definition,
                        List<ComponentVO> components, List<RollVO> rolls) {
        /**
         * 构造条目展示对象，并保存初始化参数。
         *
         * @param dataset 本次操作所属的数据文件或社区资料类型。
         * @param definition 定义。
         * @param components 组件集合。
         * @param rolls 抽取集合。
         */
        public EntryVO {
            components = List.copyOf(components);
            rolls = List.copyOf(rolls);
        }
    }

    public record ComponentVO(int order, String itemKey, String name, Long amount, String rarity) {
    }

    public record RollVO(int order, List<WeightVO> weights) {
        /**
         * 构造抽取展示对象，并保存初始化参数。
         *
         * @param order 成员在输入集合中的位置。
         * @param weights 权重集合。
         */
        public RollVO {
            weights = List.copyOf(weights);
        }
    }

    public record WeightVO(String rarity, BigDecimal weight) {
    }
}
