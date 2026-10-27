package com.nnm.bot.warframe.persistent.official.service.reference;

import com.nnm.bot.warframe.persistent.official.pojo.dto.reference.WarframeReferenceResolutionDTO;
import java.util.Collection;
import java.util.Map;

/** 内部业务键的精确名称投影，不使用路径截断或玩家输入模糊匹配。 */
public interface WarframeItemNameService {
    /**
     * 批量解析引用，复用同次读取获得的资料。
     *
     * @param itemKeys 物品键集集合。
     * @return 以本方法约定的业务键组织的数据映射。
     */
    Map<String, WarframeReferenceResolutionDTO> resolveAll(Collection<String> itemKeys);
}
