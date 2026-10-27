package com.nnm.bot.warframe.persistent.official.service.reference;

import com.nnm.bot.warframe.persistent.official.constant.WarframeReferenceType;
import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.WarframeReferenceResolutionDTO;
import java.util.Map;

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
     * @param references 引用。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    Map<String, WarframeReferenceResolutionDTO> resolveAll(Map<String, WarframeReferenceType> references);
}
