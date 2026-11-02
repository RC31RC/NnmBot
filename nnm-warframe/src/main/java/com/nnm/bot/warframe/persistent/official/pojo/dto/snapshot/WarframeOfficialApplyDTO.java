package com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot;

import java.util.List;

/** 官方预览的部分采纳命令。 */
public record WarframeOfficialApplyDTO(List<WarframeOfficialRecordSelectionDTO> changes) {
    public WarframeOfficialApplyDTO {
        changes = changes == null ? List.of() : List.copyOf(changes);
    }
}
