package com.nnm.bot.warframe.runtime.pojo.vo.worldstate;

import java.util.List;

/** 世界状态 Java 模型字段检查视图。 */
public record WarframeWorldStateFieldInspectionVO(boolean available, boolean protocolModelAvailable, String source,
    String fetchedAt, String contentHash, List<WarframeWorldStateFieldSectionVO> sections) {

    /**
     * 规范化字段分区，避免响应携带可变集合。
     *
     * @param available 是否存在有效世界状态快照。
     * @param protocolModelAvailable 是否保留了类型化上游协议模型。
     * @param source 快照来源。
     * @param fetchedAt 快照抓取时间。
     * @param contentHash 原始响应摘要。
     * @param sections 与可视化页签一一对应的协议字段、解析字段和待确认项。
     */
    public WarframeWorldStateFieldInspectionVO {
        sections = sections == null ? List.of() : List.copyOf(sections);
    }
}
