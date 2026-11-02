package com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot;

import java.util.Map;
import java.util.Set;

/** 过滤已采纳差异后新冻结的剩余审核状态。 */
public record OfficialFilteredChangesDTO(String objectKey, Map<String, Integer> remainingCounts,
    Set<String> remainingPaths, Set<String> selectedIdentities, int matchedSelections) {
    public OfficialFilteredChangesDTO {
        remainingCounts = Map.copyOf(remainingCounts);
        remainingPaths = Set.copyOf(remainingPaths);
        selectedIdentities = Set.copyOf(selectedIdentities);
    }
}
