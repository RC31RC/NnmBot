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

    /**
     * 查询指定文件和差异分类的数量。
     *
     * @param path 官方 JSON 相对路径。
     * @param category 差异分类编码。
     * @return 对应记录数；没有计数时返回零。
     */
    public int count(String path, String category) {
        return files.getOrDefault(path, Map.of()).getOrDefault(category, 0);
    }

    /**
     * 汇总所有文件中各差异分类的数量。
     *
     * @return 分类编码到总记录数的不可变映射。
     */
    public Map<String, Integer> totals() {
        Map<String, Integer> totals = new HashMap<>();
        files.values()
            .forEach(counts -> counts.forEach((category, count) -> totals.merge(category, count, Math::addExact)));
        return Map.copyOf(totals);
    }
}
