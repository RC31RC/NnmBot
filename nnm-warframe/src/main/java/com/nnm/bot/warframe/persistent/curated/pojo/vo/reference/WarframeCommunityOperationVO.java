package com.nnm.bot.warframe.persistent.curated.pojo.vo.reference;

import com.nnm.bot.warframe.persistent.curated.pojo.dto.reference.WarframeCommunityInputDTO;
import java.util.Map;

public record WarframeCommunityOperationVO(String id, String key, String type, WarframeCommunityInputDTO value, Long baselineId,
                        Long baselineVersion, Map<String, WarframeCommunityFieldChangeVO> fields) {
}
