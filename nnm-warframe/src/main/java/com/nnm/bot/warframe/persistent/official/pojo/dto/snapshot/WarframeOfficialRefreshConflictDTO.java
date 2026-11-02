package com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** 官方刷新入口的实时冲突快照。 */
public record WarframeOfficialRefreshConflictDTO(List<TaskDTO> tasks, List<PreviewDTO> previews) {
    public record TaskDTO(UUID taskId, String productPath, String ref, String status, Instant updatedAt) {
    }

    public record PreviewDTO(String id, String productPath, String ref, Instant createdAt, Instant expiresAt) {
    }
}
