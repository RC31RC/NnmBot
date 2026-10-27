package com.nnm.bot.warframe.persistent.official.pojo.vo.crafting;

import java.util.List;

public record WarframeRecipeVO(String query, List<EntryVO> items, int page, int totalPages, int totalCount) {
    /**
     * 构造蓝图展示对象，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     * @param items 当前物品或成员列表。
     * @param page 从一开始的页码。
     * @param totalPages 总数页数。
     * @param totalCount 总数数量。
     */
    public WarframeRecipeVO {
        items = List.copyOf(items);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param query 当前业务的查询条件。
     * @param entries 条目集合。
     * @param page 从一开始的页码。
     * @return 制作配方的分页视图。
     */
    public static WarframeRecipeVO page(String query, List<EntryVO> entries, int page) {
        if (page < 1) throw new IllegalArgumentException("Page must be positive");
        int from = (int) Math.min((long) (page - 1) * 4, entries.size());
        return new WarframeRecipeVO(query, entries.subList(from, Math.min(from + 4, entries.size())),
                page, Math.max(1, (int) ((entries.size() + 3L) / 4)), entries.size());
    }

    /**
     * 生成当前数据的分页说明。
     *
     * @return 当前数据的分页说明对应的文本。
     */
    public String pagination() {
        return page + "/" + totalPages + " 页，共 " + totalCount + " 条";
    }

    public record EntryVO(String recordKey, String uniqueName, String name, String recipeKind, String dojoSection,
                        String productKey, String productName, Long buildPrice, Long buildTime,
                        Long skipTimePrice, Long replicatePrice, Long resultCount, String quantityBasis,
                        List<IngredientVO> ingredients, String imageReference, String iconUrl,
                        String sourceCode, String resolutionMode) {
        /**
         * 构造条目展示对象，并保存初始化参数。
         *
         * @param recordKey 记录键。
         * @param uniqueName 唯一名称。
         * @param name 当前业务对象的名称。
         * @param recipeKind 蓝图类型。
         * @param dojoSection 氏族道场分区。
         * @param productKey 文件产品键。
         * @param productName 文件产品名称。
         * @param buildPrice 构造价格。
         * @param buildTime 构造时间。
         * @param skipTimePrice 跳过时间价格。
         * @param replicatePrice 同步价格。
         * @param resultCount 结果数量。
         * @param quantityBasis 数量依据。
         * @param ingredients 材料集合。
         * @param imageReference 图片引用。
         * @param iconUrl 图标地址。
         * @param sourceCode 来源代码。
         * @param resolutionMode 解析状态模式。
         */
        public EntryVO {
            ingredients = List.copyOf(ingredients);
        }

        /**
         * 附带图标地址。
         *
         * @param url 本次外部请求或资源的地址。
         * @return 当前资料的单条展示内容。
         */
        public EntryVO withIconUrl(String url) {
            return new EntryVO(recordKey, uniqueName, name, recipeKind, dojoSection, productKey, productName,
                    buildPrice, buildTime, skipTimePrice, replicatePrice, resultCount, quantityBasis,
                    ingredients, imageReference, url, sourceCode, resolutionMode);
        }
    }

    public record IngredientVO(String itemKey, String name, long count) {
    }
}
