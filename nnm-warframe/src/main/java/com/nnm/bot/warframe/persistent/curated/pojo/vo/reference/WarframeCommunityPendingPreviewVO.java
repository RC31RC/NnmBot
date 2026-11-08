package com.nnm.bot.warframe.persistent.curated.pojo.vo.reference;

import java.time.Instant;

/** 社区待审核预览的管理员列表项。 */
public record WarframeCommunityPendingPreviewVO(String id, String dataset, Instant createdAt, Instant expiresAt,
    int inputCount, int addedCount, int changedCount, int invalidCount) {
}
