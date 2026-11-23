package com.nnm.bot.warframe.persistent.official.service.worldstate.impl;

import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;

import com.nnm.bot.warframe.runtime.cache.dataset.WarframeDatasetCache;
import com.nnm.bot.warframe.constant.WarframeDatasetCacheNames;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialReferenceKeys;
import com.nnm.bot.warframe.persistent.official.pojo.dto.progression.OfficialChallengeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialNameDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeStateTranslationDetailsDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialLookupService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateRuleService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

/** 查询当前完整业务树中的名称与描述；官方及自维护记录共用有效性规则。 */
@Service
public class WarframeWorldStateRuleServiceImpl implements WarframeWorldStateRuleService {
    @Resource
    private WarframeOfficialRecordReadService catalog;
    @Resource
    private WarframeDatasetCache cache;
    @Resource
    private WarframeOfficialLookupService lookup;

    /**
     * 读取已采纳的官方世界状态译名规则。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    @Override
    public List<WarframeStateTranslationDetailsDTO> stateTranslations() {
        return cache.snapshot(WarframeDatasetCacheNames.official(WarframeOfficialDataDomain.CHALLENGE), "translations",
            () -> catalog.records(WarframeOfficialDataDomain.CHALLENGE).stream()
                .map(root -> translation(root.bkey(), root)).toList());
    }

    /**
     * 在候选资料中以业务键优先定位唯一身份，再按表批量装载详情。
     *
     * @param references 本次需要展示的引用集合。
     * @return 以原始引用关联的名称和描述。
     */
    @Override
    public List<WarframeStateTranslationDetailsDTO> findTranslations(Collection<String> references) {
        if (references.isEmpty())
            return List.of();
        var distinctReferences = references.stream().distinct().toList();
        Map<String, List<WarframeOfficialNameDTO>> candidatesByReference = new LinkedHashMap<>();
        for (var domain : List.of(WarframeOfficialDataDomain.CHALLENGE, WarframeOfficialDataDomain.ACTIVITY,
            WarframeOfficialDataDomain.BOUNTY, WarframeOfficialDataDomain.ITEM_LOCALIZATION)) {
            var index = lookup.nameIndex(domain);
            for (String reference : distinctReferences) {
                for (String key : index.matches(domain, reference)) {
                    candidatesByReference.computeIfAbsent(reference, ignored -> new ArrayList<>())
                        .add(index.items().get(key));
                }
            }
        }
        Map<WarframeOfficialDataDomain, Map<String, String>> selected = new EnumMap<>(WarframeOfficialDataDomain.class);
        candidatesByReference.forEach((reference, candidates) -> {
            var exact = candidates.stream()
                .filter(candidate -> WarframeOfficialReferenceKeys
                    .identities(WarframeOfficialDataDomain.fromCode(candidate.domainCode()), candidate.bkey()).stream()
                    .anyMatch(key -> key.equalsIgnoreCase(reference.trim())))
                .toList();
            var matches = exact.isEmpty() ? candidates : exact;
            if (exact.size() > 1 && reference.trim().startsWith("/")) {
                // 同一完整路径可能同时存在于专用定义和本地化表，说明以专用定义为准。
                var definitions = exact.stream().filter(candidate -> !WarframeOfficialDataDomain.ITEM_LOCALIZATION
                    .domainCode().equals(candidate.domainCode())).toList();
                if (!definitions.isEmpty())
                    matches = definitions;
            }
            if (matches.size() == 1) {
                var match = matches.getFirst();
                selected.computeIfAbsent(WarframeOfficialDataDomain.fromCode(match.domainCode()),
                    ignored -> new LinkedHashMap<>()).put(reference, match.bkey());
            }
        });
        Map<String, WarframeStateTranslationDetailsDTO> result = new LinkedHashMap<>();
        selected.forEach((domain, requested) -> {
            var records = lookup.records(domain, requested.values());
            requested.forEach((reference, bkey) -> {
                var row = records.get(bkey.toLowerCase(Locale.ROOT));
                if (row != null)
                    result.put(reference, translation(reference, row));
            });
        });
        return List.copyOf(result.values());
    }

    /**
     * 从完整记录中生成展示投影，挑战计数取同一记录的实际参数。
     *
     * @param reference 调用方使用的引用。
     * @param row 已选择的官方或自维护完整树。
     * @return 名称与已展开计数的说明。
     */
    private static WarframeStateTranslationDetailsDTO translation(String reference, WarframeOfficialRecordReadDTO row) {
        String text = description(row.data());
        if (text != null && row.data() instanceof OfficialChallengeDTO challenge
            && challenge.getRequiredCount() != null)
            text = text.replace("|COUNT|", challenge.getRequiredCount().stripTrailingZeros().toPlainString());
        // 缺少展示名时由调用方继续查语言键，避免把主表业务键当成已经翻译的标题。
        return new WarframeStateTranslationDetailsDTO(reference, first(row.localizedName(), row.officialName()), text);
    }
}
