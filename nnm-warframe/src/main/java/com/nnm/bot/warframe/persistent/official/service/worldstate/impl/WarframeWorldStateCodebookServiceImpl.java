package com.nnm.bot.warframe.persistent.official.service.worldstate.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.cache.worldstate.WarframeOfficialWorldStateIndex;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceResolutionStatus;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceType;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeWorldStateCodebookEntryDTO;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateCodebookService;
import com.nnm.bot.warframe.normalization.WarframeNameNormalizer;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
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
        return reference == null ? Optional.empty()
            : Optional.ofNullable(findAll(type, List.of(reference)).get(reference));
    }

    /**
     * 读取本次引用的代码簿入口，按语言键优先补中文，缺失时接受已入库英文。
     *
     * @param type 引用类型。
     * @param references 本次原始引用。
     * @return 以原始引用组织的投影；展示语言不改变业务身份。
     */
    @Override
    public Map<String, WarframeWorldStateCodebookEntryDTO> findAll(WarframeReferenceType type,
        Collection<String> references) {
        if (type == null || references.isEmpty())
            return Map.of();
        var entries = type == WarframeReferenceType.LANGUAGE_STRING
            ? Map.<String, WarframeWorldStateCodebookEntryDTO>of() : officialIndex.codebook(type);
        Map<String, WarframeWorldStateCodebookEntryDTO> result = new LinkedHashMap<>();
        Map<String, String> languageKeys = new LinkedHashMap<>();
        for (String reference : references) {
            if (StrUtil.isBlank(reference))
                continue;
            var entry = entries.get(reference.trim().toLowerCase(Locale.ROOT));
            if (entry != null) {
                result.put(reference, entry);
                if (!WarframeNameNormalizer.hasChinese(entry.displayName()) && StrUtil.isNotBlank(entry.nameKey()))
                    languageKeys.put(reference, entry.nameKey());
            } else if (reference.startsWith("/Lotus/Language/")) {
                languageKeys.put(reference, reference);
            }
        }
        // 仅在查询边界关联语言数据；语言表失效后不必重建其他表的代码簿。
        var languages = findLanguages(languageKeys.values().stream().distinct().toList());
        languageKeys.forEach((reference, key) -> {
            var language = languages.get(key);
            if (language == null || !WarframeNameNormalizer.isReadableDisplayName(language.displayName()))
                return;
            var entry = result.get(reference);
            // 已有可读英文名称时只用中文译名覆盖；无名称时官方英文也是有效展示结果。
            if (entry != null && entry.resolved() && WarframeNameNormalizer.isReadableDisplayName(entry.displayName())
                && !WarframeNameNormalizer.hasChinese(language.displayName()))
                return;
            result.put(reference, entry == null ? language
                : new WarframeWorldStateCodebookEntryDTO(entry.reference(), language.displayName(), entry.description(),
                    entry.relatedReference(), entry.source(), WarframeReferenceResolutionStatus.RESOLVED,
                    entry.factionReference(), entry.selfMaintained(), entry.nameKey()));
        });
        return Map.copyOf(result);
    }

    /**
     * 收集语言引用后只发起一次批量补查。
     *
     * @param references 本次语言键集合。
     * @return 以请求键组织的查询结果。
     */
    @Override
    public Map<String, WarframeWorldStateCodebookEntryDTO> findLanguages(Collection<String> references) {
        if (references.isEmpty())
            return Map.of();
        var values = officialIndex.language(references);
        Map<String, WarframeWorldStateCodebookEntryDTO> result = new LinkedHashMap<>();
        for (String key : references) {
            if (StrUtil.isBlank(key))
                continue;
            String text = values.get(key.trim().toLowerCase(Locale.ROOT));
            if (WarframeNameNormalizer.isReadableDisplayName(text))
                result.put(key, new WarframeWorldStateCodebookEntryDTO(key, text, "", "", "official:language_string",
                    WarframeReferenceResolutionStatus.RESOLVED, "", false, key));
        }
        return Map.copyOf(result);
    }
}
