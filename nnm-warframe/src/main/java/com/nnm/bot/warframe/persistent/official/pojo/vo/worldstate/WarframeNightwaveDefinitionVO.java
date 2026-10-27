package com.nnm.bot.warframe.persistent.official.pojo.vo.worldstate;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.constant.WarframeCatalogConstants.NightwaveSection;
import java.util.List;

public record WarframeNightwaveDefinitionVO(String query, NightwaveSection section, int page, int totalPages,
        int totalCount, List<String> seasons, List<EntryVO> items) {
    /**
     * 构造夜波定义展示对象，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     * @param section 分区。
     * @param page 从一开始的页码。
     * @param totalPages 总数页数。
     * @param totalCount 总数数量。
     * @param seasons 赛季集合。
     * @param items 当前物品或成员列表。
     */
    public WarframeNightwaveDefinitionVO {
        seasons = List.copyOf(seasons);
        items = List.copyOf(items);
    }

    /**
     * 对已筛选数据执行分页。
     *
     * @param query 当前业务的查询条件。
     * @param section 分区。
     * @param seasons 赛季集合。
     * @param matches 匹配集合。
     * @param requested 请求的。
     * @return 午夜电波静态定义的分页视图。
     */
    public static WarframeNightwaveDefinitionVO page(String query, NightwaveSection section, List<String> seasons,
            List<EntryVO> matches, int requested) {
        int pages = Math.max(1, (int) ((matches.size() + 7L) / 8));
        int page = Math.max(1, Math.min(requested, pages));
        int from = (page - 1) * 8;
        return new WarframeNightwaveDefinitionVO(query, section, page, pages, matches.size(), seasons,
                matches.subList(from, Math.min(from + 8, matches.size())));
    }

    /**
     * 生成当前数据对应的文本内容。
     *
     * @return 按当前对象字段组成的文本内容。
     */
    public String text() {
        StringBuilder text = new StringBuilder("电波静态").append(section == NightwaveSection.CHALLENGES ? "挑战" : "奖励")
                .append("：").append(page).append("/").append(totalPages).append(" 页，共 ").append(totalCount).append(" 条");
        for (EntryVO entry : items) {
            text.append("\n\n").append(entry.name()).append("\n季节：").append(entry.seasonTag());
            if (entry.rewardOrder() != null) text.append("\n来源序号：").append(entry.rewardOrder());
            text.append("\n").append(entry.reference());
            if (entry.description() != null) text.append("\n").append(entry.description());
            if (section == NightwaveSection.CHALLENGES) {
                text.append("\n所需计数：").append(count(entry.requiredCount()))
                        .append("\n声望：").append(count(entry.standing()));
                if (entry.tip() != null) text.append("\n提示：").append(entry.tip());
            } else {
                text.append("\n数量：").append(count(entry.itemCount()));
            }
        }
        if (CollUtil.isEmpty(items)) text.append("\n没有匹配的已采纳静态定义");
        return text.toString();
    }

    /**
     * 计算当前条件下的成员数量。
     *
     * @param value 待处理的字段值。
     * @return 计算当前条件下的成员数量对应的文本。
     */
    private static String count(Long value) {
        return value == null ? "未提供" : value.toString();
    }

    public record EntryVO(Long id, String key, Long parentId, String seasonTag, String parentSourceCode,
            String sourceCode, String resolutionMode, String reference, Integer rewardOrder, String name,
            String description, String imageReference, String iconUrl, Long requiredCount, Long standing,
            Long itemCount, String tip, String tipImageReference, String tipIconUrl) {
    }
}
