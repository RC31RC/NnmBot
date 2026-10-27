package com.nnm.bot.warframe.persistent.official.service.reference.impl;

import cn.hutool.core.collection.CollUtil;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeItemNameService;
import com.nnm.bot.warframe.persistent.official.service.reference.WarframeReferenceResolverService;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceResolutionStatus;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceType;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.WarframeReferenceResolutionDTO;
import com.nnm.bot.warframe.persistent.official.service.worldstate.WarframeWorldStateCodebookService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public WarframeReferenceResolutionDTO resolve(String reference) {
        if (StrUtil.isBlank(reference)) return unresolved(reference);
        return resolveAll(Map.of(reference, WarframeReferenceType.ITEM)).get(reference);
    }

    /**
     * 批量解析引用，复用同次读取获得的资料。
     *
     * @param references 引用。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    public Map<String, WarframeReferenceResolutionDTO> resolveAll(Map<String, WarframeReferenceType> references) {
        if (CollUtil.isEmpty(references)) return Map.of();
        Map<String, WarframeReferenceResolutionDTO> result = new LinkedHashMap<>();
        List<String> itemKeys = references.entrySet().stream()
                .filter(entry -> entry.getValue() == null || entry.getValue() == WarframeReferenceType.ITEM)
                .map(Map.Entry::getKey).filter(StrUtil::isNotBlank).toList();
        result.putAll(itemNameService.resolveAll(itemKeys));
        references.forEach((reference, type) -> {
            if (reference == null) return;
            if (type == null || type == WarframeReferenceType.ITEM) {
                result.putIfAbsent(reference, unresolved(reference));
                return;
            }
            var entry = worldStateCodebookService.find(type, reference);
            result.put(reference, entry.filter(value -> value.resolved()).map(value -> new WarframeReferenceResolutionDTO(
                    reference, value.displayName(), reference, value.source(), WarframeReferenceResolutionStatus.RESOLVED,
                    List.of(reference))).orElseGet(() -> unresolved(reference)));
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
