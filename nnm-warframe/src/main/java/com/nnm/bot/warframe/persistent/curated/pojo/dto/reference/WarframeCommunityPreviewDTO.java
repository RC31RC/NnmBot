package com.nnm.bot.warframe.persistent.curated.pojo.dto.reference;

import java.time.Instant;
import java.util.List;

public record WarframeCommunityPreviewDTO(String id, String dataset, Instant createdAt, Instant expiresAt,
                      int inputCount, int addedCount, int changedCount, List<WarframeCommunityOperationDTO> operations) {
}
