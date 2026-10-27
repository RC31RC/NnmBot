package com.nnm.bot.warframe.persistent.official.pojo.vo.worldstate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.nnm.bot.core.plugin.annotation.CommandTextInline;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeStateTranslationDetailsDTO;
import java.util.List;

/** 官方日历任务字典的展示协议。 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record WarframeCalendarCommandVO(
        @JsonPropertyDescription("查询")
        String query,
        @JsonPropertyDescription("资料")
        List<CalendarEntryVO> items,
        @JsonPropertyDescription("提示")
        String notice) {

    @CommandTextInline
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record CalendarEntryVO(
            @JsonPropertyDescription("名称")
            String name,
            @JsonPropertyDescription("说明")
            String description) {
        /**
         * 将输入对象转换为当前业务数据对象。
         *
         * @param details 详情。
         * @return 单个日历活动的展示内容。
         */
        public static CalendarEntryVO from(WarframeStateTranslationDetailsDTO details) {
            return new CalendarEntryVO(details.displayName(), details.description());
        }
    }
}
