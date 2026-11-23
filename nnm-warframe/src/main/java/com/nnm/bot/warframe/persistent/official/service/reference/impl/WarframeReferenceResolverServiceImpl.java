package com.nnm.bot.warframe.persistent.official.service.reference.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceResolutionStatus;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceType;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.WarframeReferenceResolutionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeWorldStateCodebookEntryDTO;
import com.nnm.bot.warframe.persistent.official.pojo.query.reference.WarframeReferenceQuery;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeItemNameService;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeReferenceResolverService;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateCodebookService;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/** 动态引用使用同身份优先的官方/自维护树；未知代码原样交给上层标记。 */
@Service
public class WarframeReferenceResolverServiceImpl implements WarframeReferenceResolverService {
    /** 按资料类型批量查找官方代码及语言键。 */
    @Resource
    private WarframeWorldStateCodebookService worldStateCodebookService;
    /** 查询物品身份，兼顾缺少专用表记录的修饰项。 */
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
        var query = new WarframeReferenceQuery(reference, WarframeReferenceType.ITEM);
        return resolveAll(List.of(query)).get(query);
    }

    /**
     * 批量解析引用，复用同次读取获得的资料。
     *
     * @param references 引用。
     * @return 按资料类型及原始引用索引的解析结果。
     */
    @Override
    public Map<WarframeReferenceQuery, WarframeReferenceResolutionDTO>
        resolveAll(Collection<WarframeReferenceQuery> references) {
        if (CollUtil.isEmpty(references))
            return Map.of();
        List<String> itemKeys = references.stream()
            .filter(
                query -> query.type() == WarframeReferenceType.ITEM || query.type() == WarframeReferenceType.MODIFIER)
            .map(WarframeReferenceQuery::reference).filter(CharSequenceUtil::isNotBlank).distinct().toList();
        Map<String, WarframeReferenceResolutionDTO> items = itemNameService.resolveAll(itemKeys);
        Map<WarframeReferenceType, List<String>> typedReferences = new EnumMap<>(WarframeReferenceType.class);
        references.forEach(query -> {
            if (CharSequenceUtil.isNotBlank(query.reference()) && query.type() != WarframeReferenceType.ITEM)
                typedReferences.computeIfAbsent(query.type(), ignored -> new ArrayList<>()).add(query.reference());
        });
        Map<WarframeReferenceType, Map<String, WarframeWorldStateCodebookEntryDTO>> codebooks =
            new EnumMap<>(WarframeReferenceType.class);
        typedReferences.forEach((type, keys) -> codebooks.put(type, worldStateCodebookService.findAll(type, keys)));
        Map<WarframeReferenceQuery, WarframeReferenceResolutionDTO> result = new LinkedHashMap<>();
        for (WarframeReferenceQuery query : references) {
            String reference = query.reference();
            if (CharSequenceUtil.isBlank(reference)) {
                result.put(query, unresolved(reference));
                continue;
            }
            var entry = codebooks.getOrDefault(query.type(), Map.of()).get(reference);
            if (entry != null && entry.resolved()) {
                result.put(query, new WarframeReferenceResolutionDTO(reference, entry.displayName(), entry.reference(),
                    entry.source(), WarframeReferenceResolutionStatus.RESOLVED, List.of(entry.reference())));
            } else if (entry != null && entry.status() == WarframeReferenceResolutionStatus.AMBIGUOUS) {
                result.put(query, new WarframeReferenceResolutionDTO(reference, reference, reference, entry.source(),
                    entry.status(), entry.candidates()));
            } else if (query.type() == WarframeReferenceType.ITEM || query.type() == WarframeReferenceType.MODIFIER) {
                result.put(query, items.getOrDefault(reference, unresolved(reference)));
            } else {
                result.put(query, unresolved(reference));
            }
        }
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
