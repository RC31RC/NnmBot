package com.nnm.bot.warframe.persistent.official.pojo.vo.upgrades;

import java.math.BigDecimal;
import java.util.List;

public record WarframeRivenDefinitionVO(
        String query, String mode, List<EntryVO> items, int page, int totalPages, int totalCount) {
    /**
     * 构造紫卡定义展示对象，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     * @param mode 模式。
     * @param items 当前物品或成员列表。
     * @param page 从一开始的页码。
     * @param totalPages 总数页数。
     * @param totalCount 总数数量。
     */
    public WarframeRivenDefinitionVO {
        items = List.copyOf(items);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param query 当前业务的查询条件。
     * @param mode 模式。
     * @param entries 条目集合。
     * @param page 从一开始的页码。
     * @return 紫卡定义的分页视图。
     */
    public static WarframeRivenDefinitionVO page(String query, String mode, List<EntryVO> entries, int page) {
        if (page < 1) throw new IllegalArgumentException("Page must be positive");
        int from = (int) Math.min((page - 1L) * 6, entries.size());
        return new WarframeRivenDefinitionVO(query, mode,
                entries.subList(from, Math.min(from + 6, entries.size())),
                page, Math.max(1, (int) ((entries.size() + 5L) / 6)), entries.size());
    }

    /**
     * 生成当前数据的分页说明。
     *
     * @return 当前数据的分页说明对应的文本。
     */
    public String pagination() {
        return page + "/" + totalPages + " 页，共 " + totalCount + " 条";
    }

    public record EntryVO(String modKey, String modName, String recordKey, String sourceCode,
                        AttributeVO attribute, ChallengeVO challenge) {
    }

    public record AttributeVO(String tag, String prefixKey, String prefix, String suffixKey, String suffix,
                            Boolean canBeBuff, Boolean canBeCurse, List<VO> values) {
        /**
         * 构造属性展示对象，并保存初始化参数。
         *
         * @param tag 标签。
         * @param prefixKey 前缀键。
         * @param prefix 前缀。
         * @param suffixKey 后缀键。
         * @param suffix 后缀。
         * @param canBeBuff 可以允许增益。
         * @param canBeCurse 可以允许诅咒。
         * @param values 待处理的值集合。
         */
        public AttributeVO {
            values = List.copyOf(values);
        }
    }

    public record VO(BigDecimal baseCoefficient, String localizationKey, String description,
                        Boolean reverseValueSymbol) {
    }

    public record ChallengeVO(String challengeKey, String descriptionKey, String description,
                            String singleDescriptionKey, String singleDescription,
                            Integer countMin, Integer countMax, BigDecimal complicationChance,
                            List<ComplicationVO> complications) {
        /**
         * 构造挑战展示对象，并保存初始化参数。
         *
         * @param challengeKey 挑战键。
         * @param descriptionKey 说明键。
         * @param description 说明。
         * @param singleDescriptionKey 单条说明键。
         * @param singleDescription 单条说明。
         * @param countMin 数量最小。
         * @param countMax 数量最大。
         * @param complicationChance 附加条件几率。
         * @param complications 附加条件集合。
         */
        public ChallengeVO {
            complications = List.copyOf(complications);
        }
    }

    public record ComplicationVO(String complicationKey, String descriptionKey, String description,
                               BigDecimal countMultiplier, BigDecimal selectionWeight) {
    }
}
