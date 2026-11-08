package com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot;

import java.util.List;
import java.util.Set;

/** 官方预览的分类跨页选择及单项覆盖；类别使用冻结差异的 ADDED/CHANGED/REMOVED。 */
public record WarframeOfficialApplyDTO(List<WarframeOfficialRecordSelectionDTO> changes,
    Set<WarframeOfficialFileCategoryDTO> allGroups, List<WarframeOfficialRecordSelectionDTO> excluded) {
    public WarframeOfficialApplyDTO {
        changes = changes == null ? List.of() : List.copyOf(changes);
        allGroups = allGroups == null ? Set.of() : Set.copyOf(allGroups);
        excluded = excluded == null ? List.of() : List.copyOf(excluded);
    }
}
