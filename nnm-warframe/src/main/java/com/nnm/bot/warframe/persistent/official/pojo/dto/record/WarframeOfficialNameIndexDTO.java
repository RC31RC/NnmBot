package com.nnm.bot.warframe.persistent.official.pojo.dto.record;

import com.nnm.bot.warframe.runtime.cache.dataset.DatasetSnapshotMetrics;
import com.nnm.bot.warframe.persistent.official.constant.OfficialDataConstants;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialReferenceKeys;
import com.nnm.bot.warframe.normalization.WarframeNameNormalizer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** 单个官方主表的轻量不可变索引，不包含社区别名或业务详情。 */
public record WarframeOfficialNameIndexDTO(Map<String, WarframeOfficialNameDTO> items,
    Map<String, List<String>> names) implements DatasetSnapshotMetrics {
    /** @return 本数据集快照拥有的业务记录数。 */
    @Override
    public long entryCount() {
        return items.size();
    }

    /**
     * 标准名称使用名称索引，完整路径只允许精确身份及合同明确声明的分组身份。
     *
     * @param domain 当前索引所属领域。
     * @param reference 原始完整路径或标准名称。
     * @return 不重复的候选身份；不以删除路径标点来确认对象。
     */
    public List<String> matches(WarframeOfficialDataDomain domain, String reference) {
        Set<String> matches = new LinkedHashSet<>();
        for (String candidate : WarframeOfficialReferenceKeys.candidates(reference)) {
            if (!candidate.startsWith("/")) {
                matches.addAll(names.getOrDefault(WarframeNameNormalizer.item(candidate), List.of()));
                continue;
            }
            String direct = identity(domain.domainCode(), candidate);
            if (items.containsKey(direct))
                matches.add(direct);
            for (String group : OfficialDataConstants.RECORD_GROUPS.getOrDefault(domain, Set.of())) {
                String grouped = identity(domain.domainCode(), group + "/" + candidate);
                if (items.containsKey(grouped))
                    matches.add(grouped);
            }
        }
        return List.copyOf(matches);
    }

    /**
     * 各领域独立保留同名对象的身份，键比较忽略大小写。
     *
     * @param domainCode 独立领域代码。
     * @param bkey 稳定业务键。
     * @return 名称索引内的确定身份。
     */
    public static String identity(String domainCode, String bkey) {
        return domainCode + ":" + bkey.toLowerCase(Locale.ROOT);
    }
}
