package com.nnm.bot.warframe.dto;

import java.util.List;

/** 官方 PublicExport 刷新统计。 */
public record WarframeOfficialRefreshResult(
        int createdItems,
        int updatedItems,
        int disabledItems,
        int createdAliases,
        int updatedAliases,
        List<WarframeOfficialDataChange> jsonChanges
) {

    public WarframeOfficialRefreshResult(
            int createdItems,
            int updatedItems,
            int disabledItems,
            int createdAliases,
            int updatedAliases) {
        this(createdItems, updatedItems, disabledItems, createdAliases, updatedAliases, List.of());
    }

    public WarframeOfficialRefreshResult {
        jsonChanges = List.copyOf(jsonChanges == null ? List.of() : jsonChanges);
    }
}
