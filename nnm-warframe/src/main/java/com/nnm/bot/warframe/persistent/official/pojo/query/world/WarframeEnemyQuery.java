package com.nnm.bot.warframe.persistent.official.pojo.query.world;

import com.nnm.bot.warframe.persistent.official.constant.WarframeCatalogConstants.EnemySection;

/** warframe.enemies 命令的类型化查询条件。 */
public record WarframeEnemyQuery(String keyword, EnemySection section, int page, String error) {
}
