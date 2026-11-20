package com.nnm.bot.warframe.persistent.official.service.reference.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceResolutionStatus;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceType;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.WarframeReferenceResolutionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeWorldStateCodebookEntryDTO;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeItemNameService;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeReferenceResolverService;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateCodebookService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

/** 动态引用使用同身份优先的官方/自维护树；未知代码原样交给上层标记。 */
@Service
public class WarframeReferenceResolverServiceImpl implements WarframeReferenceResolverService {
    @Resource
    private WarframeWorldStateCodebookService worldStateCodebookService;
    @Resource
    private WarframeItemNameService itemNameService;

    /**
     * 解析引用或输入对应的实际业务对象。
     *
     * @param reference 引用。
     * @return 游戏资料引用解析状态对象。
     */
    @Override
    public WarframeReferenceResolutionDTO resolve(String reference) {
        if (CharSequenceUtil.isBlank(reference))
            return unresolved(reference);
        return resolveAll(Map.of(reference, WarframeReferenceType.ITEM)).get(reference);
    }

    /**
     * 批量解析引用，复用同次读取获得的资料。
     *
     * @param references 引用。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    @Override
    public Map<String, WarframeReferenceResolutionDTO> resolveAll(Map<String, WarframeReferenceType> references) {
        if (CollUtil.isEmpty(references))
            return Map.of();
        Map<String, WarframeReferenceResolutionDTO> result = new LinkedHashMap<>();
        List<String> itemKeys = references.entrySet().stream()
            .filter(entry -> entry.getValue() == null || entry.getValue() == WarframeReferenceType.ITEM
                || entry.getValue() == WarframeReferenceType.MODIFIER)
            .map(Map.Entry::getKey).filter(CharSequenceUtil::isNotBlank).toList();
        result.putAll(itemNameService.resolveAll(itemKeys));
        Map<WarframeReferenceType, List<String>> typedReferences = new EnumMap<>(WarframeReferenceType.class);
        references.forEach((reference, type) -> {
            if (CharSequenceUtil.isNotBlank(reference) && type != null && type != WarframeReferenceType.ITEM)
                typedReferences.computeIfAbsent(type, ignored -> new ArrayList<>()).add(reference);
        });
        Map<WarframeReferenceType, Map<String, WarframeWorldStateCodebookEntryDTO>> codebooks =
            new EnumMap<>(WarframeReferenceType.class);
        typedReferences.forEach((type, keys) -> codebooks.put(type, worldStateCodebookService.findAll(type, keys)));
        references.forEach((reference, type) -> {
            if (reference == null)
                return;
            if (type == null || type == WarframeReferenceType.ITEM) {
                result.putIfAbsent(reference, unresolved(reference));
                return;
            }
            var entry = Optional.ofNullable(codebooks.getOrDefault(type, Map.of()).get(reference));
            result.put(reference,
                entry.filter(value -> value.resolved())
                    .map(value -> new WarframeReferenceResolutionDTO(reference, value.displayName(), value.reference(),
                        value.source(), WarframeReferenceResolutionStatus.RESOLVED, List.of(value.reference())))
                    .orElseGet(() -> type == WarframeReferenceType.MODIFIER
                        ? result.getOrDefault(reference, unresolved(reference)) : unresolved(reference)));
        });
        return Map.copyOf(result);
    }

    /**
     * 构造保留原始引用的未解析结果。
     *
     * @param reference 引用。
     * @return 游戏资料引用解析状态对象。
     */
    private static WarframeReferenceResolutionDTO unresolved(String reference) {
        return new WarframeReferenceResolutionDTO(reference, reference, reference, null,
            WarframeReferenceResolutionStatus.UNRESOLVED, List.of());
    }
}
