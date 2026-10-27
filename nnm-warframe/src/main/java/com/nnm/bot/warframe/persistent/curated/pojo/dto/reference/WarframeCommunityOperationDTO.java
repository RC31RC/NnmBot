package com.nnm.bot.warframe.persistent.curated.pojo.dto.reference;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityInputDTO;
import java.util.Map;

public record WarframeCommunityOperationDTO(String id, String key, String type, WarframeCommunityInputDTO value, Long baselineId,
                        Long baselineVersion, Map<String, WarframeCommunityFieldChangeDTO> fields) {
}
