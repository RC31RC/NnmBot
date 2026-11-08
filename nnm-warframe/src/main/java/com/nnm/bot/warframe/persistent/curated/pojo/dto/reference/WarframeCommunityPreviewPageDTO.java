package com.nnm.bot.warframe.persistent.curated.pojo.dto.reference;

import com.nnm.bot.common.response.PageResult;
import java.time.Instant;
import java.util.List;

public record WarframeCommunityPreviewPageDTO(String id, String dataset, Instant createdAt, Instant expiresAt,
    int inputCount, int addedCount, int changedCount, int invalidCount, List<WarframeCommunityImportIssueDTO> issues,
    PageResult<WarframeCommunityOperationDTO> operations) {
}
