package com.nnm.bot.warframe.persistent.official.pojo.dto.snapshot;

import java.util.HashMap;
import java.util.Map;

/** 每个官方 JSON 文件的差异计数；不为单条业务记录建元数据行。 */
public record OfficialChangeCountsDTO(Map<String, Map<String, Integer>> files) {
    public OfficialChangeCountsDTO {
        Map<String, Map<String, Integer>> copy = new HashMap<>();
        files.forEach((path, counts) -> copy.put(path, Map.copyOf(counts)));
        files = Map.copyOf(copy);
    }

    public int count(String path, String category) {
        return files.getOrDefault(path, Map.of()).getOrDefault(category, 0);
    }

    public Map<String, Integer> totals() {
        Map<String, Integer> totals = new HashMap<>();
        files.values()
            .forEach(counts -> counts.forEach((category, count) -> totals.merge(category, count, Math::addExact)));
        return Map.copyOf(totals);
    }
}
