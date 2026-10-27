package com.nnm.bot.warframe.persistent.official.pojo.dto.reference;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceResolutionStatus;
import java.util.List;

/**
 * WorldStateDTO 内部路径或代码的玩家可读解析结果。
 */
public record WarframeReferenceResolutionDTO(
        String reference,
        String displayName,
        String uniqueName,
        String domainCode,
        WarframeReferenceResolutionStatus status,
        List<String> candidates) {

    /**
     * 构造引用解析状态传输对象，并保存初始化参数。
     *
     * @param reference 引用。
     * @param displayName 展示名称。
     * @param uniqueName 唯一名称。
     * @param domainCode 业务领域代码。
     * @param status 状态。
     * @param candidates 候选集合。
     */
    public WarframeReferenceResolutionDTO {
        reference = StrUtil.emptyIfNull(reference);
        displayName = StrUtil.blankToDefault(displayName, reference);
        candidates = CollUtil.isEmpty(candidates) ? List.of() : List.copyOf(candidates);
    }

    /**
     * 构造已经确定业务身份的引用解析结果。
     *
     * @return 满足构造已经确定业务身份的引用解析结果时为真，否则为假。
     */
    public boolean resolved() {
        return status == WarframeReferenceResolutionStatus.RESOLVED;
    }
}
