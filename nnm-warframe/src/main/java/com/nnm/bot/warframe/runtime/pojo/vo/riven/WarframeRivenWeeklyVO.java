package com.nnm.bot.warframe.runtime.pojo.vo.riven;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record WarframeRivenWeeklyVO(
        @JsonPropertyDescription("查询")
        String query,
        @JsonPropertyDescription("平台")
        String platform,
        @JsonPropertyDescription("分页")
        String pagination,
        @JsonPropertyDescription("数据来源")
        String sourceCode,
        @JsonPropertyDescription("拉取时间")
        Instant fetchedAt,
        @JsonPropertyDescription("过期快照")
        boolean stale,
        @JsonPropertyDescription("周成交统计")
        List<EntryVO> items,
        @JsonPropertyDescription("提示")
        String notice) {
    /**
     * 构造紫卡每周展示对象，并保存初始化参数。
     *
     * @param query 当前业务的查询条件。
     * @param platform 市场或官方接口使用的平台代码。
     * @param pagination 分页。
     * @param sourceCode 来源代码。
     * @param fetchedAt 拉取时间时刻。
     * @param stale 过期。
     * @param items 当前物品或成员列表。
     * @param notice 提示。
     */
    public WarframeRivenWeeklyVO {
        items = List.copyOf(items);
    }

    /**
     * 拉取时间时刻标签标题。
     *
     * @return 拉取时间时刻标签标题对应的文本。
     */
    @JsonProperty("fetchedAtLabel")
    @JsonPropertyDescription("拉取时间（UTC）")
    public String fetchedAtLabel() {
        return fetchedAt == null ? null : DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm 'UTC'")
                .withZone(ZoneOffset.UTC).format(fetchedAt);
    }

    public record EntryVO(
            @JsonPropertyDescription("武器")
            String weaponName,
            @JsonPropertyDescription("武器英文名")
            String compatibility,
            @JsonPropertyDescription("紫卡类型")
            String itemType,
            @JsonPropertyDescription("已洗")
            boolean rerolled,
            @JsonPropertyDescription("均价")
            BigDecimal avg,
            @JsonPropertyDescription("中位价")
            BigDecimal median,
            @JsonPropertyDescription("最低价")
            BigDecimal min,
            @JsonPropertyDescription("最高价")
            BigDecimal max,
            @JsonPropertyDescription("价格标准差")
            BigDecimal stddev,
            @JsonPropertyDescription("相对交易热度")
            BigDecimal pop,
            @JsonPropertyDescription("匹配成功的静态武器 uniqueName；未匹配或歧义时为空")
            String weaponUniqueName) {}
}
