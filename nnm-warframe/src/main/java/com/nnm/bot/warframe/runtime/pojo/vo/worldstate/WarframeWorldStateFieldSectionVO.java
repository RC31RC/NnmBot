package com.nnm.bot.warframe.runtime.pojo.vo.worldstate;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 世界状态字段视图中的可视化分区对应项。 */
public record WarframeWorldStateFieldSectionVO(String key, String title, boolean available, int itemCount,
    Map<String, Object> protocolModel, Map<String, Object> parsedModel, Map<String, Object> projectionModel,
    List<WarframeWorldStateResolutionVO> pendingResolutions) {

    /**
     * 复制字段树和待确认项，避免响应携带可变集合。
     *
     * @param key 与可视化页签一致的稳定分区键。
     * @param title 与可视化页签一致的标题。
     * @param available 当前分区是否有数据。
     * @param itemCount 当前分区的数据条数。
     * @param protocolModel 类型化上游输入模型中与分区有关的字段。
     * @param parsedModel 业务解析模型中与分区有关的字段。
     * @param projectionModel 含可视化结果的结构化模型，字段路径与待确认诊断一致。
     * @param pendingResolutions 当前分区尚未完成的引用解析项。
     */
    public WarframeWorldStateFieldSectionVO {
        protocolModel =
            protocolModel == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(protocolModel));
        parsedModel = parsedModel == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(parsedModel));
        projectionModel =
            projectionModel == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(projectionModel));
        pendingResolutions = pendingResolutions == null ? List.of() : List.copyOf(pendingResolutions);
    }
}
