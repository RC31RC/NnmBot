package com.nnm.bot.warframe.pojo;

import cn.hutool.core.util.StrUtil;
import com.nnm.bot.warframe.constant.WarframeReferenceResolutionStatus;

/**
 * WorldState 固定代码的结构化解析结果。
 *
 * <p>即使后续代码簿从 JSON 或数据库加载，也保持同一返回结构，
 * 调用方不需要关心具体来源。</p>
 */
public record WarframeWorldStateCodebookEntry(
        String reference,
        String displayName,
        String description,
        String relatedReference,
        String source,
        WarframeReferenceResolutionStatus status,
        String factionReference) {

    public WarframeWorldStateCodebookEntry(
            String reference,
            String displayName,
            String description,
            String relatedReference,
            String source,
            WarframeReferenceResolutionStatus status) {
        this(reference, displayName, description, relatedReference, source, status, "");
    }

    public WarframeWorldStateCodebookEntry {
        reference = StrUtil.emptyIfNull(reference);
        displayName = StrUtil.blankToDefault(displayName, reference);
        description = StrUtil.emptyIfNull(description);
        relatedReference = StrUtil.emptyIfNull(relatedReference);
        source = StrUtil.emptyIfNull(source);
        status = status == null
                ? WarframeReferenceResolutionStatus.UNRESOLVED
                : status;
        factionReference = StrUtil.emptyIfNull(factionReference);
    }

    public boolean resolved() {
        return status == WarframeReferenceResolutionStatus.RESOLVED;
    }
}
