package com.nnm.bot.warframe.persistent.official.service.record.impl;

import com.nnm.bot.warframe.config.cache.WarframeDatasetCache;
import com.nnm.bot.warframe.constant.WarframeDatasetCacheNames;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialNameDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialNameIndexDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialLookupService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.util.WarframeNameNormalizer;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class WarframeOfficialLookupServiceImpl implements WarframeOfficialLookupService {
    @Resource
    private WarframeDatasetCache cache;
    @Resource
    private WarframeOfficialRecordReadService records;

    /**
     * 按数据集缓存轻量名称源；各自更新，互不连带清空。
     *
     * @param domain 独立 JSON 领域。
     * @return 不可变轻量名称列表。
     */
    @Override
    public WarframeOfficialNameIndexDTO nameIndex(WarframeOfficialDataDomain domain) {
        return cache.snapshot(WarframeDatasetCacheNames.official(domain), WarframeDatasetCacheNames.NAMES,
            () -> buildNameIndex(domain));
    }

    /**
     * 构建一个独立主表的名称索引；同名候选不会相互覆盖。
     *
     * @param domain 独立 JSON 领域。
     * @return 不可变轻量索引。
     */
    private WarframeOfficialNameIndexDTO buildNameIndex(WarframeOfficialDataDomain domain) {
        Map<String, WarframeOfficialNameDTO> items = new LinkedHashMap<>();
        Map<String, List<String>> names = new LinkedHashMap<>();
        for (var row : records.names(domain)) {
            String key = WarframeOfficialNameIndexDTO.identity(domain.domainCode(), row.bkey());
            items.put(key, row);
            String terminal = row.bkey().substring(row.bkey().lastIndexOf('/') + 1);
            Stream.of(row.bkey(), row.officialName(), row.localizedName(), terminal).filter(Objects::nonNull)
                .map(WarframeNameNormalizer::item).filter(value -> !value.isBlank()).distinct()
                .forEach(name -> names.computeIfAbsent(name, ignored -> new ArrayList<>()).add(key));
        }
        Map<String, List<String>> frozen = new LinkedHashMap<>();
        names.forEach((key, values) -> frozen.put(key, List.copyOf(values)));
        return new WarframeOfficialNameIndexDTO(Map.copyOf(items), Map.copyOf(frozen));
    }

    /**
     * 合并本次同表引用并执行真正的批量补查。
     *
     * @param domain 独立 JSON 领域。
     * @param keys 本次需要的业务键。
     * @return 不包含查无记录占位值的查询结果。
     */
    @Override
    public Map<String, WarframeOfficialRecordReadDTO> records(WarframeOfficialDataDomain domain,
        Collection<String> keys) {
        List<String> normalized = keys.stream().filter(key -> key != null && !key.isBlank())
            .map(key -> key.trim().toLowerCase(Locale.ROOT)).distinct().toList();
        return cache.records(WarframeDatasetCacheNames.official(domain), normalized, missing -> {
            Map<String, WarframeOfficialRecordReadDTO> loaded = new LinkedHashMap<>();
            records.recordsByKeys(domain, List.copyOf(missing))
                .forEach(row -> loaded.put(row.bkey().toLowerCase(Locale.ROOT), row));
            return loaded;
        });
    }

    /**
     * 大关系表先由数据库筛选主键，详情只补查缓存缺失的主记录及所属子树。
     *
     * @param domain 独立 JSON 领域。
     * @param ids 已筛选主键。
     * @return 主键对应的详情投影。
     */
    @Override
    public Map<Long, WarframeOfficialRecordReadDTO> recordsByIds(WarframeOfficialDataDomain domain,
        Collection<Long> ids) {
        return cache.records(WarframeDatasetCacheNames.official(domain), ids, missing -> {
            Map<Long, WarframeOfficialRecordReadDTO> loaded = new LinkedHashMap<>();
            records.recordsByIds(domain, List.copyOf(missing)).forEach(row -> loaded.put(row.databaseId(), row));
            return loaded;
        });
    }
}
