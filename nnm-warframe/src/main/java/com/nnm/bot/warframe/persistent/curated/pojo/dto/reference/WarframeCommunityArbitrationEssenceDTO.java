package com.nnm.bot.warframe.persistent.curated.pojo.dto.reference;

import lombok.Getter;
import com.nnm.bot.warframe.persistent.curated.constant.WarframeArbitrationEfficiencyGrade;
import lombok.AllArgsConstructor;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class WarframeCommunityArbitrationEssenceDTO implements WarframeCommunityRowDTO {
    private final Long id;
    private final String recordKey;
    private final Instant updatedAt;
    private final long version;
    private final String nodeKey;
    private final String nodeName;
    private final String systemName;
    private final Integer essenceAmount;
    private final WarframeArbitrationEfficiencyGrade efficiencyGrade;
}
