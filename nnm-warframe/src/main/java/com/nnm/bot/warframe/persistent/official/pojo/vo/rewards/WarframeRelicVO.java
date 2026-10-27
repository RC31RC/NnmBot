package com.nnm.bot.warframe.persistent.official.pojo.vo.rewards;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nnm.bot.warframe.persistent.official.pojo.dto.rewards.WarframeRelicDetailsDTO;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.nnm.bot.core.plugin.annotation.CommandTextInline;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/** 遗物及奖励的命令展示字段，数据库关联键不参与用户输出。 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record WarframeRelicVO(
        @JsonPropertyDescription("查询")
        String query,
        @JsonPropertyDescription("遗物")
        List<RelicVO> items,
        @JsonPropertyDescription("奖励")
        List<RewardVO> rewards,
        @JsonPropertyDescription("分页")
        String pagination,
        @JsonPropertyDescription("提示")
        String notice) {

    private static final int DETAIL_PAGE_SIZE = 10;
    private static final int REWARD_PAGE_SIZE = 8;
    private static final int PAGE_TEXT_BUDGET = 480;

    /**
     * 查询遗物与奖励的对应资料。
     *
     * @param query 当前业务的查询条件。
     * @param details 详情集合。
     * @param page 从一开始的页码。
     * @return 遗物精炼等级及奖励的展示结果。
     */
    public static WarframeRelicVO relics(String query, List<WarframeRelicDetailsDTO> details, int page) {
        return relics(query, details, page, relic -> null);
    }

    /**
     * 查询遗物与奖励的对应资料。
     *
     * @param query 当前业务的查询条件。
     * @param details 详情集合。
     * @param page 从一开始的页码。
     * @param iconResolver 图标解析器。
     * @return 遗物精炼等级及奖励的展示结果。
     */
    public static WarframeRelicVO relics(String query, List<WarframeRelicDetailsDTO> details, int page,
            Function<WarframeRelicDetailsDTO.RelicDTO, String> iconResolver) {
        long totalPages = 0;
        List<RelicVO> selected = new ArrayList<>();
        for (WarframeRelicDetailsDTO detail : details) {
            List<Integer> weights = new ArrayList<>();
            detail.rewards().forEach(reward -> weights.add(Math.max(40, length(reward.rewardName()))));
            detail.drops().forEach(drop -> weights.add(Math.max(40, length(drop.locationText()))));
            List<RangeVO> ranges = ranges(weights, DETAIL_PAGE_SIZE);
            int pages = ranges.size();
            if (page > totalPages && page <= totalPages + pages) {
                int localPage = (int) (page - totalPages);
                int start = ranges.get(localPage - 1).start();
                int end = ranges.get(localPage - 1).end();
                int rewardCount = detail.rewards().size();
                List<RewardVO> rewards = detail.rewards().subList(Math.min(start, rewardCount), Math.min(end, rewardCount))
                        .stream().map(RewardVO::from).toList();
                List<DropVO> drops = detail.drops().subList(Math.max(0, start - rewardCount),
                                Math.max(0, end - rewardCount))
                        .stream().map(DropVO::from).toList();
                selected.add(new RelicVO(name(detail.relic().relicName()), detail.relic().vaulted(),
                        rewards, drops, detail.relic().quality(),
                        localPage + "/" + pages + " 页，" + rewardCount + " 奖励 / "
                                + detail.drops().size() + " 地点", detail.relic().icon(),
                        detail.relic().iconUrl(),
                        detail.relic().sourceCode(), "SOURCE_SELECTED"));
            }
            totalPages += pages;
        }
        return new WarframeRelicVO(query, List.copyOf(selected), List.of(),
                page + "/" + Math.max(1, totalPages) + " 页，共 " + details.size() + " 个遗物",
                CollUtil.isEmpty(selected) ? (CollUtil.isEmpty(details) ? "没有找到匹配的遗物"
                        : "此页没有记录，请查询前面的页码") : null);
    }

    /**
     * 按目标奖励与稀有度筛选遗物奖励，并组装分页展示结果。
     * @param query 当前业务的查询条件。
     * @param details 详情集合。
     * @param rarity 稀有度。
     * @param page 从一开始的页码。
     * @return 遗物精炼等级及奖励的展示结果。
     */
    public static WarframeRelicVO rewards(
            String query, List<WarframeRelicDetailsDTO> details, String rarity, int page) {
        List<RewardVO> all = details.stream().flatMap(detail -> detail.rewards().stream()
                .filter(reward -> rarity.equalsIgnoreCase(reward.rarity()))
                .filter(reward -> StrUtil.isBlank(query)
                        || StrUtil.containsIgnoreCase(detail.relic().relicName(), query)
                        || StrUtil.containsIgnoreCase(detail.relic().recordKey(), query)
                        || StrUtil.containsIgnoreCase(reward.rewardName(), query))
                .map(reward -> RewardVO.from(reward, name(detail.relic().relicName())))).toList();
        List<RangeVO> ranges = ranges(all.stream()
                .map(reward -> Math.max(60, length(reward.rewardName()) + length(reward.relicName())))
                .toList(), REWARD_PAGE_SIZE);
        List<RewardVO> selected = page <= ranges.size()
                ? all.subList(ranges.get(page - 1).start(), ranges.get(page - 1).end()) : List.of();
        return new WarframeRelicVO(query, List.of(), List.copyOf(selected),
                page + "/" + ranges.size() + " 页，共 " + all.size() + " 条奖励",
                CollUtil.isEmpty(selected) ? (CollUtil.isEmpty(all) ? "没有找到匹配的遗物奖励"
                        : "此页没有记录，请查询前面的页码") : null);
    }

    /**
     * 按奖励权重计算展示表中各奖励所占的行区间。
     * @param weights 权重集合。
     * @param maxRows 最大数据行。
     * @return 按当前条件筛选或转换后的数据集合。
     */
    private static List<RangeVO> ranges(List<Integer> weights, int maxRows) {
        List<RangeVO> ranges = new ArrayList<>();
        int start = 0;
        int weight = 0;
        // 行数与文本量同时限额；单条长文本完整保留，不通过 CSS 隐藏溢出内容。
        for (int index = 0; index < weights.size(); index++) {
            if (index > start && (index - start >= maxRows || weight + weights.get(index) > PAGE_TEXT_BUDGET)) {
                ranges.add(new RangeVO(start, index));
                start = index;
                weight = 0;
            }
            weight += weights.get(index);
        }
        ranges.add(new RangeVO(start, weights.size()));
        return ranges;
    }

    private record RangeVO(int start, int end) {}

    /**
     * 计算当前文本或集合的有效长度。
     *
     * @param value 待校验或规范化的文本。
     * @return 当前操作计算或读取的数值。
     */
    private static int length(String value) {
        return value == null ? 0 : value.length();
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record RelicVO(
            @JsonPropertyDescription("遗物")
            String relicName,
            @JsonPropertyDescription("入库")
            Boolean vaulted,
            @JsonPropertyDescription("奖励")
            List<RewardVO> rewards,
            @JsonPropertyDescription("获取地点")
            List<DropVO> drops,
            @JsonPropertyDescription("精炼")
            String quality,
            @JsonPropertyDescription("明细分页")
            String detailPagination,
            @JsonPropertyDescription("原始图片引用")
            String imageReference,
            @JsonPropertyDescription("图标")
            String iconUrl,
            @JsonPropertyDescription("数据来源")
            @JsonIgnore
            String sourceCode,
            @JsonPropertyDescription("来源选择")
            String resolutionMode) {}

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record RewardVO(
            @JsonPropertyDescription("物品")
            String rewardName,
            @JsonPropertyDescription("稀有度")
            String rarity,
            @JsonPropertyDescription("概率（%）")
            BigDecimal chance,
            @JsonPropertyDescription("所属遗物")
            String relicName) {

        /**
         * 将输入对象转换为当前业务数据对象。
         *
         * @param reward 奖励。
         * @return 单条奖励及其数量、概率的展示内容。
         */
        public static RewardVO from(WarframeRelicDetailsDTO.RewardDTO reward) {
            return from(reward, null);
        }

        /**
         * 将输入对象转换为当前业务数据对象。
         *
         * @param reward 奖励。
         * @param relicName 遗物名称。
         * @return 单条奖励及其数量、概率的展示内容。
         */
        private static RewardVO from(WarframeRelicDetailsDTO.RewardDTO reward, String relicName) {
            return new RewardVO(name(reward.rewardName()), rarityName(reward.rarity()),
                    reward.chance(), relicName);
        }
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record DropVO(
            @JsonPropertyDescription("地点")
            String location,
            @JsonPropertyDescription("稀有度")
            String rarity,
            @JsonPropertyDescription("概率（%）")
            BigDecimal chance) {

        /**
         * 将输入对象转换为当前业务数据对象。
         *
         * @param drop 掉落。
         * @return 单条掉落来源的展示内容。
         */
        private static DropVO from(WarframeRelicDetailsDTO.DropDTO drop) {
            return new DropVO(drop.locationText(), rarityName(drop.rarity()), drop.chance());
        }
    }

    /**
     * 将奖励稀有度代码转换为中文名称。
     * @param rarity 稀有度。
     * @return 奖励稀有度的中文名称。
     */
    private static String rarityName(String rarity) {
        return switch (rarity == null ? "" : rarity.trim().toLowerCase(Locale.ROOT)) {
            case "rare" -> "稀有";
            case "uncommon" -> "罕见";
            case "common" -> "常见";
            default -> rarity;
        };
    }

    /**
     * 取得优先使用的业务名称。
     *
     * @param value 待校验或规范化的文本。
     * @return 优先使用的业务名称对应的文本。
     */
    private static String name(String value) {
        return StrUtil.isBlank(value) ? "名称未提供" : value;
    }
}
