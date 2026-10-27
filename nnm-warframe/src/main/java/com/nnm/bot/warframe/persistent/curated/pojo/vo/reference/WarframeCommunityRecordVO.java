package com.nnm.bot.warframe.persistent.curated.pojo.vo.reference;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityInputDTO;
import java.time.Instant;

public record WarframeCommunityRecordVO(Long id, String key, WarframeCommunityInputDTO value, Instant updatedAt, long version) {
}
