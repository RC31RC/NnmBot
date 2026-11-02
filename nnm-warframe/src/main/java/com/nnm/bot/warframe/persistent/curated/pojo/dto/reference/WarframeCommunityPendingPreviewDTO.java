package com.nnm.bot.warframe.persistent.curated.pojo.dto.reference;

import java.time.Instant;

/** 社区待审核预览的轻量索引，不重复存储冻结的业务差异。 */
public record WarframeCommunityPendingPreviewDTO(String id, String dataset, Instant createdAt, Instant expiresAt,
    int inputCount, int addedCount, int changedCount) {
    /**
     * 从冻结预览提取供列表展示的轻量索引。
     *
     * @param preview 冻结的社区差异。
     * @return 不含全部操作的待审核索引。
     */
    public static WarframeCommunityPendingPreviewDTO from(WarframeCommunityPreviewDTO preview) {
        return new WarframeCommunityPendingPreviewDTO(preview.id(), preview.dataset(), preview.createdAt(),
            preview.expiresAt(), preview.inputCount(), preview.addedCount(), preview.changedCount());
    }
}
