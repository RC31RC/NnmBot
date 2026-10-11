package com.nnm.bot.warframe.service.impl;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.application.WarframeRelicService;
import com.nnm.bot.warframe.constant.WarframeCommunityRecordSource;
import com.nnm.bot.warframe.constant.WarframeCommunitySource;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRelic;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRelicReward;
import com.nnm.bot.warframe.model.entity.WarframeCommunityRelicDrop;
import com.nnm.bot.warframe.repository.WarframeCommunityRelicRepository;
import com.nnm.bot.warframe.repository.WarframeCommunityRelicDropRepository;
import com.nnm.bot.warframe.repository.WarframeCommunityRelicRewardRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.function.Function;

@Service
public class WarframeRelicServiceImpl implements WarframeRelicService {
    /*
     * 只在本 Service 维护遗物来源优先级。所有来源仍分别落库；
     * 查询时按业务对象选择一个来源，不跨来源拼接主记录和明细。
     */
    private static final List<String> SOURCE_PRIORITY = List.of(
            "SELF_MAINTAINED",
            WarframeCommunitySource.WARFRAME_RELIC_DATA.code());

    @Resource
    private WarframeCommunityRelicRepository relicRepository;
    @Resource
    private WarframeCommunityRelicRewardRepository rewardRepository;
    @Resource
    private WarframeCommunityRelicDropRepository dropRepository;

    @Override
    public List<WarframeCommunityRelic> findRelics(String keyword) {
        String value = StrUtil.nullToEmpty(keyword).trim();
        List<WarframeCommunityRelic> relics = relicRepository.findAllByEnabledTrue();
        Map<String, String> selectedSources = preferredSources(
                sourcePresence(relics, WarframeCommunityRelic::getRecordKey));
        return selectPreferred(relics, selectedSources, WarframeCommunityRelic::getRecordKey).stream()
                .filter(item -> value.isBlank()
                        || StrUtil.containsIgnoreCase(item.getRecordKey(), value)
                        || StrUtil.containsIgnoreCase(item.getRelicName(), value))
                .toList();
    }

    @Override
    public List<WarframeCommunityRelicReward> findRewards(String relicKey, String rarity) {
        List<WarframeCommunityRelicReward> all = StrUtil.isBlank(rarity)
                ? rewardRepository.findAllByEnabledTrue()
                : rewardRepository.findAllByEnabledTrueAndRarityIgnoreCase(rarity);
        String key = StrUtil.nullToEmpty(relicKey).trim();
        List<WarframeCommunityRelic> relics = relicRepository.findAllByEnabledTrue();
        Map<String, String> selectedSources = preferredSources(
                sourcePresence(relics, WarframeCommunityRelic::getRecordKey));
        List<WarframeCommunityRelicReward> selected =
                selectPreferred(all, selectedSources, WarframeCommunityRelicReward::getRelicRecordKey);
        return selected.stream().filter(item -> key.isBlank()
                || StrUtil.equalsIgnoreCase(item.getRelicRecordKey(), key)
                || StrUtil.containsIgnoreCase(item.getRewardName(), key))
                .toList();
    }

    @Override
    public List<WarframeCommunityRelicDrop> findDrops(String relicKey) {
        List<WarframeCommunityRelicDrop> all = dropRepository.findAllByEnabledTrue();
        String key = StrUtil.nullToEmpty(relicKey).trim();
        List<WarframeCommunityRelic> relics = relicRepository.findAllByEnabledTrue();
        Map<String, String> selectedSources = preferredSources(
                sourcePresence(relics, WarframeCommunityRelic::getRecordKey));
        return selectPreferred(all, selectedSources, WarframeCommunityRelicDrop::getRelicRecordKey)
                .stream()
                .filter(item -> key.isBlank()
                        || StrUtil.equalsIgnoreCase(item.getRelicRecordKey(), key)
                        || StrUtil.containsIgnoreCase(item.getRelicName(), key)
                        || StrUtil.containsIgnoreCase(item.getLocationText(), key))
                .toList();
    }

    private Map<String, String> preferredSources(
            Map<String, LinkedHashSet<String>> sourceCandidates) {
        Map<String, String> selected = new LinkedHashMap<>();
        sourceCandidates.forEach((key, sources) -> sources.stream()
                .filter(SOURCE_PRIORITY::contains)
                .min(Comparator.comparingInt(this::sourcePriority))
                .ifPresent(source -> selected.put(key, source)));
        return selected;
    }

    private <T> Map<String, LinkedHashSet<String>> sourcePresence(
            List<T> records, Function<T, String> keyExtractor) {
        return records.stream()
                .filter(record -> sourceCode(record) != null
                        && StrUtil.isNotBlank(keyExtractor.apply(record)))
                .collect(Collectors.groupingBy(
                        keyExtractor,
                        LinkedHashMap::new,
                        Collectors.mapping(
                                this::sourceCode,
                                Collectors.toCollection(LinkedHashSet::new))));
    }

    private <T> List<T> selectPreferred(
            List<T> records, Map<String, String> selectedSources, Function<T, String> keyExtractor) {
        // 来源由主记录决定；空明细不触发跨来源补充，且同一父记录的全部子项必须保留。
        return records.stream()
                .filter(record -> {
                    String selectedSource = selectedSources.get(keyExtractor.apply(record));
                    return StrUtil.isNotBlank(selectedSource)
                            && Objects.equals(sourceCode(record), selectedSource);
                })
                .toList();
    }

    private String sourceCode(Object record) {
        if (record instanceof WarframeCommunityRelic relic) {
            return effectiveSource(relic.getRecordSource(), relic.getSourceCode());
        }
        if (record instanceof WarframeCommunityRelicReward reward) {
            return effectiveSource(reward.getRecordSource(), reward.getSourceCode());
        }
        if (record instanceof WarframeCommunityRelicDrop drop) {
            return effectiveSource(drop.getRecordSource(), drop.getSourceCode());
        }
        return null;
    }

    private String effectiveSource(WarframeCommunityRecordSource recordSource, String sourceCode) {
        return recordSource == WarframeCommunityRecordSource.SELF_MAINTAINED
                ? "SELF_MAINTAINED" : sourceCode;
    }

    private int sourcePriority(String sourceCode) {
        int index = SOURCE_PRIORITY.indexOf(sourceCode);
        return index < 0 ? SOURCE_PRIORITY.size() : index;
    }
}
