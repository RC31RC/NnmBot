package com.nnm.bot.warframe.persistent.official.service.reference;

import com.nnm.bot.warframe.persistent.official.pojo.vo.reference.WarframeTranslationCommandVO;

public interface WarframeTranslationService {
    /**
     * 按关键词查询已采纳的官方译名。
     *
     * @param keyword 筛选使用的关键词。
     * @return 查询资料的命令展示内容。
     */
    WarframeTranslationCommandVO translations(String keyword);
}
