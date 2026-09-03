package com.nnm.bot.warframe.application;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.constant.WarframeReferenceResolutionStatus;

import java.util.List;

/**
 * WorldState 内部路径或代码的玩家可读解析结果。
 */
public record WarframeReferenceResolution(
        String reference,
        String displayName,
        String uniqueName,
        String exportType,
        WarframeReferenceResolutionStatus status,
        List<String> candidates) {

    public WarframeReferenceResolution {
        reference = StrUtil.emptyIfNull(reference);
        displayName = StrUtil.blankToDefault(displayName, reference);
        candidates = CollUtil.isEmpty(candidates) ? List.of() : List.copyOf(candidates);
    }

    public boolean resolved() {
        return status == WarframeReferenceResolutionStatus.RESOLVED;
    }
}
