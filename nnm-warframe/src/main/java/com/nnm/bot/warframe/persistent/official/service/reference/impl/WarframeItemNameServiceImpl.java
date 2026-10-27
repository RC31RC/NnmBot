package com.nnm.bot.warframe.persistent.official.service.reference.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceResolutionStatus;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.WarframeReferenceResolutionDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeItemNameService;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateCodebookService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** 名称来自已采纳官方/自维护整树，语言键由同一关系模型的字典解析。 */
@Service
public class WarframeItemNameServiceImpl implements WarframeItemNameService {
    @Resource
    private WarframeOfficialRecordReadService officialCatalogService;
    @Resource
    private WarframeWorldStateCodebookService codebook;

    /**
     * 批量解析引用，复用同次读取获得的资料。
     *
     * @param itemKeys 物品键集集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    @Override
    public Map<String, WarframeReferenceResolutionDTO> resolveAll(Collection<String> itemKeys) {
        Set<String> keys = itemKeys.stream().filter(StrUtil::isNotBlank).collect(Collectors.toSet());
        if (CollUtil.isEmpty(keys)) return Map.of();
        Map<String, WarframeOfficialRecordReadDTO> items = new LinkedHashMap<>();
        officialCatalogService.records().stream().filter(item -> keys.contains(item.bkey()))
                .forEach(item -> items.putIfAbsent(item.bkey(), item));
        Map<String, WarframeReferenceResolutionDTO> result = new LinkedHashMap<>();
        for (String key : keys) {
            var item = items.get(key);
            String name = key;
            String source = null;
            if (item != null) {
                if (usable(item.localizedName())) name = item.localizedName();
                else if (usable(item.officialName())) name = item.officialName();
                source = name.equals(key) ? null : item.domainCode();
            }
            if (source == null) {
                var entry = codebook.findLanguage(key);
                if (entry.isPresent()) {
                    name = entry.get().displayName();
                    source = entry.get().source();
                }
            }
            result.put(key, new WarframeReferenceResolutionDTO(key, name, key, source,
                    source == null ? WarframeReferenceResolutionStatus.UNRESOLVED : WarframeReferenceResolutionStatus.RESOLVED,
                    source == null ? List.of() : List.of(key)));
        }
        return Map.copyOf(result);
    }

    /**
     * 判断是否满足可用条件。
     *
     * @param name 当前业务对象的名称。
     * @return 满足本方法说明的业务条件时返回真，否则返回假。
     */
    private static boolean usable(String name) {
        return StrUtil.isNotBlank(name) && !name.startsWith("/");
    }
}
