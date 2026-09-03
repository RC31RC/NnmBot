package com.nnm.bot.warframe.dto;

import java.util.List;

/** 一条官方 PublicExport 记录相对上次同步的 JSON 变化。 */
public record WarframeOfficialDataChange(
        String uniqueName,
        String localizedName,
        String changeType,
        List<String> addedFields,
        List<String> changedFields,
        List<String> removedFields
) {

    public WarframeOfficialDataChange {
        addedFields = List.copyOf(addedFields == null ? List.of() : addedFields);
        changedFields = List.copyOf(changedFields == null ? List.of() : changedFields);
        removedFields = List.copyOf(removedFields == null ? List.of() : removedFields);
    }
}
