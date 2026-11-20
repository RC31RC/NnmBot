package com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate;

import cn.hutool.core.text.CharSequenceUtil;
import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceResolutionStatus;

/**
 * WorldStateDTO 固定代码的结构化解析结果。
 *
 * <p>
 * 即使后续代码簿从 JSON 或数据库加载，也保持同一返回结构， 调用方不需要关心具体来源。
 * </p>
 */
public record WarframeWorldStateCodebookEntryDTO(String reference, String displayName, String description,
    String relatedReference, String source, WarframeReferenceResolutionStatus status, String factionReference,
    boolean selfMaintained, String nameKey) {

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
     * @param selfMaintained 自维护维护。
     * @param nameKey 同身份的语言键；查询时按需解析，不在代码簿中缓存跨表结果。
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
    }

    /**
     * 附带自维护维护。
     *
     * @param value 待处理的字段值。
     * @return 游戏资料世界状态代码簿条目对象。
     */
    public WarframeWorldStateCodebookEntryDTO withSelfMaintained(boolean value) {
        return new WarframeWorldStateCodebookEntryDTO(reference, displayName, description, relatedReference, source,
            status, factionReference, value, nameKey);
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
