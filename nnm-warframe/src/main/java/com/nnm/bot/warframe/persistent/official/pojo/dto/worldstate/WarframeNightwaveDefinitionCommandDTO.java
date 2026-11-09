package com.nnm.bot.warframe.persistent.official.pojo.dto.worldstate;

import com.nnm.bot.warframe.persistent.official.constant.WarframeCatalogConstants.NightwaveSection;

/** warframe.nightwave-definitions 命令的类型化查询条件。 */
public record WarframeNightwaveDefinitionCommandDTO(String keyword, NightwaveSection section, int page, String error) {
}
