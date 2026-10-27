package com.nnm.bot.warframe.persistent.official.repository.rewards.custom;

import java.util.List;

public interface OfficialRelicRepositoryCustom {
    /**
     * 从本地遗物主表筛出可能匹配名称、奖励或获取地点的优先记录。
     *
     * @param keyword 命令查询词；为空时返回全部启用的优先遗物主记录。
     * @return 已按自维护优先规则排除被覆盖官方记录的主键列表。
     */
    List<Long> findPreferredRelicIds(String keyword);
}
