package com.nnm.bot.warframe.runtime.pojo.dto.worldstate;

import java.util.List;

/** 保留无法唯一确认的原始引用及候选，供内部审核使用；展示层仅转换未完成项为字段级待确认信息。 */
public record WarframeWorldStateUnresolvedReferenceDTO(String command, String fieldPath, String reference,
    String status, List<String> candidates) {

    /**
     * 规范化审核记录，避免空候选集合及空引用影响后续聚合。
     *
     * @param command 来源命令。
     * @param fieldPath 引用字段路径。
     * @param reference 原始引用。
     * @param status 解析状态。
     * @param candidates 候选名称。
     */
    public WarframeWorldStateUnresolvedReferenceDTO {
        command = command == null ? "" : command;
        fieldPath = fieldPath == null ? "" : fieldPath;
        reference = reference == null ? "" : reference;
        status = status == null ? "" : status;
        candidates = candidates == null ? List.of() : List.copyOf(candidates);
    }
}
