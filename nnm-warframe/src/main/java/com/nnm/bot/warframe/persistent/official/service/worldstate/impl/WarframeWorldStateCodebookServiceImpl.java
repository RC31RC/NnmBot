package com.nnm.bot.warframe.persistent.official.service.worldstate.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.cache.worldstate.WarframeOfficialWorldStateIndex;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceResolutionStatus;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceType;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeWorldStateCodebookEntryDTO;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateCodebookService;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

/** 各代码表独立缓存，语言字典只装载被引用的键。 */
@Service
public class WarframeWorldStateCodebookServiceImpl implements WarframeWorldStateCodebookService {
    @Resource
    private WarframeOfficialWorldStateIndex officialIndex;

    /**
     * 从一个独立代码表读取引用。
     *
     * @param type 引用类型。
     * @param reference 原始引用。
     * @return 找到的显示投影。
     */
    @Override
    public Optional<WarframeWorldStateCodebookEntryDTO> find(WarframeReferenceType type, String reference) {
        return type == null || StrUtil.isBlank(reference) ? Optional.empty()
            : Optional.ofNullable(officialIndex.codebook(type).get(reference.trim().toLowerCase(Locale.ROOT)));
    }

    /**
     * 收集语言引用后只发起一次批量补查。
     *
     * @param references 本次语言键集合。
     * @return 以请求键组织的查询结果。
     */
    @Override
    public Map<String, WarframeWorldStateCodebookEntryDTO> findLanguages(Collection<String> references) {
        var values = officialIndex.language(references);
        Map<String, WarframeWorldStateCodebookEntryDTO> result = new LinkedHashMap<>();
        for (String key : references) {
            if (StrUtil.isBlank(key))
                continue;
            String text = values.get(key.trim().toLowerCase(Locale.ROOT));
            if (text != null)
                result.put(key, new WarframeWorldStateCodebookEntryDTO(key, text, "", "", "official:language_string",
                    WarframeReferenceResolutionStatus.RESOLVED, "", false));
        }
        return Map.copyOf(result);
    }
}
