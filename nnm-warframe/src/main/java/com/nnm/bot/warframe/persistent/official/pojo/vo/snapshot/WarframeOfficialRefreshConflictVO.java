package com.nnm.bot.warframe.persistent.official.pojo.vo.snapshot;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** 提示管理员先取消任务或处理预览，不能将查询结果作为写入时的授权凭证。 */
public record WarframeOfficialRefreshConflictVO(List<TaskVO> tasks, List<PreviewVO> previews) {
    public record TaskVO(UUID taskId, String productPath, String ref, String operation, String status,
        Instant updatedAt) {
    }

    public record PreviewVO(String id, String productPath, String ref, String status, Instant createdAt,
        Instant expiresAt) {
    }
}
