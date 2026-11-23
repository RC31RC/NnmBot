package com.nnm.bot.warframe.runtime.pojo.vo.worldstate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.nnm.bot.core.plugin.annotation.CommandTextInline;
import com.nnm.bot.warframe.runtime.constant.WarframeWorldStateResolutionStatus;
import java.util.List;

/** 仅在世界状态引用仍需人工确认时展示的字段级解析进度。 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@CommandTextInline
public record WarframeWorldStateResolutionVO(@JsonPropertyDescription("字段") String fieldPath,
    @JsonPropertyDescription("原始值") String reference, @JsonIgnore WarframeWorldStateResolutionStatus status,
    @JsonPropertyDescription("已解析") String resolvedValue, @JsonPropertyDescription("候选") List<String> candidates) {

    /**
     * 规范化待确认引用的候选列表。
     *
     * @param fieldPath 字段路径。
     * @param reference 原始值。
     * @param status 当前匹配状态。
     * @param resolvedValue 当前可用的解析结果。
     * @param candidates 可供人工选择的候选。
     */
    public WarframeWorldStateResolutionVO {
        candidates = candidates == null ? List.of() : List.copyOf(candidates);
    }

    /** @return 供管理端和命令文案展示的状态标签。 */
    @JsonProperty("status")
    @JsonPropertyDescription("状态")
    public String statusLabel() {
        return status.displayLabel();
    }
}
