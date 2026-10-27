package com.nnm.bot.warframe.persistent.official.pojo.vo.crafting;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import java.util.List;
import java.util.stream.Collectors;

public record WarframeMaterialVO(String query, String category, int page, int totalPages,
        int totalCount, List<EntryVO> items, String notice) {
    /**
     * 构造材料展示对象，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     * @param category 分类。
     * @param page 从一开始的页码。
     * @param totalPages 总数页数。
     * @param totalCount 总数数量。
     * @param items 当前物品或成员列表。
     * @param notice 提示。
     */
    public WarframeMaterialVO {
        items = List.copyOf(items);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param query 当前业务的查询条件。
     * @param category 分类。
     * @param matches 匹配集合。
     * @param requestedPage 请求的分页。
     * @return 材料及资源的分页视图。
     */
    public static WarframeMaterialVO page(String query, String category, List<EntryVO> matches, int requestedPage) {
        int pages = Math.max(1, (matches.size() + 5) / 6);
        int page = Math.max(1, Math.min(requestedPage, pages));
        int start = (page - 1) * 6;
        return new WarframeMaterialVO(query, category, page, pages, matches.size(),
                matches.subList(start, Math.min(start + 6, matches.size())),
                CollUtil.isEmpty(matches) ? "没有找到匹配的物品" : null);
    }

    /**
     * 生成当前数据对应的文本内容。
     *
     * @return 按当前对象字段组成的文本内容。
     */
    public String text() {
        String header = category + "：" + page + "/" + totalPages + " 页，共 " + totalCount + " 条";
        if (CollUtil.isEmpty(items)) return header + "\n" + notice;
        return header + "\n" + items.stream().map(entry -> entry.name()
                + (entry.itemType() == null ? "" : " · " + entry.itemType())
                + (entry.tradable() == null ? "" : entry.tradable() ? " · 可交易" : " · 不可交易")
                + (CollUtil.isEmpty(entry.attributes()) ? "" : "\n" + entry.attributes().stream()
                        .map(attribute -> attribute.label() + "：" + attribute.value()).collect(Collectors.joining(" · ")))
                + (StrUtil.isBlank(entry.description()) ? "" : "\n" + entry.description()))
                .collect(Collectors.joining("\n\n"));
    }

    public record EntryVO(String uniqueName, String name, String description, String itemType,
            Boolean tradable, String imageReference, String iconUrl, String sourceCode,
            String resolutionMode, String propertiesJson, List<AttributeVO> attributes) {
        /**
         * 构造条目展示对象，并保存初始化参数。
         *
         * @param uniqueName 唯一名称。
         * @param name 当前业务对象的名称。
         * @param description 说明。
         * @param itemType 物品类型。
         * @param tradable 可交易。
         * @param imageReference 图片引用。
         * @param iconUrl 图标地址。
         * @param sourceCode 来源代码。
         * @param resolutionMode 解析状态模式。
         * @param propertiesJson 配置协议数据。
         * @param attributes 属性集合。
         */
        public EntryVO {
            attributes = List.copyOf(attributes);
        }

        /**
         * 构造条目展示对象，并保存初始化参数。
         *
         * @param uniqueName 唯一名称。
         * @param name 当前业务对象的名称。
         * @param description 说明。
         * @param itemType 物品类型。
         * @param tradable 可交易。
         * @param imageReference 图片引用。
         * @param iconUrl 图标地址。
         * @param sourceCode 来源代码。
         * @param resolutionMode 解析状态模式。
         * @param propertiesJson 配置协议数据。
         */
        public EntryVO(String uniqueName, String name, String description, String itemType,
                Boolean tradable, String imageReference, String iconUrl, String sourceCode,
                String resolutionMode, String propertiesJson) {
            this(uniqueName, name, description, itemType, tradable, imageReference, iconUrl,
                    sourceCode, resolutionMode, propertiesJson, List.of());
        }
    }

    public record AttributeVO(String label, String value) {
    }
}
