package com.nnm.bot.warframe.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.nnm.bot.core.plugin.annotation.CommandTextInline;

import java.util.List;

/** 社区资料命令的展示协议，不输出管理员维护状态和三份 JSON 快照。 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record WarframeCommunityCommandDisplay(
        @JsonPropertyDescription("查询")
        String query,
        @JsonPropertyDescription("资料")
        List<?> items,
        @JsonPropertyDescription("提示")
        String notice) {

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Translation(
            @JsonPropertyDescription("完整键")
            String recordKey,
            @JsonPropertyDescription("语言")
            String locale,
            @JsonPropertyDescription("翻译")
            String displayName) {
        public static Translation from(String recordKey, WarframeCommunityTranslationDetails details) {
            return new Translation(recordKey, details.locale(), details.displayName());
        }
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record Term(
            @JsonPropertyDescription("中文")
            String chineseText,
            @JsonPropertyDescription("英文")
            String englishText) {
        public static Term from(WarframeCommunityTermDetails details) {
            return new Term(details.chineseText(), details.englishText());
        }
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record CalendarEntry(
            @JsonPropertyDescription("名称")
            String name,
            @JsonPropertyDescription("说明")
            String description) {
        public static CalendarEntry from(WarframeCommunityStateTranslationDetails details) {
            return new CalendarEntry(details.displayName(), details.description());
        }
    }

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record RivenTrend(
            @JsonPropertyDescription("武器")
            String name,
            @JsonPropertyDescription("类型")
            String weaponType,
            @JsonPropertyDescription("调整前")
            Double oldValue,
            @JsonPropertyDescription("调整后")
            Double newValue,
            @JsonPropertyDescription("更新时间")
            String updatedAt) {
        public static RivenTrend from(WarframeCommunityRivenTrendDetails details) {
            return new RivenTrend(details.trendName(), details.weaponType(),
                    details.oldNum(), details.newNum(), details.trendTime());
        }
    }
}
