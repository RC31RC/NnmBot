package com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot;

import java.util.Map;
import java.util.Set;

/** 过滤已采纳差异后新冻结的剩余审核状态。 */
public record OfficialFilteredChangesDTO(String objectKey, Map<String, Integer> remainingCounts,
    Map<String, Map<String, Integer>> remainingFileCounts, Set<String> remainingPaths, Set<String> selectedPaths,
    int matchedSelections) {
    public OfficialFilteredChangesDTO {
        remainingCounts = Map.copyOf(remainingCounts);
        remainingFileCounts = new OfficialChangeCountsDTO(remainingFileCounts).files();
        remainingPaths = Set.copyOf(remainingPaths);
        selectedPaths = Set.copyOf(selectedPaths);
    }
}
