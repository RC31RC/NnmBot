package com.nnm.bot.warframe.persistent.official.service.worldstate;

import com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate.WarframeStateTranslationDetailsDTO;
import java.util.Collection;
import java.util.List;

/** 读取动态活动的静态说明与日历任务目录。 */
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
     * @return 能确定身份的名称和说明；同名多候选交由领域解析及诊断处理。
     */
    List<WarframeStateTranslationDetailsDTO> findTranslations(Collection<String> references);
}
