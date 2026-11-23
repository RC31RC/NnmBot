package com.nnm.bot.warframe.persistent.official.service.reference;

import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.WarframeReferenceResolutionDTO;
import com.nnm.bot.warframe.persistent.official.pojo.query.reference.WarframeReferenceQuery;
import java.util.Collection;
import java.util.Map;

/** 按资料类型解析动态响应与命令中的引用。 */
public interface WarframeReferenceResolverService {
    /**
     * 解析引用或输入对应的实际业务对象。
     *
     * @param reference 引用。
     * @return 游戏资料引用解析状态对象。
     */
    WarframeReferenceResolutionDTO resolve(String reference);

    /**
     * 批量解析引用，复用同次读取获得的资料。
     *
     * @param references 带资料类型的原始引用。
     * @return 按资料类型及原始引用索引的解析结果，包含未命中项。
     */
    Map<WarframeReferenceQuery, WarframeReferenceResolutionDTO>
        resolveAll(Collection<WarframeReferenceQuery> references);
}
