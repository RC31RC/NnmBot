package com.nnm.bot.warframe.persistent.curated.pojo.vo.reference;

import com.nnm.bot.common.response.PageResult;
import java.time.Instant;

public record WarframeCommunityPreviewPageVO(String id, String dataset, Instant createdAt, Instant expiresAt,
                          int inputCount, int addedCount, int changedCount, PageResult<WarframeCommunityOperationVO> operations) {
}
