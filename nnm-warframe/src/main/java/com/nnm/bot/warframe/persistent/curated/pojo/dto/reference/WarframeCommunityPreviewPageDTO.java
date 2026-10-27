package com.nnm.bot.warframe.persistent.curated.pojo.dto.reference;

import com.nnm.bot.common.response.PageResult;
import java.time.Instant;

public record WarframeCommunityPreviewPageDTO(String id, String dataset, Instant createdAt, Instant expiresAt,
                          int inputCount, int addedCount, int changedCount, PageResult<WarframeCommunityOperationDTO> operations) {
}
