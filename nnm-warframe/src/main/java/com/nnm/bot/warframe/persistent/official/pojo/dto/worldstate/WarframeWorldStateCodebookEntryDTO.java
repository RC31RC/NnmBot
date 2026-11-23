package com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate;

import cn.hutool.core.text.CharSequenceUtil;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceResolutionStatus;
import java.util.List;

/**
 * 由官方业务表读取的动态引用。业务键、语言键及同名候选分别保留，方便展示与核对。
 */
public record WarframeWorldStateCodebookEntryDTO(String reference, String displayName, String description,
    String relatedReference, String source, WarframeReferenceResolutionStatus status, String factionReference,
    boolean selfMaintained, String nameKey, List<String> candidates) {

    /**
     * 构造世界状态代码簿条目传输对象，并保存初始化参数。
     *
     * @param reference 引用。
     * @param displayName 展示名称。
     * @param description 说明。
     * @param relatedReference 关联引用。
     * @param source 当前数据来源标识。
     * @param status 状态。
     * @param factionReference 阵营引用。
     * @param selfMaintained 是否来自自维护版本。
     * @param nameKey 同身份的语言键；查询时按需解析，不在代码簿中缓存跨表结果。
     * @param candidates 同一个名称命中的多个官方业务键。
     */
    public WarframeWorldStateCodebookEntryDTO {
        reference = CharSequenceUtil.emptyIfNull(reference);
        displayName = CharSequenceUtil.blankToDefault(displayName, reference);
        description = CharSequenceUtil.emptyIfNull(description);
        relatedReference = CharSequenceUtil.emptyIfNull(relatedReference);
        source = CharSequenceUtil.emptyIfNull(source);
        status = status == null ? WarframeReferenceResolutionStatus.UNRESOLVED : status;
        factionReference = CharSequenceUtil.emptyIfNull(factionReference);
        nameKey = CharSequenceUtil.emptyIfNull(nameKey);
        candidates = candidates == null ? List.of() : List.copyOf(candidates);
    }

    /**
     * 复制解析结果并设置记录的自维护标识。
     *
     * @param value 当前选中的记录是否为自维护版本。
     * @return 保留解析结果与候选的副本。
     */
    public WarframeWorldStateCodebookEntryDTO withSelfMaintained(boolean value) {
        return new WarframeWorldStateCodebookEntryDTO(reference, displayName, description, relatedReference, source,
            status, factionReference, value, nameKey, candidates);
    }

    /**
     * 检查该引用是否已有确定身份和可读名称。
     *
     * @return 解析已完成时为真。
     */
    public boolean resolved() {
        return status == WarframeReferenceResolutionStatus.RESOLVED;
    }
}
