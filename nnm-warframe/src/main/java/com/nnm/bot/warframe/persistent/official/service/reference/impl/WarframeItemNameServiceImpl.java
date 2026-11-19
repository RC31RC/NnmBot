package com.nnm.bot.warframe.persistent.official.service.reference.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceResolutionStatus;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialReferenceKeys;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialNameDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.WarframeReferenceResolutionDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialLookupService;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeItemNameService;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateCodebookService;
import com.nnm.bot.warframe.normalization.WarframeNameNormalizer;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

/** 名称直接查询官方业务表中的有效官方／自维护记录，不依赖社区翻译兜底。 */
@Service
public class WarframeItemNameServiceImpl implements WarframeItemNameService {
    @Resource
    private WarframeOfficialLookupService officialLookup;
    @Resource
    private WarframeWorldStateCodebookService codebook;

    /**
     * 使用各表轻量名称索引批量匹配；同名多身份保留歧义，语言键按需补查。
     *
     * @param itemKeys 本次请求的完整路径或标准名称。
     * @return 以调用方原始引用组织的解析结果。
     */
    @Override
    public Map<String, WarframeReferenceResolutionDTO> resolveAll(Collection<String> itemKeys) {
        List<String> keys = itemKeys.stream().filter(StrUtil::isNotBlank).distinct().toList();
        if (keys.isEmpty())
            return Map.of();
        Map<String, Map<String, WarframeOfficialNameDTO>> matches = new LinkedHashMap<>();
        for (var domain : WarframeOfficialDataDomain.values()) {
            if (!domain.itemIndex() && domain != WarframeOfficialDataDomain.ITEM_LOCALIZATION
                && domain != WarframeOfficialDataDomain.ACTIVITY && domain != WarframeOfficialDataDomain.CHALLENGE)
                continue;
            var index = officialLookup.nameIndex(domain);
            for (String key : keys) {
                for (String identity : index.matches(domain, key)) {
                    var item = index.items().get(identity);
                    if (item == null)
                        continue;
                    var found = matches.computeIfAbsent(key, ignored -> new LinkedHashMap<>());
                    String businessKey =
                        WarframeOfficialReferenceKeys.candidates(item.bkey()).getLast().toLowerCase(Locale.ROOT);
                    // 同身份的官方记录可能同时来自物品表和本地化表；优先中文，其次保留可展示名称。
                    found.merge(businessKey, item, (previous, current) -> {
                        if (WarframeNameNormalizer.hasChinese(current.localizedName())
                            && !WarframeNameNormalizer.hasChinese(previous.localizedName()))
                            return current;
                        return displayName(previous) != null ? previous : current;
                    });
                }
            }
        }
        var languages = codebook.findLanguages(keys.stream().filter(key -> !matches.containsKey(key) || matches.get(key)
            .values().stream().noneMatch(item -> WarframeNameNormalizer.hasChinese(item.localizedName()))).toList());
        Map<String, WarframeReferenceResolutionDTO> result = new LinkedHashMap<>();
        for (String key : keys) {
            var found = matches.getOrDefault(key, Map.of());
            if (found.size() > 1) {
                result.put(key, new WarframeReferenceResolutionDTO(key, key, key, null,
                    WarframeReferenceResolutionStatus.AMBIGUOUS, List.copyOf(found.keySet())));
            } else if (found.size() == 1) {
                var item = found.values().iterator().next();
                var language = languages.get(key);
                String name = WarframeNameNormalizer.hasChinese(item.localizedName()) ? item.localizedName()
                    : language != null && WarframeNameNormalizer.hasChinese(language.displayName())
                        ? language.displayName() : displayName(item);
                boolean resolved = name != null;
                result.put(key, new WarframeReferenceResolutionDTO(
                    key, resolved ? name : key, item.bkey(), resolved ? item.domainCode() : null, resolved
                        ? WarframeReferenceResolutionStatus.RESOLVED : WarframeReferenceResolutionStatus.UNRESOLVED,
                    resolved ? List.of(item.bkey()) : List.of()));
            } else {
                var language = languages.get(key);
                boolean resolved =
                    language != null && WarframeNameNormalizer.isReadableDisplayName(language.displayName());
                result.put(key,
                    new WarframeReferenceResolutionDTO(
                        key, resolved ? language.displayName() : key, key, resolved ? language.source() : null, resolved
                            ? WarframeReferenceResolutionStatus.RESOLVED : WarframeReferenceResolutionStatus.UNRESOLVED,
                        List.of()));
            }
        }
        return Map.copyOf(result);
    }

    /**
     * 取已命中唯一官方业务身份的可读名称；没有中文译名也不影响解析结果。
     *
     * @param item 官方记录的名称投影。
     * @return 真实可展示名称，或 null。
     */
    private static String displayName(WarframeOfficialNameDTO item) {
        if (WarframeNameNormalizer.isReadableDisplayName(item.localizedName()))
            return item.localizedName();
        return WarframeNameNormalizer.isReadableDisplayName(item.officialName()) ? item.officialName() : null;
    }
}
