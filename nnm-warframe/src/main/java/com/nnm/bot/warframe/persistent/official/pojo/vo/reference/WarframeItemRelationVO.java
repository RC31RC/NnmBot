package com.nnm.bot.warframe.persistent.official.pojo.vo.reference;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public record WarframeItemRelationVO(String query, String category, int page, int totalPages, int totalCount,
        List<ComponentVO> components, List<DropVO> drops, String notice) {
    /**
     * 构造物品关联展示对象，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     * @param category 分类。
     * @param page 从一开始的页码。
     * @param totalPages 总数页数。
     * @param totalCount 总数数量。
     * @param components 组件集合。
     * @param drops 掉落集合。
     * @param notice 提示。
     */
    public WarframeItemRelationVO {
        components = List.copyOf(components);
        drops = List.copyOf(drops);
    }

    /**
     * 将组件关系条目组装为命令分页视图。
     * @param query 当前业务的查询条件。
     * @param records 待批量处理的业务记录。
     * @param page 从一开始的页码。
     * @return 组件或掉落关系的分页视图。
     */
    public static WarframeItemRelationVO componentPage(String query, List<ComponentVO> records, int page) {
        return new WarframeItemRelationVO(query, "物品组件", page, pages(records.size()), records.size(),
                slice(records, page), List.of(), notice(records.size(), page));
    }

    /**
     * 将掉落来源条目组装为命令分页视图。
     * @param query 当前业务的查询条件。
     * @param records 待批量处理的业务记录。
     * @param page 从一开始的页码。
     * @return 组件或掉落关系的分页视图。
     */
    public static WarframeItemRelationVO dropPage(String query, List<DropVO> records, int page) {
        return new WarframeItemRelationVO(query, "物品掉落", page, pages(records.size()), records.size(),
                List.of(), slice(records, page), notice(records.size(), page));
    }

    /**
     * 根据总数与每页大小计算页数。
     *
     * @param size 当前操作允许的数据数量。
     * @return 按总条数和每页上限向上取整后的总页数。
     */
    private static int pages(int size) {
        return Math.max(1, (int) ((size + 5L) / 6));
    }

    /**
     * 按当前分页范围截取记录。
     *
     * @param records 待批量处理的业务记录。
     * @param page 从一开始的页码。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static <T> List<T> slice(List<T> records, int page) {
        long start = (page - 1L) * 6;
        return start >= records.size() ? List.of() : records.subList((int) start, (int) Math.min(start + 6, records.size()));
    }

    /**
     * 根据当前数据状态生成展示提示。
     *
     * @param size 当前操作允许的数据数量。
     * @param page 从一开始的页码。
     * @return 根据当前数据状态生成展示提示对应的文本。
     */
    private static String notice(int size, int page) {
        return size == 0 ? "没有匹配的已采纳明细" : page > pages(size) ? "该页没有记录" : null;
    }

    /**
     * 生成当前数据对应的文本内容。
     *
     * @return 按当前对象字段组成的文本内容。
     */
    public String text() {
        String header = category + "：" + page + "/" + totalPages + " 页，共 " + totalCount + " 条";
        if (CollUtil.isEmpty(components) && CollUtil.isEmpty(drops)) return header + "\n" + notice;
        return header + "\n" + (CollUtil.isEmpty(components)
                ? drops.stream().map(row -> row.parentName() + (row.componentName() == null
                        ? "" : " · " + row.componentName()) + "\n"
                        + fallback(row.location(), "地点未提供")
                        + (row.dropType() == null ? "" : " · " + row.dropType())
                        + (row.rarity() == null ? "" : " · " + row.rarity())
                        + " · " + (row.sourceChance() != null && row.chancePercent() == null
                        ? "来源数值 " + row.sourceChance().stripTrailingZeros().toPlainString() + "（超出百分比范围）"
                        : row.chancePercent() == null ? "概率未提供"
                        : row.chancePercent().stripTrailingZeros().toPlainString() + "%"))
                        .collect(Collectors.joining("\n\n"))
                : components.stream().map(row -> row.parentName() + "\n" + "  ".repeat(row.depth())
                        + row.name() + " × " + (row.quantity() == null ? "数量未提供" : row.quantity()))
                        .collect(Collectors.joining("\n\n")));
    }

    /**
     * 在主要数据缺省时选择明确允许的替代值。
     *
     * @param value 待校验或规范化的文本。
     * @param missing 缺失。
     * @return 在主要数据缺省时选择明确允许的替代值对应的文本。
     */
    private static String fallback(String value, String missing) {
        return StrUtil.isBlank(value) ? missing : value;
    }

    public record ComponentVO(String parentKey, String parentName, String parentType, String itemKey, String name,
            Long quantity, int depth, String imageReference, String iconUrl, String sourceCode, String resolutionMode) {
    }

    public record DropVO(String parentKey, String parentName, String parentType, String componentName,
            String location, String dropType, String rarity, BigDecimal chancePercent, BigDecimal sourceChance,
            String sourceCode, String resolutionMode) {
    }
}
