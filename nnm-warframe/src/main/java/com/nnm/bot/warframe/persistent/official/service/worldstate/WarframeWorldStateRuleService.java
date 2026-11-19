package com.nnm.bot.warframe.persistent.official.service.worldstate;

import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeStateTranslationDetailsDTO;
import java.util.Collection;
import java.util.List;

public interface WarframeWorldStateRuleService {
    /**
     * 读取已采纳的官方世界状态译名规则。
     *
     * @return 按当前条件筛选或转换后的数据集合。
     */
    List<WarframeStateTranslationDetailsDTO> stateTranslations();

    /**
     * 只补查本次动态数据引用的挑战、活动、赏金与物品本地化资料。
     *
     * @param references 本次展示使用的业务引用或标准名称。
     * @return 保留请求身份的名称与描述，不另存跨表合并缓存。
     */
    List<WarframeStateTranslationDetailsDTO> findTranslations(Collection<String> references);
}
