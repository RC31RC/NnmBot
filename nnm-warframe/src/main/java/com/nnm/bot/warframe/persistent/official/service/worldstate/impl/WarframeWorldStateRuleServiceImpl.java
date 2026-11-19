package com.nnm.bot.warframe.persistent.official.service.worldstate.impl;

import static com.nnm.bot.warframe.persistent.official.parser.WarframeOfficialValues.*;

import com.nnm.bot.warframe.runtime.cache.dataset.WarframeDatasetCache;
import com.nnm.bot.warframe.constant.WarframeDatasetCacheNames;
import com.nnm.bot.warframe.persistent.official.constant.WarframeOfficialDataDomain;
import com.nnm.bot.warframe.persistent.official.pojo.dto.progression.OfficialChallengeDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.record.WarframeOfficialRecordReadDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeStateTranslationDetailsDTO;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialLookupService;
import com.nnm.bot.warframe.persistent.official.service.record.WarframeOfficialRecordReadService;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateRuleService;
import jakarta.annotation.Resource;
import java.util.Collection;
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
     * 使用各表名称索引定位身份，再按本次命中的业务键批量装载详情。
     *
     * @param references 本次需要展示的引用集合。
     * @return 以原始引用关联的名称和描述。
     */
    @Override
    public List<WarframeStateTranslationDetailsDTO> findTranslations(Collection<String> references) {
        Map<String, WarframeStateTranslationDetailsDTO> result = new LinkedHashMap<>();
        if (references.isEmpty())
            return List.of();
        for (var domain : List.of(WarframeOfficialDataDomain.CHALLENGE, WarframeOfficialDataDomain.ACTIVITY,
            WarframeOfficialDataDomain.BOUNTY, WarframeOfficialDataDomain.ITEM_LOCALIZATION)) {
            var index = lookup.nameIndex(domain);
            Map<String, String> requested = new LinkedHashMap<>();
            for (String reference : references) {
                if (result.containsKey(reference))
                    continue;
                var candidates = index.matches(domain, reference);
                if (candidates.size() == 1)
                    requested.put(reference, index.items().get(candidates.getFirst()).bkey());
            }
            var records = lookup.records(domain, requested.values());
            requested.forEach((reference, bkey) -> {
                var row = records.get(bkey.toLowerCase(Locale.ROOT));
                if (row != null)
                    result.put(reference, translation(reference, row));
            });
        }
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
        return new WarframeStateTranslationDetailsDTO(reference, name(row), text);
    }
}
